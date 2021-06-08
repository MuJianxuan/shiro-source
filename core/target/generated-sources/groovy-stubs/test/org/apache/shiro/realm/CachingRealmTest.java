package org.apache.shiro.realm;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CachingRealmTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testCachingEnabled() { }
@org.junit.Test() public  void testSetName() { }
@org.junit.Test() public  void testNewInstanceWithCacheManager() { }
@org.junit.Test() public  void testOnLogout() { }
@org.junit.Test() public  void testGetAvailablePrincipalWithRealmPrincipals() { }
@org.junit.Test() public  void testGetAvailablePrincipalWithoutRealmPrincipals() { }
private static final class TestCachingRealm
  extends org.apache.shiro.realm.CachingRealm  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object getInfo() { return null;}
public  void setInfo(java.lang.Object value) { }
public  boolean getTemplateMethodCalled() { return false;}
public  boolean isTemplateMethodCalled() { return false;}
public  void setTemplateMethodCalled(boolean value) { }
public  boolean getDoClearCacheCalled() { return false;}
public  boolean isDoClearCacheCalled() { return false;}
public  void setDoClearCacheCalled(boolean value) { }
public  boolean supports(org.apache.shiro.authc.AuthenticationToken token) { return false;}
public  org.apache.shiro.authc.AuthenticationInfo getAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) { return (org.apache.shiro.authc.AuthenticationInfo)null;}
@java.lang.Override() protected  void afterCacheManagerSet() { }
@java.lang.Override() protected  void doClearCache(org.apache.shiro.subject.PrincipalCollection principals) { }
}
}
