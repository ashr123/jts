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

import org.json.simple.parser.JSONParser;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Reads a GeoJSON Geometry from a JSON fragment into a {@link Geometry}.
 * <p>
 * The current GeoJSON specification is 
 * <a href='https://tools.ietf.org/html/rfc7946'>https://tools.ietf.org/html/rfc7946</a>.
 * An older specification is on the GeoJSON web site:
 * <a href='http://geojson.org/geojson-spec.html'>http://geojson.org/geojson-spec.html</a>.
 * <p>
 * The reader does not require a particular orientation for polygon rings.
 * <p>
 * The reader reads empty or null coordinate arrays as empty geometries.
 * <p>
 * It is the caller's responsibility to ensure that the supplied
 * {@link PrecisionModel} matches the precision of the incoming data. If a lower
 * precision for the data is required, a subsequent process must be run on the
 * data to reduce its precision.
 * 
 * @author Martin Davis
 * @author Paul Howells, Vivid Solutions.
 * 
 */
public class GeoJsonReader {

  private GeometryFactory gf;

  /**
   * The default constructor uses the SRID from the Geojson CRS and the
   * default <code>PrecisionModel</code> to create a
   * <code>GeometryFactory</code>. If there is no CRS specified then the default
   * CRS is a geographic coordinate reference system, using the WGS84 datum, and
   * with longitude and latitude units of decimal degrees (SRID = 4326)
   */
  public GeoJsonReader() {
    // do nothing
  }

  /**
   * This constructor accepts a <code>GeometryFactory</code> that is used
   * to create the output geometries and to override the GeoJson CRS.
   * 
   * @param geometryFactory
   *          a GeometryFactory
   */
  public GeoJsonReader(GeometryFactory geometryFactory) {
    this.gf = geometryFactory;
  }

  /**
   * Reads a GeoJson Geometry from a <tt>String</tt> into a single
   * {@link Geometry}.
   * 
   * 
   * @param json
   *          The GeoJson String to parse
   * @return the resulting JTS Geometry
   * 
   * @throws ParseException
   *           throws a ParseException if the JSON string cannot be parsed
   */
  public Geometry read(String json) throws ParseException {
      return read(new StringReader(json));
  }

  /**
   * Reads a GeoJson Geometry from a {@link Reader} into a single
   * {@link Geometry}.
   * 
   * 
   * @param reader
   *          The input source
   * @return The resulting JTS Geometry
   * 
   * @throws ParseException
   *           throws a ParseException if the JSON string cannot be parsed as a Geometry
   */
  @SuppressWarnings("unchecked")
  public Geometry read(Reader reader) throws ParseException {
    Map<String, Object> geometryMap = null;
    JSONParser parser = new JSONParser();
    try {
      Object obj =  parser.parse(reader);
      geometryMap = (Map<String, Object>) obj;
    } catch (ClassCastException e) {
      throw new ParseException("Could not parse Geometry from Json string.");
    }catch (org.json.simple.parser.ParseException | IOException e) {
      throw new ParseException(e);
    }

      GeometryFactory geometryFactory = null;
    if (this.gf == null) {
      geometryFactory = GeoJsonReader.getGeometryFactory(geometryMap);
    } else {
      geometryFactory = this.gf;
    }

      return create(geometryMap, geometryFactory);
  }

  private Geometry create(Map<String, Object> geometryMap,
      GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    String type = (String) geometryMap.get(GeoJsonConstants.NAME_TYPE);

    if (type == null) {
      throw new ParseException(
          "Could not parse Geometry from Json string.  No 'type' property found.");
    } else {

        switch (type) {
            case GeoJsonConstants.NAME_POINT:
                result = createPoint(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_LINESTRING:
                result = createLineString(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_POLYGON:
                result = createPolygon(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_MULTIPOINT:
                result = createMultiPoint(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_MULTILINESTRING:
                result = createMultiLineString(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_MULTIPOLYGON:
                result = createMultiPolygon(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_GEOMETRYCOLLECTION:
                result = createGeometryCollection(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_FEATURE:
                result = createFeature(geometryMap, geometryFactory);

                break;
            case GeoJsonConstants.NAME_FEATURECOLLECTION:
                result = createFeatureCollection(geometryMap, geometryFactory);

                break;
            default:
                throw new ParseException(
                        "Could not parse Geometry from GeoJson string.  Unsupported 'type':"
                                + type);
        }
    }

    return result;
  }

  private Geometry createFeatureCollection(Map<String, Object> geometryMap,
      GeometryFactory geometryFactory) throws ParseException {
    try {
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> features = (List<Map<String, Object>>) geometryMap.get(GeoJsonConstants.NAME_FEATURES);

      Geometry[] geometries = new Geometry[features.size()];
      int i = 0;
      for (Map<String, Object> featureMap : features) {
        geometries[i] = createFeature(featureMap, geometryFactory);
        ++i;
      }

      return geometryFactory.createGeometryCollection(geometries);
    } catch (RuntimeException e) {
      throw new ParseException("Could not parse FeatureCollection from GeoJson string.", e);
    }
  }

  private Geometry createFeature(Map<String, Object> geometryMap,
      GeometryFactory geometryFactory) throws ParseException {
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> innerGeometryMap = (Map<String, Object>) geometryMap.get(GeoJsonConstants.NAME_GEOMETRY);
      return create(innerGeometryMap, geometryFactory);
    } catch (RuntimeException e) {
      throw new ParseException("Could not parse Feature from GeoJson string.", e);
    }
  }

  private Geometry createGeometryCollection(Map<String, Object> geometryMap,
      GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<Map<String, Object>> geometriesList = (List<Map<String, Object>>) geometryMap
          .get(GeoJsonConstants.NAME_GEOMETRIES);

      Geometry[] geometries = new Geometry[geometriesList.size()];

      int i = 0;
      for (Map<String, Object> map : geometriesList) {

        geometries[i] = this.create(map, geometryFactory);

        ++i;
      }

      result = geometryFactory.createGeometryCollection(geometries);

    } catch (RuntimeException e) {
      throw new ParseException(
          "Could not parse GeometryCollection from GeoJson string.", e);
    }

    return result;
  }

  private static Geometry createMultiPolygon(Map<String, Object> geometryMap,
                                             GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<List<List<List<Number>>>> polygonsList = (List<List<List<List<Number>>>>) geometryMap
          .get(GeoJsonConstants.NAME_COORDINATES);

      Polygon[] polygons = new Polygon[polygonsList.size()];

      int p = 0;
      for (List<List<List<Number>>> ringsList : polygonsList) {

        List<CoordinateSequence> rings = new ArrayList<>();

        for (List<List<Number>> coordinates : ringsList) {

          rings.add(createCoordinateSequence(coordinates));
        }

        if (rings.isEmpty()) {
          continue;
        }

        LinearRing outer = geometryFactory.createLinearRing(rings.get(0));
        LinearRing[] inner = null;
        if (rings.size() > 1) {
          inner = new LinearRing[rings.size() - 1];
          for (int i = 1; i < rings.size(); i++) {
            inner[i - 1] = geometryFactory.createLinearRing(rings.get(i));
          }
        }

        polygons[p] = geometryFactory.createPolygon(outer, inner);

        ++p;
      }

      result = geometryFactory.createMultiPolygon(polygons);

    } catch (RuntimeException e) {
      throw new ParseException(
          "Could not parse MultiPolygon from GeoJson string.", e);
    }

    return result;
  }

  private static Geometry createMultiLineString(Map<String, Object> geometryMap,
                                                GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<List<List<Number>>> linesList = (List<List<List<Number>>>) geometryMap
          .get(GeoJsonConstants.NAME_COORDINATES);

      LineString[] lineStrings = new LineString[linesList.size()];

      int i = 0;
      for (List<List<Number>> coordinates : linesList) {

        lineStrings[i] = geometryFactory
            .createLineString(createCoordinateSequence(coordinates));

        ++i;
      }

      result = geometryFactory.createMultiLineString(lineStrings);

    } catch (RuntimeException e) {
      throw new ParseException(
          "Could not parse MultiLineString from GeoJson string.", e);
    }

    return result;
  }

  private static Geometry createMultiPoint(Map<String, Object> geometryMap,
                                           GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<List<Number>> coordinatesList = (List<List<Number>>) geometryMap
          .get(GeoJsonConstants.NAME_COORDINATES);

      CoordinateSequence coordinates = GeoJsonReader
          .createCoordinateSequence(coordinatesList);

      result = geometryFactory.createMultiPoint(coordinates);

    } catch (RuntimeException e) {
      throw new ParseException(
          "Could not parse MultiPoint from GeoJson string.", e);
    }

    return result;
  }

  private static Geometry createPolygon(Map<String, Object> geometryMap,
                                        GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<List<List<Number>>> ringsList = (List<List<List<Number>>>) geometryMap
          .get(GeoJsonConstants.NAME_COORDINATES);

      if (ringsList == null || ringsList.isEmpty()) {
        return geometryFactory.createPolygon();
      }

      List<CoordinateSequence> rings = new ArrayList<>();

      for (List<List<Number>> coordinates : ringsList) {

        rings.add(createCoordinateSequence(coordinates));
      }

      LinearRing outer = geometryFactory.createLinearRing(rings.get(0));
      LinearRing[] inner = null;
      if (rings.size() > 1) {
        inner = new LinearRing[rings.size() - 1];
        for (int i = 1; i < rings.size(); i++) {
          inner[i - 1] = geometryFactory.createLinearRing(rings.get(i));
        }
      }

      result = geometryFactory.createPolygon(outer, inner);

    } catch (RuntimeException e) {
      throw new ParseException("Could not parse Polygon from GeoJson string.",
          e);
    }

    return result;
  }

  private static Geometry createLineString(Map<String, Object> geometryMap,
                                           GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<List<Number>> coordinatesList = (List<List<Number>>) geometryMap
          .get(GeoJsonConstants.NAME_COORDINATES);

      CoordinateSequence coordinates = createCoordinateSequence(coordinatesList);

      result = geometryFactory.createLineString(coordinates);

    } catch (RuntimeException e) {
      throw new ParseException(
          "Could not parse LineString from GeoJson string.", e);
    }

    return result;
  }

  private static Geometry createPoint(Map<String, Object> geometryMap,
                                      GeometryFactory geometryFactory) throws ParseException {

    Geometry result = null;

    try {

      @SuppressWarnings("unchecked")
      List<Number> coordinateList = (List<Number>) geometryMap
          .get(GeoJsonConstants.NAME_COORDINATES);

      CoordinateSequence coordinate = GeoJsonReader.createCoordinate(coordinateList);

      result = geometryFactory.createPoint(coordinate);

    } catch (RuntimeException e) {
      throw new ParseException("Could not parse Point from GeoJson string.", e);
    }

    return result;
  }

  private static GeometryFactory getGeometryFactory(Map<String, Object> geometryMap)
      throws ParseException {

    GeometryFactory result = null;
    @SuppressWarnings("unchecked")
    Map<String, Object> crsMap = (Map<String, Object>) geometryMap.get(GeoJsonConstants.NAME_CRS);
    Integer srid = null;
    
    if (crsMap != null) {

      try {

        @SuppressWarnings("unchecked")
        Map<String, Object> propertiesMap = (Map<String, Object>) crsMap
            .get(GeoJsonConstants.NAME_PROPERTIES);
        String name = (String) propertiesMap.get(GeoJsonConstants.NAME_NAME);
        String[] split = name.split(":");
        String epsg = split[1];
        srid = Integer.valueOf(epsg);
      } catch (RuntimeException e) {
        throw new ParseException(
            "Could not parse SRID from Geojson 'crs' object.", e);
      }
    }

    if (srid == null) {
      // The default CRS is a geographic coordinate reference
      // system, using the WGS84 datum, and with longitude and
      // latitude units of decimal degrees. SRID 4326
      srid = 4326;
    }

    result = new GeometryFactory(new PrecisionModel(), srid);
    return result;
  }

  private static CoordinateSequence createCoordinateSequence(
          List<List<Number>> coordinates) {
    CoordinateSequence result = null;
    if (coordinates == null) {
      coordinates = Collections.emptyList();
    }

    result = new CoordinateArraySequence(coordinates.size());

    for (int i = 0; i < coordinates.size(); ++i) {
      List<Number> ordinates = coordinates.get(i);

      if (!ordinates.isEmpty()) {
        result.setOrdinate(i, 0, ordinates.get(0).doubleValue());
      }
      if (ordinates.size() > 1) {
        result.setOrdinate(i, 1, ordinates.get(1).doubleValue());
      }
      if (ordinates.size() > 2) {
        result.setOrdinate(i, 2, ordinates.get(2).doubleValue());
      }

    }

    return result;
  }

  private static CoordinateSequence createCoordinate(List<Number> ordinates) {
    if (ordinates == null || ordinates.isEmpty()) {
      return new CoordinateArraySequence(0);
    }

    CoordinateSequence result = new CoordinateArraySequence(1);

    if (!ordinates.isEmpty()) {
      result.setOrdinate(0, 0, ordinates.get(0).doubleValue());
    }
    if (ordinates.size() > 1) {
      result.setOrdinate(0, 1, ordinates.get(1).doubleValue());
    }
    if (ordinates.size() > 2) {
      result.setOrdinate(0, 2, ordinates.get(2).doubleValue());
    }

    return result;
  }
}
