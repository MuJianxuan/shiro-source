package org.apache.shiro.event.support;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class DefaultEventBusTest
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  org.apache.shiro.event.support.DefaultEventBus getBus() { return (org.apache.shiro.event.support.DefaultEventBus)null;}
public  void setBus(org.apache.shiro.event.support.DefaultEventBus value) { }
@org.junit.Before() public  void setUp() { }
@org.junit.Test() public  void testSetEventListenerResolver() { }
@org.junit.Test() public  void testSimpleSubscribe() { }
@org.junit.Test() public  void testPublishNullEvent() { }
@org.junit.Test() public  void testSubscribeNullInstance() { }
@org.junit.Test() public  void testSubscribeWithNullResolvedListeners() { }
@org.junit.Test() public  void testSubscribeWithoutAnnotations() { }
@org.junit.Test() public  void testUnsubscribeNullInstance() { }
@org.junit.Test() public  void testUnsubscribe() { }
@org.junit.Test() public  void testPolymorphicSubscribeMethodsOnlyOneInvoked() { }
@org.junit.Test() public  void testPolymorphicSubscribeMethodsOnlyOneInvokedWithListenerSubclass() { }
@org.junit.Test() public  void testSubscribeWithErroneousAnnotation() { }
@org.junit.Test() public  void testContinueThroughListenerExceptions() { }
}
