package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class NightVisionUtil {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private boolean enabled = false;
    private double currentGamma = 0.0;
    private static final double NIGHT_VISION_GAMMA = 12.0;
    private static final double SMOOTH_SPEED = 0.08;

    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        
        if (enabled) {
            currentGamma += (NIGHT_VISION_GAMMA - currentGamma) * SMOOTH_SPEED;
            client.options.getGamma().setValue(currentGamma);
            if (!client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                client.player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 300, 0, true, false, false));
            }
        } else if (currentGamma > 0.1) {
            currentGamma += (0.0 - currentGamma) * SMOOTH_SPEED;
            client.options.getGamma().setValue(currentGamma);
            if (client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }

    public void toggle() { enabled = !enabled; }
    public boolean isEnabled() { return enabled; }
}
