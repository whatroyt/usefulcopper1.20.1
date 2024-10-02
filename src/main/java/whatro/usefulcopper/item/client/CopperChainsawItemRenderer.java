package whatro.usefulcopper.item.client;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import whatro.usefulcopper.item.custom.CopperChainsawItem;

public class CopperChainsawItemRenderer extends GeoItemRenderer<CopperChainsawItem> {
    public CopperChainsawItemRenderer() {
        super(new CopperChainsawItemModel());
    }
}
