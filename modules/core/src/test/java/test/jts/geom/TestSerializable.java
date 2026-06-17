
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

package test.jts.geom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;




/**
 * @version 1.7
 */
public class TestSerializable {

  public static final String FILENAME = "c:\\testSerial.txt";
  public static final GeometryFactory fact = new GeometryFactory();

  private TestSerializable() {
  }

  public static void main(String[] args) {
    TestSerializable test = new TestSerializable();
    run();
  }

  public static void run()
  {
    List<Object> objList = createData();
    writeData(objList);
    readData(objList);
  }

  static List<Object> createData()
  {
    List<Object> objList = new ArrayList<>();

    Envelope env = new Envelope(123, 456, 123, 456);
    objList.add(env);

    objList.add(GeometryTestFactory.createBox(fact, 0.0, 100.0, 10, 10.0));

    return objList;

  }
  static void writeData(List<Object> objList)
  {
    File file = new File(FILENAME);             // simply a file name

    // setup a stream to a physical file on the filesystem, and attach a stream
    // capable of writing objects to the stream that is connected to the file
    try (
      FileOutputStream outStream = new FileOutputStream(file);
      ObjectOutputStream objStream = new ObjectOutputStream(outStream)
    ) {
      objStream.writeObject(objList);
//      for (Iterator i = objList.iterator(); i.hasNext(); )
//      {
//        objStream.writeObject(i.next());
//      }
    } catch(IOException e) {
      System.err.println("Things not going as planned.");
      e.printStackTrace();
    }   // catch
  }
  static void readData(List<Object> objList)
  {
    File file = new File(FILENAME);             // simply a file name

    // setup a stream to a physical file on the filesystem, and attach a stream
    // capable of reading objects from the stream that is connected to the file
    try (
      FileInputStream stream = new FileInputStream(file);
      ObjectInputStream objStream = new ObjectInputStream(stream)
    ) {
      int count = 0;
        for (Object o : (List<Object>) objStream.readObject()) {
            compare(objList.get(count++), o);
        }

//      while (objStream.available() > 0) {
//        Object obj = objStream.readObject();
//        compare(objList.get(count++), obj);
//      }
    } catch(Exception e) {
      System.err.println("Things not going as planned.");
      e.printStackTrace();
    }   // catch
  }

  static boolean compare(Object o1, Object o2)
  {
    boolean matched = false;
    if (o1 instanceof Envelope) {
      if (! o1.equals(o2) ) {
        System.out.println("expected " + o1 + ", found " + o2);
      }
      else
        matched = true;
    }
    else if (o1 instanceof Geometry) {
      if (! ((Geometry) o1).equalsExact((Geometry) o2) ) {
        System.out.println("expected " + o1 + ", found " + o2);
      }
      else
        matched = true;
    }
    if (matched)
      System.out.println("found match for object");
    return true;
  }

}
