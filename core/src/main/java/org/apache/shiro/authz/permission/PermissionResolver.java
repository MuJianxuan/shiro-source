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
package org.apache.shiro.authz.permission;

import org.apache.shiro.authz.Permission;

/**
 * 权限解析器 ，通常我们应用中都是一个 字符串表示一个 权限，转换成  shiro框架中 定义的对象 ，且做一些封装 ，并且将比较的方式 进行 自定义
 * PermisisonResolver解析一个String值，并将其转换为Permission实例
 *
 * A {@code PermisisonResolver} resolves a String value and converts it into a
 * {@link org.apache.shiro.authz.Permission Permission} instance.
 * <p/>
 * The default {@link WildcardPermissionResolver} should be
 * suitable for most purposes, which constructs {@link WildcardPermission} objects.
 * However, any resolver may be configured if an application wishes to use different
 * {@link org.apache.shiro.authz.Permission} implementations.
 * <p/>
 * A {@code PermissionResolver} is used by many Shiro components such as annotations, property file
 * configuration, URL configuration, etc.  It is useful whenever a String representation of a permission is specified
 * and that String needs to be converted to a Permission instance before executing a security check.
 * <p/>
 * Shiro chooses to support {@link WildcardPermission Wildcardpermission}s by default in almost all components and
 * we do that in the form of the {@link WildcardPermissionResolver WildcardPermissionResolver}.   One of the nice
 * things about {@code WildcardPermission}s being supported by default is that it makes it very easy to
 * store complex permissions in the database - and also makes it very easy to represent permissions in JSP files,
 * annotations, etc., where a simple string representation is useful.
 * <p/>
 * Although this happens to be the Shiro default, you are of course free to provide custom
 * String-to-Permission conversion by providing Shiro components any instance of this interface.
 *
 * @see org.apache.shiro.authz.ModularRealmAuthorizer#setPermissionResolver(PermissionResolver) ModularRealmAuthorizer.setPermissionResolver
 * @see org.apache.shiro.realm.AuthorizingRealm#setPermissionResolver(PermissionResolver) AuthorizingRealm.setPermissionResolver
 * @see PermissionResolverAware PermissionResolverAware
 * @since 0.9
 */
public interface PermissionResolver {

    /**
     * Resolves a Permission based on the given String representation.
     *
     * @param permissionString the String representation of a permission.
     * @return A Permission object that can be used internally to determine a subject's permissions.
     * @throws InvalidPermissionStringException
     *          if the permission string is not valid for this resolver.
     */
    Permission resolvePermission(String permissionString);

}
