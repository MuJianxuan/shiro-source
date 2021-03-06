package org.apache.shiro.web.jaxrs;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ContainerIntegrationIT
  extends org.apache.shiro.testing.web.AbstractContainerIT  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testNoAuthResource() { }
@org.junit.Test() public  void testNoAuthResourceAsync() { }
@org.junit.Test() public  void testSecuredRequiresAuthentication() { }
@org.junit.Test() public  void testSecuredRequiresUser() { }
@org.junit.Test() public  void testSecuredRequiresRoles() { }
@org.junit.Test() public  void testSecuredRequiresPermissions() { }
@org.junit.Test() public  void testSecuredRequiresGuest() { }
}
