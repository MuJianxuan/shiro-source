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
package org.apache.shiro.spring.remoting;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;


/**
 *   SpringBoot 远程调用框架支持 ！
 *
 Spring org.springframework.remoting.support.RemoteInvocationExecutor的实现，
 该org.springframework.remoting.support.RemoteInvocationExecutor将sessionId绑定到传入线程，
 以使其在线程执行期间可用于SecurityManager实现。
 SecurityManager实现可以使用此sessionId基于相应Session中的持久状态来重构Subject实例
 *
 * An implementation of the Spring {@link org.springframework.remoting.support.RemoteInvocationExecutor}
 * that binds a {@code sessionId} to the incoming thread to make it available to the {@code SecurityManager}
 * implementation during the thread execution.  The {@code SecurityManager} implementation can use this sessionId
 * to reconstitute the {@code Subject} instance based on persistent state in the corresponding {@code Session}.
 *
 * @since 0.1
 */
public class SecureRemoteInvocationExecutor extends DefaultRemoteInvocationExecutor {

    //TODO - complete JavaDoc

    /*--------------------------------------------
    |             C O N S T A N T S             |
    ============================================*/

    /*--------------------------------------------
    |    I N S T A N C E   V A R I A B L E S    |
    ============================================*/
    private static final Logger log = LoggerFactory.getLogger(SecureRemoteInvocationExecutor.class);

    /**
     * SecurityManager用于检索在远程调用时应与创建的Subject相关联的领域。
     * The SecurityManager used to retrieve realms that should be associated with the
     * created <tt>Subject</tt>s upon remote invocation.
     */
    private SecurityManager securityManager;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    public void setSecurityManager(org.apache.shiro.mgt.SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    /*--------------------------------------------
    |               M E T H O D S               |
    ============================================*/
    @SuppressWarnings({"unchecked"})
    /**
     *  该方法很重要 ，需要梳理逻辑
     */
    public Object invoke(final RemoteInvocation invocation, final Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        try {
            SecurityManager securityManager =
                    this.securityManager != null ? this.securityManager : SecurityUtils.getSecurityManager();

            Subject.Builder builder = new Subject.Builder(securityManager);

            String host = (String) invocation.getAttribute(SecureRemoteInvocationFactory.HOST_KEY);
            if (host != null) {
                builder.host(host);
            }

            Serializable sessionId = invocation.getAttribute(SecureRemoteInvocationFactory.SESSION_ID_KEY);
            if (sessionId != null) {
                builder.sessionId(sessionId);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("RemoteInvocation did not contain a Shiro Session id attribute under " +
                            "key [" + SecureRemoteInvocationFactory.SESSION_ID_KEY + "].  A Subject based " +
                            "on an existing Session will not be available during the method invocation.");
                }
            }

            Subject subject = builder.buildSubject();

            // 通过 Subject 来触发调用
            return subject.execute(new Callable() {
                public Object call() throws Exception {
                    return SecureRemoteInvocationExecutor.super.invoke(invocation, targetObject);
                }
            });
        }
        catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) cause;
            } else if (cause instanceof IllegalAccessException) {
                throw (IllegalAccessException) cause;
            } else if (cause instanceof InvocationTargetException) {
                throw (InvocationTargetException) cause;
            } else {
                throw new InvocationTargetException(cause);
            }
        }
        catch (Throwable t) {
            throw new InvocationTargetException(t);
        }
    }
}
