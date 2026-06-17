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
package org.locationtech.jtstest.function;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.GeometryMapper;
import org.locationtech.jts.geom.util.LinearComponentExtracter;
import org.locationtech.jts.noding.SegmentString;
import org.locationtech.jts.operation.buffer.BufferCurveSetBuilder;
import org.locationtech.jts.operation.buffer.BufferInputLineSimplifier;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.buffer.OffsetCurveBuilder;
import org.locationtech.jts.operation.buffer.VariableBuffer;
import org.locationtech.jts.operation.buffer.validate.BufferResultValidator;
import org.locationtech.jtstest.geomfunction.Metadata;


public class BufferFunctions {
	
	public static final String bufferDescription = "Buffers a geometry by a distance";

    private BufferFunctions() {
    }

    @Metadata(description="Buffer a geometry by a distance")
  public static Geometry buffer(Geometry g, double distance)    {   return g.buffer(distance);  }

	public static Geometry bufferWithParams(Geometry g, 
	    Double distance,
	    @Metadata(title="Quadrant Segs")
			Integer quadrantSegments, 
      @Metadata(title="Cap style")
			Integer capStyle, 
      @Metadata(title="Join style")
			Integer joinStyle, 
      @Metadata(title="Mitre limit")
			Double mitreLimit)	
	{
	    double dist = 0;
	    if (distance != null) dist = distance;
	    
	    BufferParameters bufParams = new BufferParameters();
	    if (quadrantSegments != null)	bufParams.setQuadrantSegments(quadrantSegments);
	    if (capStyle != null)	bufParams.setEndCapStyle(capStyle);
	    if (joinStyle != null) 	bufParams.setJoinStyle(joinStyle);
	    if (mitreLimit != null) 	bufParams.setMitreLimit(mitreLimit);
	    
	    return BufferOp.bufferOp(g, dist, bufParams);
	}
	
	public static Geometry bufferWithSimplify(Geometry g, Double distance,
	    @Metadata(title="Simplify factor")
			Double simplifyFactor)	
	{
	    double dist = 0;
	    if (distance != null) dist = distance;
	    
	    BufferParameters bufParams = new BufferParameters();
	    if (simplifyFactor != null)	bufParams.setSimplifyFactor(simplifyFactor);
	    
	    return BufferOp.bufferOp(g, dist, bufParams);
	}
	
	public static Geometry bufferCurve(Geometry g, double distance)	
	{		
    return buildCurveSet(g, distance, new BufferParameters());
	}
	
	public static Geometry bufferCurveWithParams(Geometry g, 
      Double distance,
      @Metadata(title="Quadrant Segs")
      Integer quadrantSegments, 
      @Metadata(title="Cap style")
      Integer capStyle, 
      @Metadata(title="Join style")
      Integer joinStyle, 
      @Metadata(title="Mitre limit")
      Double mitreLimit)  	
	{
    double dist = 0;
    if (distance != null) dist = distance;
    
    BufferParameters bufParams = new BufferParameters();
    if (quadrantSegments != null)	bufParams.setQuadrantSegments(quadrantSegments);
    if (capStyle != null)	bufParams.setEndCapStyle(capStyle);
    if (joinStyle != null) 	bufParams.setJoinStyle(joinStyle);
    if (mitreLimit != null) 	bufParams.setMitreLimit(mitreLimit);
    
    return buildCurveSet(g, dist, bufParams);
	}
	
  private static Geometry buildCurveSet(Geometry g, double dist, BufferParameters bufParams)
  {
    // --- now construct curve
    BufferCurveSetBuilder ocsb = new BufferCurveSetBuilder(g, dist, 
        g.getFactory().getPrecisionModel(),
        bufParams);
    List<SegmentString> curves = ocsb.getCurves();

    List<LineString> lines = new ArrayList<>();
      for (SegmentString ss : curves) {
          Coordinate[] pts = ss.getCoordinates();
          lines.add(g.getFactory().createLineString(pts));
      }
      return g.getFactory().buildGeometry(lines);
  }

	public static Geometry bufferLineSimplifier(Geometry g, double distance)	
	{   
    return buildBufferLineSimplifiedSet(g, distance);
	}

  private static Geometry buildBufferLineSimplifiedSet(Geometry g, double distance)
  {
    List<LineString> simpLines = new ArrayList<>();

    List<LineString> lines = new ArrayList<>();
    LinearComponentExtracter.getLines(g, lines);
      for (LineString line : lines) {
          Coordinate[] pts = line.getCoordinates();
          simpLines.add(g.getFactory().createLineString(BufferInputLineSimplifier.simplify(pts, distance)));
      }
      return g.getFactory().buildGeometry(simpLines);
  }

  public static Geometry bufferValidated(Geometry g, double distance)
  {
    Geometry buf = g.buffer(distance);
    String errMsg = BufferResultValidator.isValidMsg(g, distance, buf);
    if (errMsg != null)
      throw new IllegalStateException("Buffer Validation error: " + errMsg);
    return buf;
  }

  public static Geometry bufferValidatedGeom(Geometry g, double distance)
  {
    Geometry buf = g.buffer(distance);
    BufferResultValidator validator = new BufferResultValidator(g, distance, buf);    
    boolean isValid = validator.isValid();
    return validator.getErrorIndicator();
  }

  public static Geometry singleSidedBufferCurve(Geometry geom, double distance) {
    BufferParameters bufParam = new BufferParameters();
    bufParam.setSingleSided(true);
    OffsetCurveBuilder ocb = new OffsetCurveBuilder(
        geom.getFactory().getPrecisionModel(), bufParam
        );
    Coordinate[] pts = ocb.getLineCurve(geom.getCoordinates(), distance);
      return geom.getFactory().createLineString(pts);
  }
  
  public static Geometry singleSidedBuffer(Geometry geom, double distance) {
    BufferParameters bufParams = new BufferParameters();
    bufParams.setSingleSided(true);
    return BufferOp.bufferOp(geom, distance, bufParams);
  }
  
  public static Geometry bufferEach(Geometry g, final double distance)
  {
    return GeometryMapper.map(g, g1 -> g1.buffer(distance));
  }

  public static Geometry bufferAndInverse(Geometry g, double distance) {
    return g.buffer(distance).buffer(-distance);
  }
  
  @Metadata(description="Buffer a line by a distance varying along the line")
  public static Geometry variableBuffer(Geometry line,
      @Metadata(title="Start distance")
      double startDist,
      @Metadata(title="End distance")
      double endDist) {
    if (line instanceof Polygon) {
      line = ((Polygon) line).getExteriorRing();
    }
    return VariableBuffer.buffer(line, startDist, endDist);
  }
  
  @Metadata(description="Buffer a line by a distance varying along the line, with distances for start/end and the middle")
  public static Geometry variableBufferMid(Geometry line,
      @Metadata(title="Start distance")
      double startDist,
      @Metadata(title="Middle distance")
      double midDist)  
  {
    if (line instanceof Polygon) {
      line = ((Polygon) line).getExteriorRing();
    }
    return VariableBuffer.buffer(line, startDist, midDist, startDist);
  }
  
  public static Geometry bufferRadius(Geometry radiusLine) {
    double distance = radiusLine.getLength();
    Coordinate centrePt = radiusLine.getCoordinate();
    Point centre = radiusLine.getFactory().createPoint(centrePt);
    return centre.buffer(distance);
  }
}
