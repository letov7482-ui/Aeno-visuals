package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.stat.Stats;

public class CosmeticManager {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private TrailType activeTrail = TrailType.NONE;
    private AuraType activeAura = AuraType.NONE;
    private WingType activeWings = WingType.NONE;
    private FootprintType activeFootprints = FootprintType.NONE;
    private KillEffectType activeKillEffect = KillEffectType.NONE;
    
    private float[] auraColor = {0.0f, 0.8f, 1.0f};
    private float[] trailColor = {1.0f, 1.0f, 1.0f};
    
    private final TrailRenderer trailRenderer = new TrailRenderer();
    private final AuraRenderer auraRenderer = new AuraRenderer();
    private final WingRenderer wingRenderer = new WingRenderer();
    private final FootprintRenderer footprintRenderer = new FootprintRenderer();
    private final KillEffectRenderer killEffectRenderer = new KillEffectRenderer();
    
    private int lastKillCount = 0;
    private double lastX, lastY, lastZ;
    private float moveSpeed = 0;
    private long lastFootprintTime = 0;
    private boolean cosmeticsEnabled = true;

    public void tick(MinecraftClient client) {
        if (!cosmeticsEnabled || client.player == null) return;
        
        PlayerEntity player = client.player;
        long now = System.currentTimeMillis();
        
        double dx = player.getX() - lastX;
        double dy = player.getY() - lastY;
        double dz = player.getZ() - lastZ;
        moveSpeed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        boolean wasMoving = moveSpeed > 0.01;
        
        if (activeTrail != TrailType.NONE && wasMoving)
            trailRenderer.addParticle(player, moveSpeed, activeTrail, trailColor);
        trailRenderer.tick();
        
        if (activeAura != AuraType.NONE)
            auraRenderer.tick(player, activeAura, auraColor);
        
        if (activeWings != WingType.NONE)
            wingRenderer.tick(player, activeWings, moveSpeed);
        
        if (activeFootprints != FootprintType.NONE && player.isOnGround() && wasMoving) {
            if (now - lastFootprintTime > 300) {
                footprintRenderer.addFootprint(player, activeFootprints);
                lastFootprintTime = now;
            }
        }
        footprintRenderer.tick();
        
        int currentKills = MC.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.MOB_KILLS));
        if (currentKills > lastKillCount) {
            lastKillCount = currentKills;
            if (activeKillEffect != KillEffectType.NONE)
                killEffectRenderer.trigger(player, activeKillEffect);
        }
        killEffectRenderer.tick();
        
        lastX = player.getX();
        lastY = player.getY();
        lastZ = player.getZ();
    }

    public void renderWorld(WorldRenderContext context) {
        if (!cosmeticsEnabled) return;
        trailRenderer.render(context);
        auraRenderer.render(context);
        wingRenderer.render(context);
        footprintRenderer.render(context);
        killEffectRenderer.render(context);
    }

    // ===== СЕТТЕРЫ =====
    public void setTrail(TrailType t) { activeTrail = t; }
    public void setAura(AuraType t) { activeAura = t; }
    public void setWings(WingType t) { activeWings = t; }
    public void setFootprints(FootprintType t) { activeFootprints = t; }
    public void setKillEffect(KillEffectType t) { activeKillEffect = t; }
    public void setAuraColor(float r, float g, float b) { auraColor = new float[]{r,g,b}; }
    public void setTrailColor(float r, float g, float b) { trailColor = new float[]{r,g,b}; }
    
    // ===== ГЕТТЕРЫ =====
    public TrailType getTrail() { return activeTrail; }
    public AuraType getAura() { return activeAura; }
    public WingType getWings() { return activeWings; }
    public FootprintType getFootprints() { return activeFootprints; }
    public KillEffectType getKillEffect() { return activeKillEffect; }
    public float[] getAuraColor() { return auraColor; }
    
    // ===== ПЕРЕКЛЮЧЕНИЕ ПО КРУГУ =====
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
    
    public void toggleCosmetics() { cosmeticsEnabled = !cosmeticsEnabled; }
    public boolean isEnabled() { return cosmeticsEnabled; }
    }
