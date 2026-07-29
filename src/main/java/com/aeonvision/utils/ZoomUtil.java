package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class ZoomUtil {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean enabled = false;
    private boolean keyPressed = false;
    
    private double currentFovMultiplier = 1.0;
    private static final double ZOOM_MULTIPLIER = 0.23; // Сила зума (меньше = сильнее)
    private static final double SMOOTH_SPEED_IN = 0.12;
    private static final double SMOOTH_SPEED_OUT = 0.08;
    
    private double originalFov = 70;
    private boolean wasSmoothCamera = false;

    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        
        // Плавная интерполяция
        if (keyPressed && !enabled) {
            // Активируем зум
            enabled = true;
            originalFov = client.options.getFov().getValue();
            wasSmoothCamera = client.options.smoothCameraEnabled;
            client.options.smoothCameraEnabled = true;
        } else if (!keyPressed && enabled) {
            // Деактивируем зум
            enabled = false;
            client.options.smoothCameraEnabled = wasSmoothCamera;
        }
        
        // Плавное изменение FOV
        double target = enabled ? ZOOM_MULTIPLIER : 1.0;
        double speed = enabled ? SMOOTH_SPEED_IN : SMOOTH_SPEED_OUT;
        
        currentFovMultiplier += (target - currentFovMultiplier) * speed;
        
        // Применяем
        double newFov = originalFov * currentFovMultiplier;
        newFov = MathHelper.clamp(newFov, 1.0, 170.0);
        client.options.getFov().setValue(newFov);
    }

    public void press() {
        keyPressed = true;
    }

    public void release() {
        keyPressed = false;
    }

    public void toggle() {
        if (enabled) {
            release();
        } else {
            press();
        }
    }

    public boolean isZooming() {
        return enabled;
    }
}
