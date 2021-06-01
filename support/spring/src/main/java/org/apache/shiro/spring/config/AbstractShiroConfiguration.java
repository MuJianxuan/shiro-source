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
package org.apache.shiro.spring.config;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 *
 *  可以默认实例的类对象
 *
 * @since 1.4.0
 */
public class AbstractShiroConfiguration {

    @Autowired(required = false)
    protected CacheManager cacheManager;

    @Autowired(required = false)
    protected RolePermissionResolver rolePermissionResolver;

    @Autowired(required = false)
    protected PermissionResolver permissionResolver;

    @Autowired
    protected EventBus eventBus;

    @Value("#{ @environment['shiro.sessionManager.deleteInvalidSessions'] ?: true }")
    protected boolean sessionManagerDeleteInvalidSessions;

    /**
     * securityManager 需要留意什么时候设置进去 Subject里的
     * @param realms
     * @return
     */
    protected SessionsSecurityManager securityManager(List<Realm> realms) {
        //创建默认安全管理器 和 初始化一些默认的参数
        SessionsSecurityManager securityManager = createSecurityManager();

        // 设置 认证手段
        securityManager.setAuthenticator(authenticator());

        //设置 鉴权手段
        securityManager.setAuthorizer(authorizer());

        securityManager.setRealms(realms);

        securityManager.setSessionManager(sessionManager());

        securityManager.setEventBus(eventBus);

        if (cacheManager != null) {
            securityManager.setCacheManager(cacheManager);
        }

        return securityManager;
    }

    protected SessionManager sessionManager() {
        // 创建了一个默认的
        DefaultSessionManager sessionManager = new DefaultSessionManager();

        sessionManager.setSessionDAO(sessionDAO());

        sessionManager.setSessionFactory(sessionFactory());
        //设置删除无效的会话
        sessionManager.setDeleteInvalidSessions( sessionManagerDeleteInvalidSessions);
        return sessionManager;
    }


    protected SessionsSecurityManager createSecurityManager() {
        // 创建默认的 安全管理器
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        //
        securityManager.setSubjectDAO(subjectDAO());
        //
        securityManager.setSubjectFactory(subjectFactory());

        RememberMeManager rememberMeManager = rememberMeManager();
        if (rememberMeManager != null) {
            // 设置 记住你管理器
            securityManager.setRememberMeManager(rememberMeManager);
        }

        return securityManager;
    }

    protected RememberMeManager rememberMeManager() {
        return null;
    }

    protected SubjectDAO subjectDAO() {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
        return subjectDAO;
    }

    protected SessionStorageEvaluator sessionStorageEvaluator() {
        return new DefaultSessionStorageEvaluator();
    }

    protected SubjectFactory subjectFactory() {
        return new DefaultSubjectFactory();
    }


    protected SessionFactory sessionFactory() {
        return new SimpleSessionFactory();
    }

    protected SessionDAO sessionDAO() {
        return new MemorySessionDAO();
    }

    protected Authorizer authorizer() {

        //初始化 模块化认证器
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();

        if (permissionResolver != null) {
            authorizer.setPermissionResolver(permissionResolver);
        }

        if (rolePermissionResolver != null) {
            authorizer.setRolePermissionResolver(rolePermissionResolver);
        }

        return authorizer;
    }

    protected AuthenticationStrategy authenticationStrategy() {
        //认证 策略
        return new AtLeastOneSuccessfulStrategy();
    }

    protected Authenticator authenticator() {
        // 模块化 认证手段
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy( authenticationStrategy());
        return authenticator;
    }

    protected Realm iniRealmFromLocation(String iniLocation) {
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm( ini );
    }
}
