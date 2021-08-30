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
package org.apache.shiro.spring.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 *  Spring的 注解解析器
 *
 * {@code AnnotationResolver} implementation that uses Spring's more robust
 * {@link AnnotationUtils AnnotationUtils} to find method annotations instead of the JDKs simpler
 * (and rather lacking) {@link Method}.{@link Method#getAnnotation(Class) getAnnotation(class)}
 * implementation.
 *
 * @since 1.1
 */
public class SpringAnnotationResolver implements AnnotationResolver {

    /**
     *  要调用的拦截方法。
     * @param mi the intercepted method to be invoked.
     * @param clazz the annotation class of the annotation to find.
     * @return
     */
    @Override
    public Annotation getAnnotation(MethodInvocation mi, Class<? extends Annotation> clazz) {

        // 获取方法对象
        Method method = mi. getMethod();

        //  在这个方法对象查找 注解对象
        Annotation a = AnnotationUtils.findAnnotation( method, clazz);
        if (a != null) return a;

        // 如果没找到

        //The MethodInvocation's method object could be a method defined in an interface.
        //However, if the annotation existed in the interface's implementation (and not
        //the interface itself), it won't be on the above method object.  Instead, we need to
        //acquire the method representation from the targetClass and check directly on the
        //implementation itself:
        // 类对象的转变
        Class<?> targetClass = mi.getThis().getClass();
        //获取最具体的方法  借助Spring中写好的 注解解析器！
        method = ClassUtils.getMostSpecificMethod(method, targetClass);

        a = AnnotationUtils.findAnnotation(method, clazz);
        if (a != null) return a;
        // See if the class has the same annotation
        //查看类是否具有相同的批注
        return AnnotationUtils.findAnnotation(mi.getThis().getClass(), clazz);
    }
}
