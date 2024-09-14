package whatro.usefulcopper.networking.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.item.custom.CopperRevolverItem;

public class PacketHandler {
    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "reload_packet"), (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof CopperRevolverItem) {
                    ((CopperRevolverItem) stack.getItem()).reload(stack, player.getWorld(), player);
                }
            });
        });
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "particle_packet"), (client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            client.execute(() -> {
                if (client.world != null) {
                    // Spawn the explosion particle on the client
                    client.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 0.0D, 0.0D, 0.0D);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "smoke_packet"), (client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();

            client.execute(() -> {
                if (client.world != null) {
                    // Spawn the explosion particle on the client
                    client.world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
                }
            });
        });
    }
}
