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

package org.locationtech.jtstest.testbuilder.topostretch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.GeometryEditor;

public class GeometryVerticesMover 
{
  public static Geometry move(Geometry geom, Map<Coordinate, Coordinate> moves)
  {
  	GeometryVerticesMover mover = new GeometryVerticesMover(geom, moves);
    return mover.move();
  }

  private final Geometry geom;
  private final Map<Coordinate, Coordinate> moves;
  private final List<Coordinate> modifiedCoords = new ArrayList<>();

  public GeometryVerticesMover(Geometry geom, Map<Coordinate, Coordinate> moves)
  {
  	this.geom = geom;
  	this.moves = moves;
  }
  
  public Geometry move()
  {
    GeometryEditor editor = new GeometryEditor();
    MoveVerticesOperation op = new MoveVerticesOperation(moves);
      return editor.edit(geom, new MoveVerticesOperation(moves));
  }
  
  public List<Coordinate> getModifiedCoordinates()
  {
  	return modifiedCoords;
  }

  private class MoveVerticesOperation
    extends GeometryEditor.CoordinateOperation
  {
    private final Map<Coordinate, Coordinate> moves;

    public MoveVerticesOperation(Map<Coordinate, Coordinate> moves)
    {
      this.moves = moves;
    }
    
    
    public Coordinate[] edit(Coordinate[] coords,
        Geometry geometry)
    {
      Coordinate[] newPts = new Coordinate[coords.length];
      for (int i = 0; i < coords.length; i++) {
        newPts[i] = movedPt(coords[i]);                   
      }
      return newPts;
    }
    
    private Coordinate movedPt(Coordinate orig)
    {
    	Coordinate newLoc = moves.get(orig);
    	if (newLoc == null)
    		return orig;
    	Coordinate mod = (Coordinate) newLoc.clone();
    	modifiedCoords.add(mod);
    	return mod;
    }
  }

  
}
