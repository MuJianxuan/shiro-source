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
package org.apache.shiro.web.mgt;

import org.apache.shiro.mgt.SecurityManager;

/**
 * This interface represents a {@link SecurityManager} implementation that can used in web-enabled applications.
 *
 * @since 1.0
 */
public interface WebSecurityManager extends SecurityManager {

    /**
     * 从请求到请求都需要保留安全信息，因此 Shiro 为此使用了会话。
     * 通常，安全管理器将使用 servlet 容器的 HTTP 会话，但也可以使用自定义会话实现，例如基于 EhCache。
     * 此方法指示安全管理器是否正在使用 HTTP 会话。
     *
     * Security information needs to be retained from request to request, so Shiro makes use of a
     * session for this. Typically, a security manager will use the servlet container's HTTP session
     * but custom session implementations, for example based on EhCache, may also be used. This
     * method indicates whether the security manager is using the HTTP session or not.
     *
     * @return <code>true</code> if the security manager is using the HTTP session; otherwise,
     *         <code>false</code>.
     */
    boolean isHttpSessionMode();
}
