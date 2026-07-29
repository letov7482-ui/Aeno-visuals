package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class CoordsOverlay {
    private boolean visible = false;
    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!visible || MC.player == null) return;
        
        int x = (int) MC.player.getX();
        int y = (int) MC.player.getY();
        int z = (int) MC.player.getZ();
        
        String coords = String.format("XYZ: %d / %d / %d", x, y, z);
        int width = MC.getWindow().getScaledWidth();
        
        context.drawText(MC.textRenderer, Text.literal(coords), 
            width / 2 - MC.textRenderer.getWidth(coords) / 2, 30, 0xFFFFFFFF, true);
    }

    public void toggle() { visible = !visible; }
}
