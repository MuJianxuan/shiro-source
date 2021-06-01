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

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.session.mgt.NativeSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.util.WebUtils;

/**
 * A web-specific {@code SessionStorageEvaluator} that performs the same logic as the parent class
 * {@link DefaultSessionStorageEvaluator} but additionally checks for a request-specific flag that may enable or
 * disable session access.
 * <p/>
 * This implementation usually works in conjunction with the
 * {@link org.apache.shiro.web.filter.session.NoSessionCreationFilter}:  If the {@code NoSessionCreationFilter}
 * is configured in a filter chain, that filter will set a specific
 * {@code ServletRequest} {@link javax.servlet.ServletRequest#setAttribute attribute} indicating that session creation
 * should be disabled.
 * <p/>
 * This {@code DefaultWebSessionStorageEvaluator} will then inspect this attribute, and if it has been set, will return
 * {@code false} from {@link #isSessionStorageEnabled(org.apache.shiro.subject.Subject)} method, thereby preventing
 * Shiro from creating a session for the purpose of storing subject state.
 * <p/>
 * If the request attribute has
 * not been set (i.e. the {@code NoSessionCreationFilter} is not configured or has been disabled), this class does
 * nothing and delegates to the parent class for existing behavior.
 *
 * @since 1.2
 */
public class DefaultWebSessionStorageEvaluator extends DefaultSessionStorageEvaluator {

    //since 1.2.1
    private SessionManager sessionManager;

    /**
     * Sets the session manager to use when checking to see if session storage is possible.
     * @param sessionManager the session manager instance for checking.
     * @since 1.2.1
     */
    //package protected on purpose to maintain point-version compatibility: (1.2.3 -> 1.2.1 should work always).
    void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     *
     * 返回true如果会话存储一般可（由超类的全局配置属性决定isSessionStorageEnabled()并没有具体的要求，覆盖已关闭会话存储， false否则。
     *
     * 这意味着，如果isSessionStorageEnabled()属性为false或发现请求属性为当前请求关闭了会话存储，则会话存储将被禁用。
     *
     * Returns {@code true} if session storage is generally available (as determined by the super class's global
     * configuration property {@link #isSessionStorageEnabled()} and no request-specific override has turned off
     * session storage, {@code false} otherwise.
     * <p/>
     * This means session storage is disabled if the {@link #isSessionStorageEnabled()} property is {@code false} or if
     * a request attribute is discovered that turns off session storage for the current request.
     *
     * @param subject the {@code Subject} for which session state persistence may be enabled
     * @return {@code true} if session storage is generally available (as determined by the super class's global
     *         configuration property {@link #isSessionStorageEnabled()} and no request-specific override has turned off
     *         session storage, {@code false} otherwise.
     */
    @SuppressWarnings({"SimplifiableIfStatement"})
    @Override
    public boolean isSessionStorageEnabled(Subject subject) {
        if (subject.getSession(false) != null) {
            //use what already exists
            return true;
        }

        if (!isSessionStorageEnabled()) {
            //honor global setting:
            return false;
        }

        //非Web主题实例不能保存到仅Web的会话管理器中
        //SHIRO-350: non-web subject instances can't be saved to web-only session managers:
        //since 1.2.1:
        if (!(subject instanceof WebSubject) && (this.sessionManager != null && !(this.sessionManager instanceof NativeSessionManager))) {
            return false;
        }

        return WebUtils._isSessionCreationEnabled(subject);
    }


}