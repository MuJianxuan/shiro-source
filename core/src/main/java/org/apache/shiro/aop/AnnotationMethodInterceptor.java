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
package org.apache.shiro.aop;

import java.lang.annotation.Annotation;

/**
 *
 *   拦截器基类  shiro 是基于 拦截器实现的
 *
 *   权限 、角色是怎么拦截的呢？
 *     比如代码中需要获取某个用户是否 拥有这个角色  这个角色拥有哪些权限
 *
 *
 *     角色 拥有全权限 ，而用户  拥有角色 ！ RBAC！ 是否 细化到 权限层
 *         判断某个当前角色是否拥有 某项权限
 *
 *   Role : admin / dev / test
 *  需求 ：我一个接口 需要根据不同权限 查看不同的 数据列表 比如 admin 可以查看 ALL  而 dev 只能查看自己的 ；但是我可能会添加  test 可动态
 *  拆分接口 ? 同一个列表查询为什么需要拆分接口， 因此需要获取当前角色 是否拥有 某项权限 即可/。
 *
 *    那么资源的拦截在哪呢 ？
 *
 * MethodInterceptor that inspects a specific annotation on the method invocation before continuing
 * its execution.
 * </p>
 * The annotation is acquired from the {@link MethodInvocation MethodInvocation} via a
 * {@link AnnotationResolver AnnotationResolver} instance that may be configured.  Unless
 * overridden, the default {@code AnnotationResolver} is a
 *
 * @since 0.9
 */
public abstract class AnnotationMethodInterceptor extends MethodInterceptorSupport {

    /**
     * 注解拦截器   ：
     *
     *    实际执行对象  委托给这个对象实际执行
     *
     */
    private AnnotationHandler handler;

    /**
     *
     * 注解解析器  在 实例化的时候 会默认初始化 这个值
     *
     * The resolver to use to find annotations on intercepted methods.
     *
     * @since 1.1
     */
    private AnnotationResolver resolver;

    /**
     * Constructs an <code>AnnotationMethodInterceptor</code> with the
     * {@link AnnotationHandler AnnotationHandler} that will be used to process annotations of a
     * corresponding type.
     *
     * @param handler the handler to delegate to for processing the annotation.
     */
    public AnnotationMethodInterceptor(AnnotationHandler handler) {
        this(handler, new DefaultAnnotationResolver());
    }

    /**
     * 可自定义 注解解析器
     *
     * Constructs an <code>AnnotationMethodInterceptor</code> with the
     * {@link AnnotationHandler AnnotationHandler} that will be used to process annotations of a
     * corresponding type, using the specified {@code AnnotationResolver} to acquire annotations
     * at runtime.
     *
     * @param handler  the handler to use to process any discovered annotation
     * @param resolver the resolver to use to locate/acquire the annotation
     * @since 1.1
     */
    public AnnotationMethodInterceptor(AnnotationHandler handler, AnnotationResolver resolver) {
        if (handler == null) {
            throw new IllegalArgumentException("AnnotationHandler argument cannot be null.");
        }

        //
        this.setHandler(handler);

        // 调用自身的set 方法 进行校验
        this.setResolver(resolver != null ? resolver : new DefaultAnnotationResolver());
    }

    /**
     * Returns the {@code AnnotationHandler} used to perform authorization behavior based on
     * an annotation discovered at runtime.
     *
     * @return the {@code AnnotationHandler} used to perform authorization behavior based on
     *         an annotation discovered at runtime.
     */
    public AnnotationHandler getHandler() {

        //
        return handler;
    }

    /**
     * Sets the {@code AnnotationHandler} used to perform authorization behavior based on
     * an annotation discovered at runtime.
     *
     * @param handler the {@code AnnotationHandler} used to perform authorization behavior based on
     *                an annotation discovered at runtime.
     */
    public void setHandler(AnnotationHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns the {@code AnnotationResolver} to use to acquire annotations from intercepted
     * methods at runtime.  The annotation is then used by the {@link #getHandler handler} to
     * perform authorization logic.
     *
     * @return the {@code AnnotationResolver} to use to acquire annotations from intercepted
     *         methods at runtime.
     * @since 1.1
     */
    public AnnotationResolver getResolver() {
        return resolver;
    }

    /**
     * Returns the {@code AnnotationResolver} to use to acquire annotations from intercepted
     * methods at runtime.  The annotation is then used by the {@link #getHandler handler} to
     * perform authorization logic.
     *
     * @param resolver the {@code AnnotationResolver} to use to acquire annotations from intercepted
     *                 methods at runtime.
     * @since 1.1
     */
    public void setResolver(AnnotationResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Returns <code>true</code> if this interceptor supports, that is, should inspect, the specified
     * <code>MethodInvocation</code>, <code>false</code> otherwise.
     * <p/>
     * The default implementation simply does the following:
     * <p/>
     * <code>return {@link #getAnnotation(MethodInvocation) getAnnotation(mi)} != null</code>
     *
     * @param mi the <code>MethodInvocation</code> for the method being invoked.
     * @return <code>true</code> if this interceptor supports, that is, should inspect, the specified
     *         <code>MethodInvocation</code>, <code>false</code> otherwise.
     */
    public boolean supports(MethodInvocation mi) {
        return getAnnotation(mi) != null;
    }

    /**
     *
     *  返回此拦截器将为指定的方法调用处理的Annotation。
     *
     * Returns the Annotation that this interceptor will process for the specified method invocation.
     * <p/>
     * The default implementation acquires the annotation using an annotation
     * {@link #getResolver resolver} using the internal annotation {@link #getHandler handler}'s
     * {@link org.apache.shiro.aop.AnnotationHandler#getAnnotationClass() annotationClass}.
     *
     * @param mi the MethodInvocation wrapping the Method from which the Annotation will be acquired.
     * @return the Annotation that this interceptor will process for the specified method invocation.
     */
    protected Annotation getAnnotation(MethodInvocation mi) {
        return getResolver().getAnnotation(mi, getHandler().getAnnotationClass());
    }
}
