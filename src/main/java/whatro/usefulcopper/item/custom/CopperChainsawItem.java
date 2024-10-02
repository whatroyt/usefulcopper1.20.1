package whatro.usefulcopper.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.RenderUtils;
import whatro.usefulcopper.item.client.CopperChainsawItemRenderer;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.AnimationState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CopperChainsawItem extends AxeItem implements GeoItem {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public CopperChainsawItem(Settings settings) {
        super(ToolMaterials.NETHERITE, 1.0F, 16.0F, settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final CopperChainsawItemRenderer renderer = new CopperChainsawItemRenderer();

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.copperchainsaw.on", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        // Only execute the following on the server side
        if (!world.isClient) {
            // Attempt to break a block in front of the player
            performAttack(player, world);
            player.sendMessage(Text.of("Block broken!"), true); // Feedback message
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    private void performAttack(PlayerEntity player, World world) {
        // Define the raycasting parameters
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
                }
            }
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
}
