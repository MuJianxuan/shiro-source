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
package org.apache.shiro.web.filter.authz;

import org.apache.shiro.lang.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A filter that translates an HTTP Request's Method (eg GET, POST, etc)
 * into an corresponding action (verb) and uses that verb to construct a permission that will be checked to determine
 * access.
 * <p/>
 * This Filter is primarily provided to support REST environments where the type (Method)
 * of request translates to an action being performed on one or more resources.  This paradigm works well with Shiro's
 * concepts of using permissions for access control and can be leveraged to easily perform permission checks.
 * <p/>
 * This filter functions as follows:
 * <ol>
 * <li>The incoming HTTP request's Method (GET, POST, PUT, DELETE, etc) is discovered.</li>
 * <li>The Method is translated into a more 'application friendly' verb, such as 'create', edit', 'delete', etc.</li>
 * <li>The verb is appended to any configured permissions for the
 * {@link org.apache.shiro.web.filter.PathMatchingFilter currently matching path}.</li>
 * <li>If the current {@code Subject} {@link org.apache.shiro.subject.Subject#isPermitted(String) isPermitted} to
 * perform the resolved action, the request is allowed to continue.</li>
 * </ol>
 * <p/>
 * For example, if the following filter chain was defined, where 'rest' was the name given to a filter instance of
 * this class:
 * <pre>
 * /user/** = rest[user]</pre>
 * Then an HTTP {@code GET} request to {@code /user/1234} would translate to the constructed permission
 * {@code user:read} (GET is mapped to the 'read' action) and execute the permission check
 * <code>Subject.isPermitted(&quot;user:read&quot;)</code> in order to allow the request to continue.
 * <p/>
 * Similarly, an HTTP {@code POST} to {@code /user} would translate to the constructed permission
 * {@code user:create} (POST is mapped to the 'create' action) and execute the permission check
 * <code>Subject.isPermitted(&quot;user:create&quot;)</code> in order to allow the request to continue.
 * <p/>
 * <h3>Method To Verb Mapping</h3>
 * The following table represents the default HTTP Method-to-action verb mapping:
 * <table>
 * <tr><th>HTTP Method</th><th>Mapped Action</th><th>Example Permission</th><th>Runtime Check</th></tr>
 * <tr><td>head</td><td>read</td><td>perm1</td><td>perm1:read</td></tr>
 * <tr><td>get</td><td>read</td><td>perm2</td><td>perm2:read</td></tr>
 * <tr><td>put</td><td>update</td><td>perm3</td><td>perm3:update</td></tr>
 * <tr><td>post</td><td>create</td><td>perm4</td><td>perm4:create</td></tr>
 * <tr><td>mkcol</td><td>create</td><td>perm5</td><td>perm5:create</td></tr>
 * <tr><td>options</td><td>read</td><td>perm6</td><td>perm6:read</td></tr>
 * <tr><td>trace</td><td>read</td><td>perm7</td><td>perm7:read</td></tr>
 * </table>
 *
 * @since 1.0
 */
public class HttpMethodPermissionFilter extends PermissionsAuthorizationFilter {

    /**
     * 一个过滤器，它将HTTP请求的方法（例如GET，POST等）转换为相应的动作（动词），并使用该动词来构造将被检查以确定访问权限的权限。
     *
     * 提供此过滤器主要是为了支持REST环境，在该环境中，请求的类型（方法）转换为对一个或多个资源执行的操作。
     * 此范例与Shiro的使用权限进行访问控制的概念很好地配合使用，可以轻松地执行权限检查。
     *
     * 该过滤器的功能如下：
     * 发现传入的HTTP请求的方法（GET，POST，PUT，DELETE等）。
     * 该方法被翻译成更“易于应用”的动词，例如“创建”，“编辑”，“删除”等。
     * 该动词将附加到currently matching path任何已配置权限。
     * 如果当前Subject isPermitted进行解析动作，请求被允许继续。
     */


    /**
     * This class's private logger.
     */
    private static final Logger log = LoggerFactory.getLogger(HttpMethodPermissionFilter.class);

    /**
     * Map that contains a mapping between http methods to permission actions (verbs)
     */
    private final Map<String, String> httpMethodActions = new HashMap<String, String>();

    //Actions representing HTTP Method values (GET -> read, POST -> create, etc)
    private static final String CREATE_ACTION = "create";
    private static final String READ_ACTION = "read";
    private static final String UPDATE_ACTION = "update";
    private static final String DELETE_ACTION = "delete";

    /**
     * 定义明确的映射值的常量的枚举。 在Filter的构造函数中使用，以执行运行时使用的地图实例。
     *
     * Enum of constants for well-defined mapping values.  Used in the Filter's constructor to perform the map instance
     * used at runtime.
     */
    private static enum HttpMethodAction {

        DELETE(DELETE_ACTION),
        GET(READ_ACTION),
        HEAD(READ_ACTION),
        MKCOL(CREATE_ACTION), //webdav, but useful here
        OPTIONS(READ_ACTION),
        POST(CREATE_ACTION),
        PUT(UPDATE_ACTION),
        TRACE(READ_ACTION);

        private final String action;

        private HttpMethodAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return this.action;
        }
    }

    /**
     * Creates the filter instance with default method-to-action values in the instance's
     * {@link #getHttpMethodActions() http method actions map}.
     */
    public HttpMethodPermissionFilter() {
        for (HttpMethodAction methodAction : HttpMethodAction.values()) {
            httpMethodActions.put(methodAction.name().toLowerCase(), methodAction.getAction());
        }
    }

    /**
     * Returns the HTTP Method name (key) to action verb (value) mapping used to resolve actions based on an
     * incoming {@code HttpServletRequest}.  All keys and values are lower-case.  The
     * default key/value pairs are defined in the top class-level JavaDoc.
     *
     * @return the HTTP Method lower-case name (key) to lower-case action verb (value) mapping
     */
    protected Map<String, String> getHttpMethodActions() {
        return this.httpMethodActions;
    }

    /**
     * Determines the action (verb) attempting to be performed on the filtered resource by the current request.
     * <p/>
     * This implementation expects the incoming request to be an {@link HttpServletRequest} and returns a mapped
     * action based on the HTTP request {@link javax.servlet.http.HttpServletRequest#getMethod() method}.
     *
     * @param request to pull the method from.
     * @return The string equivalent verb of the http method.
     */
    protected String getHttpMethodAction(ServletRequest request) {
        String method = ((HttpServletRequest) request).getMethod();
        return getHttpMethodAction(method);
    }

    /**
     * Determines the corresponding application action that will be performed on the filtered resource based on the
     * specified HTTP method (GET, POST, etc).
     *
     * @param method to be translated into the verb.
     * @return The string equivalent verb of the method.
     */
    protected String getHttpMethodAction(String method) {
        String lc = method.toLowerCase();
        String resolved = getHttpMethodActions().get(lc);
        return resolved != null ? resolved : method;
    }

    /**
     * Returns a collection of String permissions with which to perform a permission check to determine if the filter
     * will allow the request to continue.
     * <p/>
     * This implementation merely delegates to {@link #buildPermissions(String[], String)} and ignores the inbound
     * HTTP servlet request, but it can be overridden by subclasses for more complex request-specific building logic
     * if necessary.
     *
     * @param request         the inbound HTTP request - ignored in this implementation, but available to
     *                        subclasses for more complex construction building logic if necessary
     * @param configuredPerms any url-specific permissions mapped to this filter in the URL rules mappings.
     * @param action          the application-friendly action (verb) resolved based on the HTTP Method name.
     * @return a collection of String permissions with which to perform a permission check to determine if the filter
     *         will allow the request to continue.
     */
    protected String[] buildPermissions(HttpServletRequest request, String[] configuredPerms, String action) {
        return buildPermissions(configuredPerms, action);
    }

    /**
     * 根据原始参数构建新的权限字符串数组，并根据WildcardPermission约定将指定的动作动词附加到每个动作动词之后。
     * 内置的权限字符串将是在权限检查期间在运行时使用的字符串，用于确定是否应允许继续进行过滤器访问。
     *
     * Builds a new array of permission strings based on the original argument, appending the specified action verb
     * to each one per {@link org.apache.shiro.authz.permission.WildcardPermission WildcardPermission} conventions.  The
     * built permission strings will be the ones used at runtime during the permission check that determines if filter
     * access should be allowed to continue or not.
     * <p/>
     * For example, if the {@code configuredPerms} argument contains the following 3 permission strings:
     * <p/>
     * <ol>
     * <li>permission:one</li>
     * <li>permission:two</li>
     * <li>permission:three</li>
     * </ol>
     * And the action is {@code read}, then the return value will be:
     * <ol>
     * <li>permission:one:read</li>
     * <li>permission:two:read</li>
     * <li>permission:three:read</li>
     * </ol>
     * per {@link org.apache.shiro.authz.permission.WildcardPermission WildcardPermission} conventions.  Subclasses
     * are of course free to override this method or the
     * {@link #buildPermissions(javax.servlet.http.HttpServletRequest, String[], String) buildPermissions} request
     * variant for custom building logic or with different permission formats.
     *
     * @param configuredPerms list of configuredPerms to be converted.
     *                        要转换的configurePerms的列表。
     *
     * @param action          the resolved action based on the request method to be appended to permission strings.
     *                        基于要附加到权限字符串的request方法解决的操作。
     *
     * @return an array of permission strings with each element appended with the action.
     */
    protected String[] buildPermissions(String[] configuredPerms, String action) {


        if (configuredPerms == null || configuredPerms.length <= 0 || !StringUtils.hasText(action)) {
            return configuredPerms;
        }

        String[] mappedPerms = new String[configuredPerms.length];

        // loop and append :action
        for (int i = 0; i < configuredPerms.length; i++) {
            mappedPerms[i] = configuredPerms[i] + ":" + action;
        }

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mappedPerms.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(mappedPerms[i]);
            }
            log.trace("MAPPED '{}' action to permission(s) '{}'", action, sb);
        }

        return mappedPerms;
    }

    /**
     *
     *
     * Resolves an 'application friendly' action verb based on the {@code HttpServletRequest}'s method, appends that
     * action to each configured permission (the {@code mappedValue} argument is a {@code String[]} array), and
     * delegates the permission check for the newly constructed permission(s) to the superclass
     * {@link PermissionsAuthorizationFilter#isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, Object) isAccessAllowed}
     * implementation to perform the actual permission check.
     *
     * @param request     the inbound {@code ServletRequest}
     * @param response    the outbound {@code ServletResponse}
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings.
     * @return {@code true} if the request should proceed through the filter normally, {@code false} if the
     *         request should be processed by this filter's
     *         {@link #onAccessDenied(ServletRequest,ServletResponse,Object)} method instead.
     * @throws IOException
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        // 配置 转换 ！
        String[] perms = (String[]) mappedValue;
        // append the http action to the end of the permissions and then back to super
        //获取Http方法操作
        String action = getHttpMethodAction(request);

        //  重点分析一下 权限数据是怎么拆分的 ！
        String[] resolvedPerms = buildPermissions(perms, action);
        return super.isAccessAllowed(request, response, resolvedPerms);
    }
}
