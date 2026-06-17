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
package org.locationtech.jts.io;

import org.locationtech.jts.geom.GeometryFactory;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Tests the {@link WKTReader} with various syntax errors
 */
public class WKTReaderParseErrorTest
    extends TestCase
{
  public static void main(String[] args) {
    TestRunner.run(WKTReaderParseErrorTest.class);
  }

  private final GeometryFactory fact = new GeometryFactory();
  private final WKTReader rdr = new WKTReader(fact);

  public WKTReaderParseErrorTest(String name)
  {
    super(name);
  }

  public void testExtraLParen() {
    readWithParseException("POINT (( 1e01 -1E02)");
  }

  public void testMissingOrdinate() {
    readWithParseException("POINT ( 1e01 )");
  }

  public void testBadChar() {
    readWithParseException("POINT ( # 1e-04 1E-05)");
  }

  public void testBadExpFormat() {
    readWithParseException("POINT (1e0a1 1X02)");
  }

  public void testBadExpPlusSign() {
    readWithParseException("POINT (1e+01 1X02)");
  }

  public void testBadPlusSign() {
    readWithParseException("POINT ( +1e+01 1X02)");
  }

  public void testBadNumber() {
    readWithParseException("POINT (0x 0)");
    readWithParseException("POINT (0e 0)");
    readWithParseException("POINT (0.. 0)");
  }
  
  public void testBadCharsInType() {
    readWithParseException("POINTABC ( 0 0 )");
    readWithParseException("LINESTRINGABC ( 0 0 )");
    readWithParseException("LINEARRINGABC ( 0 0, 0 0, 0 0 )");
    readWithParseException("POLYGONABC (( 0 0, 0 0, 0 0, 0 0 ))");
    readWithParseException("MULTIPOINTABC (( 0 0 ), ( 0 0 ))");
    readWithParseException("MULTILINESTRINGABC (( 0 0, 1 1 ), ( 0 0, 1 1 ))");
    readWithParseException("MULTIPOLYGONABC ((( 0 0, 1 1, 2 2, 0 0 )), (( 0 0, 1 1, 2 2, 0 0 )))");
    readWithParseException("GEOMETRYCOLLECTIONABC (POINT( 0 0 ), LINESTRING( 0 0, 1 1))"); 
  }

  public void testBadCharsInTypeZ() {
    readWithParseException("POINTABCZ ( 0 0 )");
    readWithParseException("LINESTRINGABCZ ( 0 0 )");
    readWithParseException("LINEARRINGABCZ ( 0 0, 0 0, 0 0 )");
    readWithParseException("POLYGONABCZ (( 0 0, 0 0, 0 0, 0 0 ))");
    readWithParseException("MULTIPOINTABCZ (( 0 0 ), ( 0 0 ))");
    readWithParseException("MULTILINESTRINGABCZ (( 0 0, 1 1 ), ( 0 0, 1 1 ))");
    readWithParseException("MULTIPOLYGONABCZ ((( 0 0, 1 1, 2 2, 0 0 )), (( 0 0, 1 1, 2 2, 0 0 )))");
    readWithParseException("GEOMETRYCOLLECTIONABCZ (POINT( 0 0 ), LINESTRING( 0 0, 1 1))");
  }

  public void testBadCharsInTypeM() {
    readWithParseException("LINESTRINGABCM ( 0 0 0, 1 1 1 )");
  }

  public void testBadCharsInTypeZM() {
    readWithParseException("LINESTRINGABCZM ( 0 0 0 0, 1 1 1 1 )");
  }

  public void testBadType() {
    readWithParseException("POIN (0 0)");
    readWithParseException("POIN T(0 0)");
    readWithParseException("P OINT (0 0)");
    readWithParseException("POINtt (0 0)");
    readWithParseException("POINTzz (0 0)");
    readWithParseException("POINTabc (0 0)");
    readWithParseException("POINTxy (0 0)");
    readWithParseException("POINT XY (0 0)");
    readWithParseException("POINT XY EMPT");
    readWithParseException("POINT XY EMPT Y");
    readWithParseException("POINT XY EMPTYY");  
    
    //-- not an error, since parser stops after correct parse
    //checkParseError("POINT EMPTY Z");  
  }
  
  public void testBadDimension() {
    readWithParseException("POINTZZ (0 0 0)");
    readWithParseException("POINT ZZ (0 0 0)");
    readWithParseException("POINT ZZM (0 0 0)");
    
    readWithParseException("POINT Z M (0 0 0 0)");
    readWithParseException("POINTZ M (0 0 0 0)");
    readWithParseException("POINT MZ (0 0 0 0)");
    readWithParseException("POINTMZ (0 0 0 0)");
    readWithParseException("POINTZ ZM (0 0 0 0)");
    readWithParseException("POINT ZMc (0 0 0 0)");  
    
    //-- not errors; perhaps should be?
    //checkParseErrorZ("POINTZ Z (0 0 0)");
    //checkParseErrorZM("POINTZM Z (0 0 0 0)");
  }
  
  public void testMissingOrdinates() {
    readWithParseException("POINT (0)");
    readWithParseException("LINESTRING (0, 1 1)");
  }
  
  public void testMissingComponents() {
    readWithParseException("MULTILINESTRING (0 0)");
    readWithParseException("MULTILINESTRING ()");
    readWithParseException("GEOMETRYCOLLECTION ()");
    readWithParseException("GEOMETRYCOLLECTION");  
  }
  
  public void testEmptyComponents() throws ParseException {
    readWithInvalidException("POLYGON( EMPTY, (1 1,2 2,1 2,1 1))");
    
    //-- empty rings are valid
    //checkInvalidError("POLYGON( (1 1,2 2,1 2,1 1), EMPTY)");
  }
  
  private void readWithParseException(String wkt) {
    boolean threwParseEx = false;
    try {
      rdr.read(wkt);
    }
    catch (ParseException ex) {
      //System.out.println(ex.getMessage());
      threwParseEx = true;
    }
    assertTrue(threwParseEx);
  }
  
  private void readWithInvalidException(String wkt)
      throws ParseException
  {
    try {
      rdr.read(wkt);
    }
    catch (IllegalArgumentException ex) {
      //System.out.println(ex.getMessage());
      return;
    }
    fail();  
  }
}
