package com.aeonvision;

import com.aeonvision.hud.*;
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
    public static final CompassOverlay COMPASS = new CompassOverlay();
    public static final CoordsOverlay COORDS = new CoordsOverlay();
    public static final TimerUtil TIMER = new TimerUtil();
    public static final NotesUtil NOTES = new NotesUtil();
    public static final AutoTorchUtil AUTO_TORCH = new AutoTorchUtil();
    public static final InventorySorter SORTER = new InventorySorter();
    public static MinecraftClient MC;

    @Override
    public void onInitializeClient() {
        MC = MinecraftClient.getInstance();
        AeonVisionMod.LOGGER.info("Æon Vision 2.0 loading...");
        
        KeyBindManager.register();
        
        // HUD элементы
        HudRenderCallback.EVENT.register(WatermarkOverlay::render);
        HudRenderCallback.EVENT.register(TargetHUD::render);
        HudRenderCallback.EVENT.register(ArmorHUD::render);
        HudRenderCallback.EVENT.register(COMPASS::render);
        HudRenderCallback.EVENT.register(COORDS::render);
        HudRenderCallback.EVENT.register(TIMER::render);
        HudRenderCallback.EVENT.register(NOTES::render);
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                ZOOM.tick(client);
                NIGHT_VISION.tick(client);
                COSMETICS.tick(client);
                COMPASS.tick(client);
                AUTO_TORCH.tick(client);
            }
        });
        
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (MC.player != null && AeonVisionMod.cosmeticEnabled) {
                COSMETICS.renderWorld(context);
            }
        });
        
        AeonVisionMod.LOGGER.info("Æon Vision 2.0 ready!");
    }
}
