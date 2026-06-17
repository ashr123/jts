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

package org.locationtech.jts.operation.overlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Position;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geomgraph.DirectedEdge;
import org.locationtech.jts.geomgraph.DirectedEdgeStar;
import org.locationtech.jts.geomgraph.EdgeEnd;
import org.locationtech.jts.geomgraph.GeometryGraph;
import org.locationtech.jts.geomgraph.Label;
import org.locationtech.jts.geomgraph.Node;
import org.locationtech.jts.geomgraph.PlanarGraph;

/**
 * Tests whether the polygon rings in a {@link GeometryGraph}
 * are consistent.
 * Used for checking if Topology errors are present after noding.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class ConsistentPolygonRingChecker
{
  private final PlanarGraph graph;

  public ConsistentPolygonRingChecker(PlanarGraph graph) {
    this.graph = graph;
  }

  public void checkAll()
  {
    check(OverlayOp.INTERSECTION);
    check(OverlayOp.DIFFERENCE);
    check(OverlayOp.UNION);
    check(OverlayOp.SYMDIFFERENCE);
  }

  /**
   * Tests whether the result geometry is consistent
   *
   * @throws TopologyException if inconsistent topology is found
   */
  public void check(int opCode)
  {
    for (Iterator<Node> nodeit = graph.getNodeIterator(); nodeit.hasNext(); ) {
      Node node = nodeit.next();
      testLinkResultDirectedEdges((DirectedEdgeStar) node.getEdges(), opCode);
    }
  }

  private static List<DirectedEdge> getPotentialResultAreaEdges(DirectedEdgeStar deStar, int opCode)
  {
//print(System.out);
    List<DirectedEdge> resultAreaEdgeList = new ArrayList<>();
    for (Iterator<EdgeEnd> it = deStar.iterator(); it.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) it.next();
      if (isPotentialResultAreaEdge(de, opCode) || isPotentialResultAreaEdge(de.getSym(), opCode) )
        resultAreaEdgeList.add(de);
    }
    return resultAreaEdgeList;
  }

  private static boolean isPotentialResultAreaEdge(DirectedEdge de, int opCode)
  {
    // mark all dirEdges with the appropriate label
    Label label = de.getLabel();
      //Debug.print("in result "); Debug.println(de);
      return label.isArea()
              && !de.isInteriorAreaEdge()
              && OverlayOp.isResultOfOp(
              label.getLocation(0, Position.RIGHT),
              label.getLocation(1, Position.RIGHT),
              opCode);
  }

  private static final int SCANNING_FOR_INCOMING = 1;
  private static final int LINKING_TO_OUTGOING = 2;

  private static void testLinkResultDirectedEdges(DirectedEdgeStar deStar, int opCode)
  {
    // make sure edges are copied to resultAreaEdges list
    List<DirectedEdge> ringEdges = getPotentialResultAreaEdges(deStar, opCode);
    // find first area edge (if any) to start linking at
    DirectedEdge firstOut = null;
    DirectedEdge incoming = null;
    int state = SCANNING_FOR_INCOMING;
    // link edges in CCW order
      for (DirectedEdge nextOut : ringEdges) {
          DirectedEdge nextIn = nextOut.getSym();

          // skip de's that we're not interested in
          if (!nextOut.getLabel().isArea()) continue;

          // record first outgoing edge, in order to link the last incoming edge
          if (firstOut == null
                  && isPotentialResultAreaEdge(nextOut, opCode))
              firstOut = nextOut;
          // assert: sym.isInResult() == false, since pairs of dirEdges should have been removed already

          switch (state) {
              case SCANNING_FOR_INCOMING:
                  if (!isPotentialResultAreaEdge(nextIn, opCode)) continue;
                  incoming = nextIn;
                  state = LINKING_TO_OUTGOING;
                  break;
              case LINKING_TO_OUTGOING:
                  if (!isPotentialResultAreaEdge(nextOut, opCode)) continue;
                  //incoming.setNext(nextOut);
                  state = SCANNING_FOR_INCOMING;
                  break;
          }
      }
//Debug.print(this);
    if (state == LINKING_TO_OUTGOING) {
//Debug.print(firstOut == null, this);
      if (firstOut == null)
        throw new TopologyException("no outgoing dirEdge found", deStar.getCoordinate());
    }

  }




}
