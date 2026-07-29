package com.aeonvision.keybind;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.AeonVisionClient;
import com.aeonvision.hud.RadialHudScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindManager {

    public static KeyBinding HUD_PANEL_KEY;
    public static KeyBinding ZOOM_KEY;
    public static KeyBinding NIGHT_VISION_KEY;
    public static KeyBinding COMPASS_KEY;
    public static KeyBinding COORDS_KEY;
    public static KeyBinding TIMER_KEY;
    public static KeyBinding NOTES_KEY;
    public static KeyBinding AUTO_TORCH_KEY;

    private static boolean hudPanelOpen = false;
    private static boolean shiftWasPressed = false;

    public static void register() {
        AeonVisionMod.LOGGER.info("Регистрация клавиш Æon Vision...");

        HUD_PANEL_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.hud_panel",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "category.aeonvision.main"
        ));

        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.zoom",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.aeonvision.main"
        ));

        NIGHT_VISION_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.night_vision",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.aeonvision.main"
        ));

        COMPASS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.compass",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.aeonvision.main"
        ));

        COORDS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.coords",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category.aeonvision.main"
        ));

        TIMER_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.timer",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.aeonvision.main"
        ));

        NOTES_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.notes",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.aeonvision.main"
        ));

        AUTO_TORCH_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.auto_torch",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "category.aeonvision.main"
        ));

        AeonVisionMod.LOGGER.info("✓ Зарегистрировано 8 клавиш");
    }

    public static boolean handleKey(int key, int scancode, int action, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Left Shift — открытие HUD-панели
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
        
        // Обработка нажатий (одиночный клик, не зажатие)
        if (action == GLFW.GLFW_PRESS) {
            if (key == ZOOM_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.ZOOM.toggle();
                return false; // Не блокируем, чтобы не мешать игре
            }
            if (key == NIGHT_VISION_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.NIGHT_VISION.toggle();
                return false;
            }
            if (key == COMPASS_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.COMPASS.toggle();
                return false;
            }
            if (key == COORDS_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.COORDS.toggle();
                return false;
            }
            if (key == TIMER_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.TIMER.toggle();
                return false;
            }
            if (key == NOTES_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.NOTES.toggle();
                return false;
            }
            if (key == AUTO_TORCH_KEY.getDefaultKey().getCode()) {
                AeonVisionClient.AUTO_TORCH.toggle();
                return false;
            }
        }
        
        return false;
    }

    private static void openHudPanel() {
        MinecraftClient client = MinecraftClient.getInstance();
        hudPanelOpen = true;
        client.setScreen(new RadialHudScreen());
        AeonVisionMod.LOGGER.debug("HUD Panel opened");
    }

    private static void closeHudPanel() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof RadialHudScreen) {
            client.setScreen(null);
        }
        hudPanelOpen = false;
        AeonVisionMod.LOGGER.debug("HUD Panel closed");
    }

    public static boolean isHudPanelOpen() {
        return hudPanelOpen;
    }
}
