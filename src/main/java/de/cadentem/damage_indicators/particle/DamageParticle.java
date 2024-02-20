package de.cadentem.damage_indicators.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.cadentem.damage_indicators.registry.DIParticles;
import de.cadentem.damage_indicators.config.ClientConfig;
import de.cadentem.damage_indicators.utils.DamageType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class DamageParticle extends Particle {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final DecimalFormat DF2 = new DecimalFormat("#.##");

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final Component text;
    private final int color;
    private final int darkColor;
    private float fadeout = -1;
    private float prevFadeout = -1;

    private float visualDY = 0;
    private float prevVisualDY = 0;
    private float visualDX = 0;
    private float prevVisualDX = 0;

    public DamageParticle(ClientLevel clientLevel, double x, double y, double z, float damage, float initialDamage, int damageType, boolean isCrit, double critMultiplier) {
        super(clientLevel, x, y + RANDOM.nextDouble() / 2 - 0.25, z);
        this.lifetime = 35;

        this.color = damage < 0 ? 0xff00ff00 : DamageType.getColor(DamageType.get(damageType));
        this.darkColor = FastColor.ARGB32.color(255, (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25f), (int) (this.rCol * 0.25));
        this.yd = 1;

        float difference = initialDamage - damage;
        String differenceText = "";

        if (difference > 0) {
            differenceText = " (" + DF2.format(difference) + " RES)";
        }

        String critText = "";

        if (isCrit) {
            critText = " (" + DF2.format(critMultiplier) + "x)";
        }

        text = Component.literal((damage < 0 ? "+" : "") + DF2.format(damage) + critText + differenceText);
    }

    @Override
    public void render(@NotNull final VertexConsumer consumer, final Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        int light = ClientConfig.LIT_UP_NUMBERS.get() ? LightTexture.FULL_BRIGHT : this.getLightColor(partialTicks);

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);

        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();
        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);

        // animation
        poseStack.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, this.prevVisualDY, this.visualDY), 0);
        // rotate towards camera

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = 0.006f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        // animation
        poseStack.translate((1 + inc) * Mth.lerp(partialTicks, this.prevVisualDX, this.visualDX), 0, 0);
        // scale depending on distance so size remains the same
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;

        fontRenderer.drawInBatch(text, x1, 0, color, false, poseStack.last().pose(), buffer, false, 0, light);
        poseStack.translate(1, 1, +0.03);
        fontRenderer.drawInBatch(text, x1, 0, darkColor, false, poseStack.last().pose(), buffer, false, 0, light);

        buffer.endBatch();
        poseStack.popPose();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float length = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (lifetime - length) ? ((float) lifetime - this.age) / length : 1;

            this.prevVisualDY = this.visualDY;
            this.visualDY += (float) this.yd;
            this.prevVisualDX = this.visualDX;
            this.visualDX += (float) this.xd;

            //spawn numbers in a sort of ellipse centered on his torso
            if (Math.sqrt(Mth.square(this.visualDX * 1.5) + Mth.square(this.visualDY - 1)) < 1.9 - 1) {
                this.yd = this.yd / 2;
            } else {
                this.yd = 0;
                this.xd = 0;
            }
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Factory implements ParticleProvider<DamageParticleType> {
        public Factory(final SpriteSet sprite) { /* Nothing to do */ }

        @SubscribeEvent
        public static void register(final RegisterParticleProvidersEvent event) {
            event.register(DIParticles.DAMAGE_PARTICLE.get(), Factory::new);
        }

        @Override
        public Particle createParticle(@NotNull final DamageParticleType type, @NotNull final ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DamageParticle(level, x, y, z, type.damage(), type.initialDamage(), type.damageType(), type.isCritical(), type.critMultiplier());
        }
    }
}