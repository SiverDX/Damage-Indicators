package de.cadentem.damage_indicators.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_NUMBER_COLOR;
    public static final ForgeConfigSpec.BooleanValue ALWAYS_DISPLAY; // TODO :: unused
    public static final ForgeConfigSpec.BooleanValue LIT_UP_NUMBERS;

    static {
        ALWAYS_DISPLAY = BUILDER.comment("If true, the numbers will appear when any entity takes damage, regardless if the cause is the player or not").define("always_display", false);
        LIT_UP_NUMBERS = BUILDER.comment("Whether or not to make the numbers light up. Works with shaders").define("lit_up_numbers", true);
        DEFAULT_NUMBER_COLOR = BUILDER.comment("The damageType of the numbers").define("default_number_color", 0xFFFFFF);

        SPEC = BUILDER.build();
    }
}
