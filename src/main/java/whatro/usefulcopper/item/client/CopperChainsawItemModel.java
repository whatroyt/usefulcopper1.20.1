package whatro.usefulcopper.item.client;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import whatro.usefulcopper.Usefulcopper;
import whatro.usefulcopper.item.custom.CopperChainsawItem;

public class CopperChainsawItemModel extends GeoModel<CopperChainsawItem> {
    @Override
    public Identifier getModelResource(CopperChainsawItem animatable) {
        return new Identifier(Usefulcopper.MOD_ID, "geo/copper_chainsaw.geo.json");
    }

    @Override
    public Identifier getTextureResource(CopperChainsawItem animatable) {
        return new Identifier(Usefulcopper.MOD_ID, "textures/item/copper_chainsaw.png");
    }

    @Override
    public Identifier getAnimationResource(CopperChainsawItem animatable) {
        return new Identifier(Usefulcopper.MOD_ID, "animations/copper_chainsaw.animation.json");
    }
}
