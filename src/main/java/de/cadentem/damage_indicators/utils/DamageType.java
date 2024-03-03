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
    WITHER,
    BLOOD,
    HOLY,
    ENDER,
    ELDRITCH,
    ICE;

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

        String type = damageSource.getMsgId();

        if (type.contains("poison") || type.contains("nature")) {
            return POISON;
        } else if (type.contains("blood")) {
            return BLOOD;
        } else if (type.contains("holy")) {
            return HOLY;
        } else if (type.contains("ender")) {
            return ENDER;
        } else if (type.contains("eldritch")) {
            return ELDRITCH;
        } else if (type.contains("ice") || type.contains("frost")) {
            return ICE;
        } else if (damageSource == DamageSource.WITHER || type.contains("wither")) {
            return WITHER;
        } else if (damageSource == DamageSource.LIGHTNING_BOLT || type.contains("lightning")) {
            return LIGHTNING;
        } else if (damageSource.isFire() || type.contains("fire")) {
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
            case WITHER -> 0x1e1e1e;
            case BLOOD -> ChatFormatting.DARK_RED.getColor();
            case HOLY -> /* Iron's Spells 'n Spellbooks (Holy) */ 0xfff8d4;
            case ENDER -> ChatFormatting.DARK_PURPLE.getColor();
            case ELDRITCH -> /* Iron's Spells 'n Spellbooks (Eldritch) */ 0x0f839c;
            case ICE -> /* Iron's Spells 'n Spellbooks (Ice) */ 0xd0f9ff;
        };
    }
}