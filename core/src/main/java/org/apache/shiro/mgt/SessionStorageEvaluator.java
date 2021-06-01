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

import org.apache.shiro.subject.Subject;

/**
 *
 * 一个session 是否持久化都要写个接口来判断！
 *
 *  评估Shiro是否可以使用Subject的Session来保持Subject的内部状态。
 *
 * 使用主题的会话来保留主题的身份和身份验证状态（例如，登录后）是一种常见的Shiro实现策略，这样就无需为任何其他请求/调用传递信息。
 * 这有效地允许将会话ID用于任何请求或调用，作为Shiro唯一需要的“指针”，并且Shiro可以从中基于引用的Session重新创建Subject实例。
 *
 * 但是，在纯粹的无状态应用程序中，例如某些REST应用程序或对每个请求进行身份验证的应用程序，
 * 通常不需要或不希望使用Session存储此状态（因为实际上是在每个请求上都重新创建了该状态）。 在这些应用程序中，将永远不会使用会话。
 *
 * 该接口允许实现精确地确定当会话可能使用或不保存Subject在每个主题的基础状态。
 *
 * 如果您只是希望在全局级别上为所有Subject启用或禁用会话使用，则DefaultSessionStorageEvaluator应该足够了。 每个对象的行为应在此接口的自定义实现中执行
 *
 * Evaluates whether or not Shiro may use a {@code Subject}'s {@link org.apache.shiro.session.Session Session}
 * to persist that {@code Subject}'s internal state.
 * <p/>
 * It is a common Shiro implementation strategy to use a Subject's session to persist the Subject's identity and
 * authentication state (e.g. after login) so that information does not need to be passed around for any further
 * requests/invocations.  This effectively allows a session id to be used for any request or invocation as the only
 * 'pointer' that Shiro needs, and from that, Shiro can re-create the Subject instance based on the referenced Session.
 * <p/>
 * However, in purely stateless applications, such as some REST applications or those where every request is
 * authenticated, it is usually not needed or desirable to use Sessions to store this state (since it is in
 * fact re-created on every request).  In these applications, sessions would never be used.
 * <p/>
 * This interface allows implementations to determine exactly when a Session might be used or not to store
 * {@code Subject} state on a <em>per-Subject</em> basis.
 * <p/>
 * If you simply wish to enable or disable session usage at a global level for all {@code Subject}s, the
 * {@link DefaultSessionStorageEvaluator} should be sufficient.  Per-subject behavior should be performed in custom
 * implementations of this interface.
 *
 * @see Subject#getSession()
 * @see Subject#getSession(boolean)
 * @see DefaultSessionStorageEvaluator
 * @since 1.2
 *
 * 评估Shiro是否可以使用Subject的Session来保持Subject的内部状态。 这句话要怎么理解
 *
 *     功能接口 ： session是可持久化的?  返回 布尔
 *
 */
public interface SessionStorageEvaluator {

    /**
     * 启用了会话存储
     *
     * Returns {@code true} if the specified {@code Subject}'s
     * {@link org.apache.shiro.subject.Subject#getSession() session} may be used to persist that Subject's
     * state, {@code false} otherwise.
     *
     * @param subject the {@code Subject} for which session state persistence may be enabled
     * @return {@code true} if the specified {@code Subject}'s
     *         {@link org.apache.shiro.subject.Subject#getSession() session} may be used to persist that Subject's
     *         state, {@code false} otherwise.
     * @see Subject#getSession()
     * @see Subject#getSession(boolean)
     */
    boolean isSessionStorageEnabled(Subject subject);

}
