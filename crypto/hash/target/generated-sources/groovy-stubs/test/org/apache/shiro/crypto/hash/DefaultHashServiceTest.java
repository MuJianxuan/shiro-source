package org.apache.shiro.crypto.hash;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DefaultHashServiceTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testNullRequest() { }
@org.junit.Test() public  void testDifferentAlgorithmName() { }
@org.junit.Test() public  void testDifferentIterations() { }
@org.junit.Test() public  void testDifferentRandomNumberGenerator() { }
@org.junit.Test() public  void testWithRandomlyGeneratedSalt() { }
@org.junit.Test() public  void testRequestWithEmptySource() { }
@org.junit.Test() public  void testOnlyRandomSaltHash() { }
@org.junit.Test() public  void testBothSaltsRandomness() { }
@org.junit.Test() public  void testBothSaltsReturn() { }
@org.junit.Test() public  void testBothSaltsHash() { }
@org.junit.Test() public  void testPrivateSaltChangesResult() { }
protected  org.apache.shiro.crypto.hash.Hash hash(org.apache.shiro.crypto.hash.HashService hashService, java.lang.Object source) { return (org.apache.shiro.crypto.hash.Hash)null;}
protected  org.apache.shiro.crypto.hash.Hash hash(org.apache.shiro.crypto.hash.HashService hashService, java.lang.Object source, java.lang.Object salt) { return (org.apache.shiro.crypto.hash.Hash)null;}
}
