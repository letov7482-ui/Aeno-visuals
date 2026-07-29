package com.aeonvision.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import java.util.*;

public class ServerManagerScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private List<ServerInfo> servers = new ArrayList<>();

    public ServerManagerScreen() {
        super(Text.literal("Æon Vision - Серверы"));
    }

    @Override
    protected void init() {
        servers.clear();
        // Получаем список серверов
        if (MC.getServerList() != null) {
            for (int i = 0; i < MC.getServerList().size(); i++) {
                servers.add(MC.getServerList().get(i));
            }
        }

        int centerX = width / 2;
        int y = 50;

        // Переход в стандартный список серверов
        addDrawableChild(ButtonWidget.builder(
            Text.literal("🌐 Стандартный список серверов"),
            btn -> MC.setScreen(new MultiplayerScreen(this))
        ).dimensions(centerX - 110, y, 220, 22).build());

        // Список серверов
        for (int i = 0; i < Math.min(servers.size(), 12); i++) {
            ServerInfo server = servers.get(i);
            int serverY = y + 30 + i * 28;

            String label = server.isOnline() ? "§a● " : "§7● ";
            label += server.name;
            if (server.isOnline()) {
                label += " §7(" + server.playerCountLabel + ")";
            }

            final ServerInfo srv = server;
            addDrawableChild(ButtonWidget.builder(
                Text.literal(label),
                btn -> {
                    // Подключение к серверу
                    MC.setScreen(new net.minecraft.client.gui.screen.ConnectScreen(
                        this, MC, 
                        new net.minecraft.client.network.ServerAddress(srv.address, srv.port),
                        srv, false, null
                    ));
                }
            ).dimensions(centerX - 150, serverY, 250, 22).build());

            // Пинг
            addDrawableChild(ButtonWidget.builder(
                Text.literal("📶"),
                btn -> pingServer(srv)
            ).dimensions(centerX + 105, serverY, 30, 22).build());
        }

        // Добавить сервер
        addDrawableChild(ButtonWidget.builder(
            Text.literal("➕ Добавить сервер"),
            btn -> {
                MC.setScreen(new net.minecraft.client.gui.screen.multiplayer.AddServerScreen(
                    this, serverInfo -> {
                        MC.getServerList().add(serverInfo, false);
                        MC.getServerList().saveFile();
                        clearAndInit();
                    }
                ));
            }
        ).dimensions(centerX - 80, y + 30 + 12*28 + 10, 160, 22).build());

        // Закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    private void pingServer(ServerInfo server) {
        // Заглушка — в будущем можно добавить реальный пинг
        if (MC.player != null) {
            MC.player.sendMessage(Text.literal("§eПинг до " + server.name + ": проверка..."), false);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - СЕРВЕРЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - СЕРВЕРЫ")/2, 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Серверов: " + servers.size()),
            width/2 - 40, 30, 0x80FFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
          }
