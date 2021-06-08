package org.apache.shiro.spring.config;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@org.springframework.test.context.web.WebAppConfiguration() @org.springframework.test.context.ContextConfiguration(classes={org.apache.shiro.spring.testconfig.RealmTestConfiguration.class,org.apache.shiro.spring.config.ShiroWebFilterConfigurationTest.FilterConfiguration.class,org.apache.shiro.spring.config.ShiroConfiguration.class,org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration.class}) public class ShiroWebFilterConfigurationTest
  extends org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testShiroFilterFactoryBeanContainsSpringFilters() { }
@org.springframework.context.annotation.Configuration() public static class FilterConfiguration
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.springframework.context.annotation.Bean() public  javax.servlet.Filter testFilter() { return (javax.servlet.Filter)null;}
@org.springframework.context.annotation.Bean() public  org.apache.shiro.spring.web.config.ShiroFilterChainDefinition shiroFilterChainDefinition() { return (org.apache.shiro.spring.web.config.ShiroFilterChainDefinition)null;}
}
public static class ExpectedTestFilter
  extends java.lang.Object  implements
    javax.servlet.Filter,    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@java.lang.Override() public  void init(javax.servlet.FilterConfig filterConfig)throws javax.servlet.ServletException { }
@java.lang.Override() public  void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response, javax.servlet.FilterChain chain)throws java.io.IOException, javax.servlet.ServletException { }
@java.lang.Override() public  void destroy() { }
}
}
