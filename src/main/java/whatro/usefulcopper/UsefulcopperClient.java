package whatro.usefulcopper;

import net.fabricmc.api.ClientModInitializer;
import whatro.usefulcopper.client.CopperRevolverAmmoHud;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.event.KeyInputHandler;
import whatro.usefulcopper.networking.ModMessages;

public class UsefulcopperClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntities.registerRenderers();
        ModEntities.registerModelLayers();
        KeyInputHandler.register();
        ModMessages.registerS2CPackets();
        CopperRevolverAmmoHud.register();
    }
}
