package whatro.usefulcopper.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.entity.custom.BlobEntity;
import whatro.usefulcopper.item.client.CopperChainsawItemRenderer;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.AnimationState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import whatro.usefulcopper.sound.ModSounds;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CopperChainsawItem extends AxeItem implements GeoItem {
    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("animation.copperchainsaw.on");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    private static final float DAMAGE = 10.0F;
    private static final int AMOUNT_OF_BLOBS_LEFT_CLICK = 5;
    private static final int AMOUNT_OF_BLOBS_RIGHT_CLICK = 1;
    private static final double DISTANCE_FACTOR = 0.75; // Strength of blob velocity
    private final Random random = new Random();
    private int timer;
    private int impactSoundTimer = 0; // Cooldown timer for impact sound
    private static final int IMPACT_SOUND_COOLDOWN = 10; // Cooldown duration (0.5 seconds = 10 ticks)
    private int durabilityCooldown = 0;
    private static final int DURABILITY_COOLDOWN_DURATION = 10;

    private static final SoundEvent CHAINSAW_IDLE = ModSounds.CHAINSAW_IDLE;
    private static final SoundEvent CHAIN = SoundEvents.BLOCK_CHAIN_PLACE;
    private static final SoundEvent CHAINSAW_IMPACT = ModSounds.CHAINSAW_IMPACT;

    public CopperChainsawItem(Settings settings) {
        super(ToolMaterials.NETHERITE, 1.7F, 16.0F, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private CopperChainsawItemRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new CopperChainsawItemRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activate", 0, state -> PlayState.STOP)
                .triggerableAnim("Activate", ACTIVATE_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private boolean isActive(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("Active");
    }

    private void setActive(ItemStack stack, boolean active) {
        stack.getOrCreateNbt().putBoolean("Active", active);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!world.isClient) {
            boolean currentActiveState = isActive(itemStack);
            setActive(itemStack, !currentActiveState);
            String message = !currentActiveState ? "Chainsaw turned on!" : "Chainsaw turned off!";
            world.playSound(null, player.getBlockPos(), CHAIN, SoundCategory.PLAYERS, 1.0F, 1.0F);
            player.sendMessage(Text.literal(message), true);
        }

        return super.use(world, player, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user instanceof PlayerEntity player && isActive(stack)) {
            world.playSound(null, player.getBlockPos(), CHAINSAW_IDLE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            setActive(stack, false);
        }
    }

    private void performAttack(PlayerEntity player, World world) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        triggerAnim(player, GeoItem.getOrAssignId(itemStack, (ServerWorld) world), "Activate", "Activate");
        Vec3d playerPos = player.getCameraPosVec(1.0F); // Get player's position
        Vec3d playerLook = player.getRotationVec(1.0F); // Get the player's look direction

        // Define the max distance for raycasting (3 blocks)
        double maxDistance = 3.0;

        // Create a raycast
        for (int i = 1; i <= 3; i++) {
            // Calculate the target position for each step
            Vec3d targetPos = playerPos.add(playerLook.multiply(i)); // Extend the ray in the direction of the player's look

            // Get the block position at the target
            BlockPos blockPos = new BlockPos(MathHelper.floor(targetPos.x), MathHelper.floor(targetPos.y), MathHelper.floor(targetPos.z));
            BlockState blockState = world.getBlockState(blockPos);

            // Check if the block can be broken
            if (!blockState.isAir() && blockState.getHardness(world, blockPos) != -1) {
                if (blockState.isIn(BlockTags.LOGS) || blockState.isIn(BlockTags.LEAVES)) {
                    // Break the block
                    world.breakBlock(blockPos, true, player);
                    // Decrease durability of the chainsaw
                     // Get the item in the player's main hand
                    itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(Hand.MAIN_HAND)); // Damage the item by 1
                }
            }

            // Check for entities within a certain range
            world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().stretch(playerLook.multiply(maxDistance)), entity -> entity != player).forEach(entity -> {
                if (entity instanceof BlobEntity) {
                    return; // Skip further processing for blobs
                }

                entity.damage(entity.getDamageSources().generic(), DAMAGE); // Adjust damage as needed

                // Check if we can play the impact sound
                if (impactSoundTimer <= 0) {
                    world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            CHAINSAW_IMPACT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    impactSoundTimer = IMPACT_SOUND_COOLDOWN; // Reset the cooldown timer
                }

                // Spawn blobs from the target entity's position towards the player
                spawnBlobs(world, entity, player, false); // Pass both the target entity and the player

                // Check if the durability cooldown has expired
                if (durabilityCooldown <= 0) {
                    // Decrease durability of the chainsaw
                    itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(Hand.MAIN_HAND)); // Damage the item by 1
                    durabilityCooldown = DURABILITY_COOLDOWN_DURATION; // Reset the cooldown timer
                }
            });
        }

        // Decrease the cooldown timer at the end of each performAttack call
        if (durabilityCooldown > 0) {
            durabilityCooldown--; // Decrease the cooldown timer
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());

        // Check if the block is a log (or part of the log tag)
        if (blockState.isIn(BlockTags.LOGS)) {
            return ActionResult.PASS; // Prevent stripping
        }

        // Call the default behavior for other block types
        return super.useOnBlock(context);
    }

    private void spawnBlobs(World world, LivingEntity targetEntity, PlayerEntity player, boolean isLeftClick) {
        int amountOfBlobs = isLeftClick ? AMOUNT_OF_BLOBS_LEFT_CLICK : AMOUNT_OF_BLOBS_RIGHT_CLICK;

        // Get the center position of the target entity
        Vec3d centerPos = new Vec3d(
                targetEntity.getX(), // X coordinate
                targetEntity.getY() + (targetEntity.getHeight() / 2), // Y coordinate (center)
                targetEntity.getZ()  // Z coordinate
        );

        Vec3d playerPos = player.getCameraPosVec(1.0F); // Get the player's position

        // Calculate the direction from the center of the entity to the player
        Vec3d directionToPlayer = playerPos.subtract(centerPos).normalize();

        // Spawn blobs at the center position of the target entity
        for (int i = 0; i < amountOfBlobs; i++) {
            BlobEntity blobEntity = new BlobEntity(ModEntities.BLOB, world);
            blobEntity.setPosition(centerPos.x, centerPos.y, centerPos.z); // Use center position for spawning

            // Set the blob's velocity towards the player
            double randomX = directionToPlayer.x + (random.nextDouble() * 0.5 - 0.25); // Add slight randomness to x
            double randomZ = directionToPlayer.z + (random.nextDouble() * 0.5 - 0.25); // Add slight randomness to z

            blobEntity.setVelocity(randomX * DISTANCE_FACTOR, 0.5, randomZ * DISTANCE_FACTOR);

            // Set a random yaw for the blob (facing direction)
            blobEntity.setYaw(random.nextFloat() * 360); // Random yaw between 0 and 360 degrees
            blobEntity.prevYaw = blobEntity.getYaw(); // Update the previous yaw to match

            // Spawn the blob in the world
            world.spawnEntity(blobEntity);
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Only execute the following on the server side
        if (!attacker.getWorld().isClient) {
            // Damage the target entity
            target.damage(target.getDamageSources().generic(), DAMAGE);

            // Play the impact sound regardless of the isActive state
            attacker.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(),
                    CHAINSAW_IMPACT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Spawn blobs from the target entity's position towards the attacker
            spawnBlobs(attacker.getWorld(), target, (PlayerEntity) attacker, true); // Cast attacker to PlayerEntity
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // Check if the entity is a ServerPlayerEntity and if the item in their main hand is the CopperChainsawItem
        if (entity instanceof ServerPlayerEntity player && slot == player.getInventory().selectedSlot && stack.getItem() instanceof CopperChainsawItem) {
            if (impactSoundTimer > 0) {
                impactSoundTimer--; // Decrease the cooldown timer
            }

            // Check if the chainsaw is active from NBT
            boolean active = isActive(stack);

            if (active) {
                if (timer < 29) {
                    timer++;
                } else {
                    if (!world.isClient) {
                        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.CHAINSAW_IDLE, SoundCategory.PLAYERS, 0.8F, 1.0F);
                        timer = 0;
                    }
                }
                performAttack(player, world);
            } else {
                if (!world.isClient) {
                    player.networkHandler.sendPacket(new StopSoundS2CPacket(ModSounds.CHAINSAW_IDLE.getId(), SoundCategory.PLAYERS));
                }
                timer = 29; // Reset timer to prevent sound from playing again immediately
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

}
