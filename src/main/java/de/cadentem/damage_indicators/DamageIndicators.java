package de.cadentem.damage_indicators;

import de.cadentem.damage_indicators.registry.DIParticles;
import de.cadentem.damage_indicators.config.ClientConfig;
import de.cadentem.damage_indicators.network.NetworkHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DamageIndicators.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DamageIndicators {
    public static final String MODID = "damage_indicators";

    public DamageIndicators() {
        DIParticles.PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    public static void handleCommon(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }
}