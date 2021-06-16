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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 *
 提供所有标准Shiro请求过滤行为并期望子类实现特定于配置的逻辑（INI，XML，.properties等）的抽象基类。

 子类应在重写的init()方法实现中执行配置和构造逻辑。
 该实现应分别通过调用setSecurityManager(WebSecurityManager)和setFilterChainResolver(FilterChainResolver)方法
 来使任何构造的SecurityManager和FilterChainResolver可用。
 静态SecurityManager
 默认情况下，不会通过SecurityUtils.在静态内存中启用此过滤器启用的SecurityManager实例SecurityUtils. setSecurityManager方法。
 相反，期望Subject实例将始终通过此Filter类的实例构造在请求处理线程上。

 但是，如果您需要在单独的（非请求处理）线程上构造Subject实例，则最简单的方法是通过SecurityUtils.getSecurityManager()方法
 使SecurityUtils.getSecurityManager()在静态内存中可用。 您可以通过另外指定一个init-param来做到这一点：

 <filter>
 ... other config here ...
 <init-param>
 <param-name>staticSecurityManagerEnabled</param-name>
 <param-value>true</param-value>
 </init-param>
 </filter>

 *
 * Abstract base class that provides all standard Shiro request filtering behavior and expects
 * subclasses to implement configuration-specific logic (INI, XML, .properties, etc).
 * <p/>
 * Subclasses should perform configuration and construction logic in an overridden
 * {@link #init()} method implementation.  That implementation should make available any constructed
 * {@code SecurityManager} and {@code FilterChainResolver} by calling
 * {@link #setSecurityManager(org.apache.shiro.web.mgt.WebSecurityManager)} and
 * {@link #setFilterChainResolver(org.apache.shiro.web.filter.mgt.FilterChainResolver)} methods respectively.
 * <h3>Static SecurityManager</h3>
 * By default the {@code SecurityManager} instance enabled by this filter <em>will not</em> be enabled in static
 * memory via the {@code SecurityUtils.}{@link SecurityUtils#setSecurityManager(org.apache.shiro.mgt.SecurityManager) setSecurityManager}
 * method.  Instead, it is expected that Subject instances will always be constructed on a request-processing thread
 * via instances of this Filter class.
 * <p/>
 * However, if you need to construct {@code Subject} instances on separate (non request-processing) threads, it might
 * be easiest to enable the SecurityManager to be available in static memory via the
 * {@link SecurityUtils#getSecurityManager()} method.  You can do this by additionally specifying an {@code init-param}:
 * <pre>
 * &lt;filter&gt;
 *     ... other config here ...
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;staticSecurityManagerEnabled&lt;/param-name&gt;
 *         &lt;param-value&gt;true&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * &lt;/filter&gt;
 * </pre>
 * See the Shiro <a href="http://shiro.apache.org/subject.html">Subject documentation</a> for more information as to
 * if you would do this, particularly the sections on the {@code Subject.Builder} and Thread Association.
 *
 * @since 1.0
 * @see <a href="http://shiro.apache.org/subject.html">Subject documentation</a>
 */
public abstract class AbstractShiroFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AbstractShiroFilter.class);

    private static final String STATIC_INIT_PARAM_NAME = "staticSecurityManagerEnabled";

    // 对此过滤器使用的安全管理器的引用
    // Reference to the security manager used by this filter
    private WebSecurityManager securityManager;

    //用于确定哪个链应该处理传入的请求响应
    // Used to determine which chain should handle an incoming request/response
    private FilterChainResolver filterChainResolver;

    /**
     *
     * 是否将构造的SecurityManager实例绑定到静态内存（通过SecurityUtils.setSecurityManager）。
     * 添加此功能是为了支持https://issues.apache.org/jira/browse/SHIRO-287
     *
     * Whether or not to bind the constructed SecurityManager instance to static memory (via
     * SecurityUtils.setSecurityManager).  This was added to support https://issues.apache.org/jira/browse/SHIRO-287
     * @since 1.2
     */
    private boolean staticSecurityManagerEnabled;

    protected AbstractShiroFilter() {
        this.staticSecurityManagerEnabled = false;
    }

    public WebSecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(WebSecurityManager sm) {
        this.securityManager = sm;
    }

    public FilterChainResolver getFilterChainResolver() {
        return filterChainResolver;
    }

    public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
        this.filterChainResolver = filterChainResolver;
    }

    /**
     * Returns {@code true} if the constructed {@link #getSecurityManager() securityManager} reference should be bound
     * to static memory (via
     * {@code SecurityUtils.}{@link SecurityUtils#setSecurityManager(org.apache.shiro.mgt.SecurityManager) setSecurityManager}),
     * {@code false} otherwise.
     * <p/>
     * The default value is {@code false}.
     * <p/>
     *
     *
     * @return {@code true} if the constructed {@link #getSecurityManager() securityManager} reference should be bound
     *         to static memory (via {@code SecurityUtils.}{@link SecurityUtils#setSecurityManager(org.apache.shiro.mgt.SecurityManager) setSecurityManager}),
     *         {@code false} otherwise.
     * @since 1.2
     * @see <a href="https://issues.apache.org/jira/browse/SHIRO-287">SHIRO-287</a>
     */
    public boolean isStaticSecurityManagerEnabled() {
        return staticSecurityManagerEnabled;
    }

    /**
     * Sets if the constructed {@link #getSecurityManager() securityManager} reference should be bound
     * to static memory (via {@code SecurityUtils.}{@link SecurityUtils#setSecurityManager(org.apache.shiro.mgt.SecurityManager) setSecurityManager}).
     * <p/>
     * The default value is {@code false}.
     *
     * @param staticSecurityManagerEnabled if the constructed {@link #getSecurityManager() securityManager} reference
     *                                       should be bound to static memory (via
     *                                       {@code SecurityUtils.}{@link SecurityUtils#setSecurityManager(org.apache.shiro.mgt.SecurityManager) setSecurityManager}).
     * @since 1.2
     * @see <a href="https://issues.apache.org/jira/browse/SHIRO-287">SHIRO-287</a>
     */
    public void setStaticSecurityManagerEnabled(boolean staticSecurityManagerEnabled) {
        this.staticSecurityManagerEnabled = staticSecurityManagerEnabled;
    }

    /**
     * 这个方法很重要 ！ ！！
     * @throws Exception
     */
    protected final void onFilterConfigSet() throws Exception {
        //added in 1.2 for SHIRO-287:

        //设置
        applyStaticSecurityManagerEnabledConfig();
        //调用初始化方法
        init();


        ensureSecurityManager();

        //  applyStaticSecurityManagerEnabledConfig 与这个方法有关系
        //added in 1.2 for SHIRO-287:
        if (isStaticSecurityManagerEnabled()) {
            //设置到静态内存中
            SecurityUtils.setSecurityManager(getSecurityManager());
        }
    }

    /**
     * Checks if the init-param that configures the filter to use static memory has been configured, and if so,
     * sets the {@link #setStaticSecurityManagerEnabled(boolean)} attribute with the configured value.
     *
     * @since 1.2
     * @see <a href="https://issues.apache.org/jira/browse/SHIRO-287">SHIRO-287</a>
     */
    private void applyStaticSecurityManagerEnabledConfig() {
        String value = getInitParam(STATIC_INIT_PARAM_NAME);
        if (value != null) {
            boolean b = Boolean.parseBoolean(value);
            setStaticSecurityManagerEnabled(b);
        }
    }

    public void init() throws Exception {
    }

    /**
     * 一种在onFilterConfigSet()调用的后备机制，以确保已经通过配置设置了securityManager属性，如果没有，则自动创建一个
     *
     * A fallback mechanism called in {@link #onFilterConfigSet()} to ensure that the
     * {@link #getSecurityManager() securityManager} property has been set by configuration, and if not,
     * creates one automatically.
     */
    private void ensureSecurityManager() {
        WebSecurityManager securityManager = getSecurityManager();
        if (securityManager == null) {
            log.info("No SecurityManager configured.  Creating default.");


            // 创建默认的 安全管理器 ！
            securityManager = createDefaultSecurityManager();

            setSecurityManager(securityManager);
        }
    }

    /**
     *  创建默认的 Web安全管理器
     * @return
     */
    protected WebSecurityManager createDefaultSecurityManager() {
        return new DefaultWebSecurityManager();
    }

    protected boolean isHttpSessions() {
        return getSecurityManager().isHttpSessionMode();
    }

    /**
     * Wraps the original HttpServletRequest in a {@link ShiroHttpServletRequest}, which is required for supporting
     * Servlet Specification behavior backed by a {@link org.apache.shiro.subject.Subject Subject} instance.
     *
     * @param orig the original Servlet Container-provided incoming {@code HttpServletRequest} instance.
     * @return {@link ShiroHttpServletRequest ShiroHttpServletRequest} instance wrapping the original.
     * @since 1.0
     */
    protected ServletRequest wrapServletRequest(HttpServletRequest orig) {
        return new ShiroHttpServletRequest(orig, getServletContext(), isHttpSessions());
    }

    /**
     * Prepares the {@code ServletRequest} instance that will be passed to the {@code FilterChain} for request
     * processing.
     * <p/>
     * If the {@code ServletRequest} is an instance of {@link HttpServletRequest}, the value returned from this method
     * is obtained by calling {@link #wrapServletRequest(javax.servlet.http.HttpServletRequest)} to allow Shiro-specific
     * HTTP behavior, otherwise the original {@code ServletRequest} argument is returned.
     *
     * @param request  the incoming ServletRequest
     * @param response the outgoing ServletResponse
     * @param chain    the Servlet Container provided {@code FilterChain} that will receive the returned request.
     * @return the {@code ServletRequest} instance that will be passed to the {@code FilterChain} for request processing.
     * @since 1.0
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected ServletRequest prepareServletRequest(ServletRequest request, ServletResponse response, FilterChain chain) {
        ServletRequest toUse = request;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest http = (HttpServletRequest) request;
            toUse = wrapServletRequest(http);
        }
        return toUse;
    }

    /**
     * Returns a new {@link ShiroHttpServletResponse} instance, wrapping the {@code orig} argument, in order to provide
     * correct URL rewriting behavior required by the Servlet Specification when using Shiro-based sessions (and not
     * Servlet Container HTTP-based sessions).
     *
     * @param orig    the original {@code HttpServletResponse} instance provided by the Servlet Container.
     * @param request the {@code ShiroHttpServletRequest} instance wrapping the original request.
     * @return the wrapped ServletResponse instance to use during {@link FilterChain} execution.
     * @since 1.0
     */
    protected ServletResponse wrapServletResponse(HttpServletResponse orig, ShiroHttpServletRequest request) {
        return new ShiroHttpServletResponse(orig, getServletContext(), request);
    }

    /**
     * Prepares the {@code ServletResponse} instance that will be passed to the {@code FilterChain} for request
     * processing.
     * <p/>
     * This implementation delegates to {@link #wrapServletRequest(javax.servlet.http.HttpServletRequest)}
     * only if Shiro-based sessions are enabled (that is, !{@link #isHttpSessions()}) and the request instance is a
     * {@link ShiroHttpServletRequest}.  This ensures that any URL rewriting that occurs is handled correctly using the
     * Shiro-managed Session's sessionId and not a servlet container session ID.
     * <p/>
     * If HTTP-based sessions are enabled (the default), then this method does nothing and just returns the
     * {@code ServletResponse} argument as-is, relying on the default Servlet Container URL rewriting logic.
     *
     * @param request  the incoming ServletRequest
     * @param response the outgoing ServletResponse
     * @param chain    the Servlet Container provided {@code FilterChain} that will receive the returned request.
     * @return the {@code ServletResponse} instance that will be passed to the {@code FilterChain} during request processing.
     * @since 1.0
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected ServletResponse prepareServletResponse(ServletRequest request, ServletResponse response, FilterChain chain) {
        ServletResponse toUse = response;
        if (!isHttpSessions() && (request instanceof ShiroHttpServletRequest) &&
                (response instanceof HttpServletResponse)) {
            //the ShiroHttpServletResponse exists to support URL rewriting for session ids.  This is only needed if
            //using Shiro sessions (i.e. not simple HttpSession based sessions):
            toUse = wrapServletResponse((HttpServletResponse) response, (ShiroHttpServletRequest) request);
        }
        return toUse;
    }

    /**
     * Creates a {@link WebSubject} instance to associate with the incoming request/response pair which will be used
     * throughout the request/response execution.
     *
     * @param request  the incoming {@code ServletRequest}
     * @param response the outgoing {@code ServletResponse}
     * @return the {@code WebSubject} instance to associate with the request/response execution
     * @since 1.0
     */
    protected WebSubject createSubject(ServletRequest request, ServletResponse response) {
        return new WebSubject.Builder(getSecurityManager(), request, response).buildWebSubject();
    }

    /**
     * Updates any 'native'  Session's last access time that might exist to the timestamp when this method is called.
     * If native sessions are not enabled (that is, standard Servlet container sessions are being used) or there is no
     * session ({@code subject.getSession(false) == null}), this method does nothing.
     * <p/>This method implementation merely calls
     * <code>Session.{@link org.apache.shiro.session.Session#touch() touch}()</code> on the session.
     *
     * @param request  incoming request - ignored, but available to subclasses that might wish to override this method
     * @param response outgoing response - ignored, but available to subclasses that might wish to override this method
     * @since 1.0
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void updateSessionLastAccessTime(ServletRequest request, ServletResponse response) {
        if (!isHttpSessions()) { //'native' sessions
            Subject subject = SecurityUtils.getSubject();
            //Subject should never _ever_ be null, but just in case:
            if (subject != null) {
                Session session = subject.getSession(false);
                if (session != null) {
                    try {
                        session.touch();
                    } catch (Throwable t) {
                        log.error("session.touch() method invocation has failed.  Unable to update " +
                                "the corresponding session's last access time based on the incoming request.", t);
                    }
                }
            }
        }
    }

    /**
     * {@code doFilterInternal} implementation that sets-up, executes, and cleans-up a Shiro-filtered request.  It
     * performs the following ordered operations:
     * <ol>
     * <li>{@link #prepareServletRequest(ServletRequest, ServletResponse, FilterChain) Prepares}
     * the incoming {@code ServletRequest} for use during Shiro's processing</li>
     * <li>{@link #prepareServletResponse(ServletRequest, ServletResponse, FilterChain) Prepares}
     * the outgoing {@code ServletResponse} for use during Shiro's processing</li>
     * <li> {@link #createSubject(javax.servlet.ServletRequest, javax.servlet.ServletResponse) Creates} a
     * {@link Subject} instance based on the specified request/response pair.</li>
     * <li>Finally {@link Subject#execute(Runnable) executes} the
     * {@link #updateSessionLastAccessTime(javax.servlet.ServletRequest, javax.servlet.ServletResponse)} and
     * {@link #executeChain(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
     * methods</li>
     * </ol>
     * <p/>
     * The {@code Subject.}{@link Subject#execute(Runnable) execute(Runnable)} call in step #4 is used as an
     * implementation technique to guarantee proper thread binding and restoration is completed successfully.
     *
     * @param servletRequest  the incoming {@code ServletRequest}
     * @param servletResponse the outgoing {@code ServletResponse}
     * @param chain           the container-provided {@code FilterChain} to execute
     * @throws IOException                    if an IO error occurs
     * @throws javax.servlet.ServletException if an Throwable other than an IOException
     */
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain chain)
            throws ServletException, IOException {

        Throwable t = null;

        try {
            final ServletRequest request = prepareServletRequest(servletRequest, servletResponse, chain);
            final ServletResponse response = prepareServletResponse(request, servletResponse, chain);

            final Subject subject = createSubject(request, response);

            //noinspection unchecked
            subject.execute(new Callable() {
                public Object call() throws Exception {
                    updateSessionLastAccessTime(request, response);
                    executeChain(request, response, chain);
                    return null;
                }
            });
        } catch (ExecutionException ex) {
            t = ex.getCause();
        } catch (Throwable throwable) {
            t = throwable;
        }

        if (t != null) {
            if (t instanceof ServletException) {
                throw (ServletException) t;
            }
            if (t instanceof IOException) {
                throw (IOException) t;
            }
            //otherwise it's not one of the two exceptions expected by the filter method signature - wrap it in one:
            String msg = "Filtered request failed.";
            throw new ServletException(msg, t);
        }
    }

    /**
     * Returns the {@code FilterChain} to execute for the given request.
     * <p/>
     * The {@code origChain} argument is the
     * original {@code FilterChain} supplied by the Servlet Container, but it may be modified to provide
     * more behavior by pre-pending further chains according to the Shiro configuration.
     * <p/>
     * This implementation returns the chain that will actually be executed by acquiring the chain from a
     * {@link #getFilterChainResolver() filterChainResolver}.  The resolver determines exactly which chain to
     * execute, typically based on URL configuration.  If no chain is returned from the resolver call
     * (returns {@code null}), then the {@code origChain} will be returned by default.
     *
     * @param request   the incoming ServletRequest
     * @param response  the outgoing ServletResponse
     * @param origChain the original {@code FilterChain} provided by the Servlet Container
     * @return the {@link FilterChain} to execute for the given request
     * @since 1.0
     */
    protected FilterChain getExecutionChain(ServletRequest request, ServletResponse response, FilterChain origChain) {

        FilterChain chain = origChain;

        FilterChainResolver resolver = getFilterChainResolver();
        if (resolver == null) {
            log.debug("No FilterChainResolver configured.  Returning original FilterChain.");
            return origChain;
        }

        // 解析过滤器链  很重要 ！
        FilterChain resolved = resolver.getChain(request, response, origChain);
        if (resolved != null) {
            log.trace("Resolved a configured FilterChain for the current request.");
            chain = resolved;
        } else {
            log.trace("No FilterChain configured for the current request.  Using the default.");
        }

        return chain;
    }

    /**
     * Executes a {@link FilterChain} for the given request.
     * <p/>
     * This implementation first delegates to
     * <code>{@link #getExecutionChain(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) getExecutionChain}</code>
     * to allow the application's Shiro configuration to determine exactly how the chain should execute.  The resulting
     * value from that call is then executed directly by calling the returned {@code FilterChain}'s
     * {@link FilterChain#doFilter doFilter} method.  That is:
     * <pre>
     * FilterChain chain = {@link #getExecutionChain}(request, response, origChain);
     * chain.{@link FilterChain#doFilter doFilter}(request,response);</pre>
     *
     * @param request   the incoming ServletRequest
     * @param response  the outgoing ServletResponse
     * @param origChain the Servlet Container-provided chain that may be wrapped further by an application-configured
     *                  chain of Filters.
     * @throws IOException      if the underlying {@code chain.doFilter} call results in an IOException
     * @throws ServletException if the underlying {@code chain.doFilter} call results in a ServletException
     * @since 1.0
     */
    protected void executeChain(ServletRequest request, ServletResponse response, FilterChain origChain)
            throws IOException, ServletException {
        FilterChain chain = getExecutionChain(request, response, origChain);
        chain.doFilter(request, response);
    }
}
