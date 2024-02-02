package elocindev.indicators.core;

import elocindev.indicators.network.NetworkHandler;
import elocindev.indicators.network.SyncDamageType;
import elocindev.indicators.utils.DamageType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class ServerHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void syncDamageType(final LivingAttackEvent event) {
        if (event.isCanceled()) {
            return;
        }

        DamageSource damageSource = event.getSource();
        LivingEntity target = event.getEntity();

        if (target.getLevel().isClientSide()) {
            return;
        }

        // TODO :: need to clear it (clients-side on `hurt`?) if restricting to player (could also skip sending `NONE` types)
        if (target instanceof LivingEntity /*&& damageSource.getEntity() instanceof Player*/) {
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.NEAR.with(() -> PacketDistributor.TargetPoint.p(target.getX(), target.getY(), target.getZ(), 36, target.getLevel().dimension()).get()),
                    new SyncDamageType(target.getId(), DamageType.getType(damageSource).ordinal()
                    ));
        }
    }
}
