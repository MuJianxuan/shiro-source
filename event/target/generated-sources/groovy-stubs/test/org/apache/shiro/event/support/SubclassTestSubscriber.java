package org.apache.shiro.event.support;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public class SubclassTestSubscriber
  extends org.apache.shiro.event.support.TestSubscriber  implements
    groovy.lang.GroovyObject {
;
public SubclassTestSubscriber
() {}
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  int getBazCount() { return (int)0;}
public  void setBazCount(int value) { }
@org.apache.shiro.event.Subscribe() public  void onEvent(org.apache.shiro.event.support.BazEvent event) { }
@org.apache.shiro.event.Subscribe() public  void onEvent(org.apache.shiro.event.support.ErrorCausingEvent event) { }
@java.lang.Override() public  int getCount() { return (int)0;}
}
