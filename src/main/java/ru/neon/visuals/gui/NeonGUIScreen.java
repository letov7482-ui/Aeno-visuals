package ru.neon.visuals.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import ru.neon.NeonVisualsClient;

public class NeonGUIScreen extends Screen {
    private int currentTab = 0;

    public NeonGUIScreen() { super(Text.literal("Neon Visuals")); }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.literal("Visuals"), b -> currentTab = 0).dimensions(10, 10, 80, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Utilities"), b -> currentTab = 1).dimensions(100, 10, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Client Color"), b -> currentTab = 2).dimensions(210, 10, 100, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
