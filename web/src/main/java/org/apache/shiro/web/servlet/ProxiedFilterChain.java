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
package org.apache.shiro.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

/**
 *
 * 代理过滤器链是一个FilterChain实例，它代理原始FilterChain以及可能需要在最终包装的原始链之前执行的其他Filter的List 。
 * 它允许在继续原始（代理） FilterChain实例之前执行过滤器列表
 *
 * A proxied filter chain is a {@link FilterChain} instance that proxies an original {@link FilterChain} as well
 * as a {@link List List} of other {@link Filter Filter}s that might need to execute prior to the final wrapped
 * original chain.  It allows a list of filters to execute before continuing the original (proxied)
 * {@code FilterChain} instance.
 *
 * @since 0.9
 */
public class ProxiedFilterChain implements FilterChain {

    //TODO - complete JavaDoc

    private static final Logger log = LoggerFactory.getLogger(ProxiedFilterChain.class);

    private FilterChain orig;
    private List<Filter> filters;
    private int index = 0;

    public ProxiedFilterChain(FilterChain orig, List<Filter> filters) {
        if (orig == null) {
            throw new NullPointerException("original FilterChain cannot be null.");
        }
        //而这个 则是 框架的 Servlet的
        this.orig = orig;
        // 这个过滤器集合是我们自己的
        this.filters = filters;
        this.index = 0;
    }

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

        // 巧妙思路
          // 最终要保证会执行  框架的过滤链

        if (this.filters == null || this.filters.size() == this.index) {
            //we've reached the end of the wrapped chain, so invoke the original one:
            if (log.isTraceEnabled()) {
                log.trace("Invoking original filter chain.");
            }

            this.orig.doFilter(request, response);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Invoking wrapped filter at index [" + this.index + "]");
            }

            // 每执行一次则 过滤器的坐标 +1
            this.filters.get(this.index++).doFilter(request, response, this);
        }
    }
}
