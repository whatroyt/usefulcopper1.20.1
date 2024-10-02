package whatro.usefulcopper.entity.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.Usefulcopper;

public class ModModelLayers {
    public static final EntityModelLayer BLOB =
            new EntityModelLayer(new Identifier(Usefulcopper.MOD_ID, "blob"), "main");
}
