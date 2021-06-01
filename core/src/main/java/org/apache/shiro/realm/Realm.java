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
package org.apache.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * 领域是一个安全组件，可以访问特定于应用程序的安全实体，例如用户，角色和权限，以确定身份验证和授权操作。
 * 领域通常与数据源（如关系数据库，文件系统或其他类似资源）成一一对应。 这样，此接口的实现使用特定于数据源的API来确定授权数据（角色，权限等），
 * 例如JDBC，文件IO，Hibernate或JPA或任何其他数据访问API。 它们本质上是特定于安全性的DAO  。
 * 由于这些数据源中的大多数通常包含主题（即用户）信息，例如用户名和密码，因此领域可以充当PAM 配置中的可插入身份验证模块。
 * 这允许王国到用于单个数据源，这迎合了大多数应用程序执行的验证和授权的职责。
 *  留意：
 * 如果由于某种原因您不希望Realm实现执行身份验证职责，则应重写supports(AuthenticationToken)方法以始终返回false 。
 * 由于每个应用程序都不同，因此可以用多种方式表示用户和角色之类的安全数据。
 * Shiro尝试尽可能保持非介入式开发理念-它不需要您实现或扩展任何User ， Group或Role接口或类。
 * 相反，Shiro允许应用程序实现此接口以访问特定于环境的数据源和数据模型对象。 然后可以将该实现插入到应用程序的Shiro配置中。
 * 这种模块化技术可以抽象出任何环境/建模细节，并使Shiro可以部署在几乎任何应用程序环境中。
 * 大多数用户不会直接实现Realm接口，而是会扩展一个子类AuthenticatingRealm或AuthorizingRealm ，从而大大减少了从头开始实现Realm的工作量。
 *
 * A <tt>Realm</tt> is a security component that can access application-specific security entities
 * such as users, roles, and permissions to determine authentication and authorization operations.
 *
 * <p><tt>Realm</tt>s usually have a 1-to-1 correspondence with a datasource such as a relational database,
 * file system, or other similar resource.  As such, implementations of this interface use datasource-specific APIs to
 * determine authorization data (roles, permissions, etc), such as JDBC, File IO, Hibernate or JPA, or any other
 * Data Access API.  They are essentially security-specific
 * <a href="http://en.wikipedia.org/wiki/Data_Access_Object" target="_blank">DAO</a>s.
 *
 * <p>Because most of these datasources usually contain Subject (a.k.a. User) information such as usernames and
 * passwords, a Realm can act as a pluggable authentication module in a
 * <a href="http://en.wikipedia.org/wiki/Pluggable_Authentication_Modules">PAM</a> configuration.  This allows a Realm to
 * perform <i>both</i> authentication and authorization duties for a single datasource, which caters to the large
 * majority of applications.  If for some reason you don't want your Realm implementation to perform authentication
 * duties, you should override the {@link #supports(org.apache.shiro.authc.AuthenticationToken)} method to always
 * return <tt>false</tt>.
 *
 * <p>Because every application is different, security data such as users and roles can be
 * represented in any number of ways.  Shiro tries to maintain a non-intrusive development philosophy whenever
 * possible - it does not require you to implement or extend any <tt>User</tt>, <tt>Group</tt> or <tt>Role</tt>
 * interfaces or classes.
 *
 * <p>Instead, Shiro allows applications to implement this interface to access environment-specific datasources
 * and data model objects.  The implementation can then be plugged in to the application's Shiro configuration.
 * This modular technique abstracts away any environment/modeling details and allows Shiro to be deployed in
 * practically any application environment.
 *
 * <p>Most users will not implement the <tt>Realm</tt> interface directly, but will extend one of the subclasses,
 * {@link org.apache.shiro.realm.AuthenticatingRealm AuthenticatingRealm} or {@link org.apache.shiro.realm.AuthorizingRealm}, greatly reducing the effort requird
 * to implement a <tt>Realm</tt> from scratch.</p>
 *
 * @see org.apache.shiro.realm.CachingRealm CachingRealm
 * @see org.apache.shiro.realm.AuthenticatingRealm AuthenticatingRealm
 * @see org.apache.shiro.realm.AuthorizingRealm AuthorizingRealm
 * @see org.apache.shiro.authc.pam.ModularRealmAuthenticator ModularRealmAuthenticator
 * @since 0.1
 */
public interface Realm {

    /**
     * Returns the (application-unique) name assigned to this <code>Realm</code>. All realms configured for a single
     * application must have a unique name.
     *
     * @return the (application-unique) name assigned to this <code>Realm</code>.
     */
    String getName();

    /**
     * Returns <tt>true</tt> if this realm wishes to authenticate the Subject represented by the given
     * {@link org.apache.shiro.authc.AuthenticationToken AuthenticationToken} instance, <tt>false</tt> otherwise.
     *
     * <p>If this method returns <tt>false</tt>, it will not be called to authenticate the Subject represented by
     * the token - more specifically, a <tt>false</tt> return value means this Realm instance's
     * {@link #getAuthenticationInfo} method will not be invoked for that token.
     *
     * @param token the AuthenticationToken submitted for the authentication attempt
     * @return <tt>true</tt> if this realm can/will authenticate Subjects represented by specified token,
     *         <tt>false</tt> otherwise.
     */
    boolean supports(AuthenticationToken token);

    /**
     * Returns an account's authentication-specific information for the specified <tt>token</tt>,
     * or <tt>null</tt> if no account could be found based on the <tt>token</tt>.
     *
     * <p>This method effectively represents a login attempt for the corresponding user with the underlying EIS datasource.
     * Most implementations merely just need to lookup and return the account data only (as the method name implies)
     * and let Shiro do the rest, but implementations may of course perform eis specific login operations if so
     * desired.
     *
     * @param token the application-specific representation of an account principal and credentials.
     * @return the authentication information for the account associated with the specified <tt>token</tt>,
     *         or <tt>null</tt> if no account could be found.
     * @throws org.apache.shiro.authc.AuthenticationException
     *          if there is an error obtaining or constructing an AuthenticationInfo object based on the
     *          specified <tt>token</tt> or implementation-specific login behavior fails.
     */
    AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;

}
