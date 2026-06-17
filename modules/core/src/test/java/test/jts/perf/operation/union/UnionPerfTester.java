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

package test.jts.perf.operation.union;


import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.locationtech.jts.util.Stopwatch;



public class UnionPerfTester 
{
  public static final int CASCADED = 1;
  public static final int ITERATED = 2;
  public static final int BUFFER0 = 3;
  public static final int ORDERED = 4;
  
  public static void run(String testName, int testType, List<Geometry> polys)
  {
  	UnionPerfTester test = new UnionPerfTester(polys);
  	test.run(testName, testType);
  }

  public static void runAll(List<Geometry> polys)
  {
  	UnionPerfTester test = new UnionPerfTester(polys);
  	test.runAll();
  }
  
  static final int MAX_ITER = 1;

  static final PrecisionModel pm = new PrecisionModel();
  static final GeometryFactory fact = new GeometryFactory(pm, 0);
  static WKTReader wktRdr = new WKTReader(fact);
  static WKTWriter wktWriter = new WKTWriter();

  Stopwatch sw = new Stopwatch();
  final GeometryFactory factory = new GeometryFactory();
  
  private final List<Geometry> polys;

  public UnionPerfTester(List<Geometry> polys) {
  	this.polys = polys;
  }


  
  public void runAll()
  {
    System.out.println("# items: " + polys.size());
    run("Cascaded", CASCADED, polys);
//    run("Buffer-0", BUFFER0, polys);

    run("Iterated", ITERATED, polys);

  }

  public void run(String testName, int testType)
  {
  	System.out.println();
    System.out.println("======= Union Algorithm: " + testName + " ===========");

    Stopwatch sw = new Stopwatch();
    for (int i = 0; i < MAX_ITER; i++) {
    	Geometry union = null;
    	switch (testType) {
    	case CASCADED:
    		union = unionCascaded(polys);
    		break;
    	case ITERATED:
        union = unionAllSimple(polys);
        break;
    	case BUFFER0:
        union = unionAllBuffer(polys);
        break;
    	}
      
//    	printFormatted(union);

    }
    System.out.println("Finished in " + sw.getTimeString());
  }

  static void printFormatted(Geometry geom)
  {
  	WKTWriter writer = new WKTWriter();
  	System.out.println(writer.writeFormatted(geom));
  }
  
  public static Geometry unionAllSimple(List<Geometry> geoms)
  {
    Geometry unionAll = null;
    int count = 0;
      for (Geometry geom : geoms) {
          if (unionAll == null) {
              unionAll = geom.copy();
          } else {
              unionAll = unionAll.union(geom);
          }

          count++;
          if (count % 100 == 0) {
              System.out.print(".");
//        System.out.println("Adding geom #" + count);
          }
      }
    return unionAll;
  }
  
  public Geometry unionAllBuffer(List<Geometry> geoms)
  {
  	
  	Geometry gColl = factory.buildGeometry(geoms);
      return gColl.buffer(0.0);
  }
  
  public static Geometry unionCascaded(List<Geometry> geoms)
  {
  	return CascadedPolygonUnion.union(geoms);
  }

  /*
  public Geometry unionAllOrdered(List<Geometry> geoms)
  {
//  	return OrderedUnion.union(geoms);
  }
  */
  
  void printItemEnvelopes(List<?> tree)
  {
    Envelope itemEnv = new Envelope();
      for (Object o : tree) {
          if (o instanceof List) {
              printItemEnvelopes((List<?>) o);
          } else if (o instanceof Geometry) {
              itemEnv.expandToInclude(((Geometry) o).getEnvelopeInternal());
          }
      }
    System.out.println(factory.toGeometry(itemEnv));
  }
}
