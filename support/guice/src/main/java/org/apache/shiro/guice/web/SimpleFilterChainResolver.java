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
package org.apache.shiro.guice.web;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Injector;
import com.google.inject.Key;

import org.apache.shiro.util.PatternMatcher;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.util.WebUtils;

class SimpleFilterChainResolver implements FilterChainResolver {
    private final Map<String, Key<? extends Filter>[]> chains;
    private final Injector injector;
    private final PatternMatcher patternMatcher;

    SimpleFilterChainResolver(Map<String, Key<? extends Filter>[]> chains, Injector injector, PatternMatcher patternMatcher) {
        this.chains = chains;
        this.injector = injector;
        this.patternMatcher = patternMatcher;
    }

    public FilterChain getChain(ServletRequest request, ServletResponse response, final FilterChain originalChain) {

        String path = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));

        // 链
        for (final String pathPattern : chains.keySet()) {

            if (patternMatcher.matches(pathPattern, path)) {

                final Iterator<Key<? extends Filter>> chain = Arrays.asList(chains.get(pathPattern)).iterator();
                return new SimpleFilterChain(originalChain, new Iterator<Filter>() {
                    public boolean hasNext() {
                        return chain.hasNext();
                    }

                    public Filter next() {
                        return injector.getInstance(chain.next());
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                });
            }
        }
        return null;
    }

}
