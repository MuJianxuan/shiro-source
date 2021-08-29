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
package org.apache.shiro.web.filter;

import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.shiro.lang.util.StringUtils.split;

/**
 * 过滤器的基类，将仅处理指定的路径并允许所有其他路径通过
 *
 * <p>Base class for Filters that will process only specified paths and allow all others to pass through.</p>
 *
 * @since 0.9
 */
public abstract class PathMatchingFilter extends AdviceFilter implements PathConfigProcessor {

    /**
     * Log available to this class only
     */
    private static final Logger log = LoggerFactory.getLogger(PathMatchingFilter.class);

    private static final String DEFAULT_PATH_SEPARATOR = "/";

    /**
     * PatternMatcher used in determining which paths to react to for a given request.
     */
    protected PatternMatcher pathMatcher = new AntPathMatcher();

    /**
     * 配置路径条目的集合，其中键是此过滤器应处理的路径，值是该过滤器特定于该特定路径的配置元素（可能为null）。
     *
     * 换句话说，键是此筛选器将处理的路径（URL）。
     * 值是特定于过滤器的数据，此过滤器在处理相应的键（路径）时应使用该数据。 如果没有为该URL指定特定于过滤器的配置，则这些值可以为null。
     *
     * A collection of path-to-config entries where the key is a path which this filter should process and
     * the value is the (possibly null) configuration element specific to this Filter for that specific path.
     * <p/>
     * <p>To put it another way, the keys are the paths (urls) that this Filter will process.
     * <p>The values are filter-specific data that this Filter should use when processing the corresponding
     * key (path).  The values can be null if no Filter-specific config was specified for that url.
     */
    protected Map<String, Object> appliedPaths = new LinkedHashMap<String, Object>();

    /**
     * 拆分可能在config参数中找到的任何逗号分隔值，并在appliedPaths内部映射上设置生成的String[]数组。
     *
     * Splits any comma-delmited values that might be found in the <code>config</code> argument and sets the resulting
     * <code>String[]</code> array on the <code>appliedPaths</code> internal Map.
     * <p/>
     * That is:
     * <pre><code>
     * String[] values = null;
     * if (config != null) {
     *     values = split(config);
     * }
     * <p/>
     * this.{@link #appliedPaths appliedPaths}.put(path, values);
     * </code></pre>
     *
     * @param path   the application context path to match for executing this filter.
     * @param config the specified for <em>this particular filter only</em> for the given <code>path</code>
     * @return this configured filter.
     */
    public Filter processPathConfig(String path, String config) {
        String[] values = null;
        if (config != null) {
            values = split(config);
        }

        this.appliedPaths.put(path, values);
        return this;
    }

    /**
     * 根据指定的request返回应用程序中的上下文路径。
     *
     * Returns the context path within the application based on the specified <code>request</code>.
     * <p/>
     * This implementation merely delegates to
     * {@link WebUtils#getPathWithinApplication(javax.servlet.http.HttpServletRequest) WebUtils.getPathWithinApplication(request)},
     * but can be overridden by subclasses for custom logic.
     *
     * @param request the incoming <code>ServletRequest</code>
     * @return the context path within the application.
     */
    protected String getPathWithinApplication(ServletRequest request) {
        return WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
    }

    /**
     *
     * 路径匹配
     *
     *  路径匹配如果传入request与指定的path模式匹配，则返回true ，否则返回false 。
     *
     * 默认实现在应用程序内获取request的路径，并确定是否与以下条件匹配
     *
     * Returns <code>true</code> if the incoming <code>request</code> matches the specified <code>path</code> pattern,
     * <code>false</code> otherwise.
     * <p/>
     * The default implementation acquires the <code>request</code>'s path within the application and determines
     * if that matches:
     * <p/>
     * <code>String requestURI = {@link #getPathWithinApplication(javax.servlet.ServletRequest) getPathWithinApplication(request)};<br/>
     * return {@link #pathsMatch(String, String) pathsMatch(path,requestURI)}</code>
     *
     * @param path    the configured url pattern to check the incoming request against.
     *                配置的 url 模式来检查传入的请求。
     * @param request the incoming ServletRequest
     *                当前请求
     * @return <code>true</code> if the incoming <code>request</code> matches the specified <code>path</code> pattern,
     *         <code>false</code> otherwise.
     */
    protected boolean pathsMatch(String path, ServletRequest request) {

        // 可能是这个意思，可以留意具体的初始化  filter
        // path 我认为啊，path实际就是配置的过滤拦截器，每一个请求是否需要被拦截，是否处于放行的路径中  比如我们登录的接口，肯定是不需要认证就可访问的。

        // 获取请求路径
        String requestUri = getPathWithinApplication(request);

        // 正在尝试将模式“{}”与当前 requestUri“{}”匹配...
        log.trace("Attempting to match pattern '{}' with current requestUri '{}'...", path, Encode.forHtml(requestUri));

        // 匹配路径 当前请求的路径
        boolean match = pathsMatch(path, requestUri);

        if (! match) {
            //   / != 请求路径  即根路径， and  请求路径尾 != /
            if (requestUri != null && ! DEFAULT_PATH_SEPARATOR.equals(requestUri) && requestUri.endsWith(DEFAULT_PATH_SEPARATOR)) {
                 // 则 长度减一
                // http://localhost:8080/hello/
                requestUri = requestUri.substring(0, requestUri.length() - 1);
            }
            // 且 / != path  and path尾 = /
            if (path != null && ! DEFAULT_PATH_SEPARATOR.equals(path) && path.endsWith(DEFAULT_PATH_SEPARATOR)) {
                // 同理 目的是把 path尾的 / 干掉
                path = path.substring(0, path.length() - 1);
            }
            // 正在尝试将模式“{}”与当前 requestUri“{}”匹配...
            log.trace("Attempting to match pattern '{}' with current requestUri '{}'...", path, Encode.forHtml(requestUri));

            // 再次匹配
            match = pathsMatch(path, requestUri);
        }

        return match;
    }

    /**
     * 如果path与指定的pattern字符串匹配，则返回true ，否则返回false 。
     *
     * 简单地委托给this.pathMatcher. matches(pattern,path) this.pathMatcher.
     * matches(pattern,path) ，但可以被子类覆盖以实现自定义匹配行为。
     *
     * Returns <code>true</code> if the <code>path</code> matches the specified <code>pattern</code> string,
     * <code>false</code> otherwise.
     * <p/>
     * Simply delegates to
     * <b><code>this.pathMatcher.{@link PatternMatcher#matches(String, String) matches(pattern,path)}</code></b>,
     * but can be overridden by subclasses for custom matching behavior.
     *
     * @param pattern the pattern to match against
     * @param path    the value to match with the specified <code>pattern</code>
     * @return <code>true</code> if the <code>path</code> matches the specified <code>pattern</code> string,
     *         <code>false</code> otherwise.
     */
    protected boolean pathsMatch(String pattern, String path) {
        // 匹配性能会不会偏慢
        boolean matches = pathMatcher.matches(pattern, path);
        log.trace("Pattern [{}] matches path [{}] => [{}]", pattern, path, matches);
        return matches;
    }

    /**
     *
     *  父类模板定义方法
     *
     *    前置处理 !
     *
     * Implementation that handles path-matching behavior before a request is evaluated.  If the path matches and
     * the filter
     * {@link #isEnabled(javax.servlet.ServletRequest, javax.servlet.ServletResponse, String, Object) isEnabled} for
     * that path/config, the request will be allowed through via the result from
     * {@link #onPreHandle(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object) onPreHandle}.  If the
     * path does not match or the filter is not enabled for that path, this filter will allow passthrough immediately
     * to allow the {@code FilterChain} to continue executing.
     * <p/>
     * In order to retain path-matching functionality, subclasses should not override this method if at all
     * possible, and instead override
     * {@link #onPreHandle(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object) onPreHandle} instead.
     *
     * @param request  the incoming ServletRequest
     * @param response the outgoing ServletResponse
     * @return {@code true} if the filter chain is allowed to continue to execute, {@code false} if a subclass has
     *         handled the request explicitly.
     * @throws Exception if an error occurs
     */
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        if (this.appliedPaths == null || this.appliedPaths.isEmpty()) {
            if (log.isTraceEnabled()) {
                // AppliedPaths 属性为 null 或为空。此过滤器将立即通过。
                log.trace("appliedPaths property is null or empty.  This Filter will passthrough immediately.");
            }
            return true;
        }

        // 路径是否过滤 对吧 !
        for (String path : this.appliedPaths.keySet()) {
            // 如果路径匹配，则传递给子类实现以进行特定检查

            // If the path does match, then pass on to the subclass implementation for specific checks
            //(first match 'wins'):

            // 路径匹配  为什么路径匹配要执行  难道是反过来的意思， 不属于放行的才需要执行下面的方法
            if ( pathsMatch(path, request)) {
                log.trace("Current requestURI matches pattern '{}'.  Determining filter chain execution...", path);
                Object config = this.appliedPaths.get(path);
                // 匹配功能
                // 是 继续过滤链
                return isFilterChainContinued(request, response, path, config);
            }
        }

        //no path matched, allow the request to go through:
        return true;
    }

    /**
     * 从 preHandle 实现中抽象出逻辑的简单方法 - 它变得有点不守规矩。
     *
     * Simple method to abstract out logic from the preHandle implementation - it was getting a bit unruly.
     * 从 preHandle 实现中抽象出逻辑的简单方法 - 它变得有点不守规矩。
     *
     * @since 1.2
     */
    @SuppressWarnings({"JavaDoc"})
    private boolean isFilterChainContinued(ServletRequest request, ServletResponse response, String path, Object pathConfig) throws Exception {

        if (isEnabled(request, response, path, pathConfig)) { //isEnabled check added in 1.2
            if (log.isTraceEnabled()) {
                // 使用配置 [{}] 为路径“{}”下的当前请求启用过滤器“{}”。 " + "委托给子类实现以进行 'onPreHandle' 检查。
                log.trace("Filter '{}' is enabled for the current request under path '{}' with config [{}].  " +
                        "Delegating to subclass implementation for 'onPreHandle' check.",
                        getName(), path, pathConfig);
            }

            //对此特定请求启用了过滤器，因此可以委托子类实现，以便他们可以确定请求是否应继续通过链：

            // 预处理
            //过滤器已为此特定请求启用，因此委托给子类实现
            //所以他们可以决定请求是否应该继续通过链：
            //The filter is enabled for this specific request, so delegate to subclass implementations
            //so they can decide if the request should continue through the chain or not:
            return onPreHandle(request, response, pathConfig);
        }

        if (log.isTraceEnabled()) {
            // 使用配置 [{}] 对路径“{}”下的当前请求禁用过滤器“{}”。 " + "FilterChain 中的下一个元素将被立即调用。
            log.trace("Filter '{}' is disabled for the current request under path '{}' with config [{}].  " +
                    "The next element in the FilterChain will be called immediately.",
                    getName(), path, pathConfig);
        }

        //该过滤器对此特定请求禁用，请立即返回“ true”以指示该过滤器将不处理该请求，并让requestresponse继续通过过滤器链：

        // 对于此特定请求，此过滤器被禁用，立即返回 'true' 以指示过滤器将不处理该请求并让 requestresponse 继续通过过滤器链：

        //This filter is disabled for this specific request,
        //return 'true' immediately to indicate that the filter will not process the request
        //and let the request/response to continue through the filter chain:
        return true;
    }

    /**
     * 处于 预处理 流程中
     *
     * 此默认实现始终返回true并且在必要时应被自定义逻辑的子类覆盖
     *
     * This default implementation always returns {@code true} and should be overridden by subclasses for custom
     * logic if necessary.
     *
     * @param request     the incoming ServletRequest
     * @param response    the outgoing ServletResponse
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings.
     *                    URL规则映射中映射到此过滤器的特定于过滤器的配置值。
     *
     * @return {@code true} if the request should be able to continue, {@code false} if the filter will
     *         handle the response directly.
     * @throws Exception if an error occurs
     * @see #isEnabled(javax.servlet.ServletRequest, javax.servlet.ServletResponse, String, Object)
     */
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return true;
    }

    /**
     * 父类的isEnabled(ServletRequest, ServletResponse)方法的路径匹配版本，但另外允许检查与指定请求对应的任何特定于路径的配置值。
     * 子类可能希望检查这个附加的映射配置以确定是否启用了过滤器。
     *
     * 此方法的默认实现忽略path和mappedValue值参数，仅返回调用isEnabled(ServletRequest, ServletResponse) 。
     * 如果子类需要根据过滤器实例的任何特定于路径的配置为特定请求执行启用/禁用逻辑，则期望子类覆盖此方法。
     *
     * Path-matching version of the parent class's
     * {@link #isEnabled(javax.servlet.ServletRequest, javax.servlet.ServletResponse)} method, but additionally allows
     * for inspection of any path-specific configuration values corresponding to the specified request.  Subclasses
     * may wish to inspect this additional mapped configuration to determine if the filter is enabled or not.
     * <p/>
     * This method's default implementation ignores the {@code path} and {@code mappedValue} arguments and merely
     * returns the value from a call to {@link #isEnabled(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}.
     * It is expected that subclasses override this method if they need to perform enable/disable logic for a specific
     * request based on any path-specific config for the filter instance.
     *
     * @param request     the incoming servlet request
     * @param response    the outbound servlet response
     * @param path        the path matched for the incoming servlet request that has been configured with the given {@code mappedValue}.
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings for the given {@code path}.
     * @return {@code true} if this filter should filter the specified request, {@code false} if it should let the
     *         request/response pass through immediately to the next element in the {@code FilterChain}.
     * @throws Exception in the case of any error
     * @since 1.2
     */
    @SuppressWarnings({"UnusedParameters"})
    protected boolean isEnabled(ServletRequest request, ServletResponse response, String path, Object mappedValue)
            throws Exception {
        return isEnabled(request, response);
    }
}
