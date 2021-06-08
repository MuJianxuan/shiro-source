package org.apache.shiro.spring.boot.autoconfigure.web;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@org.junit.runner.RunWith(value=org.springframework.test.context.junit4.SpringRunner.class) @org.springframework.boot.test.context.SpringBootTest(classes={org.apache.shiro.spring.boot.autoconfigure.web.application.ShiroWebAutoConfigurationTestApplication.class,org.apache.shiro.spring.boot.autoconfigure.web.ConfiguredGlobalFiltersTest.Config.class}) public class ConfiguredGlobalFiltersTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testGlobalFiltersConfigured() { }
@org.springframework.context.annotation.Configuration() public static class Config
  extends org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.springframework.context.annotation.Bean() public  java.util.List<java.lang.String> globalFilters() { return (java.util.List<java.lang.String>)null;}
@org.springframework.context.annotation.Bean() @java.lang.Override() public  org.apache.shiro.spring.web.ShiroFilterFactoryBean shiroFilterFactoryBean() { return (org.apache.shiro.spring.web.ShiroFilterFactoryBean)null;}
}
}
