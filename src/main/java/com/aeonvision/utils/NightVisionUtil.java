package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;

public class NightVisionUtil {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean enabled = false;
    private float currentGamma = 0.0f;
    private static final float NIGHT_VISION_GAMMA = 12.0f;
    private static final float SMOOTH_SPEED = 0.08f;
    
    private boolean hadNightVision = false;
    private int originalDuration = 0;

    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        
        float targetGamma = enabled ? NIGHT_VISION_GAMMA : client.options.getGamma().getValue();
        
        if (enabled) {
            // Плавно повышаем гамму
            currentGamma += (NIGHT_VISION_GAMMA - currentGamma) * SMOOTH_SPEED;
            client.options.getGamma().setValue(currentGamma);
            
            // Даём эффект ночного зрения для света (опционально)
            if (!client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                client.player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NIGHT_VISION, 
                    300, // 15 секунд (постоянно обновляется)
                    0, 
                    true, 
                    false,
                    false
                ));
            }
        } else if (currentGamma > 0.1f) {
            // Плавно возвращаем стандартную гамму
            float defaultGamma = 0.0f;
            currentGamma += (defaultGamma - currentGamma) * SMOOTH_SPEED;
            client.options.getGamma().setValue(currentGamma);
            
            // Убираем эффект
            if (client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }

    public void toggle() {
        enabled = !enabled;
        if (!enabled) {
            // Сбрасываем гамму к дефолту
            currentGamma = MC.options.getGamma().getValue();
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
