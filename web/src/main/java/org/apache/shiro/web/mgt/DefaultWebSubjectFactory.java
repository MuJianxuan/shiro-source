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

import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.subject.WebSubject;

/**
 *
 *   Web 下的 主题工厂
 *     继承 默认的主题工厂
 *
 * A {@code SubjectFactory} implementation that creates {@link WebDelegatingSubject} instances.
 * <p/>
 * {@code WebDelegatingSubject} instances are required if Request/Response objects are to be maintained across
 * threads when using the {@code Subject} {@link Subject#associateWith(java.util.concurrent.Callable) createCallable}
 * and {@link Subject#associateWith(Runnable) createRunnable} methods.
 *
 * @since 1.0
 */
public class DefaultWebSubjectFactory extends DefaultSubjectFactory {

    public DefaultWebSubjectFactory() {
        super();
    }

    public Subject createSubject(SubjectContext context) {
        //SHIRO-646Check if the existing subject is NOT a WebSubject. If it isn't, then call super.createSubject instead.
        //
        //Creating a WebSubject from a non-web Subject will cause the ServletRequest and ServletResponse to be null, which wil fail when creating a session.

        // 检查现有主题是否不是WebSubject。如果不是，则调用super.createSubject。
        boolean isNotBasedOnWebSubject = context.getSubject() != null && !(context.getSubject() instanceof WebSubject);

        if (!(context instanceof WebSubjectContext) || isNotBasedOnWebSubject) {

            // 调用默认的 构建
            return super.createSubject(context);
        }

        WebSubjectContext wsc = (WebSubjectContext) context;

        SecurityManager securityManager = wsc.resolveSecurityManager();

        Session session = wsc.resolveSession();

        boolean sessionEnabled = wsc.isSessionCreationEnabled();

        PrincipalCollection principals = wsc.resolvePrincipals();
        boolean authenticated = wsc.resolveAuthenticated();
        String host = wsc.resolveHost();

        // 获取请求
        ServletRequest request = wsc.resolveServletRequest();
        // 获取响应
        ServletResponse response = wsc.resolveServletResponse();

        /**
         * 对应   WebDelegatingSubject
         */
        return new WebDelegatingSubject(principals, authenticated, host, session, sessionEnabled,
                request, response, securityManager);
    }

    /**
     * @deprecated since 1.2 - override {@link #createSubject(org.apache.shiro.subject.SubjectContext)} directly if you
     *             need to instantiate a custom {@link Subject} class.
     */
    @Deprecated
    protected Subject newSubjectInstance(PrincipalCollection principals, boolean authenticated,
                                         String host, Session session,
                                         ServletRequest request, ServletResponse response,
                                         SecurityManager securityManager) {
        return new WebDelegatingSubject(principals, authenticated, host, session, true,
                request, response, securityManager);
    }
}
