package whatro.usefulcopper.mixin;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import whatro.usefulcopper.item.custom.CopperChainsawItem;
import whatro.usefulcopper.item.custom.CopperRevolverItem;

@Mixin(BipedEntityModel.class)
public abstract class PlayerThirdPersonMixin<T extends LivingEntity> {
    @Inject(method = "setAngles", at = @At("TAIL"), cancellable = true)
    private void forceArmRotation(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        if (entity instanceof PlayerEntity player) {
            ItemStack heldItem = player.getMainHandStack(); // Get the item in the main hand

            if (heldItem.getItem() instanceof CopperRevolverItem) {
                BipedEntityModel<?> model = (BipedEntityModel<?>) (Object) this;

                // Force both arms to stay at a 90-degree angle (1.5708 radians)
                model.rightArm.pitch = (float) (-1.5708F + Math.toRadians(headPitch)); //rotates arm up
                model.rightArm.yaw = (float) (Math.toRadians(netHeadYaw));; //rotates arm left

                if (player.isSneaking()) {
                    model.leftArm.pitch = (float) (-1.5708F + Math.toRadians(headPitch)); //rotates arm up

                    //prevent this from being greater than 1.0F
                    float newLeftArmYaw = (float) (1.0F + Math.toRadians(netHeadYaw)); //rotates arm right

                    // Prevent the yaw from exceeding 1.0F
                    if (newLeftArmYaw > 1.1F) {
                        newLeftArmYaw = 1.1F; // Cap the value at 1.0F
                    }
                    model.leftArm.yaw = newLeftArmYaw; // Assign the capped value
                }

            }

            if (heldItem.getItem() instanceof CopperChainsawItem) {
                BipedEntityModel<?> model = (BipedEntityModel<?>) (Object) this;

                float upAngle = (float) Math.toRadians(67.5); // 67.5 degrees up
                float inAngle = (float) Math.toRadians(22.5); // 22.5 degrees inwards

                // Rotate the right arm
                model.rightArm.pitch = (float) (-upAngle + Math.toRadians(headPitch)); // Rotate arm up
                model.rightArm.yaw = (float) (-inAngle + Math.toRadians(netHeadYaw)); // Rotate arm inwards

                // Rotate the left arm
                model.leftArm.pitch = (float) (-upAngle + Math.toRadians(headPitch)); // Rotate arm up
                model.leftArm.yaw = (float) (inAngle + Math.toRadians(netHeadYaw)); // Rotate arm inwards

                if (player.isSneaking()) {
                    // Move arms upwards while sneaking
                    model.leftArm.pitch -= 0.5F;
                    model.rightArm.pitch -= 0.5F;
                }
            }
        }
    }
}
