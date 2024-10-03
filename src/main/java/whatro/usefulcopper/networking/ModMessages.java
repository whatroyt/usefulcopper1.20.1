package whatro.usefulcopper.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.networking.packet.*;

public class ModMessages {
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "reload_packet"), CopperRevolverReloadC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "shoot"), CopperRevolverShootC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "decrement_speedloader_packet"), CopperSpeedloaderDecrementC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "particle_packet"), CopperMaceExplosionS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("usefulcopper", "smoke_packet"), CopperRevolverSmokeS2CPacket::receive);
    }
}
