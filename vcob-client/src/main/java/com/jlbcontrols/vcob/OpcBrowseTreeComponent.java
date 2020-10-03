package com.jlbcontrols.vcob;

import com.inductiveautomation.ignition.common.BasicDataset;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.opc.BrowseElement;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.vision.api.client.components.model.AbstractVisionPanel;
import com.inductiveautomation.vision.api.client.components.model.ExtensibleComponent;
import com.inductiveautomation.vision.api.client.components.model.ExtensionFunction;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

/**
 * OPC Browse Tree Vision Client Component
 *
 * @author jlbcontrols
 */
public class OpcBrowseTreeComponent extends AbstractVisionPanel implements ExtensibleComponent {
    public static LoggerEx log = LoggerEx.newBuilder().build(OpcBrowseTreeComponent.class);

    // Needed to implement Ignition extensible component
    Map<String, ExtensionFunction> extensionFunctions = null;

    JTree tree;
    DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
    OpcBrowseNode rootNode;
    protected JButton refreshButton = new JButton();
    private Dataset selectedNodeIds = new BasicDataset(new String[]{"selectedNodeId"},new Class<?>[]{String.class},null);
    private String rootServer = "Ignition OPC UA Server";
    private String rootNodeId = "ns=1;s=Devices";

    public OpcBrowseTreeComponent() {
        super(new MigLayout("","0[]0","0[]0"));

        this.tree = new JTree(treeModel);
        this.tree.setCellRenderer(new OpcBrowseTreeCellRenderer(this));
        this.tree.setRootVisible(false);
        this.tree.setShowsRootHandles(true);
        this.tree.setRowHeight(18);
        this.tree.addTreeSelectionListener(new ClientOpcTreeSelectionListener());
        this.tree.setDragEnabled(true);
        this.tree.setTransferHandler(new ClientOpcTransferHandler());
        this.tree.getInputMap(0).put(KeyStroke.getKeyStroke(27, 0), "clearSelection");
        this.tree.getActionMap().put("clearSelection", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                OpcBrowseTreeComponent.this.tree.clearSelection();
            }
        });

        this.refreshButton.setMargin(new Insets(-5,-5,-5,-5));
        this.refreshButton.setBackground(new Color(0,0,0,0));
        this.refreshButton.setIcon(new ImageIcon(OpcBrowseTreeComponent.class.getResource("/images/refresh-24.png")));
        this.refreshButton.setBorderPainted(false);
        this.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpcBrowseTreeComponent.this.refresh();
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.add(tree);
        scrollPane.setViewportView(tree);

        this.setPreferredSize(new Dimension(300,600));
        this.setOpaque(false);
        this.add(refreshButton, "gap bottom 1, alignx right, hidemode 3, wrap");
        this.add(scrollPane,"pushx, growx, pushy, growy");
    }

    @Override
    public Map<String, ExtensionFunction> getExtensionFunctions() {
        return this.extensionFunctions;
    }

    @Override
    public void setExtensionFunctions(Map<String, ExtensionFunction> map) {
        this.extensionFunctions = map;
    }

    class ClientOpcTreeSelectionListener implements TreeSelectionListener{
        @Override
        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            DatasetBuilder selectedPathDsBuilder = new DatasetBuilder();
            selectedPathDsBuilder = selectedPathDsBuilder.colNames("selectedNodeId");
            selectedPathDsBuilder = selectedPathDsBuilder.colTypes(String.class);
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null){
                for (TreePath path : paths){
                    OpcBrowseNode node = (OpcBrowseNode)path.getLastPathComponent();
                    selectedPathDsBuilder.addRow(node.getBrowseElement().getItemId());
                }
            }
            Dataset old = selectedNodeIds;
            selectedNodeIds = selectedPathDsBuilder.build();
            firePropertyChange("selectedNodeIds", old, getSelectedNodeIds());
        }
    }

    class ClientOpcTransferHandler extends TransferHandler{
        public ClientOpcTransferHandler() {
        }
        @Override
        protected Transferable createTransferable(JComponent c) {
            TreePath[] paths = tree.getSelectionPaths();
            ArrayList<OpcBrowseNode> selectedNodes = new ArrayList<>();
            if (paths != null){
                for (TreePath path : paths){
                    OpcBrowseNode node = (OpcBrowseNode)path.getLastPathComponent();
                    selectedNodes.add((OpcBrowseNode)path.getLastPathComponent());
                }
            }
            return new OpcBrowseNodeListTransferable(selectedNodes);
        }
        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            return false;
        }
        @Override
        public int getSourceActions(JComponent c){
            return TransferHandler.COPY;
        }
    }

    public void refresh() {
        BrowseElement rootBrowseElement = new BrowseElement(0);
        rootBrowseElement.setServer(rootServer);
        rootBrowseElement.setItemId(rootNodeId);
        rootNode = new OpcBrowseNode(treeModel, null, context, rootBrowseElement);
        treeModel.setRoot(rootNode);
        rootNode.reload();
    }

    public String getRootServer() {
        return rootServer;
    }
    public void setRootServer(String rootServer) {
        String old = getRootServer();
        this.rootServer = rootServer;
        log.debug("rootServer: old="+old+" new="+getRootServer());
        refresh();
        firePropertyChange("rootServer", old, getRootServer());
    }

    public String getRootNodeId() {
        return rootNodeId;
    }
    public void setRootNodeId(String rootNodeId) {
        String old = getRootNodeId();
        this.rootNodeId = rootNodeId;
        log.debug("rootItemId: old="+old+" new="+ getRootNodeId());
        refresh();
        firePropertyChange("rootItemId", old, getRootNodeId());
    }

    public boolean isShowRefresh() {
        return refreshButton.isVisible();
    }

    public void setShowRefresh(boolean showRefresh) {
        boolean old = isShowRefresh();
        refreshButton.setVisible(showRefresh);
        log.debug("showRefresh: old="+old+" new="+ isShowRefresh());
        firePropertyChange("showRefresh", old, isShowRefresh());
    }

    @Override
    protected void onStartup(){
        refresh();
    }

    public Dataset getSelectedNodeIds(){
        return this.selectedNodeIds;
    }

    public void setSelectedNodeIds(Dataset selectedNodeIds){
    }

    @Override
    protected void onShutdown() {
    }
}
