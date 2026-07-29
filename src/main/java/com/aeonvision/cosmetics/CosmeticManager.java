package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import java.util.*;

public class CosmeticManager {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    // Активные косметики
    private TrailType activeTrail = TrailType.NONE;
    private AuraType activeAura = AuraType.NONE;
    private WingType activeWings = WingType.NONE;
    private FootprintType activeFootprints = FootprintType.NONE;
    private KillEffectType activeKillEffect = KillEffectType.NONE;
    
    // Цвета для кастомизации
    private float[] auraColor = {0.0f, 0.8f, 1.0f}; // Дефолт: голубой
    private float[] trailColor = {1.0f, 1.0f, 1.0f}; // Дефолт: белый
    
    // Менеджеры каждого типа
    private final TrailRenderer trailRenderer = new TrailRenderer();
    private final AuraRenderer auraRenderer = new AuraRenderer();
    private final WingRenderer wingRenderer = new WingRenderer();
    private final FootprintRenderer footprintRenderer = new FootprintRenderer();
    private final KillEffectRenderer killEffectRenderer = new KillEffectRenderer();
    
    // Отслеживание убийств
    private int lastKillCount = 0;
    private long lastKillTime = 0;
    
    // Для отслеживания движения
    private double lastX, lastY, lastZ;
    private boolean wasMoving = false;
    private float moveSpeed = 0;
    private long lastFootprintTime = 0;
    
    // Флаги
    private boolean cosmeticsEnabled = true;

    public void tick(MinecraftClient client) {
        if (!cosmeticsEnabled || client.player == null) return;
        
        PlayerEntity player = client.player;
        long now = System.currentTimeMillis();
        
        // Отслеживаем движение
        double dx = player.getX() - lastX;
        double dy = player.getY() - lastY;
        double dz = player.getZ() - lastZ;
        moveSpeed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        wasMoving = moveSpeed > 0.01;
        
        // Обновляем шлейф
        if (activeTrail != TrailType.NONE && wasMoving) {
            trailRenderer.addParticle(player, moveSpeed, activeTrail, trailColor);
        }
        trailRenderer.tick();
        
        // Обновляем ауру
        if (activeAura != AuraType.NONE) {
            auraRenderer.tick(player, activeAura, auraColor);
        }
        
        // Обновляем крылья
        if (activeWings != WingType.NONE) {
            wingRenderer.tick(player, activeWings, moveSpeed);
        }
        
        // Следы шагов
        if (activeFootprints != FootprintType.NONE && player.isOnGround() && wasMoving) {
            if (now - lastFootprintTime > 300) { // Каждые 300мс
                footprintRenderer.addFootprint(player, activeFootprints);
                lastFootprintTime = now;
            }
        }
        footprintRenderer.tick();
        
        // Отслеживаем убийства
        int currentKills = player.getStatHandler().getStat(
            net.minecraft.stat.Stats.CUSTOM.getOrCreateStat(net.minecraft.stat.Stats.MOB_KILLS)
        );
        if (currentKills > lastKillCount) {
            lastKillCount = currentKills;
            lastKillTime = now;
            if (activeKillEffect != KillEffectType.NONE) {
                killEffectRenderer.trigger(player, activeKillEffect);
            }
        }
        killEffectRenderer.tick();
        
        // Обновляем позицию
        lastX = player.getX();
        lastY = player.getY();
        lastZ = player.getZ();
    }

    public void renderWorld(WorldRenderContext context) {
        if (!cosmeticsEnabled) return;
        
        // Рендерим шлейф
        trailRenderer.render(context);
        
        // Рендерим ауру
        auraRenderer.render(context);
        
        // Рендерим крылья
        wingRenderer.render(context);
        
        // Рендерим следы
        footprintRenderer.render(context);
        
        // Рендерим эффекты убийств
        killEffectRenderer.render(context);
    }

    // Геттеры и сеттеры
    public void setTrail(TrailType type) { this.activeTrail = type; }
    public void setAura(AuraType type) { this.activeAura = type; }
    public void setWings(WingType type) { this.activeWings = type; }
    public void setFootprints(FootprintType type) { this.activeFootprints = type; }
    public void setKillEffect(KillEffectType type) { this.activeKillEffect = type; }
    
    public TrailType getTrail() { return activeTrail; }
    public AuraType getAura() { return activeAura; }
    public WingType getWings() { return activeWings; }
    public FootprintType getFootprints() { return activeFootprints; }
    public KillEffectType getKillEffect() { return activeKillEffect; }
    
    public void setAuraColor(float r, float g, float b) {
        auraColor[0] = r;
        auraColor[1] = g;
        auraColor[2] = b;
    }
    public float[] getAuraColor() { return auraColor; }
    
    public void setTrailColor(float r, float g, float b) {
        trailColor[0] = r;
        trailColor[1] = g;
        trailColor[2] = b;
    }
    
    public void toggleCosmetics() { cosmeticsEnabled = !cosmeticsEnabled; }
    public boolean isEnabled() { return cosmeticsEnabled; }
    
    // Переключение конкретных типов
    public void nextTrail() {
        TrailType[] values = TrailType.values();
        int next = (activeTrail.ordinal() + 1) % values.length;
        activeTrail = values[next];
    }
    public void nextAura() {
        AuraType[] values = AuraType.values();
        int next = (activeAura.ordinal() + 1) % values.length;
        activeAura = values[next];
    }
    public void nextWings() {
        WingType[] values = WingType.values();
        int next = (activeWings.ordinal() + 1) % values.length;
        activeWings = values[next];
    }
    public void nextFootprints() {
        FootprintType[] values = FootprintType.values();
        int next = (activeFootprints.ordinal() + 1) % values.length;
        activeFootprints = values[next];
    }
    public void nextKillEffect() {
        KillEffectType[] values = KillEffectType.values();
        int next = (activeKillEffect.ordinal() + 1) % values.length;
        activeKillEffect = values[next];
    }
        }
