package com.aeonvision.utils;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class CompassOverlay {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean visible = false;
    private static final String[] DIRECTIONS = {"С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ"};
    private static final int COMPASS_WIDTH = 200;
    private static final int TICK_MARKS = 20; // Количество рисок

    public void tick(MinecraftClient client) {
        // Компас не требует отдельного тика, рендерится каждый кадр
    }

    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!visible || MC.player == null) return;
        
        int width = MC.getWindow().getScaledWidth();
        int centerX = width / 2;
        int y = 5;
        
        // Фон компаса
        context.fill(centerX - COMPASS_WIDTH / 2 - 10, y - 2, 
                     centerX + COMPASS_WIDTH / 2 + 10, y + 18, 0x80000000);
        
        // Получаем угол поворота игрока
        float playerYaw = MC.player.getYaw();
        // Нормализуем (0-360, где 0 = север, 90 = восток)
        float normalizedYaw = ((playerYaw % 360) + 360) % 360;
        
        // Рисуем риски и направления
        for (int i = -TICK_MARKS / 2; i <= TICK_MARKS / 2; i++) {
            float direction = (normalizedYaw + i * (360f / TICK_MARKS)) % 360;
            if (direction < 0) direction += 360;
            
            int tickX = centerX + i * (COMPASS_WIDTH / TICK_MARKS);
            
            if (tickX < centerX - COMPASS_WIDTH / 2 || tickX > centerX + COMPASS_WIDTH / 2) continue;
            
            // Определяем, главное ли это направление
            boolean isMain = isMainDirection(direction);
            int tickHeight = isMain ? 12 : 6;
            int color = isMain ? 0xFFFFFFFF : 0x80FFFFFF;
            
            context.fill(tickX, y + 2, tickX + 1, y + 2 + tickHeight, color);
            
            // Подпись для главных направлений
            if (isMain) {
                String dirText = getDirectionText(direction);
                int textWidth = MC.textRenderer.getWidth(dirText);
                context.drawText(MC.textRenderer, Text.literal(dirText),
                    tickX - textWidth / 2, y + 14, 0xFFFFFFFF, true);
            }
        }
        
        // Центральный указатель (треугольник сверху)
        int indicatorColor = 0xFFFF5555;
        context.fill(centerX - 4, y - 2, centerX + 4, y + 2, indicatorColor);
        context.fill(centerX - 2, y - 5, centerX + 2, y - 2, indicatorColor);
        
        // Текущее направление текстом под компасом
        String currentDir = getDirectionText(normalizedYaw);
        String coords = String.format("%s | %d°", currentDir, (int)normalizedYaw);
        context.drawText(MC.textRenderer, Text.literal(coords),
            centerX - MC.textRenderer.getWidth(coords) / 2, y + 24, 0x80FFFFFF, false);
    }

    private boolean isMainDirection(float yaw) {
        return yaw >= 355 || yaw <= 5 ||   // Север
               (yaw >= 85 && yaw <= 95) ||  // Восток
               (yaw >= 175 && yaw <= 185) || // Юг
               (yaw >= 265 && yaw <= 275);   // Запад
    }

    private String getDirectionText(float yaw) {
        if (yaw >= 348.75 || yaw < 11.25) return "С";
        if (yaw >= 11.25 && yaw < 33.75) return "ССВ";
        if (yaw >= 33.75 && yaw < 56.25) return "СВ";
        if (yaw >= 56.25 && yaw < 78.75) return "ВСВ";
        if (yaw >= 78.75 && yaw < 101.25) return "В";
        if (yaw >= 101.25 && yaw < 123.75) return "ВЮВ";
        if (yaw >= 123.75 && yaw < 146.25) return "ЮВ";
        if (yaw >= 146.25 && yaw < 168.75) return "ЮЮВ";
        if (yaw >= 168.75 && yaw < 191.25) return "Ю";
        if (yaw >= 191.25 && yaw < 213.75) return "ЮЮЗ";
        if (yaw >= 213.75 && yaw < 236.25) return "ЮЗ";
        if (yaw >= 236.25 && yaw < 258.75) return "ЗЮЗ";
        if (yaw >= 258.75 && yaw < 281.25) return "З";
        if (yaw >= 281.25 && yaw < 303.75) return "ЗСЗ";
        if (yaw >= 303.75 && yaw < 326.25) return "СЗ";
        return "ССЗ";
    }

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
