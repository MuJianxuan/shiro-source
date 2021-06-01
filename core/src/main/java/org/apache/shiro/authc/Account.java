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
package org.apache.shiro.authc;

import org.apache.shiro.authz.AuthorizationInfo;

/**
 *  Account 账户  ： 包含 账号 密码 对象
 *  是一个便捷接口，它扩展了{@link AuthenticationInfo}和{@link AuthorizationInfo}，
 *  并表示了<em>单个领域<em>中<em>单个帐户<em>的身份验证和授权。 >。
 *
 *  需要注意的是 这个 对象 包含 了  ca认证 信息 和  za 角色权限信息
 *
 * An <tt>Account</tt> is a convenience interface that extends both {@link AuthenticationInfo} and
 * {@link AuthorizationInfo} and represents authentication and authorization for a <em>single account</em> in a
 * <em>single Realm</em>.
 * <p/>
 * This interface can be useful when a Realm implementation finds it more convenient to use a single object to
 * encapsulate both the authentication and authorization information used by both authc and authz operations.
 * <p/>
 * <b>Please Note</b>:  Since Shiro sometimes logs account operations, please ensure your Account's <code>toString()</code>
 * implementation does <em>not</em> print out account credentials (password, etc), as these might be viewable to
 * someone reading your logs.  This is good practice anyway, and account principals should rarely (if ever) be printed
 * out for any reason.  If you're using Shiro's default implementations of this interface, they only ever print the
 * account {@link #getPrincipals() principals}, so you do not need to do anything additional.
 *
 * 由于Shiro有时会记录帐户操作，因此请确保您帐户的<code> toString（）<code>实现不会<em> not <em>打印出帐户凭据（密码等），
 * 因为阅读日志的人可以看到这些凭据。无论如何，这都是一个好习惯，并且无论出于何种原因，都很少（如果有）打印帐户负责人。
 * 如果您使用Shiro的此接口的默认实现，则它们只会打印帐户{@link getPrincipals（）principal}，因此您无需执行任何其他操作。
 *
 * 大概意思是说 不要自己打印，会看到凭证数据 ，可以用他实现的打印，只能看到 登录的账号。
 *
 * @see SimpleAccount
 * @since 0.9
 */
public interface Account extends AuthenticationInfo, AuthorizationInfo {

}
