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

package org.locationtech.jts.io.geojson;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import test.jts.GeometryTestCase;


public class GeoJsonWriterTest extends GeometryTestCase {

  public GeoJsonWriter geoJsonWriter;

  public GeoJsonWriterTest(String name) {
    super(name);
  }

  @Override
  public void setUp() {
    this.geoJsonWriter = new GeoJsonWriter();
  }

  public void testCRS() {
    runTest("POINT (1 2)", 1234,
        "{'type':'Point','coordinates':[1,2],'crs':{'type':'name','properties':{'name':'EPSG:1234'}}}"
        );
  }

  public void testPoint() {
    runTest("POINT (1 2)",
        "{'type':'Point','coordinates':[1,2]}"
        );
  }

  public void testPointEmpty() {
    runTest("POINT EMPTY",
        "{'type':'Point','coordinates':[]}"
    );
  }

  public void testLineString() {
    runTest("LINESTRING (1 2, 10 20, 100 200)",
        "{'type':'LineString','coordinates':[[1,2],[10,20],[100,200]]}");
  }

  public void testLineStringEmpty() {
    runTest("LINESTRING EMPTY",
        "{'type':'LineString','coordinates':[]}"
    );
  }

  public void testPolygon() {
    runTest("POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0))",
        "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]]]}");
  }

  public void testPolygonEmpty() {
    runTest("POLYGON EMPTY",
        "{'type':'Polygon','coordinates':[]}"
    );
  }

  public void testPolygonWithHole() {
    runTest("POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0), (1 1, 1 10, 10 10, 10 1, 1 1) )",
        "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]],[[1,1],[1,10],[10,10],[10,1],[1,1]]]}");
  }

  public void testPolygonRightHandRule() {
    runTest("POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0))", true,
            "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]]]}");
  }

  public void testPolygonWithHoleRightHandRule() {
    runTest("POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0), (1 1, 10 1, 10 10, 1 10, 1 1) )", true,
            "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]],[[1,1],[1,10],[10,10],[10,1],[1,1]]]}");
  }

  public void testMultiPoint() {
    runTest("MULTIPOINT ((0 0), (1 4), (100 200))",
        "{'type':'MultiPoint','coordinates':[[0.0,0.0],[1,4],[100,200]]}");
  }

  public void testMultiLineString() {
    runTest("MULTILINESTRING ((0 0, 1 10), (10 10, 20 30), (123 123, 456 789))",
        "{'type':'MultiLineString','coordinates':[[[0.0,0.0],[1,10]],[[10,10],[20,30]],[[123,123],[456,789]]]}");
  }

  public void testMultiPolygon() {
    runTest("MULTIPOLYGON ( ((0 0, 100 0, 100 100, 0 100, 0 0), (1 1, 1 10, 10 10, 10 1, 1 1) ), ((200 200, 200 250, 250 250, 250 200, 200 200)) )",
        "{'type':'MultiPolygon','coordinates':[[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]],[[1,1],[1,10],[10,10],[10,1],[1,1]]],[[[200,200],[200,250],[250,250],[250,200],[200,200]]]]}"
        );
  }

  public void testGeometryCollection() {
    runTest("GEOMETRYCOLLECTION ( POINT ( 1 1), LINESTRING (0 0, 10 10), POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0)) )",
        "{'type':'GeometryCollection','geometries':[{'type':'Point','coordinates':[1,1]},{'type':'LineString','coordinates':[[0.0,0.0],[10,10]]},{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]]]}]}"
        );
  }

  // empty atomic geometries are not supported in GeoJSON
  
  public void testMultiPointEmpty() {
    runTest("MULTIPOINT EMPTY", "{'type':'MultiPoint','coordinates':[]}");
  }

  public void testMultiLineStringEmpty() {
    runTest("MULTILINESTRING EMPTY","{'type':'MultiLineString','coordinates':[]}");
  }

  public void testMultiPolygonEmpty() {
    runTest("MULTIPOLYGON EMPTY","{'type':'MultiPolygon','coordinates':[]}");
  }

  public void testGeometryCollectionEmpty() {
    runTest("GEOMETRYCOLLECTION EMPTY","{'type':'GeometryCollection','geometries':[]}");
  }

  private void runTest(String wkt) {
    Geometry expected = read(wkt);
    geoJsonWriter.setEncodeCRS(false);
    String json = this.geoJsonWriter.write(expected);
    System.out.println('"' + json.replace('"', '\'') + '"');
    //checkEqual(result, expected);
  }
 
  private void runTest(String wkt, String expectedGeojson) {
    runTest(wkt, 0, false, false, expectedGeojson);
  }

  private void runTest(String wkt, int srid, String expectedGeojson) {
    runTest(wkt, srid, true, false, expectedGeojson);
  }

  private void runTest(String wkt, boolean enforceRHR, String expectedGeojson) {
    runTest(wkt, 0, false, enforceRHR, expectedGeojson);
  }

  private void runTest(String wkt, int srid, boolean encodeCRS, boolean enforceRHR, String expectedGeojson) {
    Geometry geom = read(wkt);
    geom.setSRID(srid);
    geoJsonWriter.setEncodeCRS(encodeCRS);
    geoJsonWriter.setForceCCW(enforceRHR);
    String json = this.geoJsonWriter.write(geom);
    json = json.replace('"', '\'');
    assertEquals(expectedGeojson, json);
  }

}
