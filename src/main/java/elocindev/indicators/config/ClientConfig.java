package elocindev.indicators.config;

import net.minecraft.ChatFormatting;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> THRESHOLD_COLORS;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_NUMBER_COLOR;
    public static final ForgeConfigSpec.BooleanValue ALWAYS_DISPLAY;
    public static final ForgeConfigSpec.BooleanValue LIT_UP_NUMBERS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_THRESHOLD_NUMBERING;

    public record NumberThreshold(int threshold, int numberColor) {
        @Override
        public String toString() {
            return threshold + "," + numberColor;
        }

        public static NumberThreshold fromString(final String string) {
            String[] data = string.split(",");
            return new NumberThreshold(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
        }
    }

    static {
        THRESHOLD_COLORS = BUILDER
                .comment("The configured threshold colors (damage amount and color), only relevant if their usage is enabled")
                .define("threshold_colors", List.of(
                        new NumberThreshold(200, 0xfc4103).toString(),
                        new NumberThreshold(100, 0xfcba03).toString()
                ), ClientConfig::validator);
        ALWAYS_DISPLAY = BUILDER.comment("If true, the numbers will appear when any entity takes damage, regardless if the cause is the player or not").define("always_display", false);
        LIT_UP_NUMBERS = BUILDER.comment("Whether or not to make the numbers light up. Works with shaders").define("lit_up_numbers", false);
        ENABLE_THRESHOLD_NUMBERING = BUILDER.comment("Changes the color of the number according to the value (e.g. \"amount = 100, color = 0xfcba03\" will turn the number into the color 0xfcba03 when the amount is 100 or higher)").define("enable_threshold_numbering", false);
        DEFAULT_NUMBER_COLOR = BUILDER.comment("The color of the numbers").define("default_number_color", 0xFFFFFF);

        SPEC = BUILDER.build();
    }

    private static int getColorForDamageAmount(float damageAmount) {
        int color = DEFAULT_NUMBER_COLOR.get();

        if (ENABLE_THRESHOLD_NUMBERING.get())
            for (String raw : THRESHOLD_COLORS.get()) {
                NumberThreshold threshold = NumberThreshold.fromString(raw);

                if (damageAmount >= threshold.threshold()) {
                    color = threshold.numberColor();
                }
            }

        return color;
    }

    public static boolean validator(final Object entry) {
        if (entry == null) {
            return false;
        }

        if (entry instanceof List<?> list) {
            for (Object listEntry : list) {
                boolean isValid = checkEntry(listEntry);

                if (!isValid) {
                    return false;
                }
            }

            return true;
        }

        return checkEntry(entry);
    }

    private static boolean checkEntry(final Object entry) {
        if (!(entry instanceof String string)) {
            return false;
        }

        String[] data = string.split(",");

        if (data.length == 2) {
            try {
                Integer.parseInt(data[0]);
                Integer.parseInt(data[1]);
                return true;
            } catch (NumberFormatException ignored) { /* Nothing to do */ }
        }

        return false;
    }
}
