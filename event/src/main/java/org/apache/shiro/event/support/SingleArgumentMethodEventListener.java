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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 一个事件侦听器，该侦听器调用接受单个事件参数的目标对象的方法
 *
 * 单一参数方法事件侦听器
 *
 * A event listener that invokes a target object's method that accepts a single event argument.
 *
 * @since 1.3
 */
public class SingleArgumentMethodEventListener implements TypedEventListener {

    // 方法挂在的对象？
    private final Object target;

    // 方法对象
    private final Method method;

    public SingleArgumentMethodEventListener(Object target, Method method) {
        // 执行对象
        this.target = target;
        // 执行方法
        this.method = method;

        //断言该方法已按预期定义：
        //assert that the method is defined as expected:
        getMethodArgumentType(method);

        assertPublicMethod(method);
    }

    public Object getTarget() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }

    private void assertPublicMethod(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Event handler method [" + method + "] must be public.");
        }
    }

    public boolean accepts(Object event) {
        return event != null && getEventType().isInstance(event);
    }

    public Class getEventType() {
        return getMethodArgumentType(getMethod());
    }

    /**
     * 调用？
     *  i have idea : 是否可以将反射的信息存在内存中， 然后用以调用
     *
     *    秀啊 ，这样就可以触发事件 ，将执行封装？  那把接口封装怎么样
     * @param event the event to handle.
     */
    public void onEvent(Object event) {

        Method method = getMethod();
        try {
            //反射调用 ， 参数 时 event对象
            method.invoke( getTarget(), event);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to invoke event handler method [" + method + "].", e);
        }
    }

    /**、
     * Returns an array of Class objects that represent the formal parameter types, in declaration order,
     * of the executable represented by this object. Returns an array of length 0 if the underlying executable takes no parameters.
     *  参数？
     * @param method
     * @return
     */
    protected Class getMethodArgumentType(Method method) {
        //返回一个Class对象数组，这些对象按声明顺序表示此对象表示的可执行文件的形式参数类型。如果基础可执行文件不接受任何参数，则返回长度为0的数组。
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            //the default implementation expects a single typed argument and nothing more:
            String msg = "Event handler methods must accept a single argument.";
            throw new IllegalArgumentException(msg);
        }
        return paramTypes[0];
    }
}
