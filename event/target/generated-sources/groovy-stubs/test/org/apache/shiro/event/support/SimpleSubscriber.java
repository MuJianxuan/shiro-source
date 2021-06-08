package org.apache.shiro.event.support;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public class SimpleSubscriber
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public SimpleSubscriber
() {}
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  int getCount() { return (int)0;}
public  void setCount(int value) { }
@org.apache.shiro.event.Subscribe() public  void onEvent(org.apache.shiro.event.support.SimpleEvent event) { }
}
