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
package org.locationtech.jts.index.strtree;

import java.util.Comparator;
import java.util.List;

/**
 * One-dimensional version of an STR-packed R-tree. SIR stands for
 * "Sort-Interval-Recursive". STR-packed R-trees are described in:
 * P. Rigaux, Michel Scholl and Agnes Voisard. Spatial Databases With
 * Application To GIS. Morgan Kaufmann, San Francisco, 2002.
 * <p>
 * This class is thread-safe.  Building the tree is synchronized,
 * and querying is stateless.
 *
 * @see STRtree
 *
 * @version 1.7
 */
public class SIRtree extends AbstractSTRtree<Interval, Object> {

  private static final Comparator<Boundable<Interval>> comparator = (o1, o2) -> compareDoubles(
      o1.getBounds().getCentre(),
      o2.getBounds().getCentre());

  private static final IntersectsOp intersectsOp = (aBounds, bBounds) -> ((Interval)aBounds).intersects((Interval)bBounds);

  /**
   * Constructs an SIRtree with the default node capacity.
   */
  public SIRtree() { this(10); }

  /**
   * Constructs an SIRtree with the given maximum number of child nodes that
   * a node may have
   */
  public SIRtree(int nodeCapacity) {
    super(nodeCapacity);
  }

  @Override
  protected AbstractNode<Interval> createNode(int level) {
    return new AbstractNode<Interval>(level) {
      @Override
      protected Interval computeBounds() {
        Interval bounds = null;
          for (Boundable<Interval> childBoundable : getChildBoundables()) {
              if (bounds == null) {
                  bounds = new Interval(childBoundable.getBounds());
              } else {
                  bounds.expandToInclude(childBoundable.getBounds());
              }
          }
        return bounds;
      }
    };
  }

  /**
   * Inserts an item having the given bounds into the tree.
   */
  public void insert(double x1, double x2, Object item) {
    super.insert(new Interval(Math.min(x1, x2), Math.max(x1, x2)), item);
  }

  /**
   * Returns items whose bounds intersect the given value.
   */
  public List<Object> query(double x) {
    return query(x, x);
  }

  /**
   * Returns items whose bounds intersect the given bounds.
   * @param x1 possibly equal to x2
   */
  public List<Object> query(double x1, double x2) {
    return super.query(new Interval(Math.min(x1, x2), Math.max(x1, x2)));
  }

  @Override
  protected IntersectsOp getIntersectsOp() {
    return intersectsOp;
  }

  @Override
  protected Comparator<Boundable<Interval>> getComparator() {
    return comparator;
  }

}
