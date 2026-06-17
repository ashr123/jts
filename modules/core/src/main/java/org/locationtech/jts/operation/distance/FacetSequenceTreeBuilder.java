/*
 * Copyright (c) 2016 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jts.operation.distance;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryComponentFilter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.strtree.STRtree;


public class FacetSequenceTreeBuilder {
  // 6 seems to be a good facet sequence size
  private static final int FACET_SEQUENCE_SIZE = 6;

  // Seems to be better to use a minimum node capacity
  private static final int STR_TREE_NODE_CAPACITY = 4;

    private FacetSequenceTreeBuilder() {
    }

    public static STRtree<FacetSequence> build(Geometry g) {
    STRtree<FacetSequence> tree = new STRtree<>(STR_TREE_NODE_CAPACITY);
    List<FacetSequence> sections = computeFacetSequences(g);
      for (FacetSequence section : sections) {
          tree.insert(section.getEnvelope(), section);
      }
    tree.build();
    return tree;
  }

  /**
   * Creates facet sequences
   * 
   * @param g
   * @return List<GeometryFacetSequence>
   */
  private static List<FacetSequence> computeFacetSequences(Geometry g) {
    final List<FacetSequence> sections = new ArrayList<>();

    g.apply((GeometryComponentFilter) geom -> {
      CoordinateSequence seq = null;
      if (geom instanceof LineString) {
        seq = ((LineString) geom).getCoordinateSequence();
        addFacetSequences(seq, sections);
      }
      else if (geom instanceof Point) {
        seq = ((Point) geom).getCoordinateSequence();
        addFacetSequences(seq, sections);
      }
    });
    return sections;
  }

  private static void addFacetSequences(CoordinateSequence pts, List<FacetSequence> sections) {
    int i = 0;
    int size = pts.size();
    while (i <= size - 1) {
      int end = i + FACET_SEQUENCE_SIZE + 1;
      // if only one point remains after this section, include it in this
      // section
      if (end >= size - 1)
        end = size;
      FacetSequence sect = new FacetSequence(pts, i, end);
      sections.add(sect);
      i = i + FACET_SEQUENCE_SIZE;
    }
  }
}
