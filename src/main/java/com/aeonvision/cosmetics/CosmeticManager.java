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
    
    private final TrailRenderer trailRenderer = new TrailRenderer();
    private final AuraRenderer auraRenderer = new AuraRenderer();
    private final WingRenderer wingRenderer = new WingRenderer();
    private final FootprintRenderer footprintRenderer = new FootprintRenderer();
    private final KillEffectRenderer killEffectRenderer = new KillEffectRenderer();
    public final HitParticleRenderer hitRenderer = new HitParticleRenderer();
    
    private int lastKillCount = 0;
    private double lastX, lastY, lastZ;
    private long lastFootprintTime = 0;

    public void tick(MinecraftClient client) {
        if (client.player == null) return;
        PlayerEntity player = client.player;
        long now = System.currentTimeMillis();
        
        double dx = player.getX() - lastX, dy = player.getY() - lastY, dz = player.getZ() - lastZ;
        float speed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        boolean moving = speed > 0.01;
        
        if (activeTrail != TrailType.NONE && moving) trailRenderer.addParticle(player, speed, activeTrail, new float[]{1,1,1});
        trailRenderer.tick();
        if (activeAura != AuraType.NONE) auraRenderer.tick(player, activeAura, new float[]{0,0.8f,1});
        if (activeWings != WingType.NONE) wingRenderer.tick(player, activeWings, speed);
        if (activeFootprints != FootprintType.NONE && player.isOnGround() && moving && now - lastFootprintTime > 300) {
            footprintRenderer.addFootprint(player, activeFootprints);
            lastFootprintTime = now;
        }
        footprintRenderer.tick();
        hitRenderer.tick();
        killEffectRenderer.tick();
        
        lastX = player.getX(); lastY = player.getY(); lastZ = player.getZ();
    }

    public void renderWorld(WorldRenderContext ctx) {
        trailRenderer.render(ctx);
        auraRenderer.render(ctx);
        wingRenderer.render(ctx);
        footprintRenderer.render(ctx);
        hitRenderer.render(ctx);
        killEffectRenderer.render(ctx);
    }

    public void setTrail(TrailType t) { activeTrail = t; }
    public void setAura(AuraType t) { activeAura = t; }
    public void setWings(WingType t) { activeWings = t; }
    public TrailType getTrail() { return activeTrail; }
    public AuraType getAura() { return activeAura; }
    public WingType getWings() { return activeWings; }
    public FootprintType getFootprints() { return activeFootprints; }
    public KillEffectType getKillEffect() { return activeKillEffect; }

    public void nextTrail() { TrailType[] v = TrailType.values(); activeTrail = v[(activeTrail.ordinal()+1)%v.length]; }
    public void nextAura() { AuraType[] v = AuraType.values(); activeAura = v[(activeAura.ordinal()+1)%v.length]; }
    public void nextWings() { WingType[] v = WingType.values(); activeWings = v[(activeWings.ordinal()+1)%v.length]; }
    public void nextFootprints() { FootprintType[] v = FootprintType.values(); activeFootprints = v[(activeFootprints.ordinal()+1)%v.length]; }
    public void nextKillEffect() { KillEffectType[] v = KillEffectType.values(); activeKillEffect = v[(activeKillEffect.ordinal()+1)%v.length]; }
        }
