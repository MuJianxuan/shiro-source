package org.apache.shiro.realm;
import org.apache.shiro.authc.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class AuthenticatingRealmTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testSetName() { }
@org.junit.Test() public  void testSupports() { }
@org.junit.Test() public  void testSupportsWithCustomAuthenticationTokenClass() { }
@org.junit.Test() public  void testNewInstanceWithCacheManager() { }
@org.junit.Test() public  void testNewInstanceWithCredentialsMatcher() { }
@org.junit.Test() public  void testSetCache() { }
@org.junit.Test() public  void testGetAuthenticationInfo() { }
@org.junit.Test() public  void testGetAuthenticationInfoWithNullReturnValue() { }
@org.junit.Test() public  void testAuthenticationCachingEnabledWithCacheMiss() { }
@org.junit.Test() public  void testAuthenticationCachingEnabledWithCacheHit() { }
@org.junit.Test() public  void testLogoutWithAuthenticationCachingEnabled() { }
@org.junit.Test() public  void testAssertCredentialsMatchWithNullCredentialsMatcher() { }
@org.junit.Test() public  void testAssertCredentialsMatchFailure() { }
private class TestAuthenticatingRealm
  extends org.apache.shiro.realm.AuthenticatingRealm  implements
    groovy.lang.GroovyObject {
;
public TestAuthenticatingRealm
() {
super ();
}
public TestAuthenticatingRealm
(org.apache.shiro.cache.CacheManager cacheManager) {
super ();
}
public TestAuthenticatingRealm
(org.apache.shiro.authc.credential.CredentialsMatcher matcher) {
super ();
}
public TestAuthenticatingRealm
(org.apache.shiro.cache.CacheManager cacheManager, org.apache.shiro.authc.credential.CredentialsMatcher matcher) {
super ();
}
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  org.apache.shiro.authc.AuthenticationInfo getInfo() { return (org.apache.shiro.authc.AuthenticationInfo)null;}
public  void setInfo(org.apache.shiro.authc.AuthenticationInfo value) { }
@java.lang.Override() protected  org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) { return (org.apache.shiro.authc.AuthenticationInfo)null;}
}
private class NoLookupAuthenticatingRealm
  extends org.apache.shiro.realm.AuthenticatingRealm  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@java.lang.Override() protected  org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) { return (org.apache.shiro.authc.AuthenticationInfo)null;}
}
}
