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

import java.util.Objects;

/**
 * An immutable pair of items, used as the result of a nearest-neighbour
 * search over one or two {@link STRtree}s
 * (see {@link STRtree#nearestNeighbour(ItemDistance)} and
 * {@link STRtree#nearestNeighbour(STRtree, ItemDistance)}).
 * <p>
 * When the pair is the result of searching a single tree the two items are
 * the two nearest items in the tree. When it is the result of searching two
 * trees, {@link #getItem0()} is the item from the queried tree and
 * {@link #getItem1()} is the item from the argument tree.
 *
 * @param <T> the type of the items
 */
public final class ItemPair<T>
{
  private final T item0;
  private final T item1;

  /**
   * Creates a pair of items.
   *
   * @param item0 the first item
   * @param item1 the second item
   */
  public ItemPair(T item0, T item1)
  {
    this.item0 = item0;
    this.item1 = item1;
  }

  /**
   * Gets the first item of the pair.
   *
   * @return the first item
   */
  public T getItem0()
  {
    return item0;
  }

  /**
   * Gets the second item of the pair.
   *
   * @return the second item
   */
  public T getItem1()
  {
    return item1;
  }

  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (! (o instanceof ItemPair)) return false;
    ItemPair<?> other = (ItemPair<?>) o;
    return Objects.equals(item0, other.item0)
        && Objects.equals(item1, other.item1);
  }

  public int hashCode()
  {
    return Objects.hash(item0, item1);
  }

  public String toString()
  {
    return "ItemPair[ " + item0 + ", " + item1 + " ]";
  }
}
