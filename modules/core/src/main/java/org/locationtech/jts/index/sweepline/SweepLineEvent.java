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
package org.locationtech.jts.index.sweepline;

/**
 * @version 1.7
 */
public class SweepLineEvent
  implements Comparable<SweepLineEvent>
{
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  private final double xValue;
  private int eventType;
  private final SweepLineEvent insertEvent; // null if this is an INSERT event
  private int deleteEventIndex;

  final SweepLineInterval sweepInt;
  public SweepLineEvent(double x, SweepLineEvent insertEvent, SweepLineInterval sweepInt)
  {
    xValue = x;
    this.insertEvent = insertEvent;
    this.eventType = INSERT;
    if (insertEvent != null)
      eventType = DELETE;
    this.sweepInt = sweepInt;
  }

  public boolean isInsert() { return insertEvent == null; }
  public boolean isDelete() { return insertEvent != null; }
  public SweepLineEvent getInsertEvent() { return insertEvent; }
  public int getDeleteEventIndex() { return deleteEventIndex; }
  public void setDeleteEventIndex(int deleteEventIndex) { this.deleteEventIndex = deleteEventIndex; }

  SweepLineInterval getInterval() { return sweepInt; }

  /**
   * ProjectionEvents are ordered first by their x-value, and then by their eventType.
   * It is important that Insert events are sorted before Delete events, so that
   * items whose Insert and Delete events occur at the same x-value will be
   * correctly handled.
   */
  public int compareTo(SweepLineEvent pe) {
    if (xValue < pe.xValue) return  -1;
    if (xValue > pe.xValue) return   1;
      return Integer.compare(eventType, pe.eventType);
  }


}
