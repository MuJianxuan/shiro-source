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
package org.apache.shiro.env;

import org.apache.shiro.mgt.SecurityManager;

/**
 *
 * Environment实例封装了Shiro起作用所需的所有对象。 从本质上讲，它是一个“元”对象，可以从中获取所有Shiro组件用于应用程序。
 *
 * 通常，由于解析Shiro配置文件而创建了一个Environment实例。 可以将环境实例存储在应用程序认为必要的任何位置，并可以从中检索实现安全行为所需的Shiro的任何组件。
 *
 * An {@code Environment} instance encapsulates all of the objects that Shiro requires to function.  It is essentially
 * a 'meta' object from which all Shiro components can be obtained for an application.
 * <p/>
 * An {@code Environment} instance is usually created as a result of parsing a Shiro configuration file.  The
 * environment instance can be stored in any place the application deems necessary, and from it, can retrieve any
 * of Shiro's components that might be necessary in implementing security behavior.
 * <p/>
 * For example, the most obvious component accessible via an {@code Environment} instance is the application's
 * {@link #getSecurityManager() securityManager}.
 *
 * @since 1.2
 */
public interface Environment {

    /**
     * Returns the application's {@code SecurityManager} instance.
     *
     * @return the application's {@code SecurityManager} instance.
     */
    SecurityManager getSecurityManager();
}
