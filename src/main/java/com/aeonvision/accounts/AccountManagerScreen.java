package com.aeonvision.accounts;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AccountManagerScreen extends Screen {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class Account {
        String nickname;
        String uuid;
        long created;
        
        Account(String nickname) {
            this.nickname = nickname;
            this.uuid = UUID.randomUUID().toString();
            this.created = System.currentTimeMillis();
        }
    }
    
    private List<Account> accounts = new ArrayList<>();
    private Account activeAccount;
    private TextFieldWidget nicknameField;
    private int scrollOffset = 0;

    public AccountManagerScreen() {
        super(Text.literal("Æon Vision - Аккаунты"));
        loadAccounts();
    }

    private void loadAccounts() {
        accounts.clear();
        Path accFile = AeonVisionMod.CONFIG_DIR.toPath().resolve("accounts.json");
        try {
            if (Files.exists(accFile)) {
                String content = Files.readString(accFile);
                String[] lines = content.split("\n");
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        Account acc = new Account(parts[0]);
                        acc.uuid = parts[1];
                        if (parts.length >= 3) acc.created = Long.parseLong(parts[2]);
                        accounts.add(acc);
                    }
                }
            }
        } catch (IOException e) {
            AeonVisionMod.LOGGER.warn("Не удалось загрузить аккаунты");
        }
        
        // Активный аккаунт
        if (MC.getSession() != null) {
            String currentNick = MC.getSession().getUsername();
            activeAccount = accounts.stream()
                .filter(a -> a.nickname.equals(currentNick))
                .findFirst().orElse(null);
        }
    }

    private void saveAccounts() {
        Path accFile = AeonVisionMod.CONFIG_DIR.toPath().resolve("accounts.json");
        try {
            Files.createDirectories(accFile.getParent());
            StringBuilder sb = new StringBuilder();
            for (Account acc : accounts) {
                sb.append(acc.nickname).append(":").append(acc.uuid)
                  .append(":").append(acc.created).append("\n");
            }
            Files.writeString(accFile, sb.toString());
        } catch (IOException e) {
            AeonVisionMod.LOGGER.warn("Не удалось сохранить аккаунты");
        }
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int y = 50;

        // Поле ввода ника
        nicknameField = new TextFieldWidget(
            textRenderer, centerX - 100, y, 200, 22, Text.literal("Никнейм")
        );
        nicknameField.setMaxLength(16);
        nicknameField.setPlaceholder(Text.literal("Введите ник..."));
        addDrawableChild(nicknameField);

        // Кнопка добавить
        addDrawableChild(ButtonWidget.builder(
            Text.literal("➕ Создать аккаунт"),
            btn -> {
                String nick = nicknameField.getText().trim();
                if (!nick.isEmpty() && nick.length() >= 3) {
                    if (accounts.size() < 5) {
                        accounts.add(new Account(nick));
                        saveAccounts();
                        nicknameField.setText("");
                        clearAndInit();
                    } else {
                        if (MC.player != null) {
                            MC.player.sendMessage(Text.literal("§cМаксимум 5 аккаунтов!"), false);
                        }
                    }
                }
            }
        ).dimensions(centerX - 100, y + 28, 200, 22).build());

        // Список аккаунтов
        int listY = y + 65;
        for (int i = scrollOffset; i < Math.min(accounts.size(), scrollOffset + 5); i++) {
            Account acc = accounts.get(i);
            int accY = listY + (i - scrollOffset) * 30;
            
            boolean isActive = activeAccount != null && activeAccount.nickname.equals(acc.nickname);
            String label = isActive ? "§a● " : "§7○ ";
            label += acc.nickname;
            label += " §7(" + new java.text.SimpleDateFormat("dd.MM.yy").format(new Date(acc.created)) + ")";

            final Account a = acc;
            addDrawableChild(ButtonWidget.builder(
                Text.literal(label),
                btn -> switchToAccount(a)
            ).dimensions(centerX - 130, accY, 200, 22).build());

            // Кнопка удалить
            addDrawableChild(ButtonWidget.builder(
                Text.literal("✕"),
                btn -> {
                    accounts.remove(a);
                    saveAccounts();
                    clearAndInit();
                }
            ).dimensions(centerX + 75, accY, 25, 22).build());
        }

        // Скролл
        if (accounts.size() > 5) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("▲"),
                btn -> { scrollOffset = Math.max(0, scrollOffset - 3); clearAndInit(); }
            ).dimensions(centerX + 140, listY, 20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("▼"),
                btn -> { scrollOffset = Math.min(accounts.size() - 5, scrollOffset + 3); clearAndInit(); }
            ).dimensions(centerX + 140, listY + 4*30, 20, 20).build());
        }

        // Закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Готово"),
            btn -> close()
        ).dimensions(centerX - 50, height - 35, 100, 22).build());
    }

    private void switchToAccount(Account acc) {
        activeAccount = acc;
        // В реальном моде нужно пересоздать сессию
        // Это потребует реконнекта
        if (MC.player != null) {
            MC.player.sendMessage(
                Text.literal("§aÆon Vision: Переключено на " + acc.nickname + " (требуется перезаход)"), 
                false
            );
        }
        saveAccounts();
        clearAndInit();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawText(textRenderer, Text.literal("§lÆON VISION - АККАУНТЫ"),
            width/2 - textRenderer.getWidth("ÆON VISION - АККАУНТЫ")/2, 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Аккаунтов: " + accounts.size() + "/5"),
            width/2 - 30, 30, 0x80FFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
              }
