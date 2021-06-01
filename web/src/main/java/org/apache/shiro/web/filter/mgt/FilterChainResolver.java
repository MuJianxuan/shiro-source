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
package org.apache.shiro.web.filter.mgt;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * FilterChainResolver可以解析适当的FilterChain以在ServletRequest期间执行。
 *   它允许解析可以针对任何给定请求或URI / URL执行的任意过滤器链。
 *
 * 与普通的web.xml servlet过滤器定义相比，此机制提供了更灵活的FilterChain解析：
 *   它允许以更简洁和易于阅读的方式为每个URL定义任意过滤器链，甚至允许动态地解析或过滤过滤器链。 如果基础实现支持，则在运行时构造它
 *
 * A {@code FilterChainResolver} can resolve an appropriate {@link FilterChain} to execute during a
 * {@code ServletRequest}.  It allows resolution of arbitrary filter chains which can be executed for any given
 * request or URI/URL.
 * <p/>
 * This mechanism allows for a much more flexible FilterChain resolution than normal {@code web.xml} servlet filter
 * definitions:  it allows arbitrary filter chains to be defined per URL in a much more concise and easy to read manner,
 * and even allows filter chains to be dynamically resolved or constructed at runtime if the underlying implementation
 * supports it.
 *
 * @since 1.0
 */
public interface FilterChainResolver {

    /**
     *
     * 返回应针对给定请求执行的过滤器链，如果应使用原始链，则返回{@code null}
     *
     * Returns the filter chain that should be executed for the given request, or {@code null} if the
     * original chain should be used.
     * <p/>
     * This method allows a implementation to define arbitrary security {@link javax.servlet.Filter Filter}
     * chains for any given request or URL pattern.
     *
     * @param request       the incoming ServletRequest
     * @param response      the outgoing ServletResponse
     * @param originalChain the original {@code FilterChain} intercepted by the ShiroFilter implementation.
     * @return the filter chain that should be executed for the given request, or {@code null} if the
     *         original chain should be used.
     */
    FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain originalChain);

}
