/*
 * Copyright (c) 2017 Martin Davis.
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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class OrientationFPFunctions {

    private OrientationFPFunctions() {
    }

    public static int orientationIndex(Geometry segment, Geometry ptGeom) {
    if (segment.getNumPoints() != 2 || ptGeom.getNumPoints() != 1) {
      throw new IllegalArgumentException("A must have two points and B must have one");
    }
    Coordinate[] segPt = segment.getCoordinates();
    
    Coordinate p = ptGeom.getCoordinate();
      return orientationIndex(segPt[0], segPt[1], p);
  }

  private static int orientationIndex(Coordinate p1, Coordinate p2, Coordinate q) {
    double dx1 = p2.x - p1.x;
    double dy1 = p2.y - p1.y;
    double dx2 = q.x - p2.x;
    double dy2 = q.y - p2.y;
    double det = dx1 * dy2 - dx2 * dy1;
      return Double.compare(det, 0.0);
  }
}
