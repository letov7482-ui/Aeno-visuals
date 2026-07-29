package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class CosmeticManager {
    private boolean trailsEnabled = false;
    private boolean auraEnabled = false;
    private boolean wingsEnabled = false;

    public void tick(MinecraftClient client) {
        // Заглушка
    }

    public void renderWorld(WorldRenderContext context) {
        // Заглушка — частицы, ауры, крылья будут здесь
    }

    public void toggleTrails() { trailsEnabled = !trailsEnabled; }
    public void toggleAura() { auraEnabled = !auraEnabled; }
    public void toggleWings() { wingsEnabled = !wingsEnabled; }
    
    public boolean hasTrails() { return trailsEnabled; }
    public boolean hasAura() { return auraEnabled; }
    public boolean hasWings() { return wingsEnabled; }
}
