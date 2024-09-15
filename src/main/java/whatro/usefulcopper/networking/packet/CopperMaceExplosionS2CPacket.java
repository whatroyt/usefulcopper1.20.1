package whatro.usefulcopper.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;

public class CopperMaceExplosionS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        client.execute(() -> {
            if (client.world != null) {
                client.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        });
    }
}
