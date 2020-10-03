package com.jlbcontrols.vcob;

import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.client.util.gui.tree.AbstractAsynchronousTreeNode;
import com.inductiveautomation.ignition.client.util.gui.tree.DefaultErrorNode;
import com.inductiveautomation.ignition.client.util.gui.tree.DefaultLoadingNode;
import com.inductiveautomation.ignition.common.browsing.BrowseFilter;
import com.inductiveautomation.ignition.common.browsing.Results;
import com.inductiveautomation.ignition.common.opc.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OpcBrowseNode extends AbstractAsynchronousTreeNode {

    private ClientContext context;
    private String tagProviderString;
    private BrowseElement browseElement;

    public OpcBrowseNode(DefaultTreeModel treeModel, TreeNode parent, ClientContext context, BrowseElement browseElement) {
        super(treeModel, parent);
        this.context = context;
        this.tagProviderString = "_system_";
        this.browseElement = browseElement;
    }

    @Override
    protected TreeNode newLoadingNode() {
        return new DefaultLoadingNode<>(OpcBrowseNode.this);
    }
    @Override
    protected TreeNode newErrorNode(Throwable throwable) {
        return new DefaultErrorNode<>(OpcBrowseNode.this);
    }

    @Override
    protected List<OpcBrowseNode> loadChildren() throws Exception {
        BrowseElement requestElement = browseElement.createRequestElement(3);
        try {
            Results<BrowseElement> childBrowseElementResults = context.getTagManager().browseTagDataSourcesAsync(tagProviderString, requestElement, (BrowseFilter)null).get(60L, TimeUnit.SECONDS);
            if(childBrowseElementResults.getResults() == null){
                return Collections.emptyList();
            }else{
                return createChildNodes(childBrowseElementResults.getResults());
            }
        } catch (Exception var4) {
            return Collections.emptyList();
        }
    }

    private List<OpcBrowseNode> createChildNodes(Collection<BrowseElement> childBrowseElements){
        List<OpcBrowseNode> childNodes = new ArrayList<>();
        for (BrowseElement childBrowseElement : childBrowseElements){
            childNodes.add(new OpcBrowseNode(this.treeModel,this,this.context,childBrowseElement));
        }
        return childNodes;
    }

    public List<OpcBrowseNode> getChildNodes(){
        if (this.nodeList!=null) {
            return Collections.unmodifiableList(this.nodeList);
        }else{
            return null;
        }
    }

    /**
    Return a copy of the browse element
     */
    public BrowseElement getBrowseElement() {
        return browseElement.createRequestElement(browseElement.getItemType());
    }

    @Override
    public String toString(){
        return browseElement.getItemName();
    }
}
