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

/**
 *
 *   这个是基于方法对象的事件监听器  所以，事件对象执行实际是 封装了内部方法，实际调用时方法调用
 *
 * 事件侦听器知道如何接受和处理特定类型的事件。
 *
 * 请注意，此接口在事件实现支持包中（而不是直接在事件包中），因为它是事件总线实现的支持概念，而不是大多数使用Shiro的应用程序开发人员应直接实现的接口。
 * 应用程序开发人员应该在希望接收事件的方法上使用“ Subscribe注释。
 *
 * 因此，该接口主要表示事件总线与实际订阅组件之间的“中间人”。 这样，事件总线实现者（或框架/基础结构实现者）或希望自定义侦听器/调度功能的实现者可能会发现此概念很有用。
 *
 * An event listener knows how to accept and process events of a particular type (or types).
 * <p/>
 * Note that this interface is in the event implementation support package (and not the event package directly)
 * because it is a supporting concept for event bus implementations and not something that most application
 * developers using Shiro should implement directly.  App developers should instead use the
 * {@link org.apache.shiro.event.Subscribe Subscribe} annotation on methods they wish to receive events.
 * <p/>
 * This interface therefore mainly represents a 'middle man' between the event bus and the actual subscribing
 * component.  As such, event bus implementors (or framework/infrastructural implementors) or those that wish to
 * customize listener/dispatch functionality might find this concept useful.
 * <p/>
 * It is a concept almost always used in conjunction with a {@link EventListenerResolver} implementation.
 *
 * @see SingleArgumentMethodEventListener
 * @see AnnotationEventListenerResolver
 * 事件监听器
 * @since 1.3
 */
public interface EventListener {

    /**
     *
     *  是否支持该事件
     *
     * 如果侦听器实例可以处理指定的事件对象，则返回true否则返回false
     *
     * Returns {@code true} if the listener instance can process the specified event object, {@code false} otherwise.
     * @param event the event object to test
     * @return {@code true} if the listener instance can process the specified event object, {@code false} otherwise.
     */
    boolean accepts(Object event);

    /**
     *  执行
     *
     * 处理指定的事件。 同样，由于此接口是一个实现概念，因此此方法的实现可能会将事件分派到“实际”处理器（例如，方法）
     * Handles the specified event.  Again, as this interface is an implementation concept, implementations of this
     * method will likely dispatch the event to a 'real' processor (e.g. method).
     *
     * @param event the event to handle.
     */
    void onEvent(Object event);
}
