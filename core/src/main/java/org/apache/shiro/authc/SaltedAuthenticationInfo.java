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

import org.apache.shiro.lang.util.ByteSource;

/**
 *
 *  盐值接口
 *
 *  这个账户需要 对凭证进行 盐值处理
 *
 *  表示帐户信息的接口，在哈希凭证时可能会使用盐。
 *  此接口主要用于支持散列用户凭据（例如密码）的环境。
 *
 * Interface representing account information that may use a salt when hashing credentials.  This interface
 * exists primarily to support environments that hash user credentials (e.g. passwords).
 * <p/>
 * Salts should typically be generated from a secure pseudo-random number generator so they are effectively
 * impossible to guess.  The salt value should be safely stored along side the account information to ensure
 * it is maintained along with the account's credentials.
 * <p/>
 * This interface exists as a way for Shiro to acquire that salt so it can correctly perform
 * {@link org.apache.shiro.authc.credential.CredentialsMatcher credentials matching} during login attempts.
 * See the {@link org.apache.shiro.authc.credential.HashedCredentialsMatcher HashedCredentialsMatcher} JavaDoc for
 * more information on hashing credentials with salts.
 *
 * @see org.apache.shiro.authc.credential.HashedCredentialsMatcher
 *
 * @since 1.1
 */
public interface SaltedAuthenticationInfo extends AuthenticationInfo {

    /**
     *
     *  返回用于对帐户凭据进行加盐的盐，如果未使用盐，则返回{@code null}。
     *
     * Returns the salt used to salt the account's credentials or {@code null} if no salt was used.
     *
     * @return the salt used to salt the account's credentials or {@code null} if no salt was used.
     */
    ByteSource getCredentialsSalt();
}
