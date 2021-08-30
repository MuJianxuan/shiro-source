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
package org.apache.shiro.aop;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;


/**
 * 此类是Shiro特有的AOP方法拦截器行为的抽象，它使AOP实现的具体内容由子类实现处理
 *
 *  方法拦截支持  切面支持
 *
 * This class is an abstraction of AOP method interceptor behavior specific to Shiro that
 * leaves AOP implementation specifics to be handled by subclass implementations.  This implementation primarily
 * enables a <tt>Log</tt> and makes available the currently executing {@link Subject Subject}.
 *
 * @since 0.2
 */
public abstract class MethodInterceptorSupport implements MethodInterceptor {

    /**
     * Default no-argument constructor for subclasses.
     */
    public MethodInterceptorSupport() {
    }

    /**
     * 返回与当前正在执行的代码关联的Subject 。
     * 此默认实现仅调用SecurityUtils.getSubject() 。
     *
     * Returns the {@link Subject Subject} associated with the currently-executing code.
     * <p/>
     * This default implementation merely calls <code>{@link org.apache.shiro.SecurityUtils#getSubject SecurityUtils.getSubject()}</code>.
     *
     * @return the {@link org.apache.shiro.subject.Subject Subject} associated with the currently-executing code.
     */
    protected Subject getSubject() {
        return SecurityUtils.getSubject();
    }
}
