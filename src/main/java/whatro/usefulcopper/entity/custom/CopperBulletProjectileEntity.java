package whatro.usefulcopper.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.item.ModItems;

public class CopperBulletProjectileEntity extends ThrownItemEntity {
    private static final int DESPAWN_TIME = 96; // 2 seconds = 40 ticks (20 ticks per second)
    private int despawnTimer = 0;
    private static final float DAMAGE = 8.0F;
    private static final double DISTANCE_FACTOR = 0.75; //strength of blob velocity
    private static final int AMOUNT_OF_BLOBS = 7;

    public CopperBulletProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.COPPER_BULLET;
    }

    /*
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.damage(livingEntity.getDamageSources().generic(), DAMAGE);
            this.discard();
        }
    }

     */

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            // Deal damage to the entity
            livingEntity.damage(livingEntity.getDamageSources().generic(), DAMAGE);

            // Get the bullet's current position
            double bulletPosX = this.getX();
            double bulletPosY = this.getY();
            double bulletPosZ = this.getZ();

            // Get the bullet's velocity to determine the direction
            double bulletVelX = this.getVelocity().x;
            double bulletVelY = this.getVelocity().y;
            double bulletVelZ = this.getVelocity().z;

            // Normalize the velocity to get the direction
            double magnitude = Math.sqrt(bulletVelX * bulletVelX + bulletVelY * bulletVelY + bulletVelZ * bulletVelZ);
            if (magnitude != 0) {
                bulletVelX /= magnitude;
                bulletVelY /= magnitude;
                bulletVelZ /= magnitude;
            }

            // Summon 5 blob entities at the bullet's position
            World world = this.getWorld();
            if (!world.isClient) {
                for (int i = 0; i < AMOUNT_OF_BLOBS; i++) {
                    Entity blobEntity = new BlobEntity(ModEntities.BLOB, world);

                    // Set the blob's initial position to the bullet's position
                    blobEntity.setPosition(bulletPosX, bulletPosY, bulletPosZ);

                    // Add randomization to the velocity for a more chaotic effect
                    double randomX = bulletVelX + (Math.random() * 1.0 - 0.5); // More randomization in x direction
                    double randomZ = bulletVelZ + (Math.random() * 1.0 - 0.5); // More randomization in z direction

                    // Set the blob's velocity towards where the bullet came from
                    blobEntity.setVelocity(-randomX * DISTANCE_FACTOR, 0.5, -randomZ * DISTANCE_FACTOR); // Negative to go back in the bullet's direction

                    // Spawn the blob in the world
                    world.spawnEntity(blobEntity);
                }
            }

            // Discard the bullet after hitting the entity
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // Get the block position where the bullet hit
        BlockPos blockPos = blockHitResult.getBlockPos();
        World world = this.getWorld();

        // Get the block state at the hit position
        var blockState = world.getBlockState(blockPos);
        var block = blockState.getBlock();

        // Check if the block is any type of glass or glass pane
        if (block == Blocks.GLASS || block == Blocks.GLASS_PANE ||
                block == Blocks.TINTED_GLASS ||
                block == Blocks.WHITE_STAINED_GLASS || block == Blocks.ORANGE_STAINED_GLASS ||
                block == Blocks.MAGENTA_STAINED_GLASS || block == Blocks.LIGHT_BLUE_STAINED_GLASS ||
                block == Blocks.YELLOW_STAINED_GLASS || block == Blocks.LIME_STAINED_GLASS ||
                block == Blocks.PINK_STAINED_GLASS || block == Blocks.GRAY_STAINED_GLASS ||
                block == Blocks.LIGHT_GRAY_STAINED_GLASS || block == Blocks.CYAN_STAINED_GLASS ||
                block == Blocks.PURPLE_STAINED_GLASS || block == Blocks.BLUE_STAINED_GLASS ||
                block == Blocks.BROWN_STAINED_GLASS || block == Blocks.GREEN_STAINED_GLASS ||
                block == Blocks.RED_STAINED_GLASS || block == Blocks.BLACK_STAINED_GLASS ||
                block == Blocks.WHITE_STAINED_GLASS_PANE || block == Blocks.ORANGE_STAINED_GLASS_PANE ||
                block == Blocks.MAGENTA_STAINED_GLASS_PANE || block == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE ||
                block == Blocks.YELLOW_STAINED_GLASS_PANE || block == Blocks.LIME_STAINED_GLASS_PANE ||
                block == Blocks.PINK_STAINED_GLASS_PANE || block == Blocks.GRAY_STAINED_GLASS_PANE ||
                block == Blocks.LIGHT_GRAY_STAINED_GLASS_PANE || block == Blocks.CYAN_STAINED_GLASS_PANE ||
                block == Blocks.PURPLE_STAINED_GLASS_PANE || block == Blocks.BLUE_STAINED_GLASS_PANE ||
                block == Blocks.BROWN_STAINED_GLASS_PANE || block == Blocks.GREEN_STAINED_GLASS_PANE ||
                block == Blocks.RED_STAINED_GLASS_PANE || block == Blocks.BLACK_STAINED_GLASS_PANE) {

            // Break the glass or glass pane block
            world.breakBlock(blockPos, true);
        }

        // Remove the bullet after hitting a block
        this.discard();
    }

    @Override
    protected float getGravity() {
        return 0.001F;
    }

    @Override
    public void tick() {
        super.tick();

        // Check if the bullet is in water
        if (this.isSubmergedInWater() || this.isInLava()) {
            despawnTimer++;
            if (despawnTimer >= DESPAWN_TIME) {
                this.discard(); // Remove the bullet after 2 seconds
            }
        } else {
            // Reset despawnTimer if the bullet is not in water
            despawnTimer = 0;
        }
    }
}
