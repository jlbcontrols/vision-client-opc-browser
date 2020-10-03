package com.jlbcontrols.vcob;

import com.inductiveautomation.vision.api.client.components.model.ExtensionFunction;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class OpcBrowseTreeCellRenderer extends DefaultTreeCellRenderer {
    private OpcBrowseTreeComponent opcBrowseTreeComponent;
    public OpcBrowseTreeCellRenderer(OpcBrowseTreeComponent opcBrowseTreeComponent) {
        this.opcBrowseTreeComponent = opcBrowseTreeComponent;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        // Don't call the extension if it's an error node or loading node
        if (value instanceof OpcBrowseNode) {
            ExtensionFunction.invoke(
                    this.opcBrowseTreeComponent,
                    OpcBrowseTreeComponent.log,
                    "configureRenderer",
                    void.class,
                    null,
                    new Object[]{this, value, sel, expanded, leaf, row, hasFocus}
            );
        }
        return this;
    }
}