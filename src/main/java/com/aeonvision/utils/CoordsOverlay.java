package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class CoordsOverlay {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean visible = false;
    private String lastBiome = "Неизвестно";
    private long lastBiomeCheck = 0;
    private static final long BIOME_CHECK_INTERVAL = 1000; // 1 секунда

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!visible || MC.player == null) return;
        
        int x = (int) MC.player.getX();
        int y = (int) MC.player.getY();
        int z = (int) MC.player.getZ();
        
        // Обновляем биом раз в секунду
        long now = System.currentTimeMillis();
        if (now - lastBiomeCheck > BIOME_CHECK_INTERVAL) {
            lastBiomeCheck = now;
            BlockPos pos = MC.player.getBlockPos();
            Biome biome = MC.world.getBiome(pos).value();
            // Получаем ключ биома и форматируем
            String biomeKey = MC.world.getBiome(pos).getKey().get().getValue().getPath();
            lastBiome = formatBiomeName(biomeKey);
        }
        
        int screenWidth = MC.getWindow().getScaledWidth();
        
        // Строка координат
        String coords = String.format("§fXYZ: §a%d §f/ §a%d §f/ §a%d", x, y, z);
        
        // Направление
        String facing = getFacingDirection(MC.player.getYaw());
        String coordsWithFacing = coords + " §7• " + facing;
        
        // Рендерим фон
        int textWidth = MC.textRenderer.getWidth(coordsWithFacing);
        int bgX = screenWidth / 2 - textWidth / 2 - 5;
        int bgY = 28;
        context.fill(bgX, bgY, bgX + textWidth + 10, bgY + 26, 0x80000000);
        
        // Координаты
        context.drawText(MC.textRenderer, Text.literal(coordsWithFacing),
            screenWidth / 2 - textWidth / 2, bgY + 2, 0xFFFFFFFF, true);
        
        // Биом
        String biomeText = "§7Биом: §e" + lastBiome;
        int biomeWidth = MC.textRenderer.getWidth(biomeText);
        context.drawText(MC.textRenderer, Text.literal(biomeText),
            screenWidth / 2 - biomeWidth / 2, bgY + 14, 0xFFFFFFFF, true);
    }

    private String getFacingDirection(float yaw) {
        float normalized = ((yaw % 360) + 360) % 360;
        if (normalized >= 315 || normalized < 45) return "Юг";
        if (normalized >= 45 && normalized < 135) return "Запад";
        if (normalized >= 135 && normalized < 225) return "Север";
        return "Восток";
    }

    private String formatBiomeName(String key) {
        // Преобразует minecraft:dark_forest в "Тёмный лес"
        String[] words = key.replace("minecraft:", "").split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) result.append(" ");
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1));
        }
        return result.toString();
    }

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
