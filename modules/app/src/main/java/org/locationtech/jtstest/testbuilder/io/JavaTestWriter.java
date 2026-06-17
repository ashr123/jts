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
package org.locationtech.jtstest.testbuilder.io;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jtstest.test.Testable;
import org.locationtech.jtstest.testbuilder.model.TestBuilderModel;
import org.locationtech.jtstest.util.StringUtil;



/**
 * @version 1.7
 */
public class JavaTestWriter {
  public static String getRunJava(String className, TestBuilderModel tbModel) {
    return
        "package com.vividsolutions.jtstest.testsuite;" + StringUtil.newLine
         + StringUtil.newLine
         + "import com.vividsolutions.jtstest.test.*;" + StringUtil.newLine
         + StringUtil.newLine
         + "public class " + className + " extends TestCaseList {" + StringUtil.newLine
         + "  public static void main(String[] args) {" + StringUtil.newLine
         + "    " + className + " test = new " + className + "();" + StringUtil.newLine
         + "    test.run();" + StringUtil.newLine
         + "  }" + StringUtil.newLine
         + StringUtil.newLine
         + "  public " + className + "() {" + StringUtil.newLine
         + getTestJava(tbModel.getCases())
         + "  }" + StringUtil.newLine
         + "}";
  }

    public static String getTestJava(List<?> testCases) {
      StringBuilder java = new StringBuilder();
        for (Object testCase : testCases) {
            java.append((new JavaTestWriter()).write((Testable) testCase));
        }
      return java.toString();
    }


    private final WKTWriter writer = new WKTWriter();

    public JavaTestWriter() {}

    public String write(Testable testable) {
        StringBuilder text = new StringBuilder();
        text.append("    add(new TestCase(\n");
        String name = testable.getName() == null ? "" : testable.getName();
        String description = testable.getDescription() == null ? "" : testable.getDescription();
        String a = testable.getGeometry(0) == null ? null : writer.write(testable.getGeometry(0));
        String b = testable.getGeometry(1) == null ? null : writer.write(testable.getGeometry(1));

        text.append("          \"").append(name).append("\",\n");
        text.append("          \"").append(description).append("\",\n");
        text.append("          ").append(a == null ? "null" : "\"" + a + "\"").append(",\n");
        text.append("          ").append(b == null ? "null" : "\"" + b + "\"").append(",\n");

        return text.toString();
    }

    private String write(Geometry geometry) {
        if (geometry == null) {
            return "null";
        }
        return "\"" + writer.write(geometry) + "\"";
    }
}
