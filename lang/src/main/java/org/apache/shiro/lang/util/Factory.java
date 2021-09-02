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
package org.apache.shiro.lang.util;

/**
 * 支持工厂方法 设计模式的泛型接口
 *
 *  抄袭Spring的实现？
 *
 * Generics-aware interface supporting the
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">Factory Method</a> design pattern.
 *
 * @param <T> The type of the instance returned by the Factory implementation.
 * @since 1.0
 */
public interface Factory<T> {

    /**
     *
     *  返回所需类型的实例。该实现确定每次调用此方法时是否创建新实例或缓存实例。
     *
     * Returns an instance of the required type.  The implementation determines whether or not a new or cached
     * instance is created every time this method is called.
     *
     * @return an instance of the required type.
     */
    T getInstance();
}
