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

package org.locationtech.jts.simplify;

import java.util.Collection;

/**
 * Simplifies a collection of TaggedLineStrings, preserving topology
 * (in the sense that no new intersections are introduced).
 * This class is essentially just a container for the common
 * indexes used by {@link TaggedLineStringSimplifier}.
 */
class TaggedLinesSimplifier
{
  private final LineSegmentIndex inputIndex = new LineSegmentIndex();
  private final LineSegmentIndex outputIndex = new LineSegmentIndex();
  
  private double distanceTolerance = 0.0;

  public TaggedLinesSimplifier()
  {

  }

  /**
   * Sets the distance tolerance for the simplification.
   * All vertices in the simplified geometry will be within this
   * distance of the original geometry.
   *
   * @param distanceTolerance the approximation tolerance to use
   */
  public void setDistanceTolerance(double distanceTolerance) {
    this.distanceTolerance = distanceTolerance;
  }

  /**
   * Simplify a collection of TaggedLineStrings
   *
   * @param taggedLines the collection of lines to simplify
   */
  public void simplify(Collection<TaggedLineString> taggedLines) {
    ComponentJumpChecker jumpChecker = new ComponentJumpChecker(taggedLines);

      for (TaggedLineString line : taggedLines) {
          inputIndex.add(line);
      }
      for (TaggedLineString taggedLine : taggedLines) {
          TaggedLineStringSimplifier tlss
                  = new TaggedLineStringSimplifier(inputIndex, outputIndex, jumpChecker);
          tlss.simplify(taggedLine, distanceTolerance);
      }
  }

}
