package com.aeonvision.hud;

import com.aeonvision.AeonVisionClient;
import com.aeonvision.keybind.KeyBindManager;
import com.aeonvision.cosmetics.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import java.util.*;

public class RadialHudScreen extends Screen {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class Sector {
        String name, icon;
        Runnable action;
        
        Sector(String name, String icon, Runnable action) {
            this.name = name;
            this.icon = icon;
            this.action = action;
        }
    }
    
    private List<Sector> sectors = new ArrayList<>();
    private int cx, cy;
    private int outerR = 90, innerR = 30;
    private float time = 0;

    public RadialHudScreen() {
        super(Text.literal("Æon Vision HUD"));
    }

    @Override
    protected void init() {
        cx = width / 2;
        cy = height / 2;
        
        sectors.clear();
        
        // Косметика — переключение по кругу
        sectors.add(new Sector("Шлейф", "✦", () -> {
            AeonVisionClient.COSMETICS.nextTrail();
            MC.player.sendMessage(Text.literal("§aШлейф: " + AeonVisionClient.COSMETICS.getTrail().name), true);
        }));
        
        sectors.add(new Sector("Аура", "◎", () -> {
            AeonVisionClient.COSMETICS.nextAura();
            MC.player.sendMessage(Text.literal("§aАура: " + AeonVisionClient.COSMETICS.getAura().name), true);
        }));
        
        sectors.add(new Sector("Крылья", "🕊", () -> {
            AeonVisionClient.COSMETICS.nextWings();
            MC.player.sendMessage(Text.literal("§aКрылья: " + AeonVisionClient.COSMETICS.getWings().name), true);
        }));
        
        // Утилиты — вкл/выкл
        sectors.add(new Sector("Зум", "🔍", () -> {
            AeonVisionClient.ZOOM.toggle();
            MC.player.sendMessage(Text.literal(AeonVisionClient.ZOOM.isZooming() ? "§aЗум ВКЛ" : "§7Зум ВЫКЛ"), true);
        }));
        
        sectors.add(new Sector("Ночь", "🌙", () -> {
            AeonVisionClient.NIGHT_VISION.toggle();
            MC.player.sendMessage(Text.literal(AeonVisionClient.NIGHT_VISION.isEnabled() ? "§aНочное зрение ВКЛ" : "§7Ночное зрение ВЫКЛ"), true);
        }));
        
        sectors.add(new Sector("Компас", "🧭", () -> {
            AeonVisionClient.COMPASS.toggle();
            MC.player.sendMessage(Text.literal(AeonVisionClient.COMPASS.isVisible() ? "§aКомпас ВКЛ" : "§7Компас ВЫКЛ"), true);
        }));
        
        sectors.add(new Sector("Коорд.", "📍", () -> {
            AeonVisionClient.COORDS.toggle();
            MC.player.sendMessage(Text.literal(AeonVisionClient.COORDS.isVisible() ? "§aКоординаты ВКЛ" : "§7Координаты ВЫКЛ"), true);
        }));
        
        sectors.add(new Sector("Меню", "☰", () -> {
            MC.setScreen(new com.aeonvision.ui.ClickGuiScreen());
        }));
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        time += delta;
        
        // Затемнение фона
        ctx.fill(0, 0, width, height, 0x80000000);
        
        float aps = 360f / sectors.size();
        
        // Рисуем сектора
        for (int i = 0; i < sectors.size(); i++) {
            Sector s = sectors.get(i);
            float sa = i * aps - 90;
            float ea = (i + 1) * aps - 90;
            boolean hov = isInSector(mx, my, sa, ea);
            
            int baseColor = hov ? 0x60FFFFFF : 0x20FFFFFF;
            int borderColor = hov ? 0xFFFFFFFF : 0x40FFFFFF;
            
            // Рисуем сектор
            for (int j = 0; j < 32; j++) {
                float a1 = (float)Math.toRadians(sa + (ea - sa) * j / 32);
                float a2 = (float)Math.toRadians(sa + (ea - sa) * (j + 1) / 32);
                
                int x1 = cx + (int)(Math.cos(a1) * innerR);
                int y1 = cy + (int)(Math.sin(a1) * innerR);
                int x2 = cx + (int)(Math.cos(a1) * outerR);
                int y2 = cy + (int)(Math.sin(a1) * outerR);
                int x3 = cx + (int)(Math.cos(a2) * outerR);
                int y3 = cy + (int)(Math.sin(a2) * outerR);
                int x4 = cx + (int)(Math.cos(a2) * innerR);
                int y4 = cy + (int)(Math.sin(a2) * innerR);
                
                fillTri(ctx, x1, y1, x2, y2, x3, y3, baseColor);
                fillTri(ctx, x1, y1, x3, y3, x4, y4, baseColor);
            }
            
            // Иконка
            float ma = (sa + ea) / 2f;
            float ia = (float)Math.toRadians(ma);
            int id = (innerR + outerR) / 2;
            int ix = cx + (int)(Math.cos(ia) * id);
            int iy = cy + (int)(Math.sin(ia) * id);
            
            ctx.drawText(textRenderer, Text.literal(s.icon),
                ix - textRenderer.getWidth(s.icon)/2,
                iy - textRenderer.fontHeight/2,
                hov ? 0xFFFFFFFF : 0xA0FFFFFF, false);
        }
        
        // Центр
        drawCircle(ctx, cx, cy, innerR, 0x40FFFFFF);
        String logo = "Æ";
        ctx.drawText(textRenderer, Text.literal(logo),
            cx - textRenderer.getWidth(logo)/2,
            cy - textRenderer.fontHeight/2, 0xFFFFFFFF, false);
        
        // Подсказка
        Sector hov = null;
        float apsCheck = 360f / sectors.size();
        for (int i = 0; i < sectors.size(); i++) {
            float sa = i * apsCheck - 90;
            float ea = (i + 1) * apsCheck - 90;
            if (isInSector(mx, my, sa, ea)) {
                hov = sectors.get(i);
                break;
            }
        }
        
        if (hov != null) {
            ctx.drawText(textRenderer, Text.literal(hov.name),
                cx - textRenderer.getWidth(hov.name)/2, cy + outerR + 20, 0xFFFFFFFF, true);
        }
        
        String hint = "Выбери действие → отпусти Shift";
        ctx.drawText(textRenderer, Text.literal(hint),
            cx - textRenderer.getWidth(hint)/2, height - 30, 0x60FFFFFF, false);
    }

    private boolean isInSector(int mx, int my, float sa, float ea) {
        float dx = mx - cx;
        float dy = my - cy;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        
        if (dist < innerR || dist > outerR) return false;
        
        float ang = (float)Math.toDegrees(Math.atan2(dy, dx));
        if (ang < -90) ang += 360;
        
        if (sa > ea) return ang >= sa || ang <= ea;
        return ang >= sa && ang <= ea;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        float aps = 360f / sectors.size();
        for (int i = 0; i < sectors.size(); i++) {
            float sa = i * aps - 90;
            float ea = (i + 1) * aps - 90;
            if (isInSector((int)mx, (int)my, sa, ea)) {
                sectors.get(i).action.run();
                close();
                return true;
            }
        }
        close();
        return true;
    }

    @Override
    public void close() {
        super.close();
        KeyBindManager.closeHudPanel2();
    }

    @Override
    public boolean shouldPause() { return false; }

    // Хелперы
    private void fillTri(DrawContext ctx, int x1, int y1, int x2, int y2, int x3, int y3, int c) {
        int mnX = Math.min(x1, Math.min(x2, x3));
        int mxX = Math.max(x1, Math.max(x2, x3));
        int mnY = Math.min(y1, Math.min(y2, y3));
        int mxY = Math.max(y1, Math.max(y2, y3));
        for (int x = mnX; x <= mxX; x++)
            for (int y = mnY; y <= mxY; y++)
                if (ptInTri(x, y, x1, y1, x2, y2, x3, y3))
                    ctx.fill(x, y, x+1, y+1, c);
    }

    private boolean ptInTri(int px, int py, int x1, int y1, int x2, int y2, int x3, int y3) {
        float d1 = (px-x2)*(y1-y2) - (x1-x2)*(py-y2);
        float d2 = (px-x3)*(y2-y3) - (x2-x3)*(py-y3);
        float d3 = (px-x1)*(y3-y1) - (x3-x1)*(py-y1);
        boolean hn = (d1<0)||(d2<0)||(d3<0), hp = (d1>0)||(d2>0)||(d3>0);
        return !(hn && hp);
    }

    private void drawCircle(DrawContext ctx, int cx, int cy, int r, int c) {
        for (int x=-r; x<=r; x++)
            for (int y=-r; y<=r; y++)
                if (x*x+y*y <= r*r && x*x+y*y >= (r-1)*(r-1))
                    ctx.fill(cx+x, cy+y, cx+x+1, cy+y+1, c);
    }
    }
