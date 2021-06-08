package org.apache.shiro.spring.web.config;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(classes={org.apache.shiro.spring.testconfig.EventBusTestConfiguration.class,org.apache.shiro.spring.testconfig.RealmTestConfiguration.class,org.apache.shiro.spring.web.config.ShiroWebConfiguration.class}) @org.junit.runner.RunWith(value=org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class) public class ShiroWebConfigurationTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testMinimalConfiguration() { }
@org.junit.Test() public  void fakeCookie() { }
@org.junit.Test() public  void testSameSiteOptionExpression() { }
@org.junit.Test() public  void rememberMeCookie() { }
@org.junit.Test() public  void sessionCookie() { }
@org.junit.Test() public  void sameSiteOption() { }
}
