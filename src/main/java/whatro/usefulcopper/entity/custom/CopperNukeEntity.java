package whatro.usefulcopper.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.item.ModItems;

public class CopperNukeEntity extends ThrownItemEntity {

    public CopperNukeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public CopperNukeEntity(World world, PlayerEntity user) {
        super(ModEntities.COPPER_NUKE, user, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.COPPER_NUKE;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            // Explode when hitting something
            this.getWorld().createExplosion(null, this.getX(), this.getY(), this.getZ(), 4.0F, World.ExplosionSourceType.MOB);
            this.discard(); // Remove the entity
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        // Additional logic for when the Copper Nuke hits an entity (e.g., damage)
    }
}
