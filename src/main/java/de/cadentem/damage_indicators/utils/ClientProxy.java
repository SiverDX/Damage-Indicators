package de.cadentem.damage_indicators.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class ClientProxy {
    public static @Nullable Player getLocalPlayer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return Minecraft.getInstance().player;
        }

        return null;
    }
}
