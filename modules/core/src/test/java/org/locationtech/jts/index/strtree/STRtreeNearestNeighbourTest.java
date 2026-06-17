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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import test.jts.GeometryTestCase;

/**
 * @version 1.7
 */
public class STRtreeNearestNeighbourTest extends GeometryTestCase {
  
  private static final String POINTS_B = "MULTIPOINT( 5 5, 15 15, 5 15, 15 5, 8 8)";
  private static final String POINTS_A = "MULTIPOINT( 0 0, 10 10, 0 10, 10 0, 9 9)";
  
  public STRtreeNearestNeighbourTest(String Name_) {
    super(Name_);
  }

  public static void main(String[] args) {
    String[] testCaseName = { STRtreeNearestNeighbourTest.class.getName() };
    junit.textui.TestRunner.main(testCaseName);
  }

  public void testNearestNeighboursEmpty() {
    STRtree<Geometry> tree = new STRtree<>();

    ItemPair<Geometry> nn = tree.nearestNeighbour(new GeometryItemDistance());
      assertNull(nn);
  }

  public void testNearestNeighboursTreesEmpty() {
    STRtree<Geometry> tree = new STRtree<>();
    STRtree<Geometry> tree2 = new STRtree<>();

    ItemPair<Geometry> nn = tree.nearestNeighbour(tree2, new GeometryItemDistance());
      assertNull(nn);
  }

  public void testNearestNeighbourEmpty() {
    STRtree<Geometry> tree = new STRtree<>();
    Geometry geom = read("POINT (1 1)");
    Geometry nn = tree.nearestNeighbour(geom.getEnvelopeInternal(), geom, new GeometryItemDistance());
      assertNull(nn);
  }
  
  public void testNearestNeighbours() {
    checkNN(POINTS_A,
        "MULTIPOINT(9 9, 10 10)");
  }
  
  public void testNearestNeighbourSingleItem() {
    checkNN("POINT( 5 5 )", "POINT( 5 5 )");
  }
  
  public void testNearestNeighbours2() {
    checkNN(
        POINTS_A,
        POINTS_B,
        "POINT( 9 9 )",
        "POINT( 8 8 )");
  }
  
  public void testWithinDistance() {
    checkWithinDistance( POINTS_A, POINTS_B, 2, true );
    checkWithinDistance( POINTS_A, POINTS_B, 1, false );
  }
  
  public void testKNearestNeighborsEmpty() {
    STRtree<Geometry> tree = new STRtree<>();
    Geometry geom = read("POINT (1 1)");
    List<Geometry> nn = tree.nearestNeighbour(geom.getEnvelopeInternal(), geom, new GeometryItemDistance(), 5);
    assertTrue(nn.isEmpty());
  }

  private void checkNN(String wktItems, String wktExpected) {
    Geometry items = read(wktItems);

    STRtree<Geometry> tree = createTree( items );
    ItemPair<Geometry> nearest = tree.nearestNeighbour(new GeometryItemDistance());
    
    if (wktExpected == null) {
        assertNull(nearest);
      return;
    }
    Geometry expected = read(wktExpected);
    boolean isFound = isEqualUnordered(nearest, expected.getGeometryN(0), expected.getGeometryN(1) );
    assertTrue(isFound);
  }
  
  private void checkNN(String wktItems1, String wktItems2, 
      String wktExpected1, String wktExpected2) {
    Geometry items1 = read(wktItems1);
    Geometry items2 = read(wktItems2);
    Geometry expected1 = read(wktExpected1);
    Geometry expected2 = read(wktExpected2);
    
    STRtree<Geometry> tree1 = createTree( items1 );
    STRtree<Geometry> tree2 = createTree( items2 );

    ItemPair<Geometry> nearest = tree1.nearestNeighbour(tree2, new GeometryItemDistance());

    boolean isFound = isEqual(nearest, expected1, expected2 );
    assertTrue(isFound);
  }

  private void checkWithinDistance(String wktItems1, String wktItems2, 
      double distance, boolean expected) {
    Geometry items1 = read(wktItems1);
    Geometry items2 = read(wktItems2);
    
    STRtree<Geometry> tree1 = createTree( items1 );
    STRtree<Geometry> tree2 = createTree( items2 );

    boolean result = tree1.isWithinDistance(tree2, new GeometryItemDistance(), distance);
    
    assertEquals(expected, result);
  }

  private static boolean isEqualUnordered(ItemPair<Geometry> items, Geometry g1, Geometry g2) {
    return (isEqual(items, g1, g2) || isEqual(items, g2, g1));
  }

  private static boolean isEqual(ItemPair<Geometry> items, Geometry g1, Geometry g2) {
      return g1.equalsExact(items.getItem0())
              && g2.equalsExact(items.getItem1());
  }

  private static STRtree<Geometry> createTree(Geometry items) {
    STRtree<Geometry> tree = new STRtree<>();
    for (int i = 0; i < items.getNumGeometries(); i++) {
      Geometry item = items.getGeometryN(i);
      tree.insert( item.getEnvelopeInternal(), item);
    }
    return tree;
  }


  
  public void testKNearestNeighbors() {
    int topK = 1000;
    int totalRecords = 10000;
    GeometryFactory geometryFactory = new GeometryFactory();
    Coordinate coordinate = new Coordinate(10.1, -10.1);
    Point queryCenter = geometryFactory.createPoint(coordinate);
    int valueRange = 1000;
    List<Geometry> testDataset = new ArrayList<>();
    List<Geometry> correctData = new ArrayList<>();
    Random random = new Random();
    GeometryDistanceComparator distanceComparator = new GeometryDistanceComparator(queryCenter, true);
    /*
     * Generate the random test data set
     */
    for (int i = 0; i < totalRecords; i++) {
      coordinate = new Coordinate(-100 + random.nextInt(valueRange) * 1.1, random.nextInt(valueRange) * (-5.1));
      Point spatialObject = geometryFactory.createPoint(coordinate);
      testDataset.add(spatialObject);
    }
    /*
     * Sort the original data set and make sure the elements are sorted in an
     * ascending order
     */
    testDataset.sort(distanceComparator);
    /*
     * Get the correct top K
     */
    for (int i = 0; i < topK; i++) {
      correctData.add(testDataset.get(i));
    }

    STRtree<Geometry> strtree = new STRtree<>();
    for (int i = 0; i < totalRecords; i++) {
      strtree.insert(testDataset.get(i).getEnvelopeInternal(), testDataset.get(i));
    }
    /*
     * Shoot a random query to make sure the STR-Tree is built.
     */
    strtree.query(new Envelope(1 + 0.1, 1 + 0.1, 2 + 0.1, 2 + 0.1));
    /*
     * Issue the KNN query.
     */
    List<Geometry> testTopK = strtree.nearestNeighbour(queryCenter.getEnvelopeInternal(), queryCenter,
        new GeometryItemDistance(), topK);
    List<Geometry> topKList = new ArrayList<>(testTopK);
    topKList.sort(distanceComparator);
    /*
     * Check the difference between correct result and test result. The difference
     * should be 0.
     */
    int difference = 0;
    for (int i = 0; i < topK; i++) {
      if ( distanceComparator.compare(correctData.get(i), topKList.get(i)) != 0 ) {
        difference++;
      }
    }
    assertEquals(0, difference);
  }

}
