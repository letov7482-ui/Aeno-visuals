package com.aeonvision;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AeonVisionMod implements ModInitializer {
    public static final String MOD_ID = "aeonvision";
    public static final String MOD_NAME = "Æon Vision";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    
    // Версия мода
    public static final String VERSION = "1.0.0-alpha";
    
    // Флаги состояния
    public static boolean hudEnabled = true;
    public static boolean watermarkEnabled = true;
    public static boolean cosmeticEnabled = true;
    
    // Конфиг-директория
    public static final java.io.File CONFIG_DIR = 
        new java.io.File(
            net.fabricmc.loader.api.FabricLoader.getInstance()
                .getConfigDir().toFile(), "aeonvision"
        );

    @Override
    public void onInitialize() {
        // Создаём папку конфигов
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        
        LOGGER.info("""
                
                ╔═══════════════════════════════════╗
                ║     Æ O N   V I S I O N           ║
                ║     Version: {}              ║
                ║     Status: INITIALIZED          ║
                ║     Minecraft: 1.21.4            ║
                ╚═══════════════════════════════════╝
                """, VERSION);
        
        LOGGER.info("Новая эра визуалов запущена. Плавность и красота активны.");
    }
}
