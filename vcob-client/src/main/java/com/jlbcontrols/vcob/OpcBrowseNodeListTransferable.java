package com.jlbcontrols.vcob;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

public class OpcBrowseNodeListTransferable implements Transferable {

    public static final DataFlavor FLAVOR = new DataFlavor(OpcBrowseNodeList.class, "OPC Browse Nodes");

    private List<OpcBrowseNode> browseNodes;

    public OpcBrowseNodeListTransferable(List<OpcBrowseNode> browseNodes) {
        this.browseNodes=browseNodes;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        if (dataFlavor == FLAVOR)
            return true;
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        return browseNodes;
    }

    public interface OpcBrowseNodeList extends List<OpcBrowseNode> {
    }
}
