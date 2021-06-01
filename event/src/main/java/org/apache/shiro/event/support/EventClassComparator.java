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

import java.util.Comparator;

/**
 *
 * 根据两个事件类在类层次结构中的位置比较它们。 层次结构中较高级别的类“大于”（顺序更晚）比层次结构中较低级别的类（更早排序）。
 * 无关层次结构中的类具有相同的顺序优先级。
 *
 * 事件总线实现使用此比较器来确定定义多态侦听器方法时要调用的事件侦听器方法：
 *
 * 如果存在两个事件类A和B，其中A是B的父类（而B是A的子类），并且事件订阅者侦听这两个事件：
 *    @Subscribe
 *    public void onEvent(A a) { ... }
 *
 *    @Subscribe
 *    public void onEvent(B b) { ... }
 *
 * 所述onEvent(B b)的方法将在用户和被调用onEvent(A a)方法将不被调用。 这是为了防止将单个事件多次分派给同一使用者。
 *
 * EventClassComparator用于根据侦听器方法的事件参数类对它们进行优先级排序-处理事件子类的方法的优先级高于超类。
 *
 * Compares two event classes based on their position in a class hierarchy.  Classes higher up in a hierarchy are
 * 'greater than' (ordered later) than classes lower in a hierarchy (ordered earlier).  Classes in unrelated
 * hierarchies have the same order priority.
 * <p/>
 * Event bus implementations use this comparator to determine which event listener method to invoke when polymorphic
 * listener methods are defined:
 * <p/>
 * If two event classes exist A and B, where A is the parent class of B (and B is a subclass of A) and an event
 * subscriber listens to both events:
 * <pre>
 * &#64;Subscribe
 * public void onEvent(A a) { ... }
 *
 * &#64;Subscribe
 * public void onEvent(B b) { ... }
 * </pre>
 *
 * The {@code onEvent(B b)} method will be invoked on the subscriber and the
 * {@code onEvent(A a)} method will <em>not</em> be invoked.  This is to prevent multiple dispatching of a single event
 * to the same consumer.
 * <p/>
 * The EventClassComparator is used to order listener method priority based on their event argument class - methods
 * handling event subclasses have higher precedence than superclasses.
 *
 * @since 1.3
 */
public class EventClassComparator implements Comparator<Class> {

    /**
     * 类排序 ？
     *   比较器 ！
     *
     *  比较 留意返回 0的值
     *
     *
     *  返回0 的问题 ：
     *
     *      Comparator比较器，用作排序处理，当返回为0 时，表示两个对象为同一个对象，此时会覆盖对象！在map中，key一样
     *      对象去重 可以这样实现！
     *
     * @param a
     * @param b
     * @return
     */
    @SuppressWarnings("unchecked")
    public int compare(Class a, Class b) {
        if (a == null) {
            if (b == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (b == null) {
            return 1;
        // 比较的是 hashcode
        } else if (a == b) {
            return 0;
        } else {
            // 如果A是B的超集（父类）
            if ( a.isAssignableFrom(b)) {
                //
                return 1;
            //如果 B是A的超集 （父类 ）
            } else if ( b.isAssignableFrom(a)) {
                return -1;
            } else {

                //
                return 0;
            }
        }
    }
}
