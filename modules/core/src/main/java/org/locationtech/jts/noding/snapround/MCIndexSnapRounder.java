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
package org.locationtech.jts.noding.snapround;

import java.util.Collection;
import java.util.List;

import org.locationtech.jts.algorithm.LineIntersector;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.noding.InteriorIntersectionFinderAdder;
import org.locationtech.jts.noding.MCIndexNoder;
import org.locationtech.jts.noding.NodedSegmentString;
import org.locationtech.jts.noding.Noder;
import org.locationtech.jts.noding.SegmentString;

/**
 * Uses Snap Rounding to compute a rounded,
 * fully noded arrangement from a set of {@link SegmentString}s.
 * Implements the Snap Rounding technique described in 
 * papers by Hobby, Guibas &amp; Marimont, and Goodrich et al.
 * Snap Rounding assumes that all vertices lie on a uniform grid;
 * hence the precision model of the input must be fixed precision,
 * and all the input vertices must be rounded to that precision.
 * <p>
 * This implementation uses a monotone chains and a spatial index to
 * speed up the intersection tests.
 * 
 * <h3>KNOWN BUGS</h3>
 * This implementation is not fully robust.
 * 
 * @deprecated Not robust. Use {@link SnapRoundingNoder} instead.
 *
 * @version 1.7
 */
@Deprecated
public class MCIndexSnapRounder
    implements Noder
{
    private final LineIntersector li;
  private final double scaleFactor;
  private MCIndexNoder noder;
  private MCIndexPointSnapper pointSnapper;
  private Collection<? extends SegmentString> nodedSegStrings;

  public MCIndexSnapRounder(PrecisionModel pm) {
      li = new RobustLineIntersector();
    li.setPrecisionModel(pm);
    scaleFactor = pm.getScale();
  }

  public Collection<SegmentString> getNodedSubstrings()
  {
    return  NodedSegmentString.getNodedSubstrings(nodedSegStrings);
  }

  public void computeNodes(Collection<? extends SegmentString> inputSegmentStrings)
  {
    this.nodedSegStrings = inputSegmentStrings;
    noder = new MCIndexNoder();
    pointSnapper = new MCIndexPointSnapper(noder.getIndex());
    snapRound(inputSegmentStrings, li);

    // testing purposes only - remove in final version
    //checkCorrectness(inputSegmentStrings);
  }

  /*
  private void checkCorrectness(Collection<? extends SegmentString> inputSegmentStrings)
  {
    Collection<SegmentString> resultSegStrings = NodedSegmentString.getNodedSubstrings(inputSegmentStrings);
    NodingValidator nv = new NodingValidator(resultSegStrings);
    try {
      nv.checkValid();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
*/
  
  private void snapRound(Collection<? extends SegmentString> segStrings, LineIntersector li)
  {
    List<Coordinate> intersections = findInteriorIntersections(segStrings, li);
    computeIntersectionSnaps(intersections);
    computeVertexSnaps(segStrings);
  }

  /**
   * Computes all interior intersections in the collection of {@link SegmentString}s,
   * and returns their {@link Coordinate}s.
   *
   * Does NOT node the segStrings.
   *
   * @return a list of Coordinates for the intersections
   */
  private List<Coordinate> findInteriorIntersections(Collection<? extends SegmentString> segStrings, LineIntersector li)
  {
    InteriorIntersectionFinderAdder intFinderAdder = new InteriorIntersectionFinderAdder(li);
    noder.setSegmentIntersector(intFinderAdder);
    noder.computeNodes(segStrings);
    return intFinderAdder.getInteriorIntersections();
  }

  /**
   * Snaps segments to nodes created by segment intersections.
   */
  private void computeIntersectionSnaps(Collection<? extends Coordinate> snapPts)
  {
      for (Coordinate snapPt : snapPts) {
          HotPixel hotPixel = new HotPixel(snapPt, scaleFactor);
          pointSnapper.snap(hotPixel);
      }
  }

  /**
   * Snaps segments to all vertices.
   *
   * @param edges the list of segment strings to snap together
   */
  public void computeVertexSnaps(Collection<? extends SegmentString> edges)
  {
      for (SegmentString edge : edges) {
          NodedSegmentString edge0 = (NodedSegmentString) edge;
          computeVertexSnaps(edge0);
      }
  }

  /**
   * Snaps segments to the vertices of a Segment String.  
   */
  private void computeVertexSnaps(NodedSegmentString e)
  {
    Coordinate[] pts0 = e.getCoordinates();
    for (int i = 0; i < pts0.length ; i++) {
      HotPixel hotPixel = new HotPixel(pts0[i], scaleFactor);
      boolean isNodeAdded = pointSnapper.snap(hotPixel, e, i);
      // if a node is created for a vertex, that vertex must be noded too
      if (isNodeAdded) {
        e.addIntersection(pts0[i], i);
      }
  }
}

}
