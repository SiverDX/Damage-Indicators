package de.cadentem.damage_indicators.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.damage_indicators.registry.DIParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public record DamageParticleType(float damage, float initialDamage, boolean isCritical, float critMultiplier, int damageType) implements ParticleOptions {
    public static final Codec<DamageParticleType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damage").forGetter(DamageParticleType::damage),
            Codec.FLOAT.fieldOf("initialDamage").forGetter(DamageParticleType::initialDamage),
            Codec.BOOL.fieldOf("isCritical").forGetter(DamageParticleType::isCritical),
            Codec.FLOAT.fieldOf("critMultiplier").forGetter(DamageParticleType::critMultiplier),
            Codec.INT.fieldOf("damageType").forGetter(DamageParticleType::damageType)
    ).apply(instance, DamageParticleType::new));

    @Override
    public @NotNull ParticleType<?> getType() {
        return DIParticles.DAMAGE_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(@NotNull final FriendlyByteBuf buffer) {
        buffer.writeFloat(damage());
        buffer.writeFloat(initialDamage());
        buffer.writeInt(damageType());
    }

    @Override
    public @NotNull String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %b %.2f %d", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), damage(), initialDamage(), isCritical(), critMultiplier(), damageType());
    }

    public static final ParticleOptions.Deserializer<DamageParticleType> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public @NotNull DamageParticleType fromCommand(@NotNull final ParticleType<DamageParticleType> type, @NotNull final StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float damage = reader.readFloat();
            reader.expect(' ');
            float initialDamage = reader.readFloat();
            reader.expect(' ');
            boolean isCritical = reader.readBoolean();
            reader.expect(' ');
            float critMultiplier = reader.readFloat();
            reader.expect(' ');
            int damageType = reader.readInt();
            return new DamageParticleType(damage, initialDamage, isCritical, critMultiplier, damageType);
        }

        public @NotNull DamageParticleType fromNetwork(@NotNull final ParticleType<DamageParticleType> type, @NotNull final FriendlyByteBuf buffer) {
            return new DamageParticleType(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean(), buffer.readFloat(), buffer.readInt());
        }
    };
}
