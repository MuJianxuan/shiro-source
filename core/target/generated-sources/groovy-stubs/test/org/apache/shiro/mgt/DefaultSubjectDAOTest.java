package org.apache.shiro.mgt;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DefaultSubjectDAOTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testIsSessionStorageEnabledDefault() { }
@org.junit.Test() public  void testIsSessionStorageEnabledDefaultSubject() { }
@org.junit.Test() public  void testCustomSessionStorageEvaluator() { }
@org.junit.Test() public  void testDeleteWithoutSession() { }
@org.junit.Test() public  void testDeleteWithSession() { }
@org.junit.Test() public  void testSaveWhenSessionStorageIsDisabled() { }
@org.junit.Test() public  void testSaveWithoutSessionOrPrincipalsOrAuthentication() { }
@org.junit.Test() public  void testMergePrincipalsWithDelegatingSubject() { }
@org.junit.Test() public  void testMergePrincipalsWithSubjectPrincipalsButWithoutSession() { }
@org.junit.Test() public  void testMergePrincipalsWithoutSubjectPrincipalsOrSessionPrincipals() { }
@org.junit.Test() public  void testMergePrincipalsWithoutSubjectPrincipalsButWithSessionPrincipals() { }
@org.junit.Test() public  void testMergePrincipalsWithSubjectPrincipalsButWithDifferentSessionPrincipals() { }
@org.junit.Test() public  void testMergeAuthcWithSubjectAuthcButWithoutSession() { }
@org.junit.Test() public  void testMergeAuthcWithoutSubjectAuthcOrSessionAuthc() { }
@org.junit.Test() public  void testMergeAuthcWithoutSubjectAuthcButWithSessionAuthc() { }
@org.junit.Test() public  void testMergeAuthcWithSubjectAuthcButWithoutSessionAuthc() { }
@org.junit.Test() public  void testMergeAuthcWithSubjectAuthcButWithDifferentSessionAuthc() { }
}
