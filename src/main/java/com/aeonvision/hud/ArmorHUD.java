package com.aeonvision.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ArmorHUD {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (MC.player == null) return;
        
        int armor = MC.player.getArmor();
        if (armor == 0) return;
        
        int x = MC.getWindow().getScaledWidth() / 2 + 90;
        int y = 8;
        
        // Фон
        context.fill(x - 2, y - 2, x + 40, y + 14, 0x90101015);
        
        // Текст
        String text = "🛡 " + armor;
        context.drawText(MC.textRenderer, Text.literal(text), x, y, 0xCCCCFF, true);
        
        // Полоска брони
        float armorPercent = Math.min(1, armor / 20f);
        int barW = 34;
        context.fill(x, y + 11, x + barW, y + 12, 0x30FFFFFF);
        int fillW = (int)(barW * armorPercent);
        context.fill(x, y + 11, x + fillW, y + 12, 0xFF8888FF);
    }
}
