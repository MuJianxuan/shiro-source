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

import javax.servlet.Filter;

/**
 * PathConfigProcessor根据每个路径（url）处理配置条目。
 *
 * A PathConfigProcessor processes configuration entries on a per path (url) basis.
 *
 * @since 0.9
 */
public interface PathConfigProcessor {

    /**
     * 处理给定path唯一的指定config ，并返回应为该路径/配置组合执行的过滤器。
     * Processes the specified {@code config}, unique to the given {@code path}, and returns the Filter that should
     * execute for that path/config combination.
     *
     * @param path   the path for which the {@code config} should be applied
     * @param config the configuration for the {@code Filter} specific to the given {@code path}
     * @return the {@code Filter} that should execute for the given path/config combination.
     */
    Filter processPathConfig(String path, String config);
}
