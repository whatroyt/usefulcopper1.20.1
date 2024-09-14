package whatro.usefulcopper;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.item.custom.CopperRevolverItem;
import whatro.usefulcopper.item.custom.CopperSpeedloaderItem;
import whatro.usefulcopper.networking.packet.PacketHandler;

public class UsefulcopperClient implements ClientModInitializer {
    public static KeyBinding reloadKey;
    private static final Identifier SHOOT_PACKET_ID = new Identifier("usefulcopper", "shoot");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.COPPER_PROJECTILE, FlyingItemEntityRenderer::new);

        PacketHandler.registerClient();

        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.usefulcopper.reload",
                GLFW.GLFW_KEY_B,
                "category.usefulcopper"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reloadKey.wasPressed()) {
                PlayerEntity player = client.player;
                if (player != null) {
                    ItemStack stack = player.getMainHandStack();

                    if (stack.getItem() instanceof CopperRevolverItem gun) {
                        if (gun.getAmmo(stack) < CopperRevolverItem.MAX_AMMO) {
                            if (player.isCreative() || hasCopperSpeedloader(player)) {
                                if (!player.isCreative()) {
                                    // Remove one Copper Speedloader
                                    removeCopperSpeedloader(player);
                                }
                                // Reload the gun
                                CopperRevolverItem.sendReloadPacket();
                            }
                        }
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
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
        });
    }

    public static void sendShootPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(SHOOT_PACKET_ID, buf);
    }

    private static boolean hasCopperSpeedloader(PlayerEntity player) {
        // Check offhand first
        ItemStack offhandStack = player.getOffHandStack();
        if (offhandStack.getItem() instanceof CopperSpeedloaderItem) {
            return true;
        }

        // Then check the player's inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof CopperSpeedloaderItem) {
                return true;
            }
        }
        return false;
    }

    private static void removeCopperSpeedloader(PlayerEntity player) {
        // Check offhand first
        ItemStack offhandStack = player.getOffHandStack();
        if (offhandStack.getItem() instanceof CopperSpeedloaderItem) {
            offhandStack.decrement(1);
            return;
        }

        // Then check the player's inventory
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (stack.getItem() instanceof CopperSpeedloaderItem) {
                stack.decrement(1);
                break;
            }
        }
    }

}
