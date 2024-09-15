package whatro.usefulcopper.item.custom;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import whatro.usefulcopper.entity.ModEntities;
import whatro.usefulcopper.entity.custom.CopperBulletProjectileEntity;
import whatro.usefulcopper.sound.ModSounds;

public class CopperRevolverItem extends Item {

    public static final int MAX_AMMO = 6;
    private static final long COOLDOWN_TIME_MS = 500; // 0.5 seconds in milliseconds
    private static final String LAST_SHOOT_KEY = "LastShootTime";
    private static final float COPPER_REVOLVER_CLOCK_VOLUME = 0.9f;

    // Add a method to get the last shoot time from the NBT data
    private long getLastShootTime(ItemStack stack) {
        return stack.getOrCreateNbt().getLong(LAST_SHOOT_KEY);
    }

    // Add a method to set the last shoot time in the NBT data
    private void setLastShootTime(ItemStack stack, long time) {
        stack.getOrCreateNbt().putLong(LAST_SHOOT_KEY, time);
    }

    public CopperRevolverItem(Settings settings) {
        super(settings);
    }

    // Gets the current ammo count from the item's NBT data
    public int getAmmo(ItemStack stack) {
        int ammo = stack.getOrCreateNbt().getInt("Ammo");
        if (!stack.getOrCreateNbt().contains("Ammo")) {
            setAmmo(stack, 0); // Set the ammo to max on a new item
        }
        return ammo;
    }

    // Sets the ammo count in the item's NBT data
    private void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateNbt().putInt("Ammo", ammo);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            return TypedActionResult.pass(stack);
        }

        long currentTime = System.currentTimeMillis();
        long lastShootTime = getLastShootTime(stack);

        // Check if the cooldown has elapsed
        if (currentTime - lastShootTime < COOLDOWN_TIME_MS) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        // Update the last shoot time
        setLastShootTime(stack, currentTime);

        int ammo = getAmmo(stack);

        if (ammo <= 0) {
            if (!world.isClient) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        ModSounds.COPPER_REVOLVER_CLOCK, SoundCategory.PLAYERS, COPPER_REVOLVER_CLOCK_VOLUME, 1.0F);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        if (world.isClient) {
            CopperRevolverItem.sendShootPacket();
        }
        if (!world.isClient) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    ModSounds.COPPER_REVOLVER_GUNSHOT, SoundCategory.PLAYERS, 0.4F, 1.0F);

            Vec3d direction = user.getRotationVec(1.0F);

            double offset = 0.5;
            double bulletX = user.getX() + direction.x * offset;
            double bulletY = user.getEyeY() - 0.1 + direction.y * offset;
            double bulletZ = user.getZ() + direction.z * offset;
            Vec3d particlePos = new Vec3d(bulletX, bulletY, bulletZ).add(direction.multiply(0.5));
            sendParticlePacketToClient(particlePos, world);
        }
        setAmmo(stack, ammo - 1);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public void reload(ItemStack stack, World world, PlayerEntity player) {
        setAmmo(stack, MAX_AMMO);
        if (!world.isClient) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.COPPER_REVOLVER_RELOAD, SoundCategory.PLAYERS, 0.8F, 1.0F);
        }
    }

    public void shoot(World world, PlayerEntity player) {
        CopperBulletProjectileEntity bullet = new CopperBulletProjectileEntity(ModEntities.COPPER_PROJECTILE, world);
        Vec3d direction = player.getRotationVec(1.0F);

        double offset = 0.5;
        double bulletX = player.getX() + direction.x * offset;
        double bulletY = player.getEyeY() - 0.1 + direction.y * offset;
        double bulletZ = player.getZ() + direction.z * offset;

        bullet.setPosition(bulletX, bulletY, bulletZ);
        bullet.setVelocity(direction.x, direction.y, direction.z, 4.0F, 1.0F); // Adjust velocity as needed
        bullet.setOwner(player);

        world.spawnEntity(bullet);
    }

    public static void sendReloadPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(new Identifier("usefulcopper", "reload_packet"), buf);
    }

    private void sendParticlePacketToClient(Vec3d position, World world) {
        if (world instanceof ServerWorld serverWorld) {
            // Send a custom packet to the client to handle particle rendering
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeDouble(position.x);
            buffer.writeDouble(position.y);
            buffer.writeDouble(position.z);

            // Use a custom identifier for the particle packet
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                ServerPlayNetworking.send(player, new Identifier("usefulcopper", "smoke_packet"), buffer);
            }
        }
    }

    public static void sendShootPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(new Identifier("usefulcopper", "shoot"), buf);
    }

}
