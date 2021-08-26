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
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.lang.util.LifecycleUtils;


/**
 *
 * Shiro支持将所有身份验证操作委派给包装的Authenticator实例的SecurityManager类层次结构。
 * 也就是说，此类在SecurityManager接口中实现了所有Authenticator方法，但实际上，这些方法仅仅是对底层“真实” Authenticator实例的直通调用。
 * 所有其他SecurityManager （授权，会话等）方法均由子类实现。
 *
 * Shiro support of a {@link SecurityManager} class hierarchy that delegates all
 * authentication operations to a wrapped {@link Authenticator Authenticator} instance.  That is, this class
 * implements all the <tt>Authenticator</tt> methods in the {@link SecurityManager SecurityManager}
 * interface, but in reality, those methods are merely passthrough calls to the underlying 'real'
 * <tt>Authenticator</tt> instance.
 *
 * <p>All other <tt>SecurityManager</tt> (authorization, session, etc) methods are left to be implemented by subclasses.
 *
 * <p>In keeping with the other classes in this hierarchy and Shiro's desire to minimize configuration whenever
 * possible, suitable default instances for all dependencies are created upon instantiation.
 *
 * @since 0.9
 */
public abstract class AuthenticatingSecurityManager extends RealmSecurityManager {

    /**
     *
     *  所以我在思考， 是不是实现 与 Authenticator 一样的接口实际上只是为了方便写方法而已，不想再一个个写罢了，不会用来多态转换
     *
     * 此SecurityManager实例将用于执行所有身份验证操作的内部Authenticator委托实例
     *
     *   包含 Realm 领域集合信息
     *
     * The internal <code>Authenticator</code> delegate instance that this SecurityManager instance will use
     * to perform all authentication operations.
     */
    private Authenticator authenticator;

    /**
     * Default no-arg constructor that initializes its internal
     * <code>authenticator</code> instance to a
     * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}.
     */
    public AuthenticatingSecurityManager() {
        super();
        //   这个是ca  这个是认证 和账户 有关的 ，这个层次结构我们很容易想明白了
        this.authenticator = new ModularRealmAuthenticator();
    }

    /**
     * Returns the delegate <code>Authenticator</code> instance that this SecurityManager uses to perform all
     * authentication operations.  Unless overridden by the
     * {@link #setAuthenticator(org.apache.shiro.authc.Authenticator) setAuthenticator}, the default instance is a
     * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}.
     *
     * @return the delegate <code>Authenticator</code> instance that this SecurityManager uses to perform all
     *         authentication operations.
     */
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * Sets the delegate <code>Authenticator</code> instance that this SecurityManager uses to perform all
     * authentication operations.  Unless overridden by this method, the default instance is a
     * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator}.
     *
     * @param authenticator the delegate <code>Authenticator</code> instance that this SecurityManager will use to
     *                      perform all authentication operations.
     * @throws IllegalArgumentException if the argument is <code>null</code>.
     */
    public void setAuthenticator(Authenticator authenticator) throws IllegalArgumentException {
        if (authenticator == null) {
            String msg = "Authenticator argument cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        this.authenticator = authenticator;
    }

    /**
     * Passes on the {@link #getRealms() realms} to the internal delegate <code>Authenticator</code> instance so
     * that it may use them during authentication attempts.
     */
    protected void afterRealmsSet() {
        super.afterRealmsSet();
        if (this.authenticator instanceof ModularRealmAuthenticator) {
            ((ModularRealmAuthenticator) this.authenticator).setRealms(getRealms());
        }
    }

    /**
     *
     * 委托给包装的Authenticator进行身份验证
     *
     * Delegates to the wrapped {@link org.apache.shiro.authc.Authenticator Authenticator} for authentication.
     */
    public AuthenticationInfo authenticate(AuthenticationToken token) throws AuthenticationException {


        // 委托给  认证人去 认证
        return this.authenticator.authenticate(token);
    }

    public void destroy() {
        LifecycleUtils.destroy(getAuthenticator());
        this.authenticator = null;
        super.destroy();
    }
}
