package com.aeonvision.keybind;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.hud.RadialHudScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindManager {

    // ТОЛЬКО ОДИН БИНД — HUD-панель
    public static KeyBinding HUD_PANEL_KEY;

    private static boolean hudPanelOpen = false;
    private static boolean shiftWasPressed = false;

    public static void register() {
        AeonVisionMod.LOGGER.info("Регистрация бинда Æon Vision...");

        HUD_PANEL_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.hud_panel",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "category.aeonvision.main"
        ));

        AeonVisionMod.LOGGER.info("✓ Зарегистрирован 1 бинд: HUD Panel (Left Shift)");
    }

    public static boolean handleKey(int key, int scancode, int action, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Left Shift — открытие/закрытие HUD-панели
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
            if (action == GLFW.GLFW_PRESS && !shiftWasPressed) {
                shiftWasPressed = true;
                if (!hudPanelOpen && client.player != null) {
                    openHudPanel();
                    return true;
                }
            } else if (action == GLFW.GLFW_RELEASE) {
                shiftWasPressed = false;
                if (hudPanelOpen) {
                    closeHudPanel();
                    return true;
                }
            }
        }
        
        return false;
    }

    private static void openHudPanel() {
        MinecraftClient client = MinecraftClient.getInstance();
        hudPanelOpen = true;
        client.setScreen(new RadialHudScreen());
    }

    public static void closeHudPanel() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof RadialHudScreen) {
            client.setScreen(null);
        }
        hudPanelOpen = false;
    }
    
    public static void closeHudPanel2() {
        hudPanelOpen = false;
    }

    public static boolean isHudPanelOpen() {
        return hudPanelOpen;
    }
}
