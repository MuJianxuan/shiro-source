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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * Shiro框架对SecurityManager接口的默认具体实现，基于Realm的集合。
 * 此实现通过超类实现将其身份验证，授权和会话操作分别委派给包装的Authenticator ， Authorizer和SessionManager实例
 *
 * The Shiro framework's default concrete implementation of the {@link SecurityManager} interface,
 * based around a collection of {@link org.apache.shiro.realm.Realm}s.  This implementation delegates its
 * authentication, authorization, and session operations to wrapped {@link Authenticator}, {@link Authorizer}, and
 * {@link org.apache.shiro.session.mgt.SessionManager SessionManager} instances respectively via superclass
 * implementation.
 * <p/>
 * To greatly reduce and simplify configuration, this implementation (and its superclasses) will
 * create suitable defaults for all of its required dependencies, <em>except</em> the required one or more
 * {@link Realm Realm}s.  Because {@code Realm} implementations usually interact with an application's data model,
 * they are almost always application specific;  you will want to specify at least one custom
 * {@code Realm} implementation that 'knows' about your application's data/security model
 * (via {@link #setRealm} or one of the overloaded constructors).  All other attributes in this class hierarchy
 * will have suitable defaults for most enterprise applications.
 * <p/>
 * <b>RememberMe notice</b>: This class supports the ability to configure a
 * {@link #setRememberMeManager RememberMeManager}
 * for {@code RememberMe} identity services for login/logout, BUT, a default instance <em>will not</em> be created
 * for this attribute at startup.
 * <p/>
 * Because RememberMe services are inherently client tier-specific and
 * therefore aplication-dependent, if you want {@code RememberMe} services enabled, you will have to specify an
 * instance yourself via the {@link #setRememberMeManager(RememberMeManager) setRememberMeManager}
 * mutator.  However if you're reading this JavaDoc with the
 * expectation of operating in a Web environment, take a look at the
 * {@code org.apache.shiro.web.DefaultWebSecurityManager} implementation, which
 * <em>does</em> support {@code RememberMe} services by default at startup.
 *
 * @since 0.2
 */
public class DefaultSecurityManager extends SessionsSecurityManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultSecurityManager.class);

    protected RememberMeManager rememberMeManager;
    protected SubjectDAO subjectDAO;
    protected SubjectFactory subjectFactory;

    /**
     * Default no-arg constructor.
     */
    public DefaultSecurityManager() {
        super();

        // 创建 默认 主题工厂
        this.subjectFactory = new DefaultSubjectFactory();

        //创建 默认 的主题 操作 接口
        this.subjectDAO = new DefaultSubjectDAO();
    }

    /**
     * Supporting constructor for a single-realm application.
     *
     * @param singleRealm the single realm used by this SecurityManager.
     */
    public DefaultSecurityManager(Realm singleRealm) {
        this();
        setRealm(singleRealm);
    }

    /**
     * Supporting constructor for multiple {@link #setRealms realms}.
     *
     * @param realms the realm instances backing this SecurityManager.
     */
    public DefaultSecurityManager(Collection<Realm> realms) {
        this();
        setRealms(realms);
    }

    /**
     * Returns the {@code SubjectFactory} responsible for creating {@link Subject} instances exposed to the application.
     *
     * @return the {@code SubjectFactory} responsible for creating {@link Subject} instances exposed to the application.
     */
    public SubjectFactory getSubjectFactory() {
        return subjectFactory;
    }

    /**
     * Sets the {@code SubjectFactory} responsible for creating {@link Subject} instances exposed to the application.
     *
     * @param subjectFactory the {@code SubjectFactory} responsible for creating {@link Subject} instances exposed to the application.
     */
    public void setSubjectFactory(SubjectFactory subjectFactory) {
        this.subjectFactory = subjectFactory;
    }

    /**
     * Returns the {@code SubjectDAO} responsible for persisting Subject state, typically used after login or when an
     * Subject identity is discovered (eg after RememberMe services).  Unless configured otherwise, the default
     * implementation is a {@link DefaultSubjectDAO}.
     *
     * @return the {@code SubjectDAO} responsible for persisting Subject state, typically used after login or when an
     *         Subject identity is discovered (eg after RememberMe services).
     * @see DefaultSubjectDAO
     * @since 1.2
     */
    public SubjectDAO getSubjectDAO() {
        return subjectDAO;
    }

    /**
     * Sets the {@code SubjectDAO} responsible for persisting Subject state, typically used after login or when an
     * Subject identity is discovered (eg after RememberMe services). Unless configured otherwise, the default
     * implementation is a {@link DefaultSubjectDAO}.
     *
     * @param subjectDAO the {@code SubjectDAO} responsible for persisting Subject state, typically used after login or when an
     *                   Subject identity is discovered (eg after RememberMe services).
     * @see DefaultSubjectDAO
     * @since 1.2
     */
    public void setSubjectDAO(SubjectDAO subjectDAO) {
        this.subjectDAO = subjectDAO;
    }

    public RememberMeManager getRememberMeManager() {
        return rememberMeManager;
    }

    public void setRememberMeManager(RememberMeManager rememberMeManager) {
        this.rememberMeManager = rememberMeManager;
    }

    protected SubjectContext createSubjectContext() {
        return new DefaultSubjectContext();
    }

    /**
     * Creates a {@code Subject} instance for the user represented by the given method arguments.
     *
     * @param token    the {@code AuthenticationToken} submitted for the successful authentication.  用户认证传入的Token数据
     * @param info     the {@code AuthenticationInfo} of a newly authenticated user.
     * @param existing the existing {@code Subject} instance that initiated the authentication attempt  现存的
     * @return the {@code Subject} instance that represents the context and session data for the newly
     *         authenticated subject.
     */
    protected Subject createSubject(AuthenticationToken token, AuthenticationInfo info, Subject existing) {
        // 创建容器
        // 创建 Subject 的上下文
        SubjectContext context = createSubjectContext(); // 空的集合
        // 设置认证成功
        context.setAuthenticated(  true);
        // 传入需要认证的信息
        context.setAuthenticationToken(  token);
        // Realm 领域 返回的认证信息
        context.setAuthenticationInfo(info);
        // 设置 安全管理器
        context.setSecurityManager(this);
        //  存在？
        if (existing != null) {

            /**
             * 怎么理解？ 我们的 用户登录是调用
             */
            // 设置 当前需要处理 Subject  上下文存入
            context.setSubject(existing);
        }

        // 核心创建
        return createSubject(context);
    }

    /**
     * Binds a {@code Subject} instance created after authentication to the application for later use.
     * <p/>
     * As of Shiro 1.2, this method has been deprecated in favor of {@link #save(org.apache.shiro.subject.Subject)},
     * which this implementation now calls.
     *
     * @param subject the {@code Subject} instance created after authentication to be bound to the application
     *                for later use.
     * @see #save(org.apache.shiro.subject.Subject)
     * @deprecated in favor of {@link #save(org.apache.shiro.subject.Subject) save(subject)}.
     */
    @Deprecated
    protected void bind(Subject subject) {
        save(subject);
    }

    /**
     * 记住我成功登录   实现逻辑
     * @param token  认证令牌
     * @param info 认证信息  token验证通过 会转化成 认证信息 （表示信息已被认证 ）
     * @param subject  主题 关联信息
     */
    protected void rememberMeSuccessfulLogin(AuthenticationToken token, AuthenticationInfo info, Subject subject) {
        // 获取 记住我功能的 管理器
        // 接口更多的是表达一种功能 ，而功能的实现对象则需要 管理器去维护
        RememberMeManager rmm = getRememberMeManager();
        if (rmm != null) {
            try {
                rmm.onSuccessfulLogin(subject, token, info);

            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    String msg = "Delegate RememberMeManager instance of type [" + rmm.getClass().getName() +
                            "] threw an exception during onSuccessfulLogin.  RememberMe services will not be " +
                            "performed for account [" + info + "].";
                    log.warn(msg, e);
                }
            }
        }
        // 不重要
        else {
            if (log.isTraceEnabled()) {
                log.trace("This " + getClass().getName() + " instance does not have a " +
                        "[" + RememberMeManager.class.getName() + "] instance configured.  RememberMe services " +
                        "will not be performed for account [" + info + "].");
            }
        }
    }

    protected void rememberMeFailedLogin(AuthenticationToken token, AuthenticationException ex, Subject subject) {
        RememberMeManager rmm = getRememberMeManager();
        if (rmm != null) {
            try {
                rmm.onFailedLogin(subject, token, ex);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    String msg = "Delegate RememberMeManager instance of type [" + rmm.getClass().getName() +
                            "] threw an exception during onFailedLogin for AuthenticationToken [" +
                            token + "].";
                    log.warn(msg, e);
                }
            }
        }
    }

    protected void rememberMeLogout(Subject subject) {
        RememberMeManager rmm = getRememberMeManager();
        if (rmm != null) {
            try {
                rmm.onLogout(subject);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    String msg = "Delegate RememberMeManager instance of type [" + rmm.getClass().getName() +
                            "] threw an exception during onLogout for subject with principals [" +
                            (subject != null ? subject.getPrincipals() : null) + "]";
                    log.warn(msg, e);
                }
            }
        }
    }

    /**
     * 留意这里返回的是一个 已验证帐户身份
     *
     * 首先验证AuthenticationToken参数，如果成功，则构造一个表示已验证帐户身份的Subject实例。
     *
     * 一旦构造完成， Subject实例就会bound到应用程序以供后续访问，然后返回给调用者。
     *
     * First authenticates the {@code AuthenticationToken} argument, and if successful, constructs a
     * {@code Subject} instance representing the authenticated account's identity.
     * <p/>
     * Once constructed, the {@code Subject} instance is then {@link #bind bound} to the application for
     * subsequent access before being returned to the caller.
     *
     * @param token the authenticationToken to process for the login attempt.
     * @return a Subject representing the authenticated user.
     * @throws AuthenticationException if there is a problem authenticating the specified {@code token}.
     */
    public Subject login(Subject subject, AuthenticationToken token) throws AuthenticationException {
        AuthenticationInfo info;
        try {
            // 先认证 token
            info = authenticate(token);
        }
        // 这些都是不重要的
        catch (AuthenticationException ae) {
            try {
                // 失败！
                onFailedLogin(token, ae, subject);
            } catch (Exception e) {
                if (log.isInfoEnabled()) {
                    log.info("onFailedLogin method threw an " +
                            "exception.  Logging and propagating original AuthenticationException.", e);
                }
            }
            throw ae; //propagate
        }

        // 创建当前主题信息的时候，会尝试获取当前 请求中的cookie是否存在 remember me记录的值
        // 如果存在 则会返回一个  存有  已验证的账户身份
        // 8-26 对当前未认证的subject 绑定认证关系
        // 需要弄明白的是  subject 为什么要对   生成登录的 Subject
        Subject loggedIn = createSubject(token, info, subject);

        // 成功登录
        onSuccessfulLogin(token, info, loggedIn);

        return loggedIn;
    }

    protected void onSuccessfulLogin(AuthenticationToken token, AuthenticationInfo info, Subject subject) {
        // 记住我成功登录
        rememberMeSuccessfulLogin(token, info, subject);
    }

    protected void onFailedLogin(AuthenticationToken token, AuthenticationException ae, Subject subject) {
        rememberMeFailedLogin(token, ae, subject);
    }

    /**
     *
     * @param subject
     */
    protected void beforeLogout(Subject subject) {

        // 记住我信息清除
        rememberMeLogout(subject);
    }

    /**
     * 拷贝一波
     * @param subjectContext
     * @return
     */
    protected SubjectContext copy(SubjectContext subjectContext) {
        /**
         * 默认的主体上下文
         */
        return new DefaultSubjectContext(subjectContext);
    }

    /**
     *
     * 此实现的功能如下：
     *
     * 使用启发式方法获取可能尚未提供的数据（例如，引用的会话或记住的主体），确保SubjectContext尽可能填充。
     * 调用doCreateSubject(SubjectContext)来实际执行Subject实例的创建。
     * 调用save(subject)以确保构造的Subject的状态对于将来的请求/调用是可访问的（如有必要）。
     * 返回构造的Subject实例。
     *
     * This implementation functions as follows:
     * <p/>
     * <ol>
     * <li>Ensures the {@code SubjectContext} is as populated as it can be, using heuristics to acquire
     * data that may not have already been available to it (such as a referenced session or remembered principals).</li>
     * <li>Calls {@link #doCreateSubject(org.apache.shiro.subject.SubjectContext)} to actually perform the
     * {@code Subject} instance creation.</li>
     * <li>calls {@link #save(org.apache.shiro.subject.Subject) save(subject)} to ensure the constructed
     * {@code Subject}'s state is accessible for future requests/invocations if necessary.</li>
     * <li>returns the constructed {@code Subject} instance.</li>
     * </ol>
     *
     * @param subjectContext any data needed to direct how the Subject should be constructed.
     * @return the {@code Subject} instance reflecting the specified contextual data.
     * @see #ensureSecurityManager(org.apache.shiro.subject.SubjectContext)
     * @see #resolveSession(org.apache.shiro.subject.SubjectContext)
     * @see #resolvePrincipals(org.apache.shiro.subject.SubjectContext)
     * @see #doCreateSubject(org.apache.shiro.subject.SubjectContext)
     * @see #save(org.apache.shiro.subject.Subject)
     * @since 1.0
     */
    public Subject createSubject(SubjectContext subjectContext) {
        //create a copy so we don't modify the argument's backing map:   默认会有 org.apache.shiro.subject.support.DefaultSubjectContext.SECURITY_MANAGER
        SubjectContext context = copy(subjectContext);

        //确保上下文有一个 SecurityManager 实例，如果没有，添加一个：
        //ensure that the context has a SecurityManager instance, and if not, add one:
        context = ensureSecurityManager(context);

        // 解析关联的会话（通常基于引用的会话 ID），
        // 并在发送到 SubjectFactory 之前将其放置在上下文中。
        // SubjectFactory 不需要知道如何获取会话，因为该过程通常是特定于环境的
        // - 最好使 SF 免受这些细节的影响：

        //Resolve an associated Session (usually based on a referenced session ID), and place it in the context before
        //sending to the SubjectFactory.  The SubjectFactory should not need to know how to acquire sessions as the
        //process is often environment specific - better to shield the SF from these details:
        context = resolveSession(context);

        //类似地，SubjectFactory 不应该需要任何 RememberMe 的概念——如果可能的话，
        // 在移交给 SubjectFactory 之前先在这里翻译它：
        //Similarly, the SubjectFactory should not require any concept of RememberMe - translate that here first
        //if possible before handing off to the SubjectFactory:
        context = resolvePrincipals(context);

        // 执行创建  根据上下文创建 Subject （是否认证的 Subject） 怎么和这个 Session 关联起来呢？  session 关联 subject ，通过Cookie >>>指向 Session 实现
        Subject subject = doCreateSubject(context);

        //save this subject for future reference if necessary:
        //(this is needed here in case rememberMe principals were resolved and they need to be stored in the
        //session, so we don't constantly rehydrate the rememberMe PrincipalCollection on every operation).
        //Added in 1.2:
        // 存储 Subject
        save(subject);

        return subject;
    }

    /**
     * Actually creates a {@code Subject} instance by delegating to the internal
     * {@link #getSubjectFactory() subjectFactory}.  By the time this method is invoked, all possible
     * {@code SubjectContext} data (session, principals, et. al.) has been made accessible using all known heuristics
     * and will be accessible to the {@code subjectFactory} via the {@code subjectContext.resolve*} methods.
     *
     * @param context the populated context (data map) to be used by the {@code SubjectFactory} when creating a
     *                {@code Subject} instance.
     * @return a {@code Subject} instance reflecting the data in the specified {@code SubjectContext} data map.
     * @see #getSubjectFactory()
     * @see SubjectFactory#createSubject(org.apache.shiro.subject.SubjectContext)
     * @since 1.2
     */
    protected Subject doCreateSubject(SubjectContext context) {
        return getSubjectFactory().createSubject(context);
    }

    /**
     * Saves the subject's state to a persistent location for future reference if necessary.
     * <p/>
     * This implementation merely delegates to the internal {@link #setSubjectDAO(SubjectDAO) subjectDAO} and calls
     * {@link SubjectDAO#save(org.apache.shiro.subject.Subject) subjectDAO.save(subject)}.
     *
     * @param subject the subject for which state will potentially be persisted
     * @see SubjectDAO#save(org.apache.shiro.subject.Subject)
     * @since 1.2
     */
    protected void save(Subject subject) {
        this.subjectDAO.save(subject);
    }

    /**
     * Removes (or 'unbinds') the Subject's state from the application, typically called during {@link #logout}..
     * <p/>
     * This implementation merely delegates to the internal {@link #setSubjectDAO(SubjectDAO) subjectDAO} and calls
     * {@link SubjectDAO#delete(org.apache.shiro.subject.Subject) delete(subject)}.
     *
     * @param subject the subject for which state will be removed
     * @see SubjectDAO#delete(org.apache.shiro.subject.Subject)
     * @since 1.2
     */
    protected void delete(Subject subject) {
        this.subjectDAO.delete(subject);
    }

    /**
     * Determines if there is a {@code SecurityManager} instance in the context, and if not, adds 'this' to the
     * context.  This ensures the SubjectFactory instance will have access to a SecurityManager during Subject
     * construction if necessary.
     *
     * @param context the subject context data that may contain a SecurityManager instance.
     * @return The SubjectContext to use to pass to a {@link SubjectFactory} for subject creation.
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    protected SubjectContext ensureSecurityManager(SubjectContext context) {
        if (context.resolveSecurityManager() != null) {
            log.trace("Context already contains a SecurityManager instance.  Returning.");
            return context;
        }
        log.trace("No SecurityManager found in context.  Adding self reference.");
        context.setSecurityManager(this);
        return context;
    }

    /**
     * Attempts to resolve any associated session based on the context and returns a
     * context that represents this resolved {@code Session} to ensure it may be referenced if necessary by the
     * invoked {@link SubjectFactory} that performs actual {@link Subject} construction.
     * <p/>
     * If there is a {@code Session} already in the context because that is what the caller wants to be used for
     * {@code Subject} construction, or if no session is resolved, this method effectively does nothing
     * returns the context method argument unaltered.
     *
     * @param context the subject context data that may resolve a Session instance.
     * @return The context to use to pass to a {@link SubjectFactory} for subject creation.
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    protected SubjectContext resolveSession(SubjectContext context) {
        // 主要目的时绑定 session
        /**
         * 我们知道在Web中，session的重要性，cookie 与session 进行关联
         *  但这里属于的是 Shiro的概念， Web的体系应该是 在 WebDefaultSecurityManger
         */
        if (context.resolveSession() != null) {
            log.debug("Context already contains a session.  Returning.");
            return context;
        }
        try {
            //Context couldn't resolve it directly, let's see if we can since we have direct access to 
            //the session manager:
            Session session = resolveContextSession(context);
            if (session != null) {
                context.setSession(session);
            }
        } catch (InvalidSessionException e) {
            log.debug("Resolved SubjectContext context session is invalid.  Ignoring and creating an anonymous " +
                    "(session-less) Subject instance.", e);
        }
        return context;
    }

    protected Session resolveContextSession(SubjectContext context) throws InvalidSessionException {
        SessionKey key = getSessionKey(context); // key 为null
        if (key != null) {
            return getSession(key);
        }
        return null;
    }

    protected SessionKey getSessionKey(SubjectContext context) {
        Serializable sessionId = context.getSessionId(); //sessionId 为null
        if (sessionId != null) {
            // 创建 一个默认的 session Key
            return new DefaultSessionKey(sessionId);
        }
        return null;
    }

    private static boolean isEmpty(PrincipalCollection pc) {
        return pc == null || pc.isEmpty();
    }

    /**
     * Attempts to resolve an identity (a {@link PrincipalCollection}) for the context using heuristics.  This
     * implementation functions as follows:
     * <ol>
     * <li>Check the context to see if it can already {@link SubjectContext#resolvePrincipals resolve an identity}.  If
     * so, this method does nothing and returns the method argument unaltered.</li>
     * <li>Check for a RememberMe identity by calling {@link #getRememberedIdentity}.  If that method returns a
     * non-null value, place the remembered {@link PrincipalCollection} in the context.</li>
     * </ol>
     *
     * @param context the subject context data that may provide (directly or indirectly through one of its values) a
     *                {@link PrincipalCollection} identity.
     * @return The Subject context to use to pass to a {@link SubjectFactory} for subject creation.
     * @since 1.0
     */
    @SuppressWarnings({"unchecked"})
    protected SubjectContext resolvePrincipals(SubjectContext context) {

        // 解析 认证的信息 权限等
        PrincipalCollection principals = context.resolvePrincipals();  // 默认为null

        if (isEmpty(principals)) {
            log.trace("No identity (PrincipalCollection) found in the context.  Looking for a remembered identity.");

            // 获取 记住我的身份信息  在 主体上下文中获取
            principals = getRememberedIdentity(context);

            if (!isEmpty(principals)) {
                log.debug("Found remembered PrincipalCollection.  Adding to the context to be used " +
                        "for subject construction by the SubjectFactory.");

                context.setPrincipals(principals);

                // The following call was removed (commented out) in Shiro 1.2 because it uses the session as an
                // implementation strategy.  Session use for Shiro's own needs should be controlled in a single place
                // to be more manageable for end-users: there are a number of stateless (e.g. REST) applications that
                // use Shiro that need to ensure that sessions are only used when desirable.  If Shiro's internal
                // implementations used Subject sessions (setting attributes) whenever we wanted, it would be much
                // harder for end-users to control when/where that occurs.
                //
                // Because of this, the SubjectDAO was created as the single point of control, and session state logic
                // has been moved to the DefaultSubjectDAO implementation.

                // Removed in Shiro 1.2.  SHIRO-157 is still satisfied by the new DefaultSubjectDAO implementation
                // introduced in 1.2
                // Satisfies SHIRO-157:
                // bindPrincipalsToSession(principals, context);

            } else {
                log.trace("No remembered identity found.  Returning original context.");
            }
        }

        return context;
    }

    protected SessionContext createSessionContext(SubjectContext subjectContext) {
        DefaultSessionContext sessionContext = new DefaultSessionContext();
        if (!CollectionUtils.isEmpty(subjectContext)) {
            sessionContext.putAll(subjectContext);
        }
        Serializable sessionId = subjectContext.getSessionId();
        if (sessionId != null) {
            sessionContext.setSessionId(sessionId);
        }
        String host = subjectContext.resolveHost();
        if (host != null) {
            sessionContext.setHost(host);
        }
        return sessionContext;
    }

    public void logout(Subject subject) {

        if (subject == null) {
            throw new IllegalArgumentException("Subject method argument cannot be null.");
        }

        // 登出之前操作
        beforeLogout(subject);

        PrincipalCollection principals = subject.getPrincipals();
        if (principals != null && !principals.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Logging out subject with primary principal {}", principals.getPrimaryPrincipal());
            }
            Authenticator authc = getAuthenticator();
            //
            if (authc instanceof LogoutAware) {
                ((LogoutAware) authc).onLogout(principals);
            }
        }

        try {
            // 删除 Subject
            delete(subject);

            // 不重要
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                String msg = "Unable to cleanly unbind Subject.  Ignoring (logging out).";
                log.debug(msg, e);
            }
        } finally {
            try {
                stopSession(subject);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    String msg = "Unable to cleanly stop Session for Subject [" + subject.getPrincipal() + "] " +
                            "Ignoring (logging out).";
                    log.debug(msg, e);
                }
            }
        }
    }

    protected void stopSession(Subject subject) {
        Session s = subject.getSession(false);
        if (s != null) {
            s.stop();
        }
    }

    /**
     * Unbinds or removes the Subject's state from the application, typically called during {@link #logout}.
     * <p/>
     * This has been deprecated in Shiro 1.2 in favor of the {@link #delete(org.apache.shiro.subject.Subject) delete}
     * method.  The implementation has been updated to invoke that method.
     *
     * @param subject the subject to unbind from the application as it will no longer be used.
     * @deprecated in Shiro 1.2 in favor of {@link #delete(org.apache.shiro.subject.Subject)}
     */
    @Deprecated
    @SuppressWarnings({"UnusedDeclaration"})
    protected void unbind(Subject subject) {
        delete(subject);
    }

    /**
     * 获取身份信息
     * @param subjectContext
     * @return
     */
    protected PrincipalCollection getRememberedIdentity(SubjectContext subjectContext) {
        RememberMeManager rmm = getRememberMeManager();
        if (rmm != null) {
            try {
                return rmm.getRememberedPrincipals(subjectContext);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    String msg = "Delegate RememberMeManager instance of type [" + rmm.getClass().getName() +
                            "] threw an exception during getRememberedPrincipals().";
                    log.warn(msg, e);
                }
            }
        }
        return null;
    }
}
