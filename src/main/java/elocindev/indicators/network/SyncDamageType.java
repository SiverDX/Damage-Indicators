package elocindev.indicators.network;

import elocindev.indicators.core.LivingEntityAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record SyncDamageType(int entityId, int damageType) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(damageType);
    }

    public static SyncDamageType decode(final FriendlyByteBuf buffer) {
        return new SyncDamageType(buffer.readInt(), buffer.readInt());
    }

    public static void handle(final SyncDamageType message, final Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                Player localPlayer = getLocalPlayer();

                if (localPlayer == null) {
                    return;
                }

                if (localPlayer.getLevel().getEntity(message.entityId) instanceof LivingEntityAccess access) {
                    access.mmmIndicators$setDamageType(message.damageType);
                }
            });
        }

        context.setPacketHandled(true);
    }

    private static @Nullable Player getLocalPlayer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return Minecraft.getInstance().player;
        }

        return null;
    }
}
