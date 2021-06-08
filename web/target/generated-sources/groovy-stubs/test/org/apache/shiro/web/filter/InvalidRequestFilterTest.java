package org.apache.shiro.web.filter;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.MatcherAssert.assertThat;

public class InvalidRequestFilterTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public static  void assertPathBlocked(org.apache.shiro.web.filter.InvalidRequestFilter filter, java.lang.String requestUri, java.lang.String servletPath) { }
public static  void assertPathBlocked(org.apache.shiro.web.filter.InvalidRequestFilter filter, java.lang.String requestUri) { }
public static  void assertPathAllowed(org.apache.shiro.web.filter.InvalidRequestFilter filter, java.lang.String requestUri, java.lang.String servletPath) { }
public static  void assertPathAllowed(org.apache.shiro.web.filter.InvalidRequestFilter filter, java.lang.String requestUri) { }
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void defaultConfig() { }
@org.junit.Test() public  void systemPropertyAllowBackslash() { }
@org.junit.Test() public  void testFilterBlocks() { }
@org.junit.Test() public  void testFilterAllowsBackslash() { }
@org.junit.Test() public  void testFilterAllowsNonAscii() { }
@org.junit.Test() public  void testFilterAllowsSemicolon() { }
public static  void assertPathBlocked(org.apache.shiro.web.filter.InvalidRequestFilter filter, java.lang.String requestUri, java.lang.String servletPath, java.lang.String pathInfo) { }
public static  void assertPathAllowed(org.apache.shiro.web.filter.InvalidRequestFilter filter, java.lang.String requestUri, java.lang.String servletPath, java.lang.String pathInfo) { }
public static  javax.servlet.http.HttpServletRequest mockRequest(java.lang.String requestUri, java.lang.String servletPath, java.lang.String pathInfo) { return (javax.servlet.http.HttpServletRequest)null;}
}
