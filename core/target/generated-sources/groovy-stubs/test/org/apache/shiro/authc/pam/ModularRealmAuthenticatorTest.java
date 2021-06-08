package org.apache.shiro.authc.pam;
import org.apache.shiro.authc.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ModularRealmAuthenticatorTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testNewInstance() { }
@org.junit.Test() public  void testDoAuthenticateNoRealms() { }
@org.junit.Test() public  void testSingleRealmAuthenticationSuccess() { }
@org.junit.Test() public  void testSingleRealmAuthenticationWithUnsupportedToken() { }
@org.junit.Test() public  void testSingleRealmAuthenticationWithNullAuthenticationInfo() { }
@org.junit.Test() public  void testMultiRealmAuthenticationSuccess() { }
@org.junit.Test() public  void testMultiRealmAuthenticationWithAuthenticationException() { }
@org.junit.Test() public  void testOnLogout() { }
private static interface LogoutAwareRealm
  extends
    org.apache.shiro.realm.Realm,    org.apache.shiro.authc.LogoutAware {
;
}
}
