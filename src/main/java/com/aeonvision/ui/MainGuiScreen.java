package com.aeonvision.ui;

import com.aeonvision.AeonVisionMod;
import com.aeonvision.AeonVisionClient;
import com.aeonvision.keybind.KeyBindManager;
import com.aeonvision.cosmetics.*;
import com.aeonvision.hud.WatermarkOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.*;

public class MainGuiScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private int panelX, panelY, panelW = 220, panelH = 280;
    
    // Категории модулей
    private static class Module {
        String name;
        boolean enabled;
        List<String> options;
        int selectedOption;
        
        Module(String name, boolean enabled, List<String> options) {
            this.name = name;
            this.enabled = enabled;
            this.options = options;
            this.selectedOption = 0;
        }
    }
    
    private List<Module> modules = new ArrayList<>();
    private int selectedModule = -1;
    private boolean showOptions = false;

    public MainGuiScreen() {
        super(Text.literal("Æon Vision"));
    }

    @Override
    protected void init() {
        panelX = width / 2 - panelW / 2;
        panelY = height / 2 - panelH / 2;
        
        modules.clear();
        modules.add(new Module("Шлейф", AeonVisionClient.COSMETICS.getTrail() != TrailType.NONE, 
            Arrays.asList("Огонь", "Звёзды", "Сакура", "Электричество", "Аква", "Эндер", "Радуга", "Скверна", "Нет")));
        modules.add(new Module("Аура", AeonVisionClient.COSMETICS.getAura() != AuraType.NONE,
            Arrays.asList("Базовая", "Пульсар", "Спираль", "Кристалл", "Плазма", "Божество", "Нет")));
        modules.add(new Module("Крылья", AeonVisionClient.COSMETICS.getWings() != WingType.NONE,
            Arrays.asList("Ангел", "Демон", "Кибер", "Дракон", "Феникс", "Нет")));
        modules.add(new Module("Hit Particles", false,
            Arrays.asList("Нет")));
        modules.add(new Module("Zoom", AeonVisionClient.ZOOM.isZooming(),
            Arrays.asList("Нет")));
        modules.add(new Module("FullBright", AeonVisionClient.NIGHT_VISION.isEnabled(),
            Arrays.asList("Нет")));
        modules.add(new Module("Watermark", AeonVisionMod.watermarkEnabled,
            Arrays.asList("Фиолетовый", "Синий", "Красный", "Зелёный")));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int y = panelY + 35;
        for (int i = 0; i < modules.size(); i++) {
            if (mx >= panelX + 10 && mx <= panelX + panelW - 10 && my >= y && my <= y + 22) {
                if (button == 0) {
                    // ЛКМ — вкл/выкл
                    toggleModule(i);
                    return true;
                } else if (button == 1 && !modules.get(i).options.get(0).equals("Нет")) {
                    // ПКМ — настройки
                    selectedModule = i;
                    showOptions = true;
                    return true;
                }
            }
            y += 26;
        }
        
        if (showOptions && selectedModule >= 0) {
            int oy = panelY + 35 + (selectedModule + 1) * 26;
            Module mod = modules.get(selectedModule);
            for (int j = 0; j < mod.options.size(); j++) {
                if (mx >= panelX + 30 && mx <= panelX + panelW - 10 && my >= oy + j * 22 && my <= oy + j * 22 + 20) {
                    mod.selectedOption = j;
                    applyOption(selectedModule, j);
                    showOptions = false;
                    selectedModule = -1;
                    clearAndInit();
                    return true;
                }
            }
        }
        
        return super.mouseClicked(mx, my, button);
    }

    private void toggleModule(int index) {
        Module mod = modules.get(index);
        mod.enabled = !mod.enabled;
        
        switch(index) {
            case 0: AeonVisionClient.COSMETICS.nextTrail(); break;
            case 1: AeonVisionClient.COSMETICS.nextAura(); break;
            case 2: AeonVisionClient.COSMETICS.nextWings(); break;
            case 3: /* Hit Particles toggle */ break;
            case 4: AeonVisionClient.ZOOM.toggle(); break;
            case 5: AeonVisionClient.NIGHT_VISION.toggle(); break;
            case 6: AeonVisionMod.watermarkEnabled = !AeonVisionMod.watermarkEnabled; break;
        }
        clearAndInit();
    }

    private void applyOption(int modIndex, int optIndex) {
        switch(modIndex) {
            case 6: // Watermark color
                switch(optIndex) {
                    case 0: WatermarkOverlay.bgColor = 0x6610; WatermarkOverlay.bgAlpha = 0x80; break; // Фиолетовый
                    case 1: WatermarkOverlay.bgColor = 0x1066; WatermarkOverlay.bgAlpha = 0x80; break; // Синий
                    case 2: WatermarkOverlay.bgColor = 0x6600; WatermarkOverlay.bgAlpha = 0x80; break; // Красный
                    case 3: WatermarkOverlay.bgColor = 0x0660; WatermarkOverlay.bgAlpha = 0x80; break; // Зелёный
                }
                break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x80000000);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xE8101018);
        context.fill(panelX, panelY, panelX + panelW, panelY + 1, 0x60AA55FF);
        
        context.drawText(textRenderer, Text.literal("ÆON VISION"),
            panelX + panelW/2 - textRenderer.getWidth("ÆON VISION")/2, panelY + 8, 0xFFAA55FF, false);
        
        // Модули
        int y = panelY + 35;
        for (int i = 0; i < modules.size(); i++) {
            Module mod = modules.get(i);
            String text = (mod.enabled ? "§a● " : "§7○ ") + mod.name;
            if (!mod.options.get(0).equals("Нет")) text += " §7▸";
            
            int color = (mouseY >= y && mouseY <= y + 22 && mouseX >= panelX + 10 && mouseX <= panelX + panelW - 10) 
                ? 0xFF333333 : 0xFF1A1A1A;
            context.fill(panelX + 10, y, panelX + panelW - 10, y + 22, color);
            context.drawText(textRenderer, Text.literal(text), panelX + 15, y + 5, 0xFFFFFFFF, false);
            y += 26;
        }
        
        // Опции (если выбраны)
        if (showOptions && selectedModule >= 0) {
            Module mod = modules.get(selectedModule);
            int oy = panelY + 35 + (selectedModule + 1) * 26;
            context.fill(panelX + 30, oy, panelX + panelW - 10, oy + mod.options.size() * 22, 0xFF0A0A0F);
            for (int j = 0; j < mod.options.size(); j++) {
                String optText = (j == mod.selectedOption ? "§a▶ " : "§7  ") + mod.options.get(j);
                context.drawText(textRenderer, Text.literal(optText), panelX + 35, oy + j * 22 + 3, 0xFFFFFFFF, false);
            }
        }
        
        // Подсказка
        context.drawText(textRenderer, Text.literal("ЛКМ — вкл/выкл  |  ПКМ — настройки"),
            panelX + 10, panelY + panelH - 15, 0x50FFFFFF, false);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() { super.close(); KeyBindManager.closeGui(); }
    @Override public boolean shouldPause() { return false; }
            }
