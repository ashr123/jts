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

package org.locationtech.jts.geom;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class CoordinateTest extends TestCase
{
  public CoordinateTest(String name)
  {
    super(name);
  }

  public static void main(String[] args) {
    TestRunner.run(CoordinateTest.class);
  }
  
  public void testConstructor3D() 
  {
    Coordinate c = new Coordinate(350.2, 4566.8, 5266.3);
    assertEquals(350.2, c.x);
    assertEquals(4566.8, c.y);
    assertEquals(5266.3, c.getZ());
  }
  
  public void testConstructor2D() 
  {
    Coordinate c = new Coordinate(350.2, 4566.8);
    assertEquals(350.2, c.x);
    assertEquals(4566.8, c.y);
    assertEquals(Coordinate.NULL_ORDINATE, c.getZ());
  }
  public void testDefaultConstructor() 
  {
    Coordinate c = new Coordinate();
    assertEquals(0.0, c.x);
    assertEquals(0.0, c.y);
    assertEquals(Coordinate.NULL_ORDINATE, c.getZ());
  }
  public void testCopyConstructor3D() 
  {
    Coordinate orig = new Coordinate(350.2, 4566.8, 5266.3);
    Coordinate c = new Coordinate(orig);
    assertEquals(350.2, c.x);
    assertEquals(4566.8, c.y);
    assertEquals(5266.3, c.getZ());
  }
  public void testSetCoordinate() 
  {
    Coordinate orig = new Coordinate(350.2, 4566.8, 5266.3);
    Coordinate c = new Coordinate();
    c.setCoordinate(orig);
    assertEquals(350.2, c.x);
    assertEquals(4566.8, c.y);
    assertEquals(5266.3, c.getZ());
  }
  public void testGetOrdinate() 
  {
    Coordinate c = new Coordinate(350.2, 4566.8, 5266.3);
    assertEquals(350.2, c.getOrdinate(Coordinate.X));
    assertEquals(4566.8, c.getOrdinate(Coordinate.Y));
    assertEquals(5266.3, c.getOrdinate(Coordinate.Z));
  }
  public void testSetOrdinate() 
  {
    Coordinate c = new Coordinate();
    c.setOrdinate(Coordinate.X, 111);
    c.setOrdinate(Coordinate.Y, 222);
    c.setOrdinate(Coordinate.Z, 333);
    assertEquals(111.0, c.getOrdinate(Coordinate.X));
    assertEquals(222.0, c.getOrdinate(Coordinate.Y));
    assertEquals(333.0, c.getOrdinate(Coordinate.Z));
  }
  public void testEquals()
  {
    Coordinate c1 = new Coordinate(1,2,3);
    String s = "Not a coordinate";
      assertFalse(c1.equals(s));
    
    Coordinate c2 = new Coordinate(1,2,3);
    assertTrue(c1.equals2D(c2));

    Coordinate c3 = new Coordinate(1,22,3);
      assertFalse(c1.equals2D(c3));
  }
  public void testEquals2D()
  {
    Coordinate c1 = new Coordinate(1,2,3);
    Coordinate c2 = new Coordinate(1,2,3);
    assertTrue(c1.equals2D(c2));
    
    Coordinate c3 = new Coordinate(1,22,3);
      assertFalse(c1.equals2D(c3));
  }
  public void testEquals3D()
  {
    Coordinate c1 = new Coordinate(1,2,3);
    Coordinate c2 = new Coordinate(1,2,3);
    assertTrue(c1.equals3D(c2));
    
    Coordinate c3 = new Coordinate(1,22,3);
      assertFalse(c1.equals3D(c3));
  }
  public void testEquals2DWithinTolerance() 
  {
    Coordinate c = new Coordinate(100.0, 200.0, 50.0);
    Coordinate aBitOff = new Coordinate(100.1, 200.1, 50.0);
    assertTrue(c.equals2D(aBitOff, 0.2));
  }

  public void testEqualsInZ() {
    
    Coordinate c = new Coordinate(100.0, 200.0, 50.0);
    Coordinate withSameZ = new Coordinate(100.1, 200.1, 50.1);
    assertTrue(c.equalInZ(withSameZ, 0.2));
  }

  public void testCompareTo() 
  {
    Coordinate lowest = new Coordinate(10.0, 100.0, 50.0);
    Coordinate highest = new Coordinate(20.0, 100.0, 50.0);
    Coordinate equalToHighest = new Coordinate(20.0, 100.0, 50.0);
    Coordinate higherStill = new Coordinate(20.0, 200.0, 50.0);
    
    assertEquals(-1, lowest.compareTo(highest));
    assertEquals(1, highest.compareTo(lowest));
    assertEquals(-1, highest.compareTo(higherStill));
    assertEquals(0, highest.compareTo(equalToHighest));
  }
  public void testToString() 
  {
    String expectedResult = "(100.0, 200.0, 50.0)";
    String actualResult = new Coordinate(100.0, 200.0, 50.0).toString();
    assertEquals(expectedResult, actualResult);
  }

  public void testClone() {
    Coordinate c = new Coordinate(100.0, 200.0, 50.0);
    Coordinate clone = (Coordinate) c.clone();
    assertTrue(c.equals3D(clone));
  }

  public void testDistance() {
    Coordinate coord1 = new Coordinate(0.0, 0.0, 0.0);
    Coordinate coord2 = new Coordinate(100.0, 200.0, 50.0);
    double distance = coord1.distance(coord2);
    assertEquals(223.60679774997897, distance, 0.00001);
  }
  public void testDistance3D() {
    Coordinate coord1 = new Coordinate(0.0, 0.0, 0.0);
    Coordinate coord2 = new Coordinate(100.0, 200.0, 50.0);
    double distance = coord1.distance3D(coord2);
    assertEquals(229.128784747792, distance, 0.000001);
  }
  public void testCoordinateXY() {
    Coordinate xy = new CoordinateXY();    
    checkZUnsupported(xy);
    checkMUnsupported(xy);
    
    xy = new CoordinateXY(1.0,1.0);        // 2D
    Coordinate coord = new Coordinate(xy); // copy
    assertEquals( xy, coord );
      assertFalse(xy.equalInZ(coord, 0.000001));
    
    coord = new Coordinate(1.0,1.0,1.0); // 2.5d
    xy = new CoordinateXY( coord ); // copy
    assertEquals( xy, coord );
      assertFalse(xy.equalInZ(coord, 0.000001));
  }
  public void testCoordinateXYM() {
      Coordinate xym = new CoordinateXYM();
      checkZUnsupported(xym);
      
      xym.setM(1.0);
      assertEquals( 1.0, xym.getM());
      
      Coordinate coord = new Coordinate(xym); // copy
      assertEquals( xym, coord );
      assertFalse(xym.equalInZ(coord, 0.000001));
      
      coord = new Coordinate(1.0,1.0,1.0); // 2.5d
      xym = new CoordinateXYM( coord ); // copy
      assertEquals( xym, coord );
      assertFalse(xym.equalInZ(coord, 0.000001));
  }
  public void testCoordinateXYZM() {
      Coordinate xyzm = new CoordinateXYZM();
      xyzm.setZ(1.0);
      assertEquals( 1.0, xyzm.getZ());
      xyzm.setM(1.0);
      assertEquals( 1.0, xyzm.getM());
      
      Coordinate coord = new Coordinate(xyzm); // copy
      assertEquals( xyzm, coord );
      assertTrue( xyzm.equalInZ(coord,0.000001) );
      assertTrue( Double.isNaN(coord.getM()));
      
      coord = new Coordinate(1.0,1.0,1.0); // 2.5d
      xyzm = new CoordinateXYZM( coord ); // copy
      assertEquals( xyzm, coord );
      assertTrue( xyzm.equalInZ(coord,0.000001) ); 
  }
  
  public void testCoordinateHash() {
    doTestCoordinateHash(true, new Coordinate(1, 2), new Coordinate(1, 2));
    doTestCoordinateHash(false, new Coordinate(1, 2), new Coordinate(3, 4));
    doTestCoordinateHash(false, new Coordinate(1, 2), new Coordinate(1, 4));
    doTestCoordinateHash(false, new Coordinate(1, 2), new Coordinate(3, 2));
    doTestCoordinateHash(false, new Coordinate(1, 2), new Coordinate(2, 1));
  }

  private static void doTestCoordinateHash(boolean equal, Coordinate a, Coordinate b) {
    assertEquals(equal, a.equals(b));
    assertEquals(equal, a.hashCode() == b.hashCode());
  }
  
  /**
   * Confirm the z field is not supported by getZ and setZ.
   */
  private static void checkZUnsupported(Coordinate coord)
  {
      try {
          coord.setZ(0.0);
          fail(coord.getClass().getSimpleName() + " does not support Z");        
      }
      catch(IllegalArgumentException expected) {        
      }       
      assertTrue( Double.isNaN(coord.z));
      coord.z = 0.0;                      // field still public
      assertTrue( "z field not used", Double.isNaN(coord.getZ())); // but not used
  }
  /**
   * Confirm the z field is not supported by getZ and setZ.
   */
  private static void checkMUnsupported(Coordinate coord)
  {
      try {
          coord.setM(0.0);
          fail(coord.getClass().getSimpleName() + " does not support M");        
      }
      catch(IllegalArgumentException expected) {        
      }
      assertTrue( Double.isNaN(coord.getM()));      
  }

}
