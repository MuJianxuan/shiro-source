package org.apache.shiro.web.filter.authc;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.easymock.EasyMock.*;

public class BearerHttpFilterAuthenticationTest
  extends org.apache.shiro.test.SecurityManagerTestSupport  implements
    groovy.lang.GroovyObject {
;
public static  javax.servlet.http.HttpServletRequest mockRequest(java.lang.String token, java.lang.String host) { return (javax.servlet.http.HttpServletRequest)null;}
public static  javax.servlet.http.HttpServletRequest mockRequest(java.lang.String token) { return (javax.servlet.http.HttpServletRequest)null;}
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.jupiter.api.Test() public  void createTokenNoAuthorizationHeader()throws java.lang.Exception { }
@org.junit.jupiter.api.Test() public  void createTokenNoValue()throws java.lang.Exception { }
@org.junit.jupiter.api.Test() public  void createTokenWithValue()throws java.lang.Exception { }
@org.junit.jupiter.api.Test() public  void createTokenJustSpaces()throws java.lang.Exception { }
@org.junit.jupiter.api.Test() public  void httpMethodDoesNotRequireAuthentication()throws java.lang.Exception { }
@org.junit.jupiter.api.Test() public  void httpMethodRequiresAuthentication()throws java.lang.Exception { }
@org.junit.jupiter.api.Test() public  void permissiveEnabledWithLoginTest() { }
@org.junit.jupiter.api.Test() public  void permissiveEnabledTest() { }
@org.junit.jupiter.api.Test() public  void httpMethodRequiresAuthenticationWithPermissive()throws java.lang.Exception { }
public static  javax.servlet.http.HttpServletRequest mockRequest() { return (javax.servlet.http.HttpServletRequest)null;}
public static  javax.servlet.http.HttpServletRequest mockRequest(java.lang.String token, java.lang.String host, groovy.lang.Closure<javax.servlet.http.HttpServletRequest> additionalMockConfig) { return (javax.servlet.http.HttpServletRequest)null;}
public static  javax.servlet.http.HttpServletResponse mockResponse() { return (javax.servlet.http.HttpServletResponse)null;}
}
