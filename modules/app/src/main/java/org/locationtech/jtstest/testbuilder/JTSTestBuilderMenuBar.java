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

import org.locationtech.jtstest.testbuilder.controller.JTSTestBuilderController;

import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class JTSTestBuilderMenuBar 
{
  final JMenuBar jMenuBar1 = new JMenuBar();
  final JMenu jMenuFile = new JMenu();
  final JMenu jMenuHelp = new JMenu();
  final JMenuItem jMenuAbout = new JMenuItem();
  final JMenu jMenuView = new JMenu();
  final JMenuItem jMenuFileExit = new JMenuItem();
  final JMenu jMenuEdit = new JMenu();
  //JMenu jMenuTools = new JMenu();
  //JMenu jMenuOptions = new JMenu();
  final JMenuItem menuViewText = new JMenuItem();
  final JMenuItem menuViewGeometry = new JMenuItem();
  final JMenuItem menuLoadXmlTestFile = new JMenuItem();
  final JMenuItem saveAsXmlMenuItem = new JMenuItem();
  final JMenuItem saveAsHtmlMenuItem = new JMenuItem();
  final JMenuItem saveAsPNGMenuItem = new JMenuItem();
  final JMenuItem saveToClipboardMenuItem = new JMenuItem();
  final JMenuItem deleteAllTestCasesMenuItem = new JMenuItem();
  final JMenuItem menuLoadXmlTestFolder = new JMenuItem();
  final JMenuItem precisionModelMenuItem = new JMenuItem();
  final JMenuItem removeDuplicatePoints = new JMenuItem();
  final JMenuItem changeToLines = new JMenuItem();

  final JTSTestBuilderFrame tbFrame;
  
  public JTSTestBuilderMenuBar(JTSTestBuilderFrame tbFrame) 
  {
    this.tbFrame = tbFrame;
  }

  public JMenuBar getMenuBar()
  {
    jMenuAbout.setText("About");
    jMenuAbout.addActionListener(
            e -> TestBuilderDialogs.showAbout(tbFrame));

    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(
            e -> JTSTestBuilderFrame.actionExit());
    menuViewText.setText("Test Case Text...");
    menuViewText.addActionListener(
            e -> TestBuilderDialogs.viewCaseText(tbFrame));
    menuViewGeometry.setText("Geometry Inspector...");
    menuViewGeometry.addActionListener(
            e -> JTSTestBuilderController.inspectGeometryDialogForCurrentCase());
    JMenuItem menuShowIndicators = menuItemCheck("Show Indicators",
        JTSTestBuilderFrame.isShowingIndicators,
            e -> JTSTestBuilderFrame.isShowingIndicators = ! JTSTestBuilderFrame.isShowingIndicators);
    JMenuItem menuSaveIndicators = menuItemCheck("Save Indicators",
        JTSTestBuilderFrame.isSavingIndicators,
            e -> JTSTestBuilderFrame.isSavingIndicators = ! JTSTestBuilderFrame.isSavingIndicators);
    menuLoadXmlTestFile.setText("Open XML File(s)...");
    menuLoadXmlTestFile.addActionListener(
            tbFrame::menuLoadXmlTestFile_actionPerformed);
    saveAsXmlMenuItem.setText("Save As XML...");
    saveAsXmlMenuItem.addActionListener(
            e -> TestBuilderDialogs.saveAsXML(tbFrame, JTSTestBuilder.model()));
    saveAsHtmlMenuItem.setText("Save As HTML...");
    saveAsHtmlMenuItem.addActionListener(
            e -> TestBuilderDialogs.saveAsHtml(tbFrame, JTSTestBuilder.model()));
    JMenuItem saveAsSvgMenuItem = new JMenuItem();
    saveAsSvgMenuItem.setText("Save As HTML+SVG...");
    saveAsSvgMenuItem.addActionListener(
            e -> TestBuilderDialogs.saveAsHtmlSVG(tbFrame, JTSTestBuilder.model()));
    saveAsPNGMenuItem.setText("Save As PNG...");
    saveAsPNGMenuItem.addActionListener(
            e -> JTSTestBuilderController.saveImageAsPNG());
    saveToClipboardMenuItem.setText("Save Screen To Clipboard");
    saveToClipboardMenuItem.addActionListener(
            e -> JTSTestBuilderController.saveImageToClipboard());
    deleteAllTestCasesMenuItem.setText("Delete All Test Cases");
    deleteAllTestCasesMenuItem.addActionListener(
            e -> tbFrame.actionDeleteAllTestCases());

    menuLoadXmlTestFolder.setText("Open XML Folder(s)...");
    menuLoadXmlTestFolder.addActionListener(
            e -> tbFrame.actionLoadXmlTestFolder());
    precisionModelMenuItem.setText("Precision Model...");
    precisionModelMenuItem.addActionListener(
            e -> TestBuilderDialogs.precisionModel(tbFrame));
    removeDuplicatePoints.setText("Remove Duplicate Points");
    removeDuplicatePoints.addActionListener(e -> JTSTestBuilderController.removeDuplicatePoints());
    changeToLines.setText("Change to Lines");
    changeToLines.addActionListener(e -> JTSTestBuilderController.changeToLines());
    jMenuFile.setText("File");
    //jMenuOptions.setText("Options");
    //jMenuTools.setText("Tools");

    jMenuFile.add(menuLoadXmlTestFile);
    jMenuFile.add(menuLoadXmlTestFolder);
    jMenuFile.add(saveAsXmlMenuItem);
    jMenuFile.add(saveAsSvgMenuItem);
    //-----------------------
    jMenuFile.addSeparator();
    jMenuFile.add(saveAsPNGMenuItem);
    jMenuFile.add(saveToClipboardMenuItem);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    //==========================
    
    jMenuHelp.setText("Help");
    jMenuHelp.add(jMenuAbout);
    //==========================
    jMenuView.setText("View");

    jMenuView.add(menuViewText);
    jMenuView.add(menuViewGeometry);
    //-----------------------
    jMenuEdit.addSeparator();
    jMenuView.add(menuShowIndicators);
    jMenuView.add(menuSaveIndicators);
    
    //==========================    
    jMenuEdit.setText("Edit");
    jMenuEdit.add(deleteAllTestCasesMenuItem);
    jMenuEdit.add(precisionModelMenuItem);
    //-----------------------
    jMenuEdit.addSeparator();
    jMenuEdit.add(removeDuplicatePoints);
    jMenuEdit.add(changeToLines);
    
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuView);
    jMenuBar1.add(jMenuEdit);
    //jMenuBar1.add(jMenuOptions);
    //jMenuBar1.add(jMenuTools);
    jMenuBar1.add(jMenuHelp);

    return jMenuBar1;
  }

  static JMenuItem menuItemCheck(String name, boolean init) {
    return createMenuItemSelectable(new JCheckBoxMenuItem(), name, init, null);
  }
  static JMenuItem menuItemCheck(String name, boolean init, ActionListener listener) {
    return createMenuItemSelectable(new JCheckBoxMenuItem(), name, init, listener);
  }
  static JMenuItem menuItemRadio(String name, boolean init) {
    return createMenuItemSelectable(new JRadioButtonMenuItem(), name, init, null);
  }
  static JMenuItem menuItemRadio(String name, boolean init, ActionListener listener) {
    return createMenuItemSelectable(new JRadioButtonMenuItem(), name, init, listener);
  }
  
  static JMenuItem createMenuItemSelectable(JMenuItem item, String name, boolean init, ActionListener listener) {
    item.setText(name);
    item.setSelected(init);
    if (listener != null) {
      item.addActionListener(listener);
    }
    return item;
  }
}
