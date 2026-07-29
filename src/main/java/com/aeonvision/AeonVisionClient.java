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
    
    // Синглтоны утилит
    public static final ZoomUtil ZOOM = new ZoomUtil();
    public static final NightVisionUtil NIGHT_VISION = new NightVisionUtil();
    public static final CosmeticManager COSMETICS = new CosmeticManager();
    public static final CompassOverlay COMPASS = new CompassOverlay();
    public static final CoordsOverlay COORDS = new CoordsOverlay();
    public static final TimerUtil TIMER = new TimerUtil();
    public static final NotesUtil NOTES = new NotesUtil();
    public static final AutoTorchUtil AUTO_TORCH = new AutoTorchUtil();
    public static final InventorySorter SORTER = new InventorySorter();
    
    // Клиент
    public static MinecraftClient MC;
    
    @Override
    public void onInitializeClient() {
        MC = MinecraftClient.getInstance();
        
        AeonVisionMod.LOGGER.info("Æon Vision Client инициализируется...");
        
        // Регистрируем клавиши
        KeyBindManager.register();
        
        // Watermark с FPS
        HudRenderCallback.EVENT.register(WatermarkOverlay::render);
        
        // Компас
        HudRenderCallback.EVENT.register(COMPASS::render);
        
        // Координаты
        HudRenderCallback.EVENT.register(COORDS::render);
        
        // Таймер
        HudRenderCallback.EVENT.register(TIMER::render);
        
        // Заметки
        HudRenderCallback.EVENT.register(NOTES::render);
        
        // Клиентский тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                ZOOM.tick(client);
                NIGHT_VISION.tick(client);
                COSMETICS.tick(client);
                COMPASS.tick(client);
                AUTO_TORCH.tick(client);
            }
        });
        
        // Рендер мира — косметика
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            if (MC.player != null && AeonVisionMod.cosmeticEnabled) {
                COSMETICS.renderWorld(context);
            }
        });
        
        AeonVisionMod.LOGGER.info("Æon Vision Client готов к работе!");
        AeonVisionMod.LOGGER.info("  ├── Watermark: {}", AeonVisionMod.watermarkEnabled ? "§a✓" : "§c✗");
        AeonVisionMod.LOGGER.info("  ├── HUD Panel: {}", AeonVisionMod.hudEnabled ? "§a✓" : "§c✗");
        AeonVisionMod.LOGGER.info("  ├── Cosmetics: {}", AeonVisionMod.cosmeticEnabled ? "§a✓" : "§c✗");
        AeonVisionMod.LOGGER.info("  ├── Utils: Zoom, NightVision, Compass, Coords, Timer, Notes, AutoTorch");
        AeonVisionMod.LOGGER.info("  └── Inventory Sorter");
    }
}
