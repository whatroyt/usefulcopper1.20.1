package whatro.usefulcopper.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import whatro.usefulcopper.item.custom.CopperRevolverItem;

public class CopperRevolverAmmoHud {
    public static void renderHud(DrawContext drawContext, float v) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player != null && player.getMainHandStack().getItem() instanceof CopperRevolverItem) {
            ItemStack gun = player.getMainHandStack();
            int ammo = ((CopperRevolverItem) gun.getItem()).getAmmo(gun);

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            int x = (screenWidth / 2) - 20;
            int y = screenHeight - 70; //34 for creative mode

            // Draw the ammo count on the screen
            client.textRenderer.draw(
                    ammo + "/6 Ammo",                      // text
                    x,                                   // x
                    y,                                   // y
                    0xFFFFFF,                              // color (white)
                    false,                                 // shadow
                    new Matrix4f(), // matrix
                    client.getBufferBuilders().getEntityVertexConsumers(), // vertexConsumers
                    TextRenderer.TextLayerType.NORMAL,      // layerType
                    0,                                     // backgroundColor (no background)
                    15728880                               // light (default white light)
            );
        }
    }
}
