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
import whatro.usefulcopper.client.CopperRevolverAmmoHud;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.event.KeyInputHandler;
import whatro.usefulcopper.item.custom.CopperRevolverItem;
import whatro.usefulcopper.item.custom.CopperSpeedloaderItem;
import whatro.usefulcopper.networking.ModMessages;

public class UsefulcopperClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.COPPER_PROJECTILE, FlyingItemEntityRenderer::new);
        KeyInputHandler.register();
        ModMessages.registerS2CPackets();
        HudRenderCallback.EVENT.register(CopperRevolverAmmoHud::renderHud);
    }
}
