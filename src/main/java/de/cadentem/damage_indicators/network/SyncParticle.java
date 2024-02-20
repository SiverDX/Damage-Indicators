package de.cadentem.damage_indicators.network;

import de.cadentem.damage_indicators.particle.DamageParticleType;
import de.cadentem.damage_indicators.utils.ClientProxy;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncParticle(float damage, float initialDamage, boolean isCritical, float critMultiplier, int color, double x, double y, double z, double width) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeFloat(damage);
        buffer.writeFloat(initialDamage);
        buffer.writeBoolean(isCritical);
        buffer.writeFloat(critMultiplier);
        buffer.writeInt(color);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeDouble(width);
    }

    public static SyncParticle decode(final FriendlyByteBuf buffer) {
        return new SyncParticle(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readFloat(),
                buffer.readInt(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }

    public static void handle(final SyncParticle message, final Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                Player localPlayer = ClientProxy.getLocalPlayer();

                if (localPlayer == null) {
                    return;
                }

                DamageParticleType damageParticle = new DamageParticleType(message.damage(), message.initialDamage(), message.isCritical(), message.critMultiplier(), message.color());

                Direction direction = localPlayer.getDirection();
                int xDirection = -1 * direction.getStepX();
                int zDirection = -1 * direction.getStepZ();
                double offset = (localPlayer.getRandom().nextDouble() * 2 - 1) * 0.7;

                double x = message.x + (message.width * xDirection) + (xDirection == 0 ? offset : 0);
                double z = message.z + (message.width * zDirection) + (zDirection == 0 ? offset : 0);

                localPlayer.getLevel().addParticle(damageParticle, x, message.y, z, 0, 0, 0);
            });
        }

        context.setPacketHandled(true);
    }
}
