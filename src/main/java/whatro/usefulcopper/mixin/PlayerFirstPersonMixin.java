package whatro.usefulcopper.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import whatro.usefulcopper.item.custom.CopperRevolverItem;

@Mixin(HeldItemRenderer.class)
public abstract class PlayerFirstPersonMixin {

    @Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"), cancellable = true)
    public void modifyArmPlacement(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                                   float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                                   VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Check if the player is holding a CopperMachineGunItem in the main hand
        ItemStack mainHandItem = player.getMainHandStack();
        boolean isHoldingCopperRevolver = mainHandItem.getItem() instanceof CopperRevolverItem;

        // Apply transformations for the offhand hand
        if (hand == Hand.OFF_HAND && isHoldingCopperRevolver) {
            matrices.translate(0.05F, -0.25F, -0.1F);
            //ci.cancel();
            return;
        }

        // Apply transformations for the main hand
        boolean isMainHand = hand == Hand.MAIN_HAND;
        if (isHoldingCopperRevolver && isMainHand) {
            if (player.isInSneakingPose()) {
                matrices.translate(-0.56F, 0.225F, 0.2F);
            } else {
                matrices.translate(-0.05F, 0.25F, 0.1F);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(9.0F));
            }
        }
    }
}
