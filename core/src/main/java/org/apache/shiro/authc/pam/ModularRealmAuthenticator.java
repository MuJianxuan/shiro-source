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
package org.apache.shiro.authc.pam;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 *
 * ModularRealmAuthenticator将帐户查找委托给Realm的可插拔（模块化）集合。 这将在Shiro中启用PAM（可插入身份验证模块）行为。
 * 除了授权职责外，还可以将Shiro Realm视为PAM的“模块”。
 *
 * 使用此Authenticator可以让您视需要“插入”自己的Realm 。 常见领域是基于访问LDAP，关系数据库，文件系统等的领域。
 *
 * 如果仅配置一个领域（对于大多数应用程序通常是这种情况），那么身份验证成功自然仅取决于调用该领域的Realm.getAuthenticationInfo(AuthenticationToken)方法。
 *
 * 但是，如果配置了两个或多个领域，则PAM行为是通过遍历领域的集合并在身份验证尝试过程中与每个领域进行交互来实现的。
 * 由于此操作更为复杂，因此此身份验证器允许使用自定义行为来解释与多个领域进行交互时发生的情况-例如，您可能要求所有领域在尝试期间都必须成功，或
 * 者可能至少一个必须成功，或者进行其他解释。 可以通过使用AuthenticationStrategy来执行此自定义行为，您可以将其作为此类的属性插入。
 *
 * 策略对象提供了回调方法，使您可以确定在多领域（PAM）方案中成功或失败的原因。 并且因为这仅在多领域方案中才有意义，所以仅在配置多个Realm时才使用策略对象。
 *
 * 由于大多数多领域应用程序至少需要一个Realm成功进行身份验证，因此默认实现是AtLeastOneSuccessfulStrategy 。
 *
 *
 * A {@code ModularRealmAuthenticator} delegates account lookups to a pluggable (modular) collection of
 * {@link Realm}s.  This enables PAM (Pluggable Authentication Module) behavior in Shiro.
 * In addition to authorization duties, a Shiro Realm can also be thought of a PAM 'module'.
 * <p/>
 * Using this Authenticator allows you to &quot;plug-in&quot; your own
 * {@code Realm}s as you see fit.  Common realms are those based on accessing
 * LDAP, relational databases, file systems, etc.
 * <p/>
 * If only one realm is configured (this is often the case for most applications), authentication success is naturally
 * only dependent upon invoking this one Realm's
 * {@link Realm#getAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)} method.
 * <p/>
 * But if two or more realms are configured, PAM behavior is implemented by iterating over the collection of realms
 * and interacting with each over the course of the authentication attempt.  As this is more complicated, this
 * authenticator allows customized behavior for interpreting what happens when interacting with multiple realms - for
 * example, you might require all realms to be successful during the attempt, or perhaps only at least one must be
 * successful, or some other interpretation.  This customized behavior can be performed via the use of a
 * {@link #setAuthenticationStrategy(AuthenticationStrategy) AuthenticationStrategy}, which
 * you can inject as a property of this class.
 * <p/>
 * The strategy object provides callback methods that allow you to
 * determine what constitutes a success or failure in a multi-realm (PAM) scenario.  And because this only makes sense
 * in a multi-realm scenario, the strategy object is only utilized when more than one Realm is configured.
 * <p/>
 * As most multi-realm applications require at least one Realm authenticates successfully, the default
 * implementation is the {@link AtLeastOneSuccessfulStrategy}.
 *
 * @see #setRealms
 * @see AtLeastOneSuccessfulStrategy
 * @see AllSuccessfulStrategy
 * @see FirstSuccessfulStrategy
 * @since 0.1
 */
public class ModularRealmAuthenticator extends AbstractAuthenticator {

    /*--------------------------------------------
    |             C O N S T A N T S             |
    ============================================*/
    private static final Logger log = LoggerFactory.getLogger(ModularRealmAuthenticator.class);

    /*--------------------------------------------
    |    I N S T A N C E   V A R I A B L E S    |
    ============================================*/
    /**
     * 存着 领域集合信息
     *
     * List of realms that will be iterated through when a user authenticates.
     */
    private Collection<Realm> realms;

    /**
     *
     * 认证策略 ：
     *      至少一个领域通过
     *      必须全部领域通过
     *      第一个必须通过
     *      ...
     *
     *
     * The authentication strategy to use during authentication attempts, defaults to a
     * {@link org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy} instance.
     */
    private AuthenticationStrategy authenticationStrategy;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

    /**
     * Default no-argument constructor which
     * {@link #setAuthenticationStrategy(AuthenticationStrategy) enables}  an
     * {@link org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy} by default.
     */
    public ModularRealmAuthenticator() {

        // 默认策略  至少一个 通过
        this.authenticationStrategy = new AtLeastOneSuccessfulStrategy();
    }

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    /**
     * Sets all realms used by this Authenticator, providing PAM (Pluggable Authentication Module) configuration.
     *
     * @param realms the realms to consult during authentication attempts.
     */
    public void setRealms(Collection<Realm> realms) {
        this.realms = realms;
    }

    /**
     *   获取 最重要的是关注 来源
     *
     *
     * Returns the realm(s) used by this {@code Authenticator} during an authentication attempt.
     *
     * @return the realm(s) used by this {@code Authenticator} during an authentication attempt.
     */
    protected Collection<Realm> getRealms() {
        return this.realms;
    }

    /**
     * Returns the {@code AuthenticationStrategy} utilized by this modular authenticator during a multi-realm
     * log-in attempt.  This object is only used when two or more Realms are configured.
     * <p/>
     * Unless overridden by
     * the {@link #setAuthenticationStrategy(AuthenticationStrategy)} method, the default implementation
     * is the {@link org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy}.
     *
     * @return the {@code AuthenticationStrategy} utilized by this modular authenticator during a log-in attempt.
     * @since 0.2
     */
    public AuthenticationStrategy getAuthenticationStrategy() {
        return authenticationStrategy;
    }

    /**
     * Allows overriding the default {@code AuthenticationStrategy} utilized during multi-realm log-in attempts.
     * This object is only used when two or more Realms are configured.
     *
     * @param authenticationStrategy the strategy implementation to use during log-in attempts.
     * @since 0.2
     */
    public void setAuthenticationStrategy(AuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }

    /*--------------------------------------------
    |               M E T H O D S               |

    /**
     * 由内部 {@link doAuthenticate} 实现使用，以确保已设置 {@code realms} 属性。默认实现确保该属性不为 null 且不为空。
     * Used by the internal {@link #doAuthenticate} implementation to ensure that the {@code realms} property
     *
     * has been set.  The default implementation ensures the property is not null and not empty.
     *
     * @throws IllegalStateException if the {@code realms} property is configured incorrectly.
     */

    protected void assertRealmsConfigured() throws IllegalStateException {
        // 前置校验
        Collection<Realm> realms = getRealms();
        if (CollectionUtils.isEmpty(realms)) {
            String msg = "Configuration error:  No realms have been configured!  One or more realms must be " +
                    "present to execute an authentication attempt.";
            throw new IllegalStateException(msg);
        }
    }

    /**
     * 通过与单个配置的领域交互来执行身份验证尝试，这比执行多领域逻辑要简单得多。
     *
     * Performs the authentication attempt by interacting with the single configured realm, which is significantly
     * simpler than performing multi-realm logic.
     *
     * @param realm the realm to consult for AuthenticationInfo.
     * @param token the submitted AuthenticationToken representing the subject's (user's) log-in principals and credentials.
     * @return the AuthenticationInfo associated with the user account corresponding to the specified {@code token}
     */
    protected AuthenticationInfo doSingleRealmAuthentication(Realm realm, AuthenticationToken token) {
        // ? 不支持！
        if (!realm.supports(token)) {
            String msg = "Realm [" + realm + "] does not support authentication token [" +
                    token + "].  Please ensure that the appropriate Realm implementation is " +
                    "configured correctly or that the realm accepts AuthenticationTokens of this type.";
            throw new UnsupportedTokenException(msg);
        }

        // 模块认证
        AuthenticationInfo info = realm.getAuthenticationInfo(token);
        if (info == null) {

            // 了然   账号为空 返回 不知道账户异常
            String msg = "Realm [" + realm + "] was unable to find account data for the " +
                    "submitted AuthenticationToken [" + token + "].";
            throw new UnknownAccountException(msg);
        }
        return info;
    }

    /**
     *
     *  多Realm 认证  需要配合策略来进行 处理 ！
     *
     * Performs the multi-realm authentication attempt by calling back to a {@link AuthenticationStrategy} object
     * as each realm is consulted for {@code AuthenticationInfo} for the specified {@code token}.
     *
     * @param realms the multiple realms configured on this Authenticator instance.
     * @param token  the submitted AuthenticationToken representing the subject's (user's) log-in principals and credentials.
     * @return an aggregated AuthenticationInfo instance representing account data across all the successfully
     *         consulted realms.
     */
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {

        // 认证策略
        AuthenticationStrategy strategy = getAuthenticationStrategy();

        AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);

        if (log.isTraceEnabled()) {
            log.trace("Iterating through {} realms for PAM authentication", realms.size());
        }

        // 循环认证   依据 策略
        for (Realm realm : realms) {

            try {
                aggregate = strategy.beforeAttempt(realm, token, aggregate);
            } catch (ShortCircuitIterationException shortCircuitSignal) {
                // Break from continuing with subsequnet realms on receiving 
                // short circuit signal from strategy
                break;
            }

            // 是否支持该  Token认证

            /**
             *  一个 Realm  至少要 控制两个方法的返回   supports  和  getAuthenticationInfo
             */
            if (realm.supports( token)) {

                if (log.isTraceEnabled()) {
                    log.trace("Attempting to authenticate token [{}] using realm [{}]", token, realm);
                }

                AuthenticationInfo info = null;
                Throwable t = null;
                try {
                    info = realm.getAuthenticationInfo(token);
                } catch (Throwable throwable) {
                    t = throwable;
                    if (log.isDebugEnabled()) {
                        String msg = "Realm [" + realm + "] threw an exception during a multi-realm authentication attempt:";
                        log.debug(msg, t);
                    }
                }

                aggregate = strategy.afterAttempt(realm, token, info, aggregate, t);

            } else {
                if( log.isDebugEnabled()){
                    log.debug("Realm [{}] does not support token {}.  Skipping realm.", realm, token);
                }
            }
        }

        aggregate = strategy.afterAllAttempts(token, aggregate);

        return aggregate;
    }


    /**
     * Attempts to authenticate the given token by iterating over the internal collection of
     * {@link Realm}s.  For each realm, first the {@link Realm#supports(org.apache.shiro.authc.AuthenticationToken)}
     * method will be called to determine if the realm supports the {@code authenticationToken} method argument.
     * <p/>
     * If a realm does support
     * the token, its {@link Realm#getAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)}
     * method will be called.  If the realm returns a non-null account, the token will be
     * considered authenticated for that realm and the account data recorded.  If the realm returns {@code null},
     * the next realm will be consulted.  If no realms support the token or all supporting realms return null,
     * an {@link AuthenticationException} will be thrown to indicate that the user could not be authenticated.
     * <p/>
     * After all realms have been consulted, the information from each realm is aggregated into a single
     * {@link AuthenticationInfo} object and returned.
     *
     * @param authenticationToken the token containing the authentication principal and credentials for the
     *                            user being authenticated.
     * @return account information attributed to the authenticated user.
     * @throws IllegalStateException   if no realms have been configured at the time this method is invoked
     * @throws AuthenticationException if the user could not be authenticated or the user is denied authentication
     *                                 for the given principal and credentials.
     */
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        assertRealmsConfigured();

        // 匹配的 Realms 来认证

        // 领域认证   怎么说呢？ 就好比  我是 你这里的人，那么你的身份当然由我来确认

        // 因此需要区分 当前这个人  属于 哪个领域  ，此外 这个token 不一定表示人  即 authenticationToken

        Collection<Realm> realms = getRealms();
        // 只有一个则无策略
        if (realms.size() == 1) {
            return doSingleRealmAuthentication(realms.iterator().next(), authenticationToken);
        } else {
            return doMultiRealmAuthentication(realms, authenticationToken);
        }
    }

    /**
     * First calls <code>super.onLogout(principals)</code> to ensure a logout notification is issued, and for each
     * wrapped {@code Realm} that implements the {@link LogoutAware LogoutAware} interface, calls
     * <code>((LogoutAware)realm).onLogout(principals)</code> to allow each realm the opportunity to perform
     * logout/cleanup operations during an user-logout.
     * <p/>
     * Shiro's Realm implementations all implement the {@code LogoutAware} interface by default and can be
     * overridden for realm-specific logout logic.
     *
     * @param principals the application-specific Subject/user identifier.
     */
    public void onLogout(PrincipalCollection principals) {
        // 通知 结束
        super.onLogout(principals);

        Collection<Realm> realms = getRealms();
        if (!CollectionUtils.isEmpty(realms)) {
            for (Realm realm : realms) {
                if (realm instanceof LogoutAware) {
                    // 登出
                    ((LogoutAware) realm).onLogout(principals);
                }
            }
        }
    }
}
