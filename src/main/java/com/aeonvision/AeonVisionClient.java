package com.aeonvision;

import com.aeonvision.hud.WatermarkOverlay;
import com.aeonvision.keybind.KeyBindManager;
import com.aeonvision.cosmetics.CosmeticManager;
import com.aeonvision.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class AeonVisionClient implements ClientModInitializer {
    
    public static final ZoomUtil ZOOM = new ZoomUtil();
    public static final NightVisionUtil NIGHT_VISION = new NightVisionUtil();
    public static final CosmeticManager COSMETICS = new CosmeticManager();
    public static final CoordsOverlay COORDS = new CoordsOverlay();
    public static MinecraftClient MC;

    @Override
    public void onInitializeClient() {
        MC = MinecraftClient.getInstance();
        KeyBindManager.register();
        
        HudRenderCallback.EVENT.register(WatermarkOverlay::render);
        HudRenderCallback.EVENT.register(COORDS::render);
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                ZOOM.tick(client);
                NIGHT_VISION.tick(client);
                COSMETICS.tick(client);
            }
        });
        
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
            if (MC.player != null) COSMETICS.renderWorld(ctx);
        });
    }
}
