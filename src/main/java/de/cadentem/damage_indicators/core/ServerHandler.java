package de.cadentem.damage_indicators.core;

import com.mojang.datafixers.util.Pair;
import de.cadentem.damage_indicators.network.NetworkHandler;
import de.cadentem.damage_indicators.network.SyncParticle;
import de.cadentem.damage_indicators.utils.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class ServerHandler {
    private static final ThreadLocal<Float> initialDamage = new ThreadLocal<>();
    private static final ThreadLocal<Pair<Boolean, Float>> isCritical = new ThreadLocal<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void syncDamageType(final LivingAttackEvent event) {
        initialDamage.set(event.getAmount());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void checkCritical(final CriticalHitEvent event) {
        isCritical.set(Pair.of(event.getResult() == Event.Result.ALLOW || event.getResult() == Event.Result.DEFAULT && event.isVanillaCritical(), event.getDamageModifier()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void displayDamage(final LivingDamageEvent event) {
        if (event.isCanceled() || event.getEntity().getLevel().isClientSide()) {
            return;
        }

        LivingEntity target = event.getEntity();
        Pair<Boolean, Float> criticalData = isCritical.get();

        boolean isCrit = criticalData != null ? criticalData.getFirst() : false;
        float critMultiplier = criticalData != null ? criticalData.getSecond() : 0;
        float initial = initialDamage.get() != null ? initialDamage.get() : 0;

        NetworkHandler.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> PacketDistributor.TargetPoint.p(target.getX(), target.getY(), target.getZ(), 36, target.getLevel().dimension()).get()),
                new SyncParticle(event.getAmount(), initial, isCrit, critMultiplier, DamageType.getType(event.getSource()).ordinal(), target.getX(), target.getY(), target.getZ(), target.getBbWidth() / 2)
        );

        isCritical.remove();
        initialDamage.remove();
    }
}
