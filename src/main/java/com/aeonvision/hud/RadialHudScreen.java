package com.aeonvision.hud;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.AeonVisionClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.List;

public class RadialHudScreen extends Screen {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class Sector {
        String name;
        String icon;
        int centerX, centerY;
        float startAngle, endAngle;
        boolean hovered;
        Runnable action;
        
        Sector(String name, String icon, Runnable action) {
            this.name = name;
            this.icon = icon;
            this.action = action;
        }
    }
    
    private List<Sector> sectors = new ArrayList<>();
    private int centerX, centerY;
    private int outerRadius = 90;
    private int innerRadius = 30;
    private float hoverAngle = -1;
    private float time = 0;

    public RadialHudScreen() {
        super(Text.literal("Æon Vision HUD"));
    }

    @Override
    protected void init() {
        centerX = width / 2;
        centerY = height / 2;
        
        sectors.clear();
        sectors.add(new Sector("Косметика", "✦", () -> openCosmetics()));
        sectors.add(new Sector("Утилиты", "⚙", () -> openUtils()));
        sectors.add(new Sector("Визуалы", "◈", () -> openVisuals()));
        sectors.add(new Sector("Миры", "⬡", () -> openWorlds()));
        sectors.add(new Sector("Серверы", "⬢", () -> openServers()));
        sectors.add(new Sector("Аккаунты", "◉", () -> openAccounts()));
        
        float anglePerSector = 360f / sectors.size();
        for (int i = 0; i < sectors.size(); i++) {
            Sector s = sectors.get(i);
            s.centerX = centerX;
            s.centerY = centerY;
            s.startAngle = i * anglePerSector - 90; // -90 чтобы начать сверху
            s.endAngle = (i + 1) * anglePerSector - 90;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        time += delta;
        
        // Затемнение фона
        context.fill(0, 0, width, height, 0x80000000);
        
        // Рисуем сектора
        for (Sector sector : sectors) {
            sector.hovered = isInSector(mouseX, mouseY, sector);
            drawSector(context, sector);
        }
        
        // Центральный круг с логотипом
        drawCenterLogo(context);
        
        // Подсказка снизу
        Sector hovered = sectors.stream().filter(s -> s.hovered).findFirst().orElse(null);
        if (hovered != null) {
            String hint = hovered.name;
            int hintWidth = textRenderer.getWidth(hint);
            context.drawText(textRenderer, Text.literal(hint),
                centerX - hintWidth / 2, centerY + outerRadius + 20, 0xFFFFFFFF, true);
            
            // Подсветка сектора при наведении
            context.drawText(textRenderer, Text.literal(hovered.icon),
                centerX, centerY - 60, 0xFFFFFFFF, true);
        }
        
        // Инструкция
        String instruction = "Наведи на сектор и отпусти Shift";
        context.drawText(textRenderer, Text.literal(instruction),
            centerX - textRenderer.getWidth(instruction) / 2, height - 30, 0x60FFFFFF, false);
    }

    private void drawSector(DrawContext context, Sector sector) {
        int segments = 32;
        float midAngle = (sector.startAngle + sector.endAngle) / 2f;
        float iconAngle = (float)Math.toRadians(midAngle);
        
        int baseColor = sector.hovered ? 0x60FFFFFF : 0x20FFFFFF;
        int borderColor = sector.hovered ? 0xFFFFFFFF : 0x40FFFFFF;
        
        // Рисуем сектор
        for (int i = 0; i < segments; i++) {
            float a1 = (float)Math.toRadians(sector.startAngle + (sector.endAngle - sector.startAngle) * i / segments);
            float a2 = (float)Math.toRadians(sector.startAngle + (sector.endAngle - sector.startAngle) * (i + 1) / segments);
            
            int x1 = centerX + (int)(Math.cos(a1) * innerRadius);
            int y1 = centerY + (int)(Math.sin(a1) * innerRadius);
            int x2 = centerX + (int)(Math.cos(a1) * outerRadius);
            int y2 = centerY + (int)(Math.sin(a1) * outerRadius);
            int x3 = centerX + (int)(Math.cos(a2) * outerRadius);
            int y3 = centerY + (int)(Math.sin(a2) * outerRadius);
            int x4 = centerX + (int)(Math.cos(a2) * innerRadius);
            int y4 = centerY + (int)(Math.sin(a2) * innerRadius);
            
            // Заливка
            fillTriangle(context, x1, y1, x2, y2, x3, y3, baseColor);
            fillTriangle(context, x1, y1, x3, y3, x4, y4, baseColor);
        }
        
        // Иконка в центре сектора
        int iconDist = (innerRadius + outerRadius) / 2;
        int iconX = centerX + (int)(Math.cos(iconAngle) * iconDist);
        int iconY = centerY + (int)(Math.sin(iconAngle) * iconDist);
        
        int iconColor = sector.hovered ? 0xFFFFFFFF : 0xA0FFFFFF;
        context.drawText(textRenderer, Text.literal(sector.icon),
            iconX - textRenderer.getWidth(sector.icon) / 2,
            iconY - textRenderer.fontHeight / 2,
            iconColor, false);
        
        // Границы сектора
        int bx1 = centerX + (int)(Math.cos(Math.toRadians(sector.startAngle)) * outerRadius);
        int by1 = centerY + (int)(Math.sin(Math.toRadians(sector.startAngle)) * outerRadius);
        context.drawLine(centerX, centerY, bx1, by1, borderColor);
        
        int bx2 = centerX + (int)(Math.cos(Math.toRadians(sector.endAngle)) * outerRadius);
        int by2 = centerY + (int)(Math.sin(Math.toRadians(sector.endAngle)) * outerRadius);
        context.drawLine(centerX, centerY, bx2, by2, borderColor);
    }

    private void drawCenterLogo(DrawContext context) {
        // Центральный круг
        for (int r = innerRadius; r >= innerRadius - 5; r--) {
            drawCircle(context, centerX, centerY, r, 0x40FFFFFF);
        }
        
        // Æ символ
        String logo = "Æ";
        context.drawText(textRenderer, Text.literal(logo),
            centerX - textRenderer.getWidth(logo) / 2,
            centerY - textRenderer.fontHeight / 2,
            0xFFFFFFFF, false);
        
        // Пульсирующее кольцо
        float pulse = 1 + MathHelper.sin(time * 3f) * 0.1f;
        int pulseRadius = (int)(innerRadius * pulse);
        drawCircle(context, centerX, centerY, pulseRadius, 0x30FFFFFF);
    }

    private boolean isInSector(int mx, int my, Sector sector) {
        float dx = mx - centerX;
        float dy = my - centerY;
        float dist = (float)Math.sqrt(dx * dx + dy * dy);
        
        if (dist < innerRadius || dist > outerRadius) return false;
        
        float angle = (float)Math.toDegrees(Math.atan2(dy, dx));
        if (angle < -90) angle += 360;
        
        float start = sector.startAngle;
        float end = sector.endAngle;
        
        if (start > end) {
            return angle >= start || angle <= end;
        }
        return angle >= start && angle <= end;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Sector sector : sectors) {
            if (isInSector((int)mouseX, (int)mouseY, sector)) {
                sector.action.run();
                close();
                return true;
            }
        }
        close();
        return true;
    }

    private void openCosmetics() {
        // Откроем меню косметики
        MC.setScreen(new com.aeonvision.ui.CosmeticScreen());
    }
    
    private void openUtils() {
        MC.setScreen(new com.aeonvision.ui.UtilsScreen());
    }
    
    private void openVisuals() {
        MC.setScreen(new com.aeonvision.ui.VisualsScreen());
    }
    
    private void openWorlds() {
        // Переход на экран выбора мира
        MC.setScreen(new com.aeonvision.ui.WorldManagerScreen());
    }
    
    private void openServers() {
        MC.setScreen(new com.aeonvision.ui.ServerManagerScreen());
    }
    
    private void openAccounts() {
        MC.setScreen(new com.aeonvision.accounts.AccountManagerScreen());
    }

    @Override
    public void close() {
        super.close();
        KeyBindManager.closeHudPanel2();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // Utility methods
    private void fillTriangle(DrawContext context, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        // Простая заливка треугольника через fill
        int minX = Math.min(x1, Math.min(x2, x3));
        int maxX = Math.max(x1, Math.max(x2, x3));
        int minY = Math.min(y1, Math.min(y2, y3));
        int maxY = Math.max(y1, Math.max(y2, y3));
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (pointInTriangle(x, y, x1, y1, x2, y2, x3, y3)) {
                    context.fill(x, y, x + 1, y + 1, color);
                }
            }
        }
    }

    private boolean pointInTriangle(int px, int py, int x1, int y1, int x2, int y2, int x3, int y3) {
        float d1 = sign(px, py, x1, y1, x2, y2);
        float d2 = sign(px, py, x2, y2, x3, y3);
        float d3 = sign(px, py, x3, y3, x1, y1);
        
        boolean hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        boolean hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        
        return !(hasNeg && hasPos);
    }

    private float sign(int px, int py, int x1, int y1, int x2, int y2) {
        return (px - x2) * (y1 - y2) - (x1 - x2) * (py - y2);
    }

    private void drawCircle(DrawContext context, int cx, int cy, int r, int color) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                if (x * x + y * y <= r * r && x * x + y * y >= (r - 1) * (r - 1)) {
                    context.fill(cx + x, cy + y, cx + x + 1, cy + y + 1, color);
                }
            }
        }
    }
          }
