package org.apache.shiro.config.ogdl;
import org.apache.shiro.config.ogdl.event.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import groovy.lang.*;
import groovy.util.*;

public class RecordingBeanListener
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
@groovy.transform.Generated() @groovy.transform.Internal() @java.beans.Transient() public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
@groovy.transform.Generated() @groovy.transform.Internal() public  void setMetaClass(groovy.lang.MetaClass mc) { }
@java.lang.SuppressWarnings(value="UnusedDeclaration") @org.apache.shiro.event.Subscribe() public  void onUnhandledBeanEvent(org.apache.shiro.config.ogdl.event.BeanEvent beanEvent) { }
@java.lang.SuppressWarnings(value="UnusedDeclaration") @org.apache.shiro.event.Subscribe() public  void onInstantiatedBeanEvent(org.apache.shiro.config.ogdl.event.InstantiatedBeanEvent beanEvent) { }
@java.lang.SuppressWarnings(value="UnusedDeclaration") @org.apache.shiro.event.Subscribe() public  void onConfiguredBeanEvent(org.apache.shiro.config.ogdl.event.ConfiguredBeanEvent beanEvent) { }
@java.lang.SuppressWarnings(value="UnusedDeclaration") @org.apache.shiro.event.Subscribe() public  void onInitializedBeanEvent(org.apache.shiro.config.ogdl.event.InitializedBeanEvent beanEvent) { }
@java.lang.SuppressWarnings(value="UnusedDeclaration") @org.apache.shiro.event.Subscribe() public  void onDestroyedBeanEvent(org.apache.shiro.config.ogdl.event.DestroyedBeanEvent beanEvent) { }
public  java.util.List<org.apache.shiro.config.ogdl.event.InstantiatedBeanEvent> getInstantiatedEvents() { return (java.util.List<org.apache.shiro.config.ogdl.event.InstantiatedBeanEvent>)null;}
public  java.util.List<org.apache.shiro.config.ogdl.event.ConfiguredBeanEvent> getConfiguredEvents() { return (java.util.List<org.apache.shiro.config.ogdl.event.ConfiguredBeanEvent>)null;}
public  java.util.List<org.apache.shiro.config.ogdl.event.InitializedBeanEvent> getInitializedEvents() { return (java.util.List<org.apache.shiro.config.ogdl.event.InitializedBeanEvent>)null;}
public  java.util.List<org.apache.shiro.config.ogdl.event.DestroyedBeanEvent> getDestroyedEvents() { return (java.util.List<org.apache.shiro.config.ogdl.event.DestroyedBeanEvent>)null;}
public  java.util.List<org.apache.shiro.config.ogdl.event.BeanEvent> getUnhandledEvents() { return (java.util.List<org.apache.shiro.config.ogdl.event.BeanEvent>)null;}
}
