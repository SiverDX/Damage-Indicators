package elocindev.indicators.mixin;

import elocindev.indicators.MmmIndicators;
import elocindev.indicators.config.ClientConfig;
import elocindev.indicators.core.LivingEntityAccess;
import elocindev.indicators.utils.DamageType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccess {
    @Unique
    private float mmmindicators$lastHealth;
    @Unique
    private float mmmindicators$damageTaken;
    @Unique
    private int mmmindicators$damageType;
    @Unique
    private boolean mmmindicators$particleDisplayedThisTick;

    @Override
    public void mmmIndicators$setDamageType(int damageType) {
        this.mmmindicators$damageType = damageType;
    }

    @Override
    public int mmmIndicators$getDamageType() {
        return mmmindicators$damageType;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(final CallbackInfo callback) {
        LivingEntity instance = (LivingEntity) (Object) this;

        if (!instance.getLevel().isClientSide()) {
            return;
        }

        if (mmmindicators$particleDisplayedThisTick) {
            mmmindicators$particleDisplayedThisTick = false;
        }

        float currentHealth = instance.getHealth();

        if (mmmindicators$lastHealth > currentHealth && !mmmindicators$particleDisplayedThisTick) {
            mmmindicators$damageTaken = mmmindicators$lastHealth - currentHealth;
        }

        mmmindicators$lastHealth = currentHealth;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void afterTick(final CallbackInfo callback) {
        LivingEntity instance = (LivingEntity) (Object) this;

        if (!instance.getLevel().isClientSide()) {
            return;
        }

        DamageSource damageSource = instance.getLastDamageSource();
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        if (ClientConfig.ALWAYS_DISPLAY.get() && (damageSource == null || damageSource.getEntity() != localPlayer)) {
            return;
        }

        float width = instance.getBbWidth() / 2;
        Direction direction = localPlayer.getDirection();
        int xDirection = -1 * direction.getStepX();
        int zDirection = -1 * direction.getStepZ();
        double offset = instance.getRandom().nextDouble() * 2 - 1;

        double x = instance.getX() + (width * xDirection) + (xDirection == 0 ? offset : 0);
        double y = instance.getY();
        double z = instance.getZ() + (width * zDirection) + (zDirection == 0 ? offset : 0);

        if (!mmmindicators$particleDisplayedThisTick && mmmindicators$damageTaken > 0) {
            DamageType damageType = DamageType.getType(instance, damageSource);

            /* Mob effect is not synced to the client
            if (damageType == DamageType.MAGIC && mmmindicators$damageTaken == 1 && instance.hasEffect(MobEffects.POISON)) {
                damageType = DamageType.POISON;
            }
            */

            instance.getLevel().addParticle(MmmIndicators.DAMAGE_PARTICLE.get(), x, y, z, mmmindicators$damageTaken, DamageType.getColor(damageType), 0);
            mmmindicators$damageTaken = 0;
            mmmindicators$particleDisplayedThisTick = true;
        }
    }
}