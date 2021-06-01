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
package org.apache.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;


/**
 * 工具类：
 *
 * 可以使用这个来操作当前登录的用户
 *
 *
 *
 *
 *
 *
 */


/**
 * 安全实用程序
 * Accesses the currently accessible {@code Subject} for the calling code depending on runtime environment.
 *
 * @since 0.2
 */
public abstract class SecurityUtils {

    /**
     *  安全管理  volatile 关键字修饰，多线程下始终可以读取最新的值
     * ONLY used as a 'backup' in VM Singleton environments (that is, standalone environments), since the
     * ThreadContext should always be the primary source for Subject instances when possible.
     */


    /**
     *   需要留意什么时候 设置的  很明显，Subject获取 安全管理器的时候是直接获取的   那么问题来了 这个值是什么时候设置到 静态类中呢？
     */
    private static volatile SecurityManager securityManager;

    /**
     * 获取主题
     * Returns the currently accessible {@code Subject} available to the calling code depending on
     * runtime environment.
     * <p/>
     * This method is provided as a way of obtaining a {@code Subject} without having to resort to
     * implementation-specific methods.  It also allows the Shiro team to change the underlying implementation of
     * this method in the future depending on requirements/updates without affecting your code that uses it.
     *
     * @return the currently accessible {@code Subject} accessible to the calling code.
     * @throws IllegalStateException if no {@link Subject Subject} instance or
     *                               {@link SecurityManager SecurityManager} instance is available with which to obtain
     *                               a {@code Subject}, which which is considered an invalid application configuration
     *                               - a Subject should <em>always</em> be available to the caller.
     */
    public static Subject getSubject() {
        Subject subject = ThreadContext.getSubject();
        if (subject == null) {
            // 创建一个新的主题
            subject = (new Subject.Builder()).buildSubject();

            // 创建的与线程绑定！
            ThreadContext.bind(subject);
        }
        return subject;
    }

    /**
     * @see org.apache.shiro.web.servlet.AbstractShiroFilter::onFilterConfigSet()
     *
     * 设置VM（静态）单例SecurityManager，专门用于getSubject()实现中的透明使用。
     *
     * 该方法调用主要用于框架开发支持。 应用程序开发人员应该很少（如果有的话）需要调用此方法。
     *
     * Shiro开发团队更喜欢SecurityManager实例是非静态应用程序单例，而不是VM静态单例。
     * 不使用静态内存的应用程序单例需要某种应用程序配置框架来为您维护应用程序范围的SecurityManager实例（例如，Spring或EJB3环境），从而对象引用不必是静态的。
     *
     * 在这些环境中，Shiro通过其自身的框架集成代码基于当前正在执行的线程获取Subject数据，这是使用Shiro的首选方式。
     *
     * 但是，在某些环境中，例如不使用Spring或EJB或类似配置框架的独立桌面应用程序或Applet，
     * 使用VM-singleton可能更有意义（尽管仍首选使用VM）。 在这些环境中，通过此方法设置SecurityManager将自动启用getSubject()调用，使其只需很少的配置即可运行。
     *
     * 例如，在以下环境中，这将起作用：
     *        DefaultSecurityManager securityManager = new DefaultSecurityManager();
     *        securityManager.setRealms( ... ); //one or more Realms
     *        SecurityUtils.setSecurityManager( securityManager );
     *
     * 然后在应用程序代码中的任何位置，以下调用将返回应用程序的主题：
     *        Subject currentUser = SecurityUtils.getSubject();
     *
     * 参数：
     * securityManager –设置为VM静态单例的securityManager实例。
     *
     * Sets a VM (static) singleton SecurityManager, specifically for transparent use in the
     * {@link #getSubject() getSubject()} implementation.
     * <p/>
     * <b>This method call exists mainly for framework development support.  Application developers should rarely,
     * if ever, need to call this method.</b>
     * <p/>
     * The Shiro development team prefers that SecurityManager instances are non-static application singletons
     * and <em>not</em> VM static singletons.  Application singletons that do not use static memory require some sort
     * of application configuration framework to maintain the application-wide SecurityManager instance for you
     * (for example, Spring or EJB3 environments) such that the object reference does not need to be static.
     * <p/>
     * In these environments, Shiro acquires Subject data based on the currently executing Thread via its own
     * framework integration code, and this is the preferred way to use Shiro.
     * <p/>
     * However in some environments, such as a standalone desktop application or Applets that do not use Spring or
     * EJB or similar config frameworks, a VM-singleton might make more sense (although the former is still preferred).
     * In these environments, setting the SecurityManager via this method will automatically enable the
     * {@link #getSubject() getSubject()} call to function with little configuration.
     * <p/>
     * For example, in these environments, this will work:
     * <pre>
     * DefaultSecurityManager securityManager = new {@link org.apache.shiro.mgt.DefaultSecurityManager DefaultSecurityManager}();
     * securityManager.setRealms( ... ); //one or more Realms
     * <b>SecurityUtils.setSecurityManager( securityManager );</b></pre>
     * <p/>
     * And then anywhere in the application code, the following call will return the application's Subject:
     * <pre>
     * Subject currentUser = SecurityUtils.getSubject();</pre>
     *
     * @param securityManager the securityManager instance to set as a VM static singleton.
     */
    public static void setSecurityManager(SecurityManager securityManager) {
        SecurityUtils.securityManager = securityManager;
    }

    /**
     *
     * 返回调用代码可访问的SecurityManager。
     *
     * 如果可以找到一个线程绑定的SecurityManager则此实现有利于获取它。
     * 如果执行线程不可用，它将尝试使用静态单例（如果有）（有关静态单例的更多信息，请参见  setSecurityManager  方法）。
     *
     * 如果线程本地实例或静态单例实例都不可用，则此方法将引发UnavailableSecurityManagerException来
     * 指示错误-始终应可访问SecurityManager来调用应用程序中的代码。 如果不是，则可能是由于Shiro配置问题所致。
     *
     * Returns the SecurityManager accessible to the calling code.
     * <p/>
     * This implementation favors acquiring a thread-bound {@code SecurityManager} if it can find one.  If one is
     * not available to the executing thread, it will attempt to use the static singleton if available (see the
     * {@link #setSecurityManager setSecurityManager} method for more on the static singleton).
     * <p/>
     * If neither the thread-local or static singleton instances are available, this method throws an
     * {@code UnavailableSecurityManagerException} to indicate an error - a SecurityManager should always be accessible
     * to calling code in an application. If it is not, it is likely due to a Shiro configuration problem.
     *
     * @return the SecurityManager accessible to the calling code.
     * @throws UnavailableSecurityManagerException
     *          if there is no {@code SecurityManager} instance available to the
     *          calling code, which typically indicates an invalid application configuration.
     */
    public static SecurityManager getSecurityManager() throws UnavailableSecurityManagerException {
        // 先从线程中获取
        SecurityManager securityManager = ThreadContext.getSecurityManager();
        if (securityManager == null) {
            securityManager = SecurityUtils.securityManager;
        }
        if (securityManager == null) {
            String msg = "No SecurityManager accessible to the calling code, either bound to the " +
                    ThreadContext.class.getName() + " or as a vm static singleton.  This is an invalid application " +
                    "configuration.";
            throw new UnavailableSecurityManagerException(msg);
        }
        return securityManager;
    }
}
