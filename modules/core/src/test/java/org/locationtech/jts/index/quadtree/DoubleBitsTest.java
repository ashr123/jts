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
package org.locationtech.jts.index.quadtree;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Tests DoubleBits
 * @version 1.7
 */
public class DoubleBitsTest extends TestCase {

  public static void main(String[] args) {
    TestRunner.run(DoubleBitsTest.class);
  }

  public DoubleBitsTest(String name) { super(name); }

  public void testExponent() {
      assertEquals(0, DoubleBits.exponent(-1));
      assertEquals(3, DoubleBits.exponent(8.0));
      assertEquals(7, DoubleBits.exponent(128.0));
  }

}
