/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jtstest.util;

import org.locationtech.jts.geom.Geometry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ClassUtil 
{
    private ClassUtil() {
    }

    public static String getClassname(Class<?> clz)
  {
    String jClassName = clz.getName();
    int lastDotPos = jClassName.lastIndexOf(".");
    return jClassName.substring(lastDotPos + 1);
  }

  public static String[] getStringArrayClassField(Class<?> clz, String name)
  {
  	try {
  		Field field = clz.getField(name);
        return (String[]) field.get(null);
  	} catch (NoSuchFieldException | IllegalAccessException ex) {
    }
      return null;
  }
  
  public static String getStringClassField(Class<?> clz, String name)
  {
  	try {
  		Field[] f = clz.getDeclaredFields();
  		Field field = clz.getField(name);
        return (String) field.get(null);
  	}
  	catch (NoSuchFieldException | IllegalAccessException ex) {  	}
      return null;
  }
  
  public static Object dynamicCall(String clzName, String methodName, Class<?>[] methodParamTypes, List<Object> methodArgs)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException
  {
    Class<?> clz = Class.forName(clzName);

    Class<?>[] constParTypes = new Class<?>[] { String.class, String.class };
    Constructor<?> constr = clz.getConstructor();
    Object dummyto = constr.newInstance();

    Method meth = clz.getMethod(methodName, methodParamTypes);
      return meth.invoke(dummyto, methodArgs.toArray(new Object[0]));
  }

  public static boolean isNumber(Class<?> clz) {
    if (clz == Double.class) return true;
    if (clz == double.class) return true;
    if (clz == Integer.class) return true;
      return clz == int.class;
  }
  
  /**
   * Converts a number-like object to a Double.
   * If the object cannot be converted null is returned.
   * 
   * @param o a number-like object
   * @return the value of the number, or null
   */
  public static Double toDouble(Object o) {
    double d = 0;
    try {
      // get Java to figure out the conversion to double
      d = (Double) o;
    //  d = ((Number) o).doubleValue();
    } catch (ClassCastException e) {
      return null;
    }
    return d;
  }

  public static boolean isDouble(Class<?> clz) {
    if (clz == Double.class) return true;
      return clz == double.class;
  }

  public static boolean isGeometry(Class<?> clz) {
    return Geometry.class.isAssignableFrom(clz);
  }

}
