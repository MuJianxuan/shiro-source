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
package org.apache.shiro.authc.credential;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;


/**
 *
 *  凭证匹配器
 *
 *    密码比较器 ，盐值使用
 *
 *       比较对象 ： Form提交的账户信息 与 后台查询的账户信息
 *
 *       此类（及其子类）的功能如下：
 * 散列用户在登录期间提供的  AuthenticationToken  凭据。 登录令牌
 * 直接将此哈希值与存储在系统中的   AuthenticationInfo  凭据进行比较（存储的帐户凭据应已采用哈希形式）。
 * 如果这两个值equal ，则提交的凭据匹配，否则不匹配
 *
 * Interface implemented by classes that can determine if an AuthenticationToken's provided
 * credentials matches a corresponding account's credentials stored in the system.
 *
 * <p>Simple direct comparisons are handled well by the
 * {@link SimpleCredentialsMatcher SimpleCredentialsMatcher}.  If you
 * hash user's credentials before storing them in a realm (a common practice), look at the
 * {@link HashedCredentialsMatcher HashedCredentialsMatcher} implementations,
 * as they support this scenario.
 *
 * @see SimpleCredentialsMatcher
 * @see AllowAllCredentialsMatcher
 * @see Md5CredentialsMatcher
 * @see Sha1CredentialsMatcher
 * @since 0.1
 */
public interface CredentialsMatcher {

    /**
     *
     *  如果提供的令牌凭证与存储的帐户凭证匹配，则返回{@code true}，否则返回{@code false}。
     *
     * Returns {@code true} if the provided token credentials match the stored account credentials,
     * {@code false} otherwise.
     *
     * @param token   the {@code AuthenticationToken} submitted during the authentication attempt
     * @param info the {@code AuthenticationInfo} stored in the system.
     * @return {@code true} if the provided token credentials match the stored account credentials,
     *         {@code false} otherwise.
     */
    boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info);

}