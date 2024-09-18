package whatro.usefulcopper.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.Usefulcopper;
import whatro.usefulcopper.entity.custom.CopperBulletProjectileEntity;
import whatro.usefulcopper.entity.custom.CopperNukeEntity;

public class ModEntities {
    public static final EntityType<CopperBulletProjectileEntity> COPPER_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Usefulcopper.MOD_ID, "copper_projectile"),
            FabricEntityTypeBuilder.<CopperBulletProjectileEntity>create(SpawnGroup.MISC, CopperBulletProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.1F, 0.1F)) // dimensions in Minecraft units of the projectile
                    .trackRangeBlocks(192).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
                    .build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
    );
    public static final EntityType<CopperNukeEntity> COPPER_NUKE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Usefulcopper.MOD_ID, "copper_nuke"),
            FabricEntityTypeBuilder.<CopperNukeEntity>create(SpawnGroup.MISC, CopperNukeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // Dimensions similar to a snowball
                    .trackRangeBlocks(64).trackedUpdateRate(10)
                    .build()
    );

    public static void registerEntities() {
        Usefulcopper.LOGGER.info("Registering Mod Entities for " + Usefulcopper.MOD_ID);
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(COPPER_PROJECTILE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(COPPER_NUKE, FlyingItemEntityRenderer::new);
    }
}
