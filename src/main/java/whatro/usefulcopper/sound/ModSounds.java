package whatro.usefulcopper.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.Usefulcopper;

public class ModSounds {

    public static final SoundEvent COPPER_REVOLVER_GUNSHOT = registerSoundEvent("copperrevolvergunshot");
    public static final SoundEvent COPPER_REVOLVER_RELOAD = registerSoundEvent("copperrevolverreload");
    public static final SoundEvent COPPER_REVOLVER_CLOCK = registerSoundEvent("copperrevolverclock");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(Usefulcopper.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Usefulcopper.LOGGER.info("Registering Sounds for " + Usefulcopper.MOD_ID);
    }
}
