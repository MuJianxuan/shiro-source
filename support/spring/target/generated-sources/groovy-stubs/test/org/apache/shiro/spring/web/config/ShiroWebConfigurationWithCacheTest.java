package org.apache.shiro.spring.web.config;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@org.springframework.test.context.ContextConfiguration(classes={org.apache.shiro.spring.testconfig.EventBusTestConfiguration.class,org.apache.shiro.spring.testconfig.RealmTestConfiguration.class,org.apache.shiro.spring.web.testconfig.CacheManagerConfiguration.class,org.apache.shiro.spring.web.config.ShiroWebConfiguration.class}) @org.junit.runner.RunWith(value=org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class) public class ShiroWebConfigurationWithCacheTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testMinimalConfiguration() { }
}
