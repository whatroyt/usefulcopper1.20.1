package whatro.usefulcopper.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.Usefulcopper;
import whatro.usefulcopper.entity.custom.BlobEntity;

public class BlobRenderer extends MobEntityRenderer<BlobEntity, BlobModel<BlobEntity>> {
    private static final Identifier TEXTURE = new Identifier(Usefulcopper.MOD_ID, "textures/entity/blob.png");

    public BlobRenderer(EntityRendererFactory.Context context) {
        super(context, new BlobModel<>(context.getPart(ModModelLayers.BLOB)), 0.0f);
    }

    @Override
    public Identifier getTexture(BlobEntity entity) {
        return TEXTURE;
    }
}
