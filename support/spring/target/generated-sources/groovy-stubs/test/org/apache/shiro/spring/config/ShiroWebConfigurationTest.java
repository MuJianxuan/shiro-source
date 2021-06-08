package org.apache.shiro.spring.config;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@org.springframework.test.context.ContextConfiguration(classes={org.apache.shiro.spring.testconfig.RealmTestConfiguration.class,org.apache.shiro.spring.config.ShiroConfiguration.class,org.apache.shiro.spring.web.config.ShiroWebConfiguration.class,org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration.class}) public class ShiroWebConfigurationTest
  extends org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testMinimalConfiguration() { }
}
