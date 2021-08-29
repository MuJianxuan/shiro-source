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
package org.apache.shiro.mgt;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DelegatingSubject;


/**
 *
 *   默认的
 *
 * Default {@link SubjectFactory SubjectFactory} implementation that creates {@link org.apache.shiro.subject.support.DelegatingSubject DelegatingSubject}
 * instances.
 *
 * @since 1.0
 */
public class DefaultSubjectFactory implements SubjectFactory {

    /**
     * 默认使用！
     */
    public DefaultSubjectFactory() {
    }

    /**
     *  默认的创建 Subject
     *    创建时机：  账户认证成功  则需要创建  AuthenticationInfo 当前账户的
     *
     *    实现要使用的上下文数据来构造适当的 {@code Subject} 实例。
     * @param context the contextual data to be used by the implementation to construct an appropriate {@code Subject}
     *                instance.
     * @return
     */
    public Subject createSubject(SubjectContext context) {
        // 获取 安全管理器
        SecurityManager securityManager = context.resolveSecurityManager();

        // session 对象
        // session 和 Subject 什么时候挂靠的？ 在上下文 处理的时候挂靠的  主要是有个sessionKey
        Session session = context.resolveSession();
        //是否启用会话创建
        boolean sessionCreationEnabled = context.isSessionCreationEnabled();

        // 认证信息转换  AuthenticationInfo
        PrincipalCollection principals = context.resolvePrincipals();

        // 认证状态  认证成功 or 失败  默认认证成功 即已设置认证成功
        boolean authenticated = context.resolveAuthenticated();

        // host
        String host = context.resolveHost();

        //   创建一个 委托主题
        return new DelegatingSubject( principals, authenticated, host, session, sessionCreationEnabled, securityManager);
    }

    /**
     * @deprecated since 1.2 - override {@link #createSubject(org.apache.shiro.subject.SubjectContext)} directly if you
     *             need to instantiate a custom {@link Subject} class.
     */
    @Deprecated
    protected Subject newSubjectInstance(PrincipalCollection principals, boolean authenticated, String host,
                                         Session session, SecurityManager securityManager) {
        return new DelegatingSubject( principals, authenticated, host, session, true, securityManager);
    }

}
