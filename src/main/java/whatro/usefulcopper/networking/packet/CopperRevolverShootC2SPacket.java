package whatro.usefulcopper.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import whatro.usefulcopper.item.custom.CopperRevolverItem;

public class CopperRevolverShootC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            if (player.getMainHandStack().getItem() instanceof CopperRevolverItem) {
                ((CopperRevolverItem) player.getMainHandStack().getItem()).shoot(player.getWorld(), player);
            }
        });
    }
}
