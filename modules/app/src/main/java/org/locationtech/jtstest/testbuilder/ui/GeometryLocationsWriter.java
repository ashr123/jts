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

package org.locationtech.jtstest.testbuilder.ui;

import java.util.List;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jtstest.testbuilder.geom.GeometryElementLocater;
import org.locationtech.jtstest.testbuilder.geom.FacetLocater;
import org.locationtech.jtstest.testbuilder.geom.GeometryLocation;
import org.locationtech.jtstest.testbuilder.geom.VertexLocater;
import org.locationtech.jtstest.testbuilder.model.Layer;
import org.locationtech.jtstest.testbuilder.model.LayerList;


public class GeometryLocationsWriter 
{
  public static String writeLocation(LayerList layers,
      Coordinate pt, double tolerance)
  {
    GeometryLocationsWriter writer = new GeometryLocationsWriter();
    return writer.writeLocationString(layers, pt, tolerance);
  }

  private static final int MAX_ITEMS_TO_DISPLAY = 10;

    private String eol = null;
  private String highlightStart = null;
  private String highlightEnd = null;
  private String documentStart = null;
  private String documentEnd = null;
  
  public GeometryLocationsWriter() {
    setHtml(true);
  }

  public void setHtml(boolean isHtmlFormatted) 
  {
      if (isHtmlFormatted) {
      eol = "<br>";
      highlightStart = "<b>";
      highlightEnd = "</b>";
      documentStart = "<html>";
      documentEnd = "</html>";
    }
    else {
      eol = "\n";
      highlightStart = "";
      highlightEnd = "";
      documentStart = "";
      documentEnd = "";     
    }
 }
  
  public String writeLocationString(LayerList layers,
      Coordinate pt, double tolerance)
  {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < layers.size(); i++) {

      Layer lyr = layers.getLayer(i);
      String locStr = writeLocation(lyr, pt, tolerance);
      if (locStr == null) continue;
      
      if (i > 0 && text.length() > 0) {
        text.append(eol);
        text.append(eol);
      }
      
      text.append(highlightStart).append(lyr.getName()).append(highlightEnd).append(eol);
      text.append(locStr);
    }
    
    if (text.length() > 0) {
      return documentStart + text +documentEnd;
    }
    return null;
  }
    
  public static String writeSingleLocation(Layer lyr, Coordinate p, double tolerance)
  {
    Geometry geom = lyr.getGeometry();
    if (geom == null) return null;
    
    VertexLocater locater = new VertexLocater(geom);
    Coordinate coord = locater.getVertex(p, tolerance);
    int index = locater.getIndex();
    
    if (coord == null) return null;
    return "[" + index + "]: " 
      + coord.x + ", " + coord.y;
  }
  
  public String writeLocation(Layer lyr, Coordinate p, double tolerance)
  {
    Geometry geom = lyr.getGeometry();
    if (geom == null) return null;
    
    String locStr = writeElementLocation(geom, p, tolerance);
    String facetStr = writeFacetLocation(geom, p, tolerance);
    if (facetStr == null) 
      return locStr;
    return locStr + facetStr;   
  }
  
  
  public String writeElementLocation(Geometry geom, Coordinate p, double tolerance)
  {
    GeometryElementLocater locater = new GeometryElementLocater(geom);
    List<GeometryLocation> locs = locater.getElements(p, tolerance);

    StringBuilder buf = new StringBuilder();
    int count = 0;
      for (GeometryLocation loc : locs) {

          Geometry comp = loc.getElement();

          String path = loc.pathString();
          path = path.isEmpty() ? "" : path;
          buf.append("[").append(path).append("]  ");

          buf.append(comp.getGeometryType().toUpperCase());
          if (comp instanceof GeometryCollection) {
              buf.append("[").append(comp.getNumGeometries()).append("]");
          } else {
              buf.append("(").append(comp.getNumPoints()).append(")");
              if (comp.getDimension() >= 1) {
                  buf.append("  Len: ").append(comp.getLength());
              }
              if (comp.getDimension() >= 2) {
                  buf.append("  Area: ").append(comp.getArea());
              }
          }
          if (comp.getUserData() != null) {
              buf.append("  Data: ");
              buf.append(comp.getUserData().toString());
          }
          buf.append(eol);

          if (count++ > MAX_ITEMS_TO_DISPLAY) {
              buf.append(" & more...").append(eol);
              break;
          }
      }
    String locStr = buf.toString();
    if (locStr.isEmpty())
      return null;
    return locStr;
  }
    
  public String writeFacetLocation(Geometry geom, Coordinate p, double tolerance)
  {
    FacetLocater locater = new FacetLocater(geom);
    List<GeometryLocation> locs = locater.getLocations(p, tolerance);
    /*
    List<GeometryLocation> vertexLocs = FacetLocater.filterVertexLocations(locs);
    
    // only show vertices if some are present, to avoid confusing with segments
    if (! vertexLocs.isEmpty()) 
      return writeFacetLocations(vertexLocs);
    */
    // write 'em all
    return writeFacetLocations(locs);
  }
    
  private String writeFacetLocations(List<GeometryLocation> locs)
  {
    if (locs.isEmpty()) return null;
    
    StringBuilder buf = new StringBuilder();
    boolean isFirst = true;
    int count = 0;
    for (GeometryLocation loc : locs) {

    	if (! isFirst) {
    		buf.append(eol);
    	}

    	isFirst = false;
      
      buf.append(componentType(loc));
      buf.append(loc.isVertex() ? "Vert" : "Seg");
    	buf.append(loc.toFacetString());
    	if (! loc.isVertex()) {
    	  buf.append(" Len: ").append(loc.getLength());
    	}
      if (count++ > MAX_ITEMS_TO_DISPLAY) {
        buf.append(eol).append(" & more...").append(eol);
        break;
      }
    }
    return buf.toString();
  }

  private static String componentType(GeometryLocation loc) {
    String compType = "";
    if (loc.getElement() instanceof LinearRing) {
      boolean isCCW = Orientation.isCCW(loc.getElement().getCoordinates());
      compType = "Ring" 
        + (isCCW ? "-CCW" : "-CW ")
          + " ";
    }
    else if (loc.getElement() instanceof LineString) { 
      compType = "Line  ";
    }
    else if (loc.getElement() instanceof Point) { 
      compType = "Point ";
    }
    return compType;
  }

  public String OLDwriteLocation(Geometry geom, Coordinate p, double tolerance)
  {
    VertexLocater locater = new VertexLocater(geom);
    List<VertexLocater.Location> locs = locater.getLocations(p, tolerance);

    if (locs.isEmpty()) return null;

    StringBuilder buf = new StringBuilder();
    boolean isFirst = true;
      for (VertexLocater.Location vertLoc : locs) {
          int index = vertLoc.getIndices()[0];
          Coordinate pt = vertLoc.getCoordinate();
          if (!isFirst) {
              buf.append(eol).append("--");
          }
          isFirst = false;
          String locStr = "[" + index + "]: "
                  + pt.x + ", " + pt.y;
          buf.append(locStr);
      }
    
    return buf.toString();
  }

}
