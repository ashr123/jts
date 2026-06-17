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
package org.locationtech.jts.geom.prep;

import org.locationtech.jts.geom.Geometry;

import junit.framework.TestCase;
import junit.textui.TestRunner;


/**
 * Stress tests {@link PreparedPolygon} for 
 * correctness of 
 * {@link PreparedPolygon#contains(Geometry)}
 * and {@link PreparedPolygon#intersects(Geometry)}
 * operations.
 * 
 * @author Owner
 *
 */
public class PreparedPolygonPredicateStressTest extends TestCase
{

  public static void main(String[] args) {
    TestRunner.run(PreparedPolygonPredicateStressTest.class);
  }

  boolean testFailed = false;

  public PreparedPolygonPredicateStressTest(String name) {
    super(name);
  }

  public void test()
  {
  	PredicateStressTester tester = new PredicateStressTester();
  	tester.run(1000);
  }
  
  static class PredicateStressTester
  extends StressTestHarness
  {
  	public boolean checkResult(Geometry target, Geometry test) {
  		if (! checkIntersects(target, test)) return false;
        return checkContains(target, test);
    }
  }
  
  public static boolean checkContains(Geometry target, Geometry test)
  {
	boolean expectedResult = target.contains(test);

      PreparedGeometry prepGeom = PreparedGeometryFactory.create(target);
    
	boolean prepResult = prepGeom.contains(test);

      return prepResult == expectedResult;
  } 

  public static boolean checkIntersects(Geometry target, Geometry test)
  {
	boolean expectedResult = target.intersects(test);

      PreparedGeometry prepGeom = PreparedGeometryFactory.create(target);
	
	boolean prepResult = prepGeom.intersects(test);

      return prepResult == expectedResult;
  } 

}
