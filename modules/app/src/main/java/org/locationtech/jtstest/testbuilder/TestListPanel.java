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
package org.locationtech.jtstest.testbuilder;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jtstest.test.Testable;
import org.locationtech.jtstest.testbuilder.model.TestCaseEdit;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;


/**
 * @version 1.7
 */
public class TestListPanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    private final DefaultListModel<Testable> listModel = new DefaultListModel<>();
    final JScrollPane jScrollPane1 = new JScrollPane();
    final JList<Testable> list = new JList<>(listModel);
    final BorderLayout borderLayout2 = new BorderLayout();

    private static class TestListCellRenderer extends JLabel implements ListCellRenderer<Testable> {
      
        private static final String INDEX_SEP = " - ";
        private static final String GEOM_SEP = " / ";
        private static final String DESC_SEP = " -- ";
        
        /*
        private final ImageIcon tickIcon =
            new ImageIcon(this.getClass().getResource("tickShaded.gif"));
        private final ImageIcon crossIcon =
            new ImageIcon(this.getClass().getResource("crossShaded.gif"));
        private final ImageIcon clearIcon = new ImageIcon(this.getClass().getResource("clear.gif"));
         */
        
        public Component getListCellRendererComponent(
            JList<? extends Testable> list,
            Testable value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            setText(testName(value));
            setOpaque(true);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            return this;
        }
        
        private static String testName(Testable testCase)
        {
          String name = testCase.getName();
          if ((name == null || name.isEmpty()) && testCase instanceof TestCaseEdit) {
              name = testCase.getDescription();
          }
          if (name == null || name.isEmpty()) {
              name = "";
          }
          int testSkey = 1 + JTSTestBuilderFrame.instance().getModel().getCases().indexOf(testCase);
          String nameFinal = "# " + testSkey + INDEX_SEP + testCaseSignatureHTML(testCase);
          if (!name.isEmpty())
          	nameFinal = nameFinal + DESC_SEP + name;
          return "<html>" + nameFinal + "<html>";
        }
        
        private static String testCaseSignatureHTML(Testable testCase)
        {
          String sig0 = geometrySignature(testCase.getGeometry(0));
          String sig1 = geometrySignature(testCase.getGeometry(1));
          Object sep = !sig0.isEmpty() && !sig1.isEmpty() ? GEOM_SEP : "";
        	return "<font color='blue'>" + sig0 + "</font>" 
        	      + sep
        	      + "<font color='red'>" + sig1 + "</font>";
        }
        
        private static String geometrySignature(Geometry geom)
        {
          // visual indication of null geometry
        	if (geom == null) 
        		return ""; 
        	
        	String sig = geom.getGeometryType();
        	if (geom instanceof GeometryCollection) {
        		sig += "[" + geom.getNumGeometries() + "]";
        	}
          else {
            sig += "(" + geom.getNumPoints() + ")";
          }
        	return sig;
        }
    }

    public TestListPanel(JTSTestBuilderFrame testBuilderFrame) {
        this();
    }

    public TestListPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        list.setCellRenderer(new TestListCellRenderer());
        registerListSelectionListener();
    }

    private void jbInit() {
        setSize(200, 250);
        setLayout(borderLayout2);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectionBackground(Color.GRAY);
        add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(list, null);
    }

    private void registerListSelectionListener() {
        list.getSelectionModel().addListSelectionListener(e -> {
            if (list.getSelectedValue() == null)
                return;
            JTSTestBuilderFrame.instance().setCurrentTestCase(
                (TestCaseEdit) list.getSelectedValue());
        });
    }

    public void populateList() {
        listModel.clear();
        for (Testable o : JTSTestBuilderFrame.instance().getModel().getCases()) {
            listModel.addElement(o);
        }
    }
}
