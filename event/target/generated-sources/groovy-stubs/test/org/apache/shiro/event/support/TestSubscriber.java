package org.apache.shiro.event.support;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public class TestSubscriber
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public TestSubscriber
() {}
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  int getFooCount() { return (int)0;}
public  void setFooCount(int value) { }
public  int getBarCount() { return (int)0;}
public  void setBarCount(int value) { }
public  java.lang.Object getLastEvent() { return null;}
public  void setLastEvent(java.lang.Object value) { }
@org.apache.shiro.event.Subscribe() public  void onFooEvent(org.apache.shiro.event.support.FooEvent event) { }
@org.apache.shiro.event.Subscribe() public  void onBarEvent(org.apache.shiro.event.support.BarEvent event) { }
public  int getCount() { return (int)0;}
}
