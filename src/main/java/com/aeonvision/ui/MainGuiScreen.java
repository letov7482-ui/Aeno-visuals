package com.aeonvision.ui;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.AeonVisionClient;
import com.aeonvision.keybind.KeyBindManager;
import com.aeonvision.cosmetics.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MainGuiScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private String tab = "main"; // main, cosmetics, hud
    private int panelX, panelY, panelW = 200, panelH = 250;

    public MainGuiScreen() {
        super(Text.literal("Æon Vision"));
    }

    @Override
    protected void init() {
        panelX = width / 2 - panelW / 2;
        panelY = height / 2 - panelH / 2;
        
        int cx = panelX + panelW / 2;
        int y = panelY + 35;
        
        if (tab.equals("main")) {
            addButton(cx - 70, y, 140, 22, "🎨 Косметика", () -> tab = "cosmetics"); y += 28;
            addButton(cx - 70, y, 140, 22, "📊 HUD", () -> tab = "hud"); y += 28;
            addButton(cx - 70, y, 140, 22, "🔍 Zoom: " + (AeonVisionClient.ZOOM.isZooming() ? "ВКЛ" : "ВЫКЛ"), 
                () -> AeonVisionClient.ZOOM.toggle()); y += 28;
            addButton(cx - 70, y, 140, 22, "💡 FullBright: " + (AeonVisionClient.NIGHT_VISION.isEnabled() ? "ВКЛ" : "ВЫКЛ"), 
                () -> AeonVisionClient.NIGHT_VISION.toggle()); y += 28;
        }
        
        if (tab.equals("cosmetics")) {
            addButton(cx - 70, y, 140, 22, "Шлейф: " + AeonVisionClient.COSMETICS.getTrail().name,
                () -> AeonVisionClient.COSMETICS.nextTrail()); y += 28;
            addButton(cx - 70, y, 140, 22, "Аура: " + AeonVisionClient.COSMETICS.getAura().name,
                () -> AeonVisionClient.COSMETICS.nextAura()); y += 28;
            addButton(cx - 70, y, 140, 22, "Крылья: " + AeonVisionClient.COSMETICS.getWings().name,
                () -> AeonVisionClient.COSMETICS.nextWings()); y += 28;
            addButton(cx - 70, y, 140, 22, "Следы: " + AeonVisionClient.COSMETICS.getFootprints().name,
                () -> AeonVisionClient.COSMETICS.nextFootprints()); y += 28;
            addButton(cx - 70, y, 140, 22, "← Назад", () -> tab = "main"); y += 28;
        }
        
        if (tab.equals("hud")) {
            addButton(cx - 70, y, 140, 22, "Watermark: " + (AeonVisionMod.watermarkEnabled ? "ВКЛ" : "ВЫКЛ"),
                () -> AeonVisionMod.watermarkEnabled = !AeonVisionMod.watermarkEnabled); y += 28;
            addButton(cx - 70, y, 140, 22, "Координаты: " + (AeonVisionClient.COORDS.isVisible() ? "ВКЛ" : "ВЫКЛ"),
                () -> AeonVisionClient.COORDS.toggle()); y += 28;
            addButton(cx - 70, y, 140, 22, "ArmorHUD: ВКЛ",
                () -> {}); y += 28;
            addButton(cx - 70, y, 140, 22, "← Назад", () -> tab = "main");
        }
    }

    private void addButton(int x, int y, int w, int h, String text, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(text), btn -> {
            action.run();
            clearAndInit();
        }).dimensions(x, y, w, h).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        context.fill(0, 0, width, height, 0x80000000);
        
        // Панель (тёмная, стильная)
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xE8101018);
        context.fill(panelX, panelY, panelX + panelW, panelY + 1, 0x60AA66FF);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0x30AA66FF);
        
        // Заголовок
        String title = "ÆON VISION";
        context.drawText(textRenderer, Text.literal(title),
            panelX + panelW/2 - textRenderer.getWidth(title)/2, panelY + 8, 0xFFAA66FF, false);
        
        // Вкладка
        String tabName = tab.equals("main") ? "Меню" : tab.equals("cosmetics") ? "Косметика" : "HUD";
        context.drawText(textRenderer, Text.literal(tabName),
            panelX + 10, panelY + 30, 0x80FFFFFF, false);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        KeyBindManager.closeGui();
    }

    @Override
    public boolean shouldPause() { return false; }
                         }
