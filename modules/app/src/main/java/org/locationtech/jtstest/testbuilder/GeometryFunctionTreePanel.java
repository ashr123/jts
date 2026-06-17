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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.locationtech.jtstest.function.DoubleKeyMap;
import org.locationtech.jtstest.geomfunction.GeometryFunction;
import org.locationtech.jtstest.geomfunction.GeometryFunctionUtil;
import org.locationtech.jtstest.testbuilder.event.GeometryFunctionEvent;
import org.locationtech.jtstest.testbuilder.event.GeometryFunctionListener;


/**
 * @version 1.7
 */
public class GeometryFunctionTreePanel extends JPanel {
  
  final JScrollPane jScrollPane = new JScrollPane();

	final JTree tree = new JTree();

	final BorderLayout borderLayout = new BorderLayout();

	Border border1;

	private static GeometryFunction getFunctionFromNode(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node == null)
			return null;
		Object nodeValue = node.getUserObject();
		if (nodeValue instanceof GeometryFunction)
			return (GeometryFunction) nodeValue;
		return null;
	}

	private static class GeometryFunctionRenderer extends DefaultTreeCellRenderer {
    private final ImageIcon binaryIcon = new ImageIcon(this.getClass()
        .getResource("BinaryGeomFunction.png"));

    private final ImageIcon binaryOptBIcon = new ImageIcon(this.getClass()
        .getResource("BinaryGeomFunctionOptB.png"));

		private final ImageIcon unaryIcon = new ImageIcon(this.getClass()
				.getResource("UnaryGeomFunction.png"));

		public GeometryFunctionRenderer() {
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
					hasFocus);
			if (leaf) {
				GeometryFunction func = getFunctionFromNode(value);
				setIcon( computeIcon(func) );
        //String name = StringUtil.capitalize(func.getName());
        String name = func.getName();
				setText(name);
				setToolTipText(GeometryFunctionUtil.toolTipText(func)); 
			} else {
				setToolTipText(null); // no tool tip
			}
			return this;
		}

    private ImageIcon computeIcon(GeometryFunction func) {
      ImageIcon icon = unaryIcon;
      if (func.isBinary()) {
        icon = func.isRequiredB() ? binaryIcon : binaryOptBIcon;
      }
      return icon;
    }

	}

	public GeometryFunctionTreePanel() {
		try {
			jbInit();
      javax.swing.ToolTipManager.sharedInstance().registerComponent(tree);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() {
		setSize(200, 250);
		border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
		setLayout(borderLayout);
		setBorder(border1);
		add(jScrollPane, BorderLayout.CENTER);
		jScrollPane.getViewport().add(tree, null);

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new GeometryFunctionRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					GeometryFunction fun = getFunction();
					if (fun != null)
						fireFunctionInvoked(new GeometryFunctionEvent(fun));
				}

			}
		});
		tree.addTreeSelectionListener(e -> {
            GeometryFunction fun = getFunction();
            if (fun != null)
                fireFunctionSelected(new GeometryFunctionEvent(fun));
        });
	}

	public GeometryFunction getFunction() {
		return getFunctionFromNode(tree.getLastSelectedPathComponent());
	}

	public void populate(DoubleKeyMap<String, String, GeometryFunction> funcs) {
		tree.setModel(createModel(funcs));
	}

	private static TreeModel createModel(DoubleKeyMap<String, String, GeometryFunction> funcMap) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode();

		Collection<String> categories = funcMap.keySet();
        for (String category : categories) {
            DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(category);
            top.add(catNode);

            Collection<GeometryFunction> funcs = funcMap.values(category);
            for (GeometryFunction func : funcs) {
                catNode.add(new DefaultMutableTreeNode(func));
            }
        }
		return new DefaultTreeModel(top);
	}

	private transient Vector<GeometryFunctionListener> eventListeners;

	public synchronized void removeGeometryFunctionListener(
			GeometryFunctionListener l) {
		if (eventListeners != null && eventListeners.contains(l)) {
			Vector<GeometryFunctionListener> v = (Vector<GeometryFunctionListener>) eventListeners.clone();
			v.removeElement(l);
			eventListeners = v;
		}
	}

	public synchronized void addGeometryFunctionListener(
			GeometryFunctionListener l) {
		Vector<GeometryFunctionListener> v = eventListeners == null ? new Vector<>(2) : (Vector<GeometryFunctionListener>) eventListeners
				.clone();
		if (!v.contains(l)) {
			v.addElement(l);
			eventListeners = v;
		}
	}

	protected void fireFunctionSelected(GeometryFunctionEvent e) {
		if (eventListeners != null) {
			Vector<GeometryFunctionListener> listeners = eventListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				listeners.elementAt(i).functionSelected(e);
			}
		}
	}

	protected void fireFunctionInvoked(GeometryFunctionEvent e) {
		if (eventListeners != null) {
			Vector<GeometryFunctionListener> listeners = eventListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				listeners.elementAt(i).functionInvoked(e);
			}
		}
	}

}
