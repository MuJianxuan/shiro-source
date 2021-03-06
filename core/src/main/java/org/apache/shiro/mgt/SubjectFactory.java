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
import org.apache.shiro.subject.SubjectContext;

import java.util.Map;

/**
 *   {@code SubjectFactory}负责根据需要构造{@link Subject Subject}实例。
 *
 *    工厂模式 ： 构建 Subject对象
 *
 * A {@code SubjectFactory} is responsible for constructing {@link Subject Subject} instances as needed.
 *
 * @since 1.0
 */
public interface SubjectFactory {

    /**
     * 创建一个反映指定上下文数据状态的新主题实例。 数据可以是构建Subject实例所需的任何内容，其内容可能因环境而异。
     *
     * Shiro 核心支持的任何数据都可以通过SubjectContext的get*或resolve*方法之一访问。 所有其他数据都可用作 Map#attribute 。
     *
     * Creates a new Subject instance reflecting the state of the specified contextual data.  The data would be
     * anything required to required to construct a {@code Subject} instance and its contents can vary based on
     * environment.
     * <p/>
     * Any data supported by Shiro core will be accessible by one of the {@code SubjectContext}'s {@code get*}
     * or {@code resolve*} methods.  All other data is available as map {@link Map#get attribute}s.
     *
     * @param context the contextual data to be used by the implementation to construct an appropriate {@code Subject}
     *                instance.
     * @return a {@code Subject} instance created based on the specified context.
     * @see SubjectContext
     */
    Subject createSubject(SubjectContext context);

}
