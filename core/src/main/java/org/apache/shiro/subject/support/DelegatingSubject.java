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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.ProxiedSession;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.lang.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * 将方法调用委托给基础SecurityManager实例进行安全检查的Subject接口的实现。 本质上，它是一个SecurityManager代理。
 *
 * 此实现不维护角色和权限之类的状态（仅Subject principals ，例如用户名或用户主键），
 * 以便在无状态体系结构中获得更好的性能。 而是每次都要求基础SecurityManager进行授权检查。
 *
 * 使用此实现的一个常见误解是，每次调用方法时，都会“命中” EIS资源（RDBMS等）。
 * 不一定是这种情况，这取决于底层SecurityManager实例的实现。 如果需要缓存授权数据（以消除EIS往返行程并因此提高数据库性能），
 * 则让底层的SecurityManager实施或其委托组件管理缓存（而不是此类）被认为更为优雅。
 *
 * SecurityManager被认为是业务层组件，可以更好地管理缓存策略。
 *
 * 从大型，集群到简单的本地JVM的应用程序都将从无状态架构中受益。 此实现在无状态编程范例中起作用，应尽可能使用它
 *
 * Implementation of the {@code Subject} interface that delegates
 * method calls to an underlying {@link org.apache.shiro.mgt.SecurityManager SecurityManager} instance for security checks.
 * It is essentially a {@code SecurityManager} proxy.
 * <p/>
 * This implementation does not maintain state such as roles and permissions (only {@code Subject}
 * {@link #getPrincipals() principals}, such as usernames or user primary keys) for better performance in a stateless
 * architecture.  It instead asks the underlying {@code SecurityManager} every time to perform
 * the authorization check.
 * <p/>
 * A common misconception in using this implementation is that an EIS resource (RDBMS, etc) would
 * be &quot;hit&quot; every time a method is called.  This is not necessarily the case and is
 * up to the implementation of the underlying {@code SecurityManager} instance.  If caching of authorization
 * data is desired (to eliminate EIS round trips and therefore improve database performance), it is considered
 * much more elegant to let the underlying {@code SecurityManager} implementation or its delegate components
 * manage caching, not this class.  A {@code SecurityManager} is considered a business-tier component,
 * where caching strategies are better managed.
 * <p/>
 * Applications from large and clustered to simple and JVM-local all benefit from
 * stateless architectures.  This implementation plays a part in the stateless programming
 * paradigm and should be used whenever possible.
 *
 * @since 0.1
 */
public class DelegatingSubject implements Subject {

    private static final Logger log = LoggerFactory.getLogger(DelegatingSubject.class);

    private static final String RUN_AS_PRINCIPALS_SESSION_KEY =
            DelegatingSubject.class.getName() + ".RUN_AS_PRINCIPALS_SESSION_KEY";

    protected PrincipalCollection principals;
    protected boolean authenticated;
    protected String host;

    /**
     *  这个session 表达的含义
     */
    protected Session session;
    /**
     * @since 1.2
     */
    protected boolean sessionCreationEnabled;

    protected transient SecurityManager securityManager;

    public DelegatingSubject(SecurityManager securityManager) {
        this(null, false, null, null, securityManager);
    }

    public DelegatingSubject(PrincipalCollection principals, boolean authenticated, String host,
                             Session session, SecurityManager securityManager) {
        this(principals, authenticated, host, session, true, securityManager);
    }

    //since 1.2
    public DelegatingSubject(PrincipalCollection principals, boolean authenticated, String host,
                             Session session, boolean sessionCreationEnabled, SecurityManager securityManager) {
        if (securityManager == null) {
            throw new IllegalArgumentException("SecurityManager argument cannot be null.");
        }
        this.securityManager = securityManager;
        this.principals = principals;
        this.authenticated = authenticated;
        this.host = host;
        if (session != null) {
            this.session = decorate(session);
        }
        this.sessionCreationEnabled = sessionCreationEnabled;
    }

    /**
     * 创建
     * @param session
     * @return
     */
    protected Session decorate(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("session cannot be null");
        }
        return new StoppingAwareProxiedSession(session, this);
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    private static boolean isEmpty(PrincipalCollection pc) {
        return pc == null || pc.isEmpty();
    }

    protected boolean hasPrincipals() {
        return !isEmpty(getPrincipals());
    }

    /**
     * Returns the host name or IP associated with the client who created/is interacting with this Subject.
     *
     * @return the host name or IP associated with the client who created/is interacting with this Subject.
     */
    public String getHost() {
        return this.host;
    }

    private Object getPrimaryPrincipal(PrincipalCollection principals) {
        if (!isEmpty(principals)) {
            return principals.getPrimaryPrincipal();
        }
        return null;
    }

    /**
     * @see Subject#getPrincipal()
     */
    public Object getPrincipal() {
        return getPrimaryPrincipal(getPrincipals());
    }

    public PrincipalCollection getPrincipals() {

        // 当前登录的 session 然后再获取 凭证信息
        List<PrincipalCollection> runAsPrincipals = getRunAsPrincipalsStack();
        return CollectionUtils.isEmpty(runAsPrincipals) ? this.principals : runAsPrincipals.get(0);
    }

    public boolean isPermitted(String permission) {
        return hasPrincipals() && securityManager.isPermitted(getPrincipals(), permission);
    }

    public boolean isPermitted(Permission permission) {
        return hasPrincipals() && securityManager.isPermitted(getPrincipals(), permission);
    }

    public boolean[] isPermitted(String... permissions) {
        if (hasPrincipals()) {
            return securityManager.isPermitted(getPrincipals(), permissions);
        } else {
            return new boolean[permissions.length];
        }
    }

    public boolean[] isPermitted(List<Permission> permissions) {
        if (hasPrincipals()) {
            return securityManager.isPermitted(getPrincipals(), permissions);
        } else {
            return new boolean[permissions.size()];
        }
    }

    public boolean isPermittedAll(String... permissions) {
        return hasPrincipals() && securityManager.isPermittedAll(getPrincipals(), permissions);
    }

    public boolean isPermittedAll(Collection<Permission> permissions) {
        return hasPrincipals() && securityManager.isPermittedAll(getPrincipals(), permissions);
    }

    protected void assertAuthzCheckPossible() throws AuthorizationException {
        if (!hasPrincipals()) {
            String msg = "This subject is anonymous - it does not have any identifying principals and " +
                    "authorization operations require an identity to check against.  A Subject instance will " +
                    "acquire these identifying principals automatically after a successful login is performed " +
                    "be executing " + Subject.class.getName() + ".login(AuthenticationToken) or when 'Remember Me' " +
                    "functionality is enabled by the SecurityManager.  This exception can also occur when a " +
                    "previously logged-in Subject has logged out which " +
                    "makes it anonymous again.  Because an identity is currently not known due to any of these " +
                    "conditions, authorization is denied.";
            throw new UnauthenticatedException(msg);
        }
    }

    public void checkPermission(String permission) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkPermission(getPrincipals(), permission);
    }

    public void checkPermission(Permission permission) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkPermission(getPrincipals(), permission);
    }

    public void checkPermissions(String... permissions) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkPermissions(getPrincipals(), permissions);
    }

    public void checkPermissions(Collection<Permission> permissions) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkPermissions(getPrincipals(), permissions);
    }

    public boolean hasRole(String roleIdentifier) {
        return hasPrincipals() && securityManager.hasRole(getPrincipals(), roleIdentifier);
    }

    public boolean[] hasRoles(List<String> roleIdentifiers) {
        if (hasPrincipals()) {
            return securityManager.hasRoles(getPrincipals(), roleIdentifiers);
        } else {
            return new boolean[roleIdentifiers.size()];
        }
    }

    public boolean hasAllRoles(Collection<String> roleIdentifiers) {
        return hasPrincipals() && securityManager.hasAllRoles(getPrincipals(), roleIdentifiers);
    }

    public void checkRole(String role) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkRole(getPrincipals(), role);
    }

    public void checkRoles(String... roleIdentifiers) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkRoles(getPrincipals(), roleIdentifiers);
    }

    public void checkRoles(Collection<String> roles) throws AuthorizationException {
        assertAuthzCheckPossible();
        securityManager.checkRoles(getPrincipals(), roles);
    }

    /**
     * 调用时机：
     *     过滤器校验 ? Form 表单提交校验
     *
     *     手动触发调用 subject.login()
     *
     * @param token the token encapsulating the subject's principals and credentials to be passed to the
     *              Authentication subsystem for verification.
     * @throws AuthenticationException
     */
    public void login(AuthenticationToken token) throws AuthenticationException {
        //清除内部运行身份
        clearRunAsIdentitiesInternal();

        // 委托给 SecurityManager 进行登录   那么我有个疑问 何时初始化的 securityManager呢？
        // 交给 安全管理1器进行登录
        Subject subject = securityManager.login(this, token);
        // 成功 返回 Subject

        PrincipalCollection principals;

        String host = null;

        if (subject instanceof DelegatingSubject) {
            DelegatingSubject delegating = (DelegatingSubject) subject;
            //we have to do this in case there are assumed identities - we don't want to lose the 'real' principals:
            principals = delegating.principals;
            host = delegating.host;
        } else {
            principals = subject.getPrincipals();
        }

        if (principals == null || principals.isEmpty()) {
            String msg = "Principals returned from securityManager.login( token ) returned a null or " +
                    "empty value.  This value must be non null and populated with one or more elements.";
            throw new IllegalStateException(msg);
        }
        this.principals = principals;
        this.authenticated = true;
        if (token instanceof HostAuthenticationToken) {
            host = ((HostAuthenticationToken) token).getHost();
        }
        if (host != null) {
            this.host = host;
        }
        Session session = subject.getSession(false);
        if (session != null) {
            // 装饰 session
            this.session = decorate(session);
        } else {
            this.session = null;
        }
    }

    /**
     * 是否认证 ？
     *  : 认证值和 存在 认证信息
     * @return
     */
    public boolean isAuthenticated() {


        return authenticated && hasPrincipals();
    }

    public boolean isRemembered() {
        PrincipalCollection principals = getPrincipals();
        return principals != null && !principals.isEmpty() && !isAuthenticated();
    }

    /**
     * Returns {@code true} if this Subject is allowed to create sessions, {@code false} otherwise.
     *
     * @return {@code true} if this Subject is allowed to create sessions, {@code false} otherwise.
     * @since 1.2
     */
    protected boolean isSessionCreationEnabled() {
        return this.sessionCreationEnabled;
    }

    public Session getSession() {
        return getSession(true);
    }

    /**
     *
     * @param create boolean argument determining if a new session should be created or not if there is no existing session.
     *               布尔参数，用于确定是否应创建新会话（如果不存在现有会话）。
     *
     * @return
     */
    public Session getSession(boolean create) {
        if (log.isTraceEnabled()) {
            log.trace("attempting to get session; create = " + create +
                    "; session is null = " + (this.session == null) +
                    "; session has id = " + (this.session != null && session.getId() != null));
        }

        if (this.session == null && create) {

            //added in 1.2:
            if (! isSessionCreationEnabled()) {
                String msg = "Session creation has been disabled for the current subject.  This exception indicates " +
                        "that there is either a programming error (using a session when it should never be " +
                        "used) or that Shiro's configuration needs to be adjusted to allow Sessions to be created " +
                        "for the current Subject.  See the " + DisabledSessionException.class.getName() + " JavaDoc " +
                        "for more.";
                throw new DisabledSessionException(msg);
            }

            log.trace("Starting session for host {}", getHost());
            SessionContext sessionContext = createSessionContext();
            //  主题中保存了这个安全管理器
            Session session = this.securityManager.start(sessionContext);
            this.session = decorate(session);
        }
        return this.session;
    }

    protected SessionContext createSessionContext() {
        SessionContext sessionContext = new DefaultSessionContext();
        if (StringUtils.hasText(host)) {
            sessionContext.setHost(host);
        }
        return sessionContext;
    }

    private void clearRunAsIdentitiesInternal() {
        //try/catch added for SHIRO-298
        try {
            clearRunAsIdentities();
        } catch (SessionException se) {
            log.debug("Encountered session exception trying to clear 'runAs' identities during logout.  This " +
                    "can generally safely be ignored.", se);
        }
    }

    /**
     * 登出
     */
    public void logout() {
        try {
            //清除内部运行身份
            clearRunAsIdentitiesInternal();
            this.securityManager.logout(this);
        } finally {
            this.session = null;
            this.principals = null;
            this.authenticated = false;
            //Don't set securityManager to null here - the Subject can still be
            //used, it is just considered anonymous at this point.  The SecurityManager instance is
            //necessary if the subject would log in again or acquire a new session.  This is in response to
            //https://issues.apache.org/jira/browse/JSEC-22
            //this.securityManager = null;
        }
    }

    private void sessionStopped() {
        this.session = null;
    }

    public <V> V execute(Callable<V> callable) throws ExecutionException {
        Callable<V> associated = associateWith(callable);
        try {
            return associated.call();
        } catch (Throwable t) {
            throw new ExecutionException(t);
        }
    }

    @Override
    public void execute(Runnable runnable) {
        Runnable associated = associateWith(runnable);
        // 同步执行
        associated.run();
    }

    public <V> Callable<V> associateWith(Callable<V> callable) {
        return new SubjectCallable<V>(this, callable);
    }

    public Runnable associateWith(Runnable runnable) {

        // 如果是线程就抛出异常
        if (runnable instanceof Thread) {
            String msg = "This implementation does not support Thread arguments because of JDK ThreadLocal " +
                    "inheritance mechanisms required by Shiro.  Instead, the method argument should be a non-Thread " +
                    "Runnable and the return value from this method can then be given to an ExecutorService or " +
                    "another Thread.";
            throw new UnsupportedOperationException(msg);
        }
        return new SubjectRunnable(this, runnable);
    }

    private class StoppingAwareProxiedSession extends ProxiedSession {

        private final DelegatingSubject owner;

        private StoppingAwareProxiedSession(Session target, DelegatingSubject owningSubject) {
            super(target);
            owner = owningSubject;
        }

        public void stop() throws InvalidSessionException {
            super.stop();
            owner.sessionStopped();
        }
    }


    // ======================================
    // 'Run As' support implementations
    // ======================================

    public void runAs(PrincipalCollection principals) {
        if (!hasPrincipals()) {
            String msg = "This subject does not yet have an identity.  Assuming the identity of another " +
                    "Subject is only allowed for Subjects with an existing identity.  Try logging this subject in " +
                    "first, or using the " + Subject.Builder.class.getName() + " to build ad hoc Subject instances " +
                    "with identities as necessary.";
            throw new IllegalStateException(msg);
        }
        pushIdentity(principals);
    }

    public boolean isRunAs() {
        List<PrincipalCollection> stack = getRunAsPrincipalsStack();
        return !CollectionUtils.isEmpty(stack);
    }

    public PrincipalCollection getPreviousPrincipals() {
        PrincipalCollection previousPrincipals = null;
        List<PrincipalCollection> stack = getRunAsPrincipalsStack();
        int stackSize = stack != null ? stack.size() : 0;
        if (stackSize > 0) {
            if (stackSize == 1) {
                previousPrincipals = this.principals;
            } else {
                //always get the one behind the current:
                assert stack != null;
                previousPrincipals = stack.get(1);
            }
        }
        return previousPrincipals;
    }

    public PrincipalCollection releaseRunAs() {
        return popIdentity();
    }

    @SuppressWarnings("unchecked")
    private List<PrincipalCollection> getRunAsPrincipalsStack() {
        Session session = getSession(false);
        if (session != null) {
            return (List<PrincipalCollection>) session.getAttribute(RUN_AS_PRINCIPALS_SESSION_KEY);
        }
        return null;
    }

    private void clearRunAsIdentities() {
        Session session = getSession(false);
        if (session != null) {
            // 移除一个key
            session.removeAttribute(RUN_AS_PRINCIPALS_SESSION_KEY);
        }
    }

    private void pushIdentity(PrincipalCollection principals) throws NullPointerException {
        if (isEmpty(principals)) {
            String msg = "Specified Subject principals cannot be null or empty for 'run as' functionality.";
            throw new NullPointerException(msg);
        }
        List<PrincipalCollection> stack = getRunAsPrincipalsStack();
        if (stack == null) {
            stack = new CopyOnWriteArrayList<PrincipalCollection>();
        }
        stack.add(0, principals);
        Session session = getSession();
        session.setAttribute(RUN_AS_PRINCIPALS_SESSION_KEY, stack);
    }

    private PrincipalCollection popIdentity() {
        PrincipalCollection popped = null;

        List<PrincipalCollection> stack = getRunAsPrincipalsStack();
        if (!CollectionUtils.isEmpty(stack)) {
            popped = stack.remove(0);
            Session session;
            if (!CollectionUtils.isEmpty(stack)) {
                //persist the changed stack to the session
                session = getSession();
                session.setAttribute(RUN_AS_PRINCIPALS_SESSION_KEY, stack);
            } else {
                //stack is empty, remove it from the session:
                clearRunAsIdentities();
            }
        }

        return popped;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "DelegatingSubject{", "}")
            .add("principals=" + principals)
            .add("authenticated=" + authenticated)
            .add("host='******")
            .add("session='******'")
            .add("sessionCreationEnabled=" + sessionCreationEnabled)
            .add("securityManager=" + securityManager)
            .toString();
    }
}
