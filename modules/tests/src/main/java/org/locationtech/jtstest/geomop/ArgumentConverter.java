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

import java.util.ArrayList;
import java.util.List;

public class ArgumentConverter
{
	private ArgumentConverter()
	{

	}

  public static List<Object> convert(Class<?>[] parameterTypes, List<Object> args)
  {
  	List<Object> actualArgs = new ArrayList<>();
    for (int i = 0; i < args.size(); i++ ) {
    	actualArgs.add(convert(parameterTypes[i], args.get(i)));
    }
    return actualArgs;
  }

  public static Object convert(Class<?> destClass, Object srcValue)
  {
    if (srcValue instanceof String) {
      return convertFromString(destClass, (String) srcValue);
    }
    if (destClass.isAssignableFrom(srcValue.getClass())) {
      return srcValue;
    }
    throwInvalidConversion(destClass, srcValue);
    return null;
  }

  private static Object convertFromString(Class<?> destClass, String src)
  {
    if (destClass == Boolean.class || destClass == boolean.class) {
      if ("true".equals(src)) {
        return Boolean.TRUE;
      }
      else if ("false".equals(src)) {
        return Boolean.FALSE;
      }
      throwInvalidConversion(destClass, src);
    }
    else if (destClass == Integer.class || destClass == int.class) {
      // try as an int
      try {
        return new Integer(src);
      }
      catch (NumberFormatException e) {
        // eat this exception - it will be reported below
      }
    }
    else if (destClass == Double.class || destClass == double.class) {
      // try as an int
      try {
        return Double.valueOf(src);
      }
      catch (NumberFormatException e) {
        // eat this exception - it will be reported below
      }
    }
    else if (destClass == String.class) {
      return src;
    }
    throwInvalidConversion(destClass, src);
    return null;
  }

  private static void throwInvalidConversion(Class<?> destClass, Object srcValue)
  {
  	throw new IllegalArgumentException("Cannot convert " + srcValue + " to " + destClass.getName());
  }
}
