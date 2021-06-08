package org.apache.shiro.event.support;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public class ExceptionThrowingSubscriber
  extends org.apache.shiro.event.support.TestSubscriber  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@org.apache.shiro.event.Subscribe() public  void onEvent(org.apache.shiro.event.support.ErrorCausingEvent event) { }
}
