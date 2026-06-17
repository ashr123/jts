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
package org.locationtech.jts.index;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;



/**
 * @version 1.7
 */
public class SpatialIndexTester 
{
  private static final boolean VERBOSE = false;
  
  private SpatialIndex<Envelope> index;
  private ArrayList<Envelope> sourceData;
  private boolean isSuccess = true;
  
  public SpatialIndexTester() {
  }

  public boolean isSuccess()
  {
    return isSuccess;
  }
  
  public void setSpatialIndex(SpatialIndex<Envelope> index)
  {
    this.index = index;
  }

  public SpatialIndex<Envelope> getSpatialIndex()
  {
    return index;
  }

  public void init() {
    sourceData = new ArrayList<>();
    addSourceData(0, sourceData);
    addSourceData(OFFSET, sourceData);
    if (VERBOSE) {
      //System.out.println("===============================");
      //System.out.println("Grid Extent: " + (CELL_EXTENT * CELLS_PER_GRID_SIDE));
      //System.out.println("Cell Extent: " + CELL_EXTENT);
      //System.out.println("Feature Extent: " + FEATURE_EXTENT);
      //System.out.println("Cells Per Grid Side: " + CELLS_PER_GRID_SIDE);
      //System.out.println("Offset For 2nd Set Of Features: " + OFFSET);
      //System.out.println("Feature Count: " + sourceData.size());
    }
    insert(sourceData, index);
  }
  
  public void run()
  {
    doTest(index, QUERY_ENVELOPE_EXTENT_1, sourceData);
    doTest(index, QUERY_ENVELOPE_EXTENT_2, sourceData);
  }

  private static void insert(List<Envelope> sourceData, SpatialIndex<Envelope> index) {
      for (Envelope envelope : sourceData) {
          index.insert(envelope, envelope);
      }
  }

  private static final double CELL_EXTENT = 20.31;
  private static final int CELLS_PER_GRID_SIDE = 10;
  private static final double FEATURE_EXTENT = 10.1;
  private static final double OFFSET = 5.03;
  private static final double QUERY_ENVELOPE_EXTENT_1 = 1.009;
  private static final double QUERY_ENVELOPE_EXTENT_2 = 11.7;

  private static void addSourceData(double offset, List<Envelope> sourceData) {
    for (int i = 0; i < CELLS_PER_GRID_SIDE; i++) {
      double minx = (i * CELL_EXTENT) + offset;
      double maxx = minx + FEATURE_EXTENT;
      for (int j = 0; j < CELLS_PER_GRID_SIDE; j++) {
        double miny = (j * CELL_EXTENT) + offset;
        double maxy = miny + FEATURE_EXTENT;
        Envelope e = new Envelope(minx, maxx, miny, maxy);
        sourceData.add(e);
      }
    }
  }

  private void doTest(SpatialIndex<Envelope> index, double queryEnvelopeExtent, List<Envelope> sourceData) {
   int extraMatchCount = 0;
    int expectedMatchCount = 0;
    int actualMatchCount = 0;
    int queryCount = 0;
    for (int x = 0; x < CELL_EXTENT * CELLS_PER_GRID_SIDE; x+= queryEnvelopeExtent) {
      for (int y = 0; y < CELL_EXTENT * CELLS_PER_GRID_SIDE; y+= queryEnvelopeExtent) {
        Envelope queryEnvelope = new Envelope(x, x+queryEnvelopeExtent, y, y+queryEnvelopeExtent);
        List<Envelope> expectedMatches = intersectingEnvelopes(queryEnvelope, sourceData);
        List<Envelope> actualMatches = index.query(queryEnvelope);
        // since index returns candidates only, it may return more than the expected value
        if (expectedMatches.size() > actualMatches.size()) {
          isSuccess = false;
        }
        extraMatchCount += (actualMatches.size() - expectedMatches.size());
        expectedMatchCount += expectedMatches.size();
        actualMatchCount += actualMatches.size();
        compare(expectedMatches, actualMatches);
        queryCount++;
      }
    }
    if (VERBOSE) {
      //System.out.println("---------------");
      //System.out.println("Envelope Extent: " + queryEnvelopeExtent);
      //System.out.println("Expected Matches: " + expectedMatchCount);
      //System.out.println("Actual Matches: " + actualMatchCount);
      //System.out.println("Extra Matches: " + extraMatchCount);
      //System.out.println("Query Count: " + queryCount);
      //System.out.println("Average Expected Matches: " + (expectedMatchCount/(double)queryCount));
      //System.out.println("Average Actual Matches: " + (actualMatchCount/(double)queryCount));
      //System.out.println("Average Extra Matches: " + (extraMatchCount/(double)queryCount));
    }
  }

  private void compare(List<Envelope> expectedEnvelopes, List<Envelope> actualEnvelopes) {
    //Don't use #containsAll because we want to check using
    //==, not #equals. [Jon Aquino]
      for (Envelope expected : expectedEnvelopes) {
          boolean found = false;
          for (Envelope actual : actualEnvelopes) {
              if (actual.equals(expected)) {
                  found = true;
                  break;
              }
          }
          if (!found) {
              isSuccess = false;
              break;
          }
      }
  }

  private static List<Envelope> intersectingEnvelopes(Envelope queryEnvelope, List<Envelope> envelopes) {
    ArrayList<Envelope> intersectingEnvelopes = new ArrayList<>();
      for (Envelope candidate : envelopes) {
          if (candidate.intersects(queryEnvelope)) {
              intersectingEnvelopes.add(candidate);
          }
      }
    return intersectingEnvelopes;
  }
}
