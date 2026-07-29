package ru.neon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import ru.neon.visuals.gui.NeonGUIScreen;
import ru.neon.visuals.render.*;

public class NeonVisualsClient implements ClientModInitializer {
    private static NeonGUIScreen guiScreen;

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        NeonParticles.INSTANCE.init();
        NeonTrails.INSTANCE.init();
        NeonHand.INSTANCE.init();
        NeonTargetHUD.INSTANCE.init();
        NeonTrajectory.INSTANCE.init();
        NeonDamageNumbers.INSTANCE.init();
        NeonSkyCustomizer.INSTANCE.init();

        ClientTickEvents.END_CLIENT_TICK.register(client1 -> {
            if (client.options.keyUse.wasPressed()) { // Right Shift
                client.setScreen(guiScreen = new NeonGUIScreen());
            }
            if (guiScreen != null && !guiScreen.isOpen()) guiScreen = null;
        });

        ClientTickEvents.END_RENDER_TICK.register(client1 -> {
            if (guiScreen != null) guiScreen.render(client1.getWindow().getScaledWidth(), client1.getWindow().getScaledHeight(), client1);
        });
    }

    public static NeonGUIScreen getGuiScreen() { return guiScreen; }
}
