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

package org.locationtech.jtstest.testbuilder.controller;


import java.awt.Color;

import javax.swing.JFileChooser;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.LinearComponentExtracter;
import org.locationtech.jtstest.clean.CleanDuplicatePoints;
import org.locationtech.jtstest.testbuilder.AppConstants;
import org.locationtech.jtstest.testbuilder.AppStrings;
import org.locationtech.jtstest.testbuilder.GeometryEditPanel;
import org.locationtech.jtstest.testbuilder.JTSTestBuilder;
import org.locationtech.jtstest.testbuilder.JTSTestBuilderFrame;
import org.locationtech.jtstest.testbuilder.JTSTestBuilderToolBar;
import org.locationtech.jtstest.testbuilder.SpatialFunctionPanel;
import org.locationtech.jtstest.testbuilder.TestBuilderDialogs;
import org.locationtech.jtstest.testbuilder.geom.GeometryElementLocater;
import org.locationtech.jtstest.testbuilder.model.GeometryEditModel;
import org.locationtech.jtstest.testbuilder.model.LayerList;
import org.locationtech.jtstest.testbuilder.model.TestBuilderModel;
import org.locationtech.jtstest.testbuilder.ui.ImageUtil;
import org.locationtech.jtstest.testbuilder.ui.SwingUtil;
import org.locationtech.jtstest.testbuilder.ui.render.ViewStyle;
import org.locationtech.jtstest.testbuilder.ui.tools.DeleteByBoxTool;
import org.locationtech.jtstest.testbuilder.ui.tools.EditVertexTool;
import org.locationtech.jtstest.testbuilder.ui.tools.ExtractComponentTool;
import org.locationtech.jtstest.testbuilder.ui.tools.InfoTool;
import org.locationtech.jtstest.testbuilder.ui.tools.LineStringTool;
import org.locationtech.jtstest.testbuilder.ui.tools.MoveTool;
import org.locationtech.jtstest.testbuilder.ui.tools.PanTool;
import org.locationtech.jtstest.testbuilder.ui.tools.PointTool;
import org.locationtech.jtstest.testbuilder.ui.tools.RectangleTool;
import org.locationtech.jtstest.testbuilder.ui.tools.SelectElementTool;
import org.locationtech.jtstest.testbuilder.ui.tools.StreamPolygonTool;
import org.locationtech.jtstest.testbuilder.ui.tools.Tool;
import org.locationtech.jtstest.testbuilder.ui.tools.ZoomTool;


public class JTSTestBuilderController 
{ 
  private static final ResultController resultController = new ResultController();

  /*
  private static boolean autoZoomOnNextChange = false;

  
  public static void requestAutoZoom()
  {
    autoZoomOnNextChange  = true;
  }
  */
  private JTSTestBuilderController() {
    
  }
  public static ResultController resultController() {
    return resultController;
  }
  public static TestBuilderModel model() {
    return frame().getModel();
  }
  public static GeometryEditPanel editPanel() {
    return JTSTestBuilderFrame.getGeometryEditPanel();
  }
  public static SpatialFunctionPanel spatialFunctionPanel() {
    return JTSTestBuilderFrame.getSpatialFunctionPanel();
  }

  public static JTSTestBuilderToolBar toolbar() {
    return frame().getToolbar();
  }  

  public static JTSTestBuilderFrame frame() {
    return JTSTestBuilderFrame.instance();
  }

  public static GeometryEditModel getGeomEditModel() {
    return JTSTestBuilder.model().getGeometryEditModel();
  }
  
  public static void reportException(Exception e) {
    SwingUtil.reportException(frame(), e);
  }
  
  public static void geometryChanged()
  {
    if (spatialFunctionPanel().isAutoExecute()) {
      resultController.execute(false);
    }
    geometryViewChanged();
  }
  
  public static void geometryViewChanged()
  {
    editPanel().updateView();
    //TODO: provide autoZoom checkbox on Edit tab to control autozooming (default = on)
  }
  
  public static Geometry getGeometryA() {
    return getGeomEditModel().getGeometry(0);
  }

  public static Geometry getGeometryB() {
    return getGeomEditModel().getGeometry(1);
  }

  public static void exchangeGeometry() {
    getGeomEditModel().exchangeGeometry();
  }
  
  public static void caseAdd(Geometry[] geoms, String name)
  {
    model().addCase(geoms, name);
    JTSTestBuilderFrame.instance().updateTestCases();
    JTSTestBuilderFrame.instance().showGeomsTab();
    selectClear();
  }
  
  public static void copyElementsToTestCase(Coordinate pt)
  {
    double toleranceInModel = editPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry comp = lyrList.getElement(pt, toleranceInModel);
    if (comp == null) 
      return;
    caseAdd(new Geometry[] { comp, null }, "Extract");
  }
  
  public static void copyElementsToTestCase(Geometry aoi, boolean isSegments)
  {
    //double toleranceInModel = JTSTestBuilderFrame.getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry[] comp;
    comp = lyrList.getElements(aoi, isSegments);
    if (comp == null) 
      return;
    caseAdd(comp, "Extract");
    toolbar().selectZoomButton();
    modeZoomIn();
  }

  public static void copyElementToClipboard(Coordinate pt)
  {
    double toleranceInModel = editPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry comp = lyrList.getElement(pt, toleranceInModel);
    if (comp == null) 
      return;
    SwingUtil.copyToClipboard(comp, false);
  }
  
  public static void selectElements(Geometry aoi)
  {
    Geometry geom = model().getGeometryEditModel().getGeometry();
    Geometry comp = null;
    if (geom != null) {
      comp = GeometryElementLocater.extractElements(geom, aoi);
    }
    if (comp == null) {
      model().clearSelection();
    } 
    else {
      model().getLayerSelect().setEnabled(true);
      model().setSelection(comp);
    }
    geometryViewChanged();
    layerListRefresh();
  }

  public static void selectClear() {
    model().clearSelection();
    layerListRefresh();
  }
  
  public static void setFocusGeometry(int index) {
    model().getGeometryEditModel().setEditGeomIndex(index);
    toolbar().setFocusGeometry(index);    
  }

  public static void flash(Geometry geom)
  {
    JTSTestBuilderFrame.getGeometryEditPanel().flash(geom);
  }
  
  public static void inspectGeometry()
  {
    JTSTestBuilderFrame.instance().inspectGeometry();
  }

  public static void inspectResult()
  {
    JTSTestBuilderFrame.instance().inspectResult();
  }

  public static void inspectGeometry(String name, Geometry geometry) {
    JTSTestBuilderFrame.instance().inspectGeometry(name, geometry);
  }

  public static void inspectGeometryDialog(String name, Geometry geometry)
  {
    TestBuilderDialogs.inspectGeometry(frame(), name, geometry);
  }
  
  public static void inspectGeometryDialogForCurrentCase()
  {
    int geomIndex = model().getGeometryEditModel().getGeomIndex();
    Geometry geometry = model().getCurrentCase().getGeometry(geomIndex);
    TestBuilderDialogs.inspectGeometry(frame(), geomIndex, geometry);
  }
  
  public static void resultClear()
  {
    frame().getResultWKTPanel().clearResult();
    model().setResult(null);
    editPanel().updateView();
  }
  
  public static void setResult(String opName, Object result) {
    model().setResult(result);
    model().setOpName(opName);
    frame().getResultWKTPanel().setOpName(opName);
    frame().getResultWKTPanel().setExecutedTime("");
    frame().getResultWKTPanel().setResult(result);
    geometryViewChanged();
  }
  
  public static void setCommandErr(String msg) {
    frame().getCommandPanel().setError(msg);
  }
  
  public static void saveImageAsPNG() {
    //JTSTestBuilderFrame.instance().actionSaveImageAsPNG();
    JFileChooser pngFileChooser = TestBuilderDialogs.getSavePNGFileChooser();
    try {
      String fullFileName = SwingUtil.chooseFilenameWithConfirm(frame(), pngFileChooser);  
      if (fullFileName == null) return;
        ImageUtil.writeImage(editPanel(), 
            fullFileName,
            ImageUtil.IMAGE_FORMAT_NAME_PNG);
    }
    catch (Exception x) {
      reportException(x);
    }
  }
  
  public static void saveImageToClipboard() {
    try {
      ImageUtil.saveImageToClipboard(editPanel(), 
          ImageUtil.IMAGE_FORMAT_NAME_PNG);
    }
    catch (Exception x) {
      reportException(x);
    }
  }

  //==================================
  
  public static void layerListUpdate() {
    JTSTestBuilderFrame.instance().updateLayerList();
  }
  
  public static void layerListRefresh() {
    JTSTestBuilderFrame.instance().refreshLayerList();
  }
  
  //================================
      

  
  private static void setTool(Tool tool) {
    editPanel().setCurrentTool(tool);
  }
  public static void modeDrawRectangle() {
    setTool(RectangleTool.getInstance());
  }

  public static void modeDrawPolygon() {
    setTool(StreamPolygonTool.getInstance());
  }

  public static void modeDrawLineString() {
    setTool(LineStringTool.getInstance());
  }

  public static void modeDrawPoint() {
    setTool(PointTool.getInstance());
  }

  public static void modeInfo() {
    setTool(InfoTool.getInstance());
  }

  public static void modeExtractComponent() {
    setTool(ExtractComponentTool.getInstance());
  }

  public static void modeSelectComponent() {
    setTool(SelectElementTool.getInstance());
  }

  public static void modeDeleteVertex() {
    setTool(DeleteByBoxTool.getInstance());
  }
  public static void modeEditVertex() {
    setTool(EditVertexTool.getInstance());
  }
  public static void modeMove() {
    setTool(MoveTool.getInstance());
  }
  public static void modeZoomIn() {
    setTool(ZoomTool.getInstance());
  }

  public static void modePan() {
    setTool(PanTool.getInstance());
  }
  public static void zoomOneToOne() {
    editPanel().getViewport().zoomToInitialExtent();
  }

  public static void zoomToFullExtent() {
    editPanel().zoomToFullExtent();
  }

  public static void zoomToResult() {
    editPanel().zoomToResult();
  }

  public static void zoomToInput() {
    editPanel().zoomToInput();
  }

  public static void zoomToInputA() {
    editPanel().zoomToGeometry(0);
  }

  public static void zoomToInputB() {
    editPanel().zoomToGeometry(1);
  }
  
  public static void zoomToGeometry(Geometry geom) {
    editPanel().zoom(geom);
  }
  
  public static void caseMoveTo(int dir, boolean isZoom) {
    if (dir < 1) {
      model().cases().prevCase();
    }
    else {
    model().cases().nextCase();
    }
    frame().updateTestCaseView();
    selectClear();
    if (isZoom) zoomToInput();
  }

  public static void caseCopy() {
    model().cases().copyCase();
    frame().updateTestCases();
    selectClear();
  }
  
  public static void caseCreateNew() {
    model().cases().createNew();
    frame().showGeomsTab();
    frame().updateTestCases();
    selectClear();
  }
  
  public static void caseDelete() {
    model().cases().deleteCase();
    frame().updateTestCases();
    selectClear();
  }
  
  //========================================

  public static void resultCopyToTest()
  {
    Object currResult = model().getResult();
    if (! (currResult instanceof Geometry))
      return;
    model().addCase(
        new Geometry[] { (Geometry) currResult, null }, 
        "Result of " + model().getOpName());
    frame().updateTestCases(); 
  }
  
  //========================================
  
  public static void displayInfo(Coordinate modelPt)
  {
    displayInfo( editPanel().getInfo(modelPt) );
  }
  
  public static void displayInfo(String s)
  {
    displayInfo(s, true);
  }
  
  public static void displayInfo(String s, boolean showTab)
  {
    frame().getLogPanel().addInfo(s);
    if (showTab) frame().showInfoTab();
  }
  
  //========================================
  
  public static void setViewStyle(ViewStyle viewStyle) {
    editPanel().setViewStyle(viewStyle);
    geometryViewChanged();
  }

  //=============================================
  
  public static void removeDuplicatePoints() {
    CleanDuplicatePoints clean = new CleanDuplicatePoints();
    Geometry cleanGeom = clean.clean(model().getGeometryEditModel().getGeometry(0));
    model().getCurrentCase().setGeometry(0, cleanGeom);
    frame().geometryChanged();
  }

  public static void changeToLines() {
    Geometry cleanGeom = LinearComponentExtracter.getGeometry(model().getGeometryEditModel().getGeometry(0));
    model().getCurrentCase().setGeometry(0, cleanGeom);
    frame().geometryChanged();
  }
  
  //=============================================

  public static void indicatorShow(Geometry geom, Color lineClr)
  {
    if (! JTSTestBuilderFrame.isShowingIndicators()) return;
    
    if (JTSTestBuilderFrame.isSavingIndicators()) {
      //-- refresh layer list panel only when indicator layer is created
      boolean refreshLayerList = ! model().hasLayer(AppStrings.LYR_INDICATORS);
      model().addIndicator(geom);
      if (refreshLayerList) 
        frame().refreshLayerList();
    }
    editPanel().draw(geom, lineClr, AppConstants.INDICATOR_FILL_CLR);
  }
}
