package whatro.usefulcopper.item.custom;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CopperMaceItem extends SwordItem {

    private static final float NORMAL_DAMAGE = 6.0F;
    private static final float AOE_DAMAGE = 16.0F;
    private static final float VELOCITY_DAMAGE = 16.0F;

    public CopperMaceItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = target.getWorld();
        float damage = getAttackDamage();

        if (!world.isClient) {
            // Check if the attacker is a player and is falling
            if (attacker instanceof PlayerEntity player && player.getVelocity().y < -0.4) {
                damage = VELOCITY_DAMAGE;

                // Delete blocks in a 3x3 radius around the target (on the server)
                deleteBlocksAroundTarget(world, target, player);

                // Send a packet to the client to spawn explosion particles
                sendParticlePacketToClient(target.getPos(), world);

                // Play anvil sound when hitting the target while falling (on the server)
                world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Play anvil sound when hitting the target while falling (on the server)
                world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Damage the item (on the server)
                stack.damage(1, attacker, (entity) -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
        target.damage(target.getDamageSources().generic(), damage);

        return super.postHit(stack, target, attacker);
    }

    private void deleteBlocksAroundTarget(World world, LivingEntity target, PlayerEntity player) {
        BlockPos targetPos = target.getBlockPos();
        int radius = 3; // 3 block radius for a 3x3 area

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = targetPos.add(x, y, z);

                    // Skip air blocks and bedrock
                    if (world.getBlockState(pos).isAir() || world.getBlockState(pos).isOf(Blocks.BEDROCK)) {
                        continue;
                    }

                    // Only delete blocks within the spherical radius
                    if (targetPos.getSquaredDistance(pos) <= radius * radius) {
                        world.breakBlock(pos, true);
                    }
                }
            }
        }
        // Damage entities within the radius around the target
        damageEntitiesAroundTarget(world, target, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            if (selected && player.getMainHandStack().getItem() instanceof CopperMaceItem) {
                // Apply Jump Boost 5 effect (level 4 because potion levels start from 0)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 10, 4, true, false, true));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void sendParticlePacketToClient(Vec3d position, World world) {
        if (world instanceof ServerWorld serverWorld) {
            // Send a custom packet to the client to handle particle rendering
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeDouble(position.x);
            buffer.writeDouble(position.y);
            buffer.writeDouble(position.z);

            // Use a custom identifier for the particle packet
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                ServerPlayNetworking.send(player, new Identifier("usefulcopper", "particle_packet"), buffer);
            }
        }
    }

    private void damageEntitiesAroundTarget(World world, LivingEntity target, PlayerEntity player) {
        BlockPos targetPos = target.getBlockPos();
        int radius = 3; // 3 block radius

        for (Entity entity : world.getEntitiesByClass(Entity.class, target.getBoundingBox().expand(radius), e -> e instanceof LivingEntity && e != player)) {
            if (entity.squaredDistanceTo(target) <= radius * radius) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.damage(livingEntity.getDamageSources().generic(), AOE_DAMAGE);
                }
            }
        }
    }

    @Override
    public float getAttackDamage() {
        return NORMAL_DAMAGE;
    }

}
