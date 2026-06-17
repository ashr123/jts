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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.operation.valid.IsSimpleOp;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.locationtech.jts.operation.valid.TopologyValidationError;
import org.locationtech.jtstest.testbuilder.event.ValidPanelEvent;
import org.locationtech.jtstest.testbuilder.event.ValidPanelListener;
import org.locationtech.jtstest.testbuilder.model.TestCaseEdit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Vector;


/**
 * @version 1.7
 */
public class ValidPanel extends JPanel {
  private static final int TEXT_BOX_WIDTH = 240;
  
  TestCaseEdit testCase;
  private Coordinate markPoint = null;
  
  //===========================================
  final JTextField txtIsValid = new JTextField();
  final JTextField txtIsSimple = new JTextField();
  final JTextArea taInvalidMsg = new JTextArea();
  final JLabel lblValidSimple = new JLabel();
  final JPanel panelValidSimple = new JPanel();
  private transient Vector<ValidPanelListener> validPanelListeners;
  GridLayout gridLayout1 = new GridLayout();
  final JPanel markPanel = new JPanel();
  final JPanel markBtnPanel = new JPanel();
  final JTextField txtMarkLocation = new JTextField();
  JTextField txtMarkLabel = new JTextField();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  final JLabel lblMark = new JLabel();
  final JButton btnClearMark = new JButton();
  final JButton btnSetMark = new JButton();
  private JCheckBox cbInvertedRingAllowed;
  final JRadioButton rbA = new JRadioButton();
  final JRadioButton rbB = new JRadioButton();
  final JRadioButton rbResult = new JRadioButton();

  public ValidPanel() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() {
    JButton btnValidate = new JButton();
    btnValidate.setText("Valid?");
    btnValidate.addActionListener(this::btnValidate_actionPerformed);
    JButton btnSimple = new JButton();
    btnSimple.setText("Simple?");
    btnSimple.addActionListener(this::btnSimple_actionPerformed);
    
    JButton btnClear = new JButton();
    btnClear.setText("Clear");
    btnClear.addActionListener(e -> clearAll());

    rbA.setSelected(true);
    rbA.setText(AppStrings.GEOM_LABEL_A);
    rbA.setForeground(AppColors.GEOM_A);
    rbB.setText(AppStrings.GEOM_LABEL_B);
    rbB.setForeground(AppColors.GEOM_B);
    rbResult.setText(AppStrings.GEOM_LABEL_RESULT);
    rbResult.setForeground(AppColors.GEOM_RESULT);
    
    rbA.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        clearAll();
    }
  });
    rbB.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        clearAll();
    }
  });
    rbResult.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        clearAll();
    }
  });


    ButtonGroup btnGrpStdInFormat = new ButtonGroup();
    btnGrpStdInFormat.add(rbA);
    btnGrpStdInFormat.add(rbB);
    btnGrpStdInFormat.add(rbResult);

    JPanel panelABR = new JPanel();
    panelABR.setLayout(new BoxLayout(panelABR, BoxLayout.X_AXIS));
    panelABR.add(rbA);
    panelABR.add(rbB);
    panelABR.add(rbResult);
    
    cbInvertedRingAllowed = new JCheckBox();
    cbInvertedRingAllowed.setToolTipText(AppStrings.TIP_ALLOW_INVERTED_RINGS);
    cbInvertedRingAllowed.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbInvertedRingAllowed.setText("Allow Inverted Rings");
    
    txtIsValid.setBackground(AppColors.BACKGROUND);
    txtIsValid.setEditable(false);
    //txtIsValid.setText("Y");
    txtIsValid.setHorizontalAlignment(SwingConstants.CENTER);
    Dimension flagSize = new Dimension(40, 24);
    txtIsValid.setMinimumSize(flagSize);
    txtIsValid.setPreferredSize(flagSize);
    
    txtIsSimple.setBackground(AppColors.BACKGROUND);
    txtIsSimple.setEditable(false);
    txtIsSimple.setHorizontalAlignment(SwingConstants.CENTER);
    txtIsSimple.setMinimumSize(flagSize);
    txtIsSimple.setPreferredSize(flagSize);
    
    taInvalidMsg.setPreferredSize(new Dimension(TEXT_BOX_WIDTH, 80));
    taInvalidMsg.setMaximumSize(new Dimension(TEXT_BOX_WIDTH, 80));
    taInvalidMsg.setMinimumSize(new Dimension(TEXT_BOX_WIDTH, 80));
    
    taInvalidMsg.setLineWrap(true);
    taInvalidMsg.setBorder(BorderFactory.createLoweredBevelBorder());
    taInvalidMsg.setToolTipText("");
    taInvalidMsg.setBackground(AppColors.BACKGROUND);
    taInvalidMsg.setEditable(true);
    taInvalidMsg.setFont(new java.awt.Font("SansSerif", 0, 12));
    
    lblValidSimple.setToolTipText("");
    lblValidSimple.setText("Valid / Simple ");
    
    lblMark.setToolTipText("");
    lblMark.setText("Mark Point ( X Y ) ");
    
    btnClearMark.setToolTipText("");
    btnClearMark.setText("Clear Mark");
    btnClearMark.addActionListener(e -> clearMark());
    
    btnSetMark.setToolTipText("");
    btnSetMark.setText("Set Mark");
    btnSetMark.addActionListener(this::btnSetMark_actionPerformed);
    
    JPanel panelValid = new JPanel();
    panelValid.add(btnValidate);
    panelValid.add(txtIsValid);
    
    JPanel panelSimple = new JPanel();
    panelSimple.add(btnSimple);
    panelSimple.add(txtIsSimple);
   
    JPanel panelMsg = new JPanel();
    panelMsg.add(taInvalidMsg);
    
    JPanel panelClear = new JPanel();
    panelClear.setLayout(new BorderLayout());
    panelClear.add(cbInvertedRingAllowed, BorderLayout.WEST);
    panelClear.add(btnClear, BorderLayout.EAST);
    
    panelValidSimple.setLayout(new BoxLayout(panelValidSimple, BoxLayout.Y_AXIS));
    panelValidSimple.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panelValidSimple.add(panelABR);
    panelValidSimple.add(panelSimple);
    panelValidSimple.add(panelValid);
    panelValidSimple.add(panelClear);
    panelValidSimple.add(panelMsg);
    
    //----------------------------------------------
    txtMarkLocation.setBorder(BorderFactory.createLoweredBevelBorder());
    txtMarkLocation.setToolTipText("");
    txtMarkLocation.setEditable(true);
    txtMarkLocation.setFont(new java.awt.Font("SansSerif", 0, 12));
    txtMarkLocation.setHorizontalAlignment(SwingConstants.LEFT);
    Dimension markDim = new Dimension(TEXT_BOX_WIDTH, 20);
    txtMarkLocation.setPreferredSize(markDim);
    txtMarkLocation.setMaximumSize(markDim);
    txtMarkLocation.setMinimumSize(markDim);

    
    markPanel.setLayout(new BorderLayout());
    
    markBtnPanel.add(btnSetMark);
    markBtnPanel.add(btnClearMark);
    markPanel.add(lblMark, BorderLayout.NORTH);
    markPanel.add(txtMarkLocation, BorderLayout.CENTER);
    //markPanel.add(txtMarkLabel, BorderLayout.CENTER);
    markPanel.add(markBtnPanel, BorderLayout.SOUTH);
    
    //----------------------------------------------
    this.setLayout(new BorderLayout());
    this.add(panelValidSimple, BorderLayout.NORTH);
    this.add(markPanel, BorderLayout.SOUTH);
  }

  public void setTestCase(TestCaseEdit testCase) {
    this.testCase = testCase;
  }

  public Coordinate getMarkPoint()   { return markPoint; }

  void clearAll() {
    clearFlag(txtIsValid);
    clearFlag(txtIsSimple);
    taInvalidMsg.setText("");
    clearMark();
  }

  void btnValidate_actionPerformed(ActionEvent e) 
  {
    clearFlag(txtIsValid);
    Geometry geom = getGeometry();
    if (geom == null)
      return;
    
    TopologyValidationError err = checkValid(geom, cbInvertedRingAllowed.isSelected());
    String msg = "";
    boolean isValid = true;
    Coordinate invalidPoint = null;
    if (err != null) {
      isValid = false;
      msg = err.toString();
      invalidPoint = err.getCoordinate();
    }
    taInvalidMsg.setText(msg);
    setFlagText(txtIsValid, isValid);
    setMarkPoint(invalidPoint);
  }
  
  private static TopologyValidationError checkValid(Geometry geom, boolean isAllowInverted) {
    TopologyValidationError err = null;
    if (geom != null) {
      IsValidOp validOp = new IsValidOp(geom);
      if (isAllowInverted) {
        validOp.setSelfTouchingRingFormingHoleValid(true);
      }
      err = validOp.getValidationError();
    }
    return err;
  }
  
  private Geometry getGeometry() {
    if (rbA.isSelected()) 
      return testCase.getGeometry(0);
    if (rbB.isSelected()) 
      return testCase.getGeometry(1);
    return testCase.getResult();
  }
  
  void btnSimple_actionPerformed(ActionEvent e) 
  {
  	boolean isSimple = true;
  	Coordinate nonSimpleLoc = null;
  	Geometry geom = getGeometry();
  	if (geom != null) {
      IsSimpleOp simpleOp = new IsSimpleOp(geom);
      isSimple = simpleOp.isSimple();
      nonSimpleLoc = simpleOp.getNonSimpleLocation(); 
    }
    String msg = isSimple ?
    		""
    		: "Non-simple intersection at " + WKTWriter.toPoint(nonSimpleLoc);
    taInvalidMsg.setText(msg);
    setFlagText(txtIsSimple, isSimple);
    setMarkPoint(nonSimpleLoc);
  }
  
  private static void setFlagText(JTextField txt, boolean val) {
    txt.setText(val ? "Y" : "N");
    txt.setBackground(val ? AppColors.BACKGROUND : AppColors.BACKGROUND_ERROR);
  }
  
  private static void clearFlag(JTextField txt) {
    txt.setText("");
    txt.setBackground(AppColors.BACKGROUND);
  }
  
  private void setMarkPoint(Coordinate coord)
  {
    markPoint = coord;
    String markText = "";
    if (markPoint != null) {
      markText = " " + coord.x + "  " + coord.y + " ";
    }
    txtMarkLocation.setText(markText);
    fireSetHighlightPerformed(new ValidPanelEvent(this));
  }
  
  private void clearMark() {
    setMarkPoint(null);
  }
  
  public synchronized void removeValidPanelListener(ValidPanelListener l) {
    if (validPanelListeners != null && validPanelListeners.contains(l)) {
      Vector<ValidPanelListener> v = (Vector<ValidPanelListener>) validPanelListeners.clone();
      v.removeElement(l);
      validPanelListeners = v;
    }
  }
  public synchronized void addValidPanelListener(ValidPanelListener l) {
    Vector<ValidPanelListener> v = validPanelListeners == null ? new Vector<>(2) : (Vector<ValidPanelListener>) validPanelListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      validPanelListeners = v;
    }
  }
  protected void fireSetHighlightPerformed(ValidPanelEvent e) {
    if (validPanelListeners != null) {
      Vector<ValidPanelListener> listeners = validPanelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        listeners.elementAt(i).setHighlightPerformed(e);
      }
    }
  }

  void btnSetMark_actionPerformed(ActionEvent e) {
    String xyStr = txtMarkLocation.getText();
    setMarkPoint(parseXY(xyStr));
  }

  static Coordinate parseXY(String xyStr)
  {
    // remove commas and underscores in case they are present
    String cleanStr = xyStr.replace("_", "");
    cleanStr = cleanStr.replace(",", " ");
    String[] xy = cleanStr.trim().split("\\s+");
    double x = parseNumber(xy, 0);
    double y = parseNumber(xy, 1);
    return new Coordinate(x, y);
  }

  static double parseNumber(String[] xy, int index)
  {
    if (xy.length <= index) return 0.0;
    String s = xy[index];
    try {
      return Double.parseDouble(s);
    } 
    catch (NumberFormatException ex)
    {
      // just eat it - not much we can do
    }
    return 0.0;
  }
}
