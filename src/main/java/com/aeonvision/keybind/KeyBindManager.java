package com.aeonvision.keybind;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.ui.MainGuiScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindManager {

    public static KeyBinding GUI_KEY;
    private static boolean guiOpen = false;

    public static void register() {
        GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aeonvision.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.aeonvision.main"
        ));
    }

    public static boolean handleKey(int key, int scancode, int action, int modifiers) {
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT && action == GLFW.GLFW_PRESS && !guiOpen) {
            openGui();
            return true;
        }
        return false;
    }

    private static void openGui() {
        guiOpen = true;
        MinecraftClient.getInstance().setScreen(new MainGuiScreen());
    }

    public static void closeGui() { guiOpen = false; }
    public static boolean isGuiOpen() { return guiOpen; }
}
