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
package org.locationtech.jtstest.geomfunction;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.util.Assert;
import org.locationtech.jtstest.util.ClassUtil;
import org.locationtech.jtstest.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * A {@link GeometryFunction} which calls a static
 * {@link Method}.
 * 
 * @author Martin Davis
 *
 */
public class StaticMethodGeometryFunction
	extends BaseGeometryFunction
{
  private static final String PARAM_NAME_TEXT = "Text";
  private static final String PARAM_NAME_COUNT = "Count";
  private static final String PARAM_NAME_DISTANCE = "Distance";
  
  private static final String FUNCTIONS_SUFFIX = "Functions";
	
	public static StaticMethodGeometryFunction createFunction(Method method)
	{
		Assert.isTrue(Geometry.class.isAssignableFrom((method.getParameterTypes())[0]));
		
		Class<?> clz = method.getDeclaringClass();
		
		String category = extractCategory(ClassUtil.getClassname(clz));
		String funcName = method.getName();
		String description = extractDescription(method);
		String[] paramNames = extractParamNames(method);
		Class<?>[] paramTypes = extractParamTypes(method);
		Class<?> returnType = method.getReturnType();
		
		return new StaticMethodGeometryFunction(category, funcName, 
				description,
				paramNames, paramTypes,
				returnType, method);    
	}
	
	private static String extractCategory(String className)
	{
        return StringUtil.removeFromEnd(className, FUNCTIONS_SUFFIX);
	}
	
	/**
	 * Java doesn't permit accessing the original code parameter names, unfortunately.
	 * 
	 * @param method
	 * @return
	 */
	private static String[] extractParamNames(Method method)
	{
		// Synthesize default names
		String[] name = defaultParamNames(method);
		// override with metadata titles, if any
    Annotation[][] anno = method.getParameterAnnotations();
		// Skip first annotation - it is the target geometry
		for (int i = 0; i < name.length; i++) {
			String annoName = MetadataUtil.title(anno[i+1]);
			if (annoName != null) {
			  name[i] = annoName;
			}
		}
		return name;
	}
	
	private static String[] defaultParamNames(Method method) {
	  int firstScalarIndex = firstScalarParamIndex(method);
	   // Synthesize default names
	  Class<?>[] type = method.getParameterTypes();
    String[] name = new String[type.length - 1];
    for (int i = 0; i < name.length; i++) {
      // for first scalar parameter choose default name based on type
      name[i] = "Arg " + i;
      if (i+1 == firstScalarIndex) {
        name[i] = paramNamePrimary(type[firstScalarIndex]);
      }
    }
    return name;
	}
	private static int firstScalarParamIndex(Method method) {
	   Class<?>[] type = method.getParameterTypes();
	   for (int i = 0; i < type.length; i++) {
	     if (! ClassUtil.isGeometry(type[i])) {
	       return i;
	     }
	   }
	   return -1;
	}
  private static String paramNamePrimary(Class<?> clz) {
    if (clz == String.class) return PARAM_NAME_TEXT;
    if (ClassUtil.isDouble(clz)) return PARAM_NAME_DISTANCE;
    //if (ClassUtil.isInt(clz)) return PARAM_NAME_COUNT;
    return PARAM_NAME_COUNT;
  }

  private static String extractDescription(Method method)
	{
    Metadata doc = method.getAnnotation(Metadata.class);
        return (doc == null) ? "" : doc.description();
	}
	
	private static Class<?>[] extractParamTypes(Method method)
	{
		Class<?>[] methodParamTypes = method.getParameterTypes();
		Class<?>[] types = new Class[methodParamTypes.length - 1];
        System.arraycopy(methodParamTypes, 1, types, 0, methodParamTypes.length - 1);
		return types;
	}

	 
  private static boolean extractRequiredB(Method method) {
    Annotation[][] anno = method.getParameterAnnotations();
    if (anno.length <= 1) return false;
    Class<?>[] methodParamTypes = method.getParameterTypes();
    boolean isRequired = false;
    if (methodParamTypes[1] == Geometry.class) {
      isRequired = MetadataUtil.isRequired(anno[1]);
    }
    return isRequired;
  }
  
  private final Method method;

	public StaticMethodGeometryFunction(
			String category,
			String name, 
			String description,
			String[] parameterNames, 
			Class<?>[] parameterTypes, 
			Class<?> returnType,
			Method method)
	{
		super(category, name, description, parameterNames, parameterTypes, returnType);
    this.method = method;
    isRequiredB = extractRequiredB(method);
	}

  public Object invoke(Geometry g, List<Object> arg)
  {
    return invoke(method, null, createFullArgs(g, arg));
  }

  /**
   * Creates an arg list which includes the target geometry as the first argument
   *
   * @param g
   * @param arg
   * @return
   */
  private static List<Object> createFullArgs(Geometry g, List<Object> arg)
  {
  	List<Object> fullArg = new java.util.ArrayList<>();
  	fullArg.add(g);
  	if (arg != null) {
  		fullArg.addAll(arg);
  	}
  	return fullArg;
  }

  public static Object invoke(Method method, Object target, List<Object> args)
  {
    Object result;
    try {
      result = method.invoke(target, args.toArray(new Object[0]));
    }
    catch (InvocationTargetException ex) {
      Throwable t = ex.getCause();
      if (t instanceof RuntimeException)
      	throw (RuntimeException) t;
      throw new RuntimeException(invocationErrMsg(ex), ex);
    }
    catch (Exception ex) {
      System.out.println(ex.getMessage());
      throw new RuntimeException(ex.getMessage());
    }
    return result;
  }
  
  private static String invocationErrMsg(InvocationTargetException ex)
  {
    Throwable targetEx = ex.getTargetException();
      return getClassname(targetEx.getClass())
      + ": " +
      targetEx.getMessage();
  }
  
  public static String getClassname(Class<?> javaClass)
  {
      return ClassUtil.getClassname(javaClass);
  }
}
