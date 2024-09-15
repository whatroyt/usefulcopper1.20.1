package whatro.usefulcopper;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.item.ModItemGroups;
import whatro.usefulcopper.item.ModItems;
import whatro.usefulcopper.item.custom.CopperRevolverItem;
import whatro.usefulcopper.networking.ModMessages;
import whatro.usefulcopper.networking.packet.PacketHandler;
import whatro.usefulcopper.sound.ModSounds;

public class Usefulcopper implements ModInitializer {
	public static final String MOD_ID = "usefulcopper";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final Identifier SHOOT_PACKET_ID = new Identifier(MOD_ID, "shoot");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerPlayNetworking.registerGlobalReceiver(SHOOT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				if (player.getMainHandStack().getItem() instanceof CopperRevolverItem) {
					((CopperRevolverItem) player.getMainHandStack().getItem()).shoot(player.getWorld(), player);
				}
			});
		});

		LOGGER.info("Hello Fabric world!");
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModEntities.registerEntities();
		ModMessages.registerC2SPackets();
		ModSounds.registerSounds();
	}
}