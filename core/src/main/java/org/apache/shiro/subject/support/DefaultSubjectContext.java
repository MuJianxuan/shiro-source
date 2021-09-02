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
package org.apache.shiro.subject.support;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.util.MapContext;
import org.apache.shiro.lang.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Default implementation of the {@link SubjectContext} interface.  Note that the getters and setters are not
 * simple pass-through methods to an underlying attribute;  the getters will employ numerous heuristics to acquire
 * their data attribute as best as possible (for example, if {@link #getPrincipals} is invoked, if the principals aren't
 * in the backing map, it might check to see if there is a subject or session in the map and attempt to acquire the
 * principals from those objects).
 *
 * @since 1.0
 */
public class DefaultSubjectContext extends MapContext implements SubjectContext {

    /**
     * 存了一些数据 而已 ！
     */
    private static final String SECURITY_MANAGER = DefaultSubjectContext.class.getName() + ".SECURITY_MANAGER";

    private static final String SESSION_ID = DefaultSubjectContext.class.getName() + ".SESSION_ID";

    private static final String AUTHENTICATION_TOKEN = DefaultSubjectContext.class.getName() + ".AUTHENTICATION_TOKEN";

    private static final String AUTHENTICATION_INFO = DefaultSubjectContext.class.getName() + ".AUTHENTICATION_INFO";

    private static final String SUBJECT = DefaultSubjectContext.class.getName() + ".SUBJECT";

    private static final String PRINCIPALS = DefaultSubjectContext.class.getName() + ".PRINCIPALS";

    private static final String SESSION = DefaultSubjectContext.class.getName() + ".SESSION";

    /**
     *  认证 Key
     */
    private static final String AUTHENTICATED = DefaultSubjectContext.class.getName() + ".AUTHENTICATED";

    private static final String HOST = DefaultSubjectContext.class.getName() + ".HOST";

    /**
     * 会话创建已启用
     */
    public static final String SESSION_CREATION_ENABLED = DefaultSubjectContext.class.getName() + ".SESSION_CREATION_ENABLED";

    /**
     * The session key that is used to store subject principals.
     */
    public static final String PRINCIPALS_SESSION_KEY = DefaultSubjectContext.class.getName() + "_PRINCIPALS_SESSION_KEY";

    /**
     * 用于存储用户是否通过身份验证的会话密钥
     * The session key that is used to store whether or not the user is authenticated.
     */
    public static final String AUTHENTICATED_SESSION_KEY = DefaultSubjectContext.class.getName() + "_AUTHENTICATED_SESSION_KEY";

    private static final transient Logger log = LoggerFactory.getLogger(DefaultSubjectContext.class);

    public DefaultSubjectContext() {

        // 初始化父类的属性
        super();
    }

    public DefaultSubjectContext(SubjectContext ctx) {
        super(ctx);
    }

    public SecurityManager getSecurityManager() {

        return getTypedValue(SECURITY_MANAGER, SecurityManager.class);
    }

    public void setSecurityManager(SecurityManager securityManager) {
        nullSafePut(SECURITY_MANAGER, securityManager);
    }

    public SecurityManager resolveSecurityManager() {
        SecurityManager securityManager = getSecurityManager();

        //  未获取
        if (securityManager == null) {
            if (log.isDebugEnabled()) {
                log.debug("No SecurityManager available in subject context map.  " +
                        "Falling back to SecurityUtils.getSecurityManager() lookup.");
            }
            try {
                // 获取 安全管理器
                securityManager = SecurityUtils.getSecurityManager();

            } catch (UnavailableSecurityManagerException e) {
                if (log.isDebugEnabled()) {
                    log.debug("No SecurityManager available via SecurityUtils.  Heuristics exhausted.", e);
                }
            }
        }
        return securityManager;
    }

    public Serializable getSessionId() {
        return getTypedValue(SESSION_ID, Serializable.class);
    }

    /**
     * 触发时机
     *    WebDefaultSubjectContext
     * @param sessionId the session id of the session that should be associated with the constructed {@link Subject}
     */
    public void setSessionId(Serializable sessionId) {
        nullSafePut(SESSION_ID, sessionId);
    }

    public Subject getSubject() {
        return getTypedValue(SUBJECT, Subject.class);
    }

    public void setSubject(Subject subject) {
        nullSafePut(SUBJECT, subject);
    }

    public PrincipalCollection getPrincipals() {
        return getTypedValue(PRINCIPALS, PrincipalCollection.class);
    }

    private static boolean isEmpty(PrincipalCollection pc) {
        return pc == null || pc.isEmpty();
    }

    public void setPrincipals(PrincipalCollection principals) {
        if (!isEmpty(principals)) {
            put(PRINCIPALS, principals);
        }
    }

    /**
     * 标识信息  登录的账号ID、唯一标识等
     *
     * 三重保证 一定获取到当前 Session中的 用户身份
     * 最终是在 session 中获取
     *
     * @return
     */
    public PrincipalCollection resolvePrincipals() {
        PrincipalCollection principals = getPrincipals(); // null

        if (isEmpty(principals)) {
            //check to see if they were just authenticated:
            AuthenticationInfo info = getAuthenticationInfo(); // null
            if (info != null) {
                // 用户ID
                principals = info.getPrincipals();
            }
        }

        if (isEmpty(principals)) {
            Subject subject = getSubject(); // null
            if (subject != null) {
                // 从 subject 中
                principals = subject.getPrincipals();
            }
        }

        //
        if (isEmpty(principals)) {
            //try the session:
            Session session = resolveSession(); //null
            if (session != null) {
                // 这个值  所以 最终登录的用户信息都是在这里获取的
                principals = (PrincipalCollection) session.getAttribute(PRINCIPALS_SESSION_KEY);
            }
        }

        return principals;
    }


    public Session getSession() {
        return getTypedValue(SESSION, Session.class);
    }

    public void setSession(Session session) {
        nullSafePut(SESSION, session);
    }

    public Session resolveSession() {
        Session session = getSession();  // 默认是 null
        if (session == null) {
            //try the Subject if it exists:
            Subject existingSubject = getSubject(); // 默认为 null
            if (existingSubject != null) {
                session = existingSubject.getSession(false); // DefaultSession  终究 是 null
            }
        }
        return session;  //当 existingSubject 为 null 时 返回 null
    }

    public boolean isSessionCreationEnabled() {
        Boolean val = getTypedValue(SESSION_CREATION_ENABLED, Boolean.class);
        return val == null || val;
    }

    public void setSessionCreationEnabled(boolean enabled) {
        nullSafePut(SESSION_CREATION_ENABLED, enabled);
    }

    public boolean isAuthenticated() {
        Boolean authc = getTypedValue(AUTHENTICATED, Boolean.class);
        return authc != null && authc;
    }

    public void setAuthenticated(boolean authc) {
        put(AUTHENTICATED, authc);
    }

    public boolean resolveAuthenticated() {
        Boolean authc = getTypedValue(AUTHENTICATED, Boolean.class);  // 身份认证
        if (authc == null) {
            //see if there is an AuthenticationInfo object.  If so, the very presence of one indicates a successful
            //authentication attempt:
            // 获取认证信息
            AuthenticationInfo info = getAuthenticationInfo();
            authc = info != null;
        }
        // 认证失败
        if (! authc) {
            //fall back to a session check:
            Session session = resolveSession(); // null
            // session 不为空
            if (session != null) {
                // 设置 认证信息 到session 中 ，前提是 session 不为null
                Boolean sessionAuthc = (Boolean) session.getAttribute(AUTHENTICATED_SESSION_KEY);
                authc = sessionAuthc != null && sessionAuthc;
            }
        }

        return authc;
    }

    public AuthenticationInfo getAuthenticationInfo() {
        return getTypedValue(AUTHENTICATION_INFO, AuthenticationInfo.class);
    }

    public void setAuthenticationInfo(AuthenticationInfo info) {
        nullSafePut(AUTHENTICATION_INFO, info);
    }

    /**
     * AuthenticationToken 是我们交给框架认证的信息
     *    而 AuthenticationInfo 是认证的数据 信息 是交给领域返回的认证信息
     * @return
     */
    public AuthenticationToken getAuthenticationToken() {
        return getTypedValue(AUTHENTICATION_TOKEN, AuthenticationToken.class);
    }

    public void setAuthenticationToken(AuthenticationToken token) {
        nullSafePut(AUTHENTICATION_TOKEN, token);
    }

    public String getHost() {
        return getTypedValue(HOST, String.class);
    }

    public void setHost(String host) {
        if (StringUtils.hasText(host)) {
            put(HOST, host);
        }
    }

    public String resolveHost() {
        String host = getHost();

        if (host == null) {
            //check to see if there is an AuthenticationToken from which to retrieve it:
            AuthenticationToken token = getAuthenticationToken();
            if (token instanceof HostAuthenticationToken) {
                host = ((HostAuthenticationToken) token).getHost();
            }
        }

        if (host == null) {
            Session session = resolveSession();
            if (session != null) {
                host = session.getHost();
            }
        }

        return host;
    }
}
