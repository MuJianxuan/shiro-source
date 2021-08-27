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

/**
 *  Subject 有哪几个是需要认证和授权的时候用到，
 *      1、账户信息是否认证；
 *      2、账户绑定的权限集合；
 *
 *  主题  数据操作接口  Dao
 *
 * A {@code SubjectDAO} is responsible for persisting a Subject instance's internal state such that the Subject instance
 * can be recreated at a later time if necessary.
 * <p/>
 * Shiro's default {@code SecurityManager} implementations typically use a {@code SubjectDAO} in conjunction
 * with a {@link SubjectFactory}: after the {@code SubjectFactory} creates a {@code Subject} instance, the
 * {@code SubjectDAO} is used to persist that subject's state such that it can be accessed later if necessary.
 * <h3>Usage</h3>
 * It should be noted that this component is used by {@code SecurityManager} implementations to manage Subject
 * state persistence.  It does <em>not</em> make Subject instances accessible to the
 * application (e.g. via {@link org.apache.shiro.SecurityUtils#getSubject() SecurityUtils.getSubject()}).
 *
 * @see DefaultSubjectDAO
 * @since 1.2
 */
public interface SubjectDAO {

    /**
     * 保存并返回
     *
     * 保存并返回保留指定主题的状态，以供以后访问。 如果不存在任何持久状态，则在可能的情况下（例如创建操作）将其持久化。
     * 如果指定的Subject存在现有状态，则此方法更新现有状态以反映当前状态（即更新操作）。
     *
     * Persists the specified Subject's state for later access.  If there is a no existing state persisted, this
     * persists it if possible (i.e. a create operation).  If there is existing state for the specified {@code Subject},
     * this method updates the existing state to reflect the current state (i.e. an update operation).
     *
     * @param subject the Subject instance for which its state will be created or updated.
     * @return the Subject instance to use after persistence is complete.  This can be the same as the method argument
     * if the underlying implementation does not need to make any Subject changes.
     */
    Subject save(Subject subject);

    /**
     *
     * 删除指定的Subject实例的所有持久状态。 这是一种删除操作，因此以后无法访问主题的状态
     *
     * Removes any persisted state for the specified {@code Subject} instance.  This is a delete operation such that
     * the Subject's state will not be accessible at a later time.
     *
     * @param subject the Subject instance for which any persistent state should be deleted.
     */
    void delete(Subject subject);
}
