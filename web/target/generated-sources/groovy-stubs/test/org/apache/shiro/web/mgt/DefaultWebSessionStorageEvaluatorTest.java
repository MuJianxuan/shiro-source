package org.apache.shiro.web.mgt;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DefaultWebSessionStorageEvaluatorTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.junit.Test() public  void testWithSession() { }
@org.junit.Test() public  void testWithoutSessionAndNonWebSubject() { }
@org.junit.Test() public  void testWithoutSessionAndGenerallyDisabled() { }
@org.junit.Test() public  void testWebSubjectWithoutSessionAndGenerallyEnabled() { }
@org.junit.Test() public  void testWebSubjectWithoutSessionAndGenerallyEnabledButRequestDisabled() { }
@org.junit.Test() public  void testWebSubjectWithoutSessionAndGenerallyEnabledWithNonBooleanRequestAttribute() { }
private interface RequestPairWebSubject
  extends
    org.apache.shiro.web.util.RequestPairSource,    org.apache.shiro.web.subject.WebSubject {
;
}
}
