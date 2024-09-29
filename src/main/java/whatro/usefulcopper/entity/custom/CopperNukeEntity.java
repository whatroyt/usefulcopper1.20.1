package whatro.usefulcopper.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.item.ModItems;

public class CopperNukeEntity extends ThrownItemEntity {

    private static final int NUKE_RADIUS = 200;

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
            World world = this.getWorld();
            BlockPos impactPos = this.getBlockPos();

            int radius = NUKE_RADIUS;

            // Iterate through blocks within a sphere rather than a cube
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos blockPos = impactPos.add(x, y, z);

                        // Skip air blocks immediately
                        if (world.getBlockState(blockPos).isAir()) {
                            continue;
                        }

                        // Calculate the distance from the center to make sure it's within a sphere
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        if (distance <= radius) {
                            // Set the block to air if it's not air already
                            world.setBlockState(blockPos, net.minecraft.block.Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }

            // Remove the entity after collision
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }
}
