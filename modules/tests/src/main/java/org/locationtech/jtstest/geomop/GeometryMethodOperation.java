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
package org.locationtech.jtstest.geomop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jtstest.testrunner.BooleanResult;
import org.locationtech.jtstest.testrunner.DoubleResult;
import org.locationtech.jtstest.testrunner.GeometryResult;
import org.locationtech.jtstest.testrunner.IntegerResult;
import org.locationtech.jtstest.testrunner.JTSTestReflectionException;
import org.locationtech.jtstest.testrunner.Result;


/**
 * Invokes a named operation on a set of arguments,
 * the first of which is a {@link Geometry}.
 * This class provides operations which are the methods 
 * defined on the Geometry class.
 * Other {@link GeometryOperation} classes can delegate to
 * instances of this class to run standard Geometry methods.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class GeometryMethodOperation
    implements GeometryOperation
{
  public static boolean isBooleanFunction(String name) {
		return getGeometryReturnType(name) == boolean.class;
	}

	public static boolean isIntegerFunction(String name) {
		return getGeometryReturnType(name) == int.class;
	}

	public static boolean isDoubleFunction(String name) {
		return getGeometryReturnType(name) == double.class;
	}

	public static boolean isGeometryFunction(String name) {
		return Geometry.class.isAssignableFrom(getGeometryReturnType(name));
	}


  public static Class<?> getGeometryReturnType(String functionName) {
		Method[] methods = Geometry.class.getMethods();
      /**
       * Filter out only acceptable classes. (For instance, don't accept the
       * relate()=>IntersectionMatrix method)
       */
      for (Method method : methods) {
          if (method.getName().equalsIgnoreCase(functionName)) {
              Class<?> returnClass = method.getReturnType();
              /**
               * Filter out only acceptable classes. (For instance, don't accept the
               * relate()=>IntersectionMatrix method)
               */
              if (returnClass == boolean.class
                      || Geometry.class.isAssignableFrom(returnClass)
                      || returnClass == double.class || returnClass == int.class) {
                  return returnClass;
              }
          }
      }
		return null;
	}

  private final Method[] geometryMethods = Geometry.class.getMethods();

  public GeometryMethodOperation() {
  }

  public Class<?> getReturnType(String opName)
  {
  	return getGeometryReturnType(opName);
  }
  
  public Result invoke(String opName, Geometry geometry, List<Object> args)
      throws Exception
  {
    List<Object> actualArgs = new ArrayList<>();
    Method geomMethod = getGeometryMethod(opName, args, actualArgs);
    if (geomMethod == null)
      throw new JTSTestReflectionException(opName, args);
    return invokeMethod(geomMethod, geometry, actualArgs);
  }

  private Method getGeometryMethod(String opName, List<Object> args, List<Object> actualArgs)
  {
    // could index methods by name for efficiency...
      for (Method geometryMethod : geometryMethods) {
          if (!geometryMethod.getName().equalsIgnoreCase(opName)) {
              continue;
          }
          if (convertArgs(geometryMethod.getParameterTypes(), args, actualArgs)) {
              return geometryMethod;
          }
      }
    return null;
  }

  private static int nonNullItemCount(List<Object> obj)
  {
    int count = 0;
      for (Object o : obj) {
          if (o != null)
              count++;
      }
    return count;
  }

  private static final Object INCOMPATIBLE = new Object();

  private static boolean convertArgs(Class<?>[] parameterTypes, List<Object> args, List<Object> actualArgs)
  {
    if (parameterTypes.length != nonNullItemCount(args))
      return false;

    actualArgs.clear();
    for (int i = 0; i < args.size(); i++ ) {
      Object converted = convertArg(parameterTypes[i], args.get(i));
      if (converted == INCOMPATIBLE)
        return false;
      actualArgs.add(converted);
    }
    return true;
  }

  private static Object convertArg(Class<?> destClass, Object srcValue)
  {
    if (srcValue instanceof String) {
      return convertArgFromString(destClass, (String) srcValue);
    }
    if (destClass.isAssignableFrom(srcValue.getClass())) {
      return srcValue;
    }
    return INCOMPATIBLE;
  }

  private static Object convertArgFromString(Class<?> destClass, String srcStr)
  {
    if (destClass == Boolean.class || destClass == boolean.class) {
      if ("true".equals(srcStr)) {
        return Boolean.TRUE;
      }
      else if ("false".equals(srcStr)) {
        return Boolean.FALSE;
      }
      return INCOMPATIBLE;
    }
    if (destClass == Integer.class || destClass == int.class) {
      // try as an int
      try {
        return new Integer(srcStr);
      }
      catch (NumberFormatException e) {
        // eat this exception
      }
      return INCOMPATIBLE;
    }

    if (destClass == Double.class || destClass == double.class) {
      // try as an int
      try {
        return Double.valueOf(srcStr);
      }
      catch (NumberFormatException e) {
        // eat this exception
      }
      return INCOMPATIBLE;
    }
    if (destClass == String.class) {
      return srcStr;
    }
    return INCOMPATIBLE;
  }


  private static Result invokeMethod(Method method, Geometry geometry, List<Object> args)
      throws Exception
  {
    Object[] methodArgs = args.toArray(new Object[0]);
    try {
      if (method.getReturnType() == boolean.class) {
        return new BooleanResult((Boolean) method.invoke(geometry, methodArgs));
      }
      if (Geometry.class.isAssignableFrom(method.getReturnType())) {
        return new GeometryResult((Geometry) method.invoke(geometry, methodArgs));
      }
      if (method.getReturnType() == double.class) {
        return new DoubleResult((Double) method.invoke(geometry, methodArgs));
      }
      if (method.getReturnType() == int.class) {
        return new IntegerResult((Integer) method.invoke(geometry, methodArgs));
      }
    }
    catch (InvocationTargetException e) {
    	Throwable t = e.getTargetException();
    	if (t instanceof Exception)
    		throw (Exception) t;
    	throw (Error) t;
    }
    throw new JTSTestReflectionException("Unsupported result type: " + method.getReturnType());
  }

}
