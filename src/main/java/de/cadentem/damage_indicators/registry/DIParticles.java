package de.cadentem.damage_indicators.registry;

import com.mojang.serialization.Codec;
import de.cadentem.damage_indicators.DamageIndicators;
import de.cadentem.damage_indicators.particle.DamageParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class DIParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DamageIndicators.MODID);
    public static final RegistryObject<ParticleType<DamageParticleType>> DAMAGE_PARTICLE = PARTICLES.register("damage", () -> new ParticleType<>(false, DamageParticleType.DESERIALIZER) {
        @Override
        public @NotNull Codec<DamageParticleType> codec() {
            return DamageParticleType.CODEC;
        }
    });
}
