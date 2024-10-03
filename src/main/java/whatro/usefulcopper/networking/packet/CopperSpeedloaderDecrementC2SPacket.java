package whatro.usefulcopper.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import whatro.usefulcopper.item.custom.CopperSpeedloaderItem;

public class CopperSpeedloaderDecrementC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            // Decrement the Copper Speedloader stack
            if (removeCopperSpeedloader(player)) {
                // Optionally, send a success message or update the client
            }
        });
    }

    private static boolean removeCopperSpeedloader(ServerPlayerEntity player) {
        // Check offhand first
        ItemStack offhandStack = player.getOffHandStack();
        if (offhandStack.getItem() instanceof CopperSpeedloaderItem) {
            offhandStack.decrement(1);
            return true;
        }

        // Then check the player's inventory
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (stack.getItem() instanceof CopperSpeedloaderItem) {
                stack.decrement(1);
                return true;
            }
        }
        return false; // No speedloader found
    }
}
