/*
 * Copyright (c) 2016 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jts.math;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class Vector2DTest extends TestCase {

  private static final double TOLERANCE = 1E-5;
  
  public static void main(String[] args) {
    TestRunner.run(Vector2DTest.class);
  }

  public Vector2DTest(String name) { super(name); }

  public void testLength()
  {
    assertEquals(1.0, Vector2D.create(0,1).length(), TOLERANCE);
    assertEquals(1.0, Vector2D.create(0,-1).length(), TOLERANCE);
    assertEquals(Vector2D.create(1,1).length(), Math.sqrt(2.0), TOLERANCE);
    assertEquals(5, Vector2D.create(3,4).length(), TOLERANCE);
  }
  
  public void testIsParallel() {
    assertTrue(Vector2D.create(0,1).isParallel(Vector2D.create(0,2)));
    assertTrue(Vector2D.create(1,1).isParallel(Vector2D.create(2,2)));
    assertTrue(Vector2D.create(-1,-1).isParallel(Vector2D.create(2,2)));

      assertFalse(Vector2D.create(1, -1).isParallel(Vector2D.create(2, 2)));
  }
  
  public void testToCoordinate()
  {
    assertEquals(Vector2D.create(
        Vector2D.create(1,2).toCoordinate()), 
        Vector2D.create(1,2), TOLERANCE);
  }

  static void assertEquals(Vector2D v1, Vector2D v2)
  {
      assertEquals(v1, v2);
  }
  
  static void assertEquals(Vector2D v1, Vector2D v2, double tolerance)
  {
    assertEquals(v1.getX(), v2.getX(), tolerance);
    assertEquals(v1.getY(), v2.getY(), tolerance);
  }
}
