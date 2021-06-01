/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.event.support;

import org.apache.shiro.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * 默认事件总线实现，将事件同步发布到注册的侦听器。 侦听器可以根据需要注册或注销事件。
 *
 * 事件总线启用了Shiro内的发布/订阅范例-组件可以发布或使用他们认为相关的事件，而无需与其他组件紧密耦合。
 * 通过促进组件之间的松散耦合和高内聚力以及更安全的可插拔体系结构（随时间变化具有更大的弹性），可以在Shiro中提供极大的灵活性。
 * 发送事件
 * 如果某个组件希望将事件发布到其他组件：
 *        MyEvent myEvent = createMyEvent();
 *        eventBus.publish(myEvent);
 *
 * 事件总线将确定事件的类型，然后将事件分派给希望接收该类型事件的组件。
 * 接收事件
 * 组件可以通过执行以下操作来接收感兴趣的事件。
 * 对于您要使用的每种事件类型，创建一个接受单个事件参数的公共方法。 方法参数类型指示要接收的事件的类型。
 * 使用Subscribe批注对每个这些公共方法进行批注。
 * 向事件总线注册组件：
 *            eventBus.register(myComponent);
 *
 * 在注册组件之后，当发布相应类型的事件时，将按预期调用该组件的“ Subscribe注释的方法。
 * 尽管没有共享/导入任何代码（尽管Guava已获得Apache 2.0许可并可以使用），但该设计（及其辅助组件）在很大程度上受Guava的EventBus 概念的影响。
 * 此实现是线程安全的，可以同时使用。
 *
 * A default event bus implementation that synchronously publishes events to registered listeners.  Listeners can be
 * registered or unregistered for events as necessary.
 * <p/>
 * An event bus enables a publish/subscribe paradigm within Shiro - components can publish or consume events they
 * find relevant without needing to be tightly coupled to other components.  This affords great
 * flexibility within Shiro by promoting loose coupling and high cohesion between components and a much safer
 * pluggable architecture that is more resilient to change over time.
 * <h2>Sending Events</h2>
 * If a component wishes to publish events to other components:
 * <pre>
 *     MyEvent myEvent = createMyEvent();
 *     eventBus.publish(myEvent);
 * </pre>
 * The event bus will determine the type of event and then dispatch the event to components that wish to receive
 * events of that type.
 * <h2>Receiving Events</h2>
 * A component can receive events of interest by doing the following.
 * <ol>
 * <li>For each type of event you wish to consume, create a public method that accepts a single event argument.
 * The method argument type indicates the type of event to receive.</li>
 * <li>Annotate each of these public methods with the {@link org.apache.shiro.event.Subscribe Subscribe} annotation.</li>
 * <li>Register the component with the event bus:
 * <pre>
 *         eventBus.register(myComponent);
 *     </pre>
 * </li>
 * </ol>
 * After registering the component, when when an event of a respective type is published, the component's
 * {@code Subscribe}-annotated method(s) will be invoked as expected.
 *
 * This design (and its constituent helper components) was largely influenced by
 * Guava's <a href="http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/eventbus/EventBus.html">EventBus</a>
 * concept, although no code was shared/imported (even though Guava is Apache 2.0 licensed and could have
 * been used).
 *
 * This implementation is thread-safe and may be used concurrently.
 *
 * @since 1.3
 */
public class DefaultEventBus implements EventBus {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventBus.class);

    private static final String EVENT_LISTENER_ERROR_MSG = "Event listener processing failed.  Listeners should " +
            "generally handle exceptions directly and not propagate to the event bus.";

    //这是无状态的，我们可以保留静态的最终引用
    //this is stateless, we can retain a static final reference:
    private static final EventListenerComparator EVENT_LISTENER_COMPARATOR = new EventListenerComparator();

    private EventListenerResolver eventListenerResolver;

    //We want to preserve registration order to deliver events to objects in the order that they are registered
    //with the event bus.  This has the nice effect that any Shiro system-level components that are registered first
    //(likely to happen upon startup) have precedence over those registered by end-user components later.
    //
    //One might think that this could have been done by just using a ConcurrentSkipListMap (which is available only on
    //JDK 6 or later).  However, this approach requires the implementation of a Comparator to sort elements, and this
    //surfaces a problem: for any given random event listener, there isn't any guaranteed property to exist that can be
    //inspected to determine order of registration, since registration order is an artifact of this EventBus
    //implementation, not the listeners themselves.
    //
    //Therefore, we use a simple concurrent lock to wrap a LinkedHashMap - the LinkedHashMap retains insertion order
    //and the lock provides thread-safety in probably a much simpler mechanism than attempting to write a
    //EventBus-specific Comparator.  This technique is also likely to be faster than a ConcurrentSkipListMap, which
    //is about 3-5 times slower than a standard ConcurrentMap.
    // 事件对应 监听者组
    private final Map<Object, Subscription> registry;

    private final Lock registryReadLock;
    private final Lock registryWriteLock;

    public DefaultEventBus() {
        this.registry = new LinkedHashMap<Object, Subscription>(); //not thread safe, so we need locks:
        //读写锁 读多写少场景
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        this.registryReadLock = rwl.readLock();
        this.registryWriteLock = rwl.writeLock();

        // 注解事件 监听 解析器
        this.eventListenerResolver = new AnnotationEventListenerResolver();
    }

    public EventListenerResolver getEventListenerResolver() {
        return eventListenerResolver;
    }

    public void setEventListenerResolver(EventListenerResolver eventListenerResolver) {
        this.eventListenerResolver = eventListenerResolver;
    }

    public void publish(Object event) {
        if (event == null) {
            log.info("Received null event for publishing.  Ignoring and returning.");
            return;
        }

        //读共享  理论上 订阅者是不会消失的！
        // 但如果我们要动态的添加某一个 订阅者和动态的取消某个订阅者呢？对吧
        registryReadLock.lock();
        try {
            //performing the entire iteration within the lock will be a slow operation if the registry has a lot of
            //contention.  However, it is expected that the very large majority of cases the registry will be
            //read-mostly with very little writes (registrations or removals) occurring during a typical application
            //lifetime.
            //
            //The alternative would be to copy the registry.values() collection to a new LinkedHashSet within the lock
            //only and the iteration on this new collection could be outside the lock.  This has the performance penalty
            //however of always creating a new collection every time an event is published,  which could be more
            //costly for the majority of applications, especially if the number of listeners is large.
            //
            //Finally, the read lock is re-entrant, so multiple publish calls will be
            //concurrent without penalty since publishing is a read-only operation on the registry.

            for (Subscription subscription : this.registry.values()) {
                subscription.onEvent(event);
            }
        } finally {
            registryReadLock.unlock();
        }
    }

    /**
     * 注册 实例对象
     * @param instance
     */
    public void register(Object instance) {
        if (instance == null) {
            log.info("Received null instance for event listener registration.  Ignoring registration request.");
            return;
        }

        //先卸载 移除这个实例对应的  订阅者
        unregister(instance);

        // 获取这个实例对象上的 目标对象与注解指定的执行方法 封装的对象 列表
        List<EventListener> listeners = getEventListenerResolver().getEventListeners(instance);

        if (listeners == null || listeners.isEmpty()) {
            //不希望是个没意义的执行
            log.warn("Unable to resolve event listeners for subscriber instance [{}]. Ignoring registration request.",
                    instance);
            return;
        }

        // 封装成一个 订阅者！
        Subscription subscription = new Subscription( listeners);

        //等待的时候会是最新的数据吗？
        this.registryWriteLock.lock();
        try {
            this.registry.put(instance, subscription);
        } finally {
            this.registryWriteLock.unlock();
        }
    }

    public void unregister(Object instance) {
        if (instance == null) {
            return;
        }
        // 同步写锁
        this.registryWriteLock.lock();
        try {
            this.registry.remove(instance);
        } finally {
            this.registryWriteLock.unlock();
        }
    }

    /**
     * 订阅者
     */
    private static class Subscription {

        private final List<EventListener> listeners;

        /**
         * 传入 事件监听者
         *    { 执行对象 ，目标执行方法 }
         * @param listeners
         */
        public Subscription(List<EventListener> listeners) {
            List<EventListener> toSort = new ArrayList<EventListener>(listeners);
            // 排序
            toSort.sort(EVENT_LISTENER_COMPARATOR);
            this.listeners = toSort;
        }

        /**
         *  订阅者触发事件调用
         * @param event
         */
        public void onEvent(Object event) {

            //当前已执行 ？ 避免事件的重复发送
            Set<Object> delivered = new HashSet<Object>();

            for (EventListener listener : this.listeners) {
                Object target = listener;
                // 类型
                if (listener instanceof SingleArgumentMethodEventListener) {
                    SingleArgumentMethodEventListener singleArgListener = (SingleArgumentMethodEventListener) listener;
                    target = singleArgListener.getTarget();
                }
                // 这里要求一个对象只能有一个监听方法，且参数一致！ 一个对象 不管有多少个注解 都会只执行一个

                // 思考： Spring是如何实现呢？一个方法上多个注解，不同事件类型，订阅发布模型！

                // 很明显这里需要修改 不应该使用 当前实例对象 应该使用 实例+注解类型+入参 判定唯一并且加上方法名
                if (listener.accepts(event) && ! delivered.contains( target)) {
                    try {
                        // 监听者触发事件
                        listener.onEvent(event);
                    } catch (Throwable t) {
                        log.warn(EVENT_LISTENER_ERROR_MSG, t);
                    }
                    delivered.add(target);
                }
            }
        }
    }
}
