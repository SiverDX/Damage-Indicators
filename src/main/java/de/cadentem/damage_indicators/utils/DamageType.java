package de.cadentem.damage_indicators.utils;

import de.cadentem.damage_indicators.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public enum DamageType {
    NONE,
    FIRE,
    MAGIC,
    LIGHTNING,
    POISON,
    WITHER;

    public static DamageType get(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        return values()[ordinal];
    }

    public static DamageType getType(final DamageSource damageSource) {
        if (damageSource == null) {
            return NONE;
        }

        if (damageSource.getMsgId().contains("poison")) {
            return POISON;
        } else if (damageSource == DamageSource.WITHER || damageSource.getMsgId().contains("wither")) {
            return WITHER;
        } else if (damageSource == DamageSource.LIGHTNING_BOLT || damageSource.getMsgId().contains("lightning")) {
            return LIGHTNING;
        } else if (damageSource.isFire()) {
            return FIRE;
        } else if (damageSource.isMagic()) {
            return MAGIC;
        }

        return NONE;
    }

    public static int getColor(final DamageSource damageSource) {
        return getColor(getType(damageSource));
    }

    public static int getColor(final DamageType damageType) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            return -1;
        }

        return switch (damageType) {
            case NONE -> ClientConfig.DEFAULT_NUMBER_COLOR.get();
            case FIRE -> ChatFormatting.GOLD.getColor();
            case MAGIC -> ChatFormatting.BLUE.getColor();
            case LIGHTNING -> ChatFormatting.AQUA.getColor();
            case POISON -> ChatFormatting.DARK_GREEN.getColor();
            case WITHER -> ChatFormatting.DARK_PURPLE.getColor();
        };
    }
}