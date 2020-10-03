package com.jlbcontrols.vcob;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.vision.api.designer.VisionDesignerInterface;
import com.inductiveautomation.vision.api.designer.palette.JavaBeanPaletteItem;
import com.inductiveautomation.vision.api.designer.palette.Palette;
import com.inductiveautomation.vision.api.designer.palette.PaletteItemGroup;


public class VcobDesignerHook extends AbstractDesignerModuleHook {

    public static final String MODULE_ID = "vision-client-opc-browser";

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        // Add the BeanInfo package to the search path
        context.addBeanInfoSearchPath("com.jlbcontrols.vcob.beaninfos");

        // Add the component to its own palette
        VisionDesignerInterface sdk = (VisionDesignerInterface) context
                .getModule(VisionDesignerInterface.VISION_MODULE_ID);
        if (sdk != null) {
            Palette palette = sdk.getPalette();

            PaletteItemGroup group = palette.addGroup("OPC Browser");
            group.addPaletteItem(new JavaBeanPaletteItem(OpcBrowseTreeComponent.class));
        }
    }

}

