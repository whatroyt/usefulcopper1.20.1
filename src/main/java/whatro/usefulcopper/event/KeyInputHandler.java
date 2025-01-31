package whatro.usefulcopper.event;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import whatro.usefulcopper.item.custom.CopperRevolverItem;
import whatro.usefulcopper.item.custom.CopperSpeedloaderItem;

public class KeyInputHandler {
    public static KeyBinding reloadKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (reloadKey.wasPressed()) {
                PlayerEntity player = client.player;
                if (player != null) {
                    ItemStack stack = player.getMainHandStack();

                    if (stack.getItem() instanceof CopperRevolverItem gun) {
                        if (gun.getAmmo(stack) < CopperRevolverItem.MAX_AMMO) {
                            if (player.isCreative() || hasCopperSpeedloader(player)) {
                                if (!player.isCreative()) {
                                    // Send the decrement packet to the server
                                    sendDecrementSpeedloaderPacket();
                                }
                                // Reload the gun
                                CopperRevolverItem.sendReloadPacket();
                            }
                        }
                    }
                }
            }
        });
    }

    private static void sendDecrementSpeedloaderPacket() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(new Identifier("usefulcopper", "decrement_speedloader_packet"), buffer);
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
                return;
            }
        }
    }

    public static void register() {
        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.usefulcopper.reload",
                GLFW.GLFW_KEY_B,
                "category.usefulcopper"
        ));

        registerKeyInputs();
    }
}
