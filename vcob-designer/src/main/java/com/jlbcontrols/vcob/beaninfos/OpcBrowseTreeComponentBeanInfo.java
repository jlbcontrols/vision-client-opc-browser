package com.jlbcontrols.vcob.beaninfos;

import com.inductiveautomation.factorypmi.designer.property.customizers.DynamicPropertyProviderCustomizer;
import com.inductiveautomation.factorypmi.designer.property.customizers.StyleCustomizer;
import com.inductiveautomation.vision.api.designer.beans.ExtensionFunctionDescriptor;
import com.jlbcontrols.vcob.OpcBrowseTreeComponent;
import com.inductiveautomation.vision.api.designer.beans.CommonBeanInfo;
import com.inductiveautomation.vision.api.designer.beans.VisionBeanDescriptor;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class OpcBrowseTreeComponentBeanInfo extends CommonBeanInfo {

    public OpcBrowseTreeComponentBeanInfo() {
        super(OpcBrowseTreeComponent.class, DynamicPropertyProviderCustomizer.VALUE_DESCRIPTOR, StyleCustomizer.VALUE_DESCRIPTOR);
    }

    @Override
    protected void initProperties() throws IntrospectionException {
        // Adds common properties
        super.initProperties();

        // Remove some properties which aren't used in our component.
        removeProp("foreground");
        removeProp("background");
        removeProp("font");
        removeProp("opaque");

        addBoundProp("rootServer", "Root Server", "The name of the OPC server to browse. Leave blank to browse all servers.",
                CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addBoundProp("rootNodeId", "Root Node ID", "OPC Node ID of the root node. Leave blank to browse all nodes.",
                CAT_DATA, PREFERRED_MASK | BOUND_MASK);

        addProp("selectedNodeIds", "Selected Node IDs", "Read only property containing the currently selected OPC Node IDs.",
                CAT_DATA, PREFERRED_MASK);

        addProp("showRefresh", "Show Refresh Button", "Show a button that is used to refresh the tree.",
                CAT_DATA, PREFERRED_MASK);

        // Custom Property Support
        addProp("dynamicProps", "Dynamic Properties", "", HIDDEN_MASK);

    }

    @Override
    public Image getIcon(int kind) {
        switch (kind) {
            case BeanInfo.ICON_COLOR_16x16:
            case BeanInfo.ICON_MONO_16x16:
                return new ImageIcon(getClass().getResource("/images/file-tree-16.png")).getImage();
            case SimpleBeanInfo.ICON_COLOR_32x32:
            case SimpleBeanInfo.ICON_MONO_32x32:
                return new ImageIcon(getClass().getResource("/images/file-tree-32.png")).getImage();
        }
        return null;
    }

    @Override
    protected void initDesc() {
        VisionBeanDescriptor bean = getBeanDescriptor();
        bean.setName("OPC Browse Tree");
        bean.setDisplayName("OPC Browse Tree");
        bean.setShortDescription("A tree component used to browse OPC tags.");
        List<ExtensionFunctionDescriptor> extensionFunctionList = new ArrayList();
        extensionFunctionList.add(ExtensionFunctionDescriptor.newFunction("configureRenderer")
                .returns(void.class)
                .description("Customize the appearance of nodes in the tree by configuring the tree cell renderer (javax.swing.tree.DefaultTreeCellRenderer).")
                .defaultImpl("# Put your code here")
                .arg("treeCellRenderer", "The tree cell renderer. Use setter methods to customize this object.")
                .arg("opcBrowseNode", "The com.jlbcontrols.OpcBrowseNode object that is being rendered. Methods include getBrowseElement(), getChildNodes().")
                .arg("sel", "True if the node is currently selected.")
                .arg("expanded", "True if the node is currently expanded (folder open).")
                .arg("leaf", "True if the node does not have children.")
                .arg("row", "The index of the node.")
                .arg("hasFocus", "True if the node is currently in focus.")
                .build());
        bean.setValue("vision-extension-functions", extensionFunctionList);
    }
}
