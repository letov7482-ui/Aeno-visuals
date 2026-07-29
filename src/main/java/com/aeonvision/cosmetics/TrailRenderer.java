package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import java.util.*;

public class TrailRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class TrailParticle {
        double x, y, z;
        float size;
        float life;
        float maxLife;
        float[] color;
        TrailType type;
        float velocityY;
        float randomOffset;
        
        TrailParticle(double x, double y, double z, TrailType type, float[] color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
            this.color = color.clone();
            this.maxLife = 1.0f + (float)Math.random() * 0.5f;
            this.life = maxLife;
            this.size = 0.1f + (float)Math.random() * 0.15f;
            this.velocityY = 0.02f + (float)Math.random() * 0.05f;
            this.randomOffset = (float)Math.random() * 0.3f;
        }
    }
    
    private final List<TrailParticle> particles = new ArrayList<>();
    private static final int MAX_PARTICLES = 150;
    private long lastSpawnTime = 0;
    
    public void addParticle(PlayerEntity player, float speed, TrailType type, float[] color) {
        long now = System.currentTimeMillis();
        int spawnRate = switch(type) {
            case FIRE -> 40;
            case RAINBOW -> 30;
            case ELECTRIC -> 60;
            default -> 50;
        };
        
        if (now - lastSpawnTime < spawnRate) return;
        lastSpawnTime = now;
        
        if (particles.size() >= MAX_PARTICLES) {
            particles.remove(0);
        }
        
        // Спавним позади игрока
        float yaw = (float)Math.toRadians(-player.getYaw());
        double behindX = -Math.sin(yaw) * 0.3;
        double behindZ = Math.cos(yaw) * 0.3;
        
        TrailParticle p = new TrailParticle(
            player.getX() + behindX + (Math.random() - 0.5) * 0.4,
            player.getY() + 0.1 + Math.random() * 0.5,
            player.getZ() + behindZ + (Math.random() - 0.5) * 0.4,
            type, color
        );
        
        // Настройки под тип
        switch(type) {
            case FIRE:
                p.velocityY = 0.05f + (float)Math.random() * 0.1f;
                p.size = 0.15f + (float)Math.random() * 0.2f;
                p.maxLife = 0.5f + (float)Math.random() * 0.3f;
                p.color = new float[]{1.0f, 0.5f + (float)Math.random() * 0.3f, 0.0f};
                break;
            case SAKURA:
                p.velocityY = -0.01f - (float)Math.random() * 0.02f;
                p.size = 0.08f;
                p.maxLife = 2.0f + (float)Math.random();
                p.color = new float[]{1.0f, 0.7f, 0.85f};
                break;
            case ELECTRIC:
                p.maxLife = 0.2f;
                p.size = 0.05f;
                p.color = new float[]{0.4f, 0.8f, 1.0f};
                break;
            case ENDER:
                p.color = new float[]{0.6f, 0.2f, 1.0f};
                p.maxLife = 0.8f;
                break;
            case AQUA:
                p.velocityY = -0.03f;
                p.color = new float[]{0.2f, 0.6f, 1.0f};
                break;
            case SCULK:
                p.color = new float[]{0.1f, 0.3f, 0.2f};
                p.maxLife = 1.5f;
                p.size = 0.2f;
                break;
        }
        
        particles.add(p);
    }
    
    public void tick() {
        Iterator<TrailParticle> it = particles.iterator();
        while (it.hasNext()) {
            TrailParticle p = it.next();
            p.life -= 0.016f;
            p.y += p.velocityY;
            
            if (p.type == TrailType.SAKURA) {
                p.x += Math.sin(p.life * 5 + p.randomOffset) * 0.02;
                p.z += Math.cos(p.life * 5 + p.randomOffset) * 0.02;
            }
            
            if (p.type == TrailType.ELECTRIC) {
                p.x += (Math.random() - 0.5) * 0.3;
                p.z += (Math.random() - 0.5) * 0.3;
            }
            
            if (p.life <= 0) it.remove();
        }
    }
    
    public void render(WorldRenderContext context) {
        if (particles.isEmpty()) return;
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        for (TrailParticle p : particles) {
            float alpha = p.life / p.maxLife;
            if (alpha <= 0) continue;
            
            float r = p.color[0];
            float g = p.color[1];
            float b = p.color[2];
            
            // Радужный меняет цвет
            if (p.type == TrailType.RAINBOW) {
                float hue = (System.currentTimeMillis() % 3000) / 3000f + p.randomOffset;
                int rgb = java.awt.Color.HSBtoRGB(hue % 1f, 1f, 1f);
                r = ((rgb >> 16) & 0xFF) / 255f;
                g = ((rgb >> 8) & 0xFF) / 255f;
                b = (rgb & 0xFF) / 255f;
            }
            
            // Позиция относительно камеры
            double dx = p.x - camPos.x;
            double dy = p.y - camPos.y;
            double dz = p.z - camPos.z;
            
            matrices.push();
            matrices.translate(dx, dy, dz);
            
            // Billboard: поворачиваем к камере
            matrices.multiply(camera.getRotation());
            
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float size = p.size * alpha;
            
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            
            buffer.vertex(matrix, -size, -size, 0).color(r, g, b, alpha).next();
            buffer.vertex(matrix, -size, size, 0).color(r, g, b, alpha).next();
            buffer.vertex(matrix, size, size, 0).color(r, g, b, alpha).next();
            buffer.vertex(matrix, size, -size, 0).color(r, g, b, alpha).next();
            
            tessellator.draw();
            
            matrices.pop();
        }
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
                  }
