package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import java.util.*;

public class KillEffectRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class EffectParticle {
        double x, y, z;
        double vx, vy, vz;
        float life;
        float maxLife;
        float size;
        float[] color;
        int type; // 0=конфетти, 1=звезда, 2=дым, 3=кристалл, 4=молния, 5=кровь
        
        EffectParticle(double x, double y, double z, int type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
            this.maxLife = 0.5f + (float)Math.random() * 1.5f;
            this.life = maxLife;
            this.size = 0.05f + (float)Math.random() * 0.15f;
            
            // Скорость зависит от типа
            switch(type) {
                case 0: // Конфетти
                    vx = (Math.random() - 0.5) * 0.3;
                    vy = 0.1 + Math.random() * 0.3;
                    vz = (Math.random() - 0.5) * 0.3;
                    color = new float[]{(float)Math.random(), (float)Math.random(), (float)Math.random()};
                    break;
                case 1: // Звёздный взрыв
                    float angle = (float)(Math.random() * Math.PI * 2);
                    float speed = 0.2f + (float)Math.random() * 0.3f;
                    vx = Math.cos(angle) * speed;
                    vy = (Math.random() - 0.5) * 0.4;
                    vz = Math.sin(angle) * speed;
                    color = new float[]{1f, 0.9f, 0.3f};
                    break;
                case 2: // Дым
                    vx = (Math.random() - 0.5) * 0.05;
                    vy = 0.03 + Math.random() * 0.08;
                    vz = (Math.random() - 0.5) * 0.05;
                    color = new float[]{0.1f, 0.1f, 0.1f};
                    size = 0.2f + (float)Math.random() * 0.3f;
                    maxLife = 2f + (float)Math.random();
                    break;
                case 3: // Кристаллы
                    vx = (Math.random() - 0.5) * 0.4;
                    vy = 0.1 + Math.random() * 0.4;
                    vz = (Math.random() - 0.5) * 0.4;
                    color = new float[]{0.4f, 0.9f, 1f};
                    size = 0.08f;
                    break;
                case 4: // Молния
                    vx = 0; vy = 0; vz = 0;
                    color = new float[]{1f, 1f, 0.3f};
                    maxLife = 0.15f;
                    size = 0.5f;
                    break;
                case 5: // Кровь
                    vx = (Math.random() - 0.5) * 0.2;
                    vy = 0.15 + Math.random() * 0.5;
                    vz = (Math.random() - 0.5) * 0.2;
                    color = new float[]{0.8f, 0.1f, 0.1f};
                    break;
                default:
                    vx = vy = vz = 0;
                    color = new float[]{1f, 1f, 1f};
            }
        }
    }
    
    private final List<EffectParticle> particles = new ArrayList<>();
    private double effectX, effectY, effectZ;
    private boolean hasActiveEffect = false;
    
    public void trigger(PlayerEntity player, KillEffectType type) {
        double x = player.getX();
        double y = player.getY() + 1;
        double z = player.getZ();
        
        effectX = x;
        effectY = y;
        effectZ = z;
        hasActiveEffect = true;
        
        int particleCount = switch(type) {
            case CONFETTI -> 40;
            case STAR_BURST -> 30;
            case SMOKE -> 20;
            case CRYSTAL -> 25;
            case LIGHTNING -> 10;
            case BLOOD -> 35;
            default -> 0;
        };
        
        int typeIndex = switch(type) {
            case CONFETTI -> 0;
            case STAR_BURST -> 1;
            case SMOKE -> 2;
            case CRYSTAL -> 3;
            case LIGHTNING -> 4;
            case BLOOD -> 5;
            default -> 0;
        };
        
        for (int i = 0; i < particleCount; i++) {
            particles.add(new EffectParticle(x, y, z, typeIndex));
        }
    }
    
    public void tick() {
        Iterator<EffectParticle> it = particles.iterator();
        while (it.hasNext()) {
            EffectParticle p = it.next();
            p.life -= 0.016f;
            p.x += p.vx;
            p.y += p.vy;
            p.z += p.vz;
            
            // Гравитация для некоторых типов
            if (p.type == 0 || p.type == 5) {
                p.vy -= 0.005;
            }
            if (p.type == 2) {
                p.size += 0.002f; // Дым расширяется
            }
            
            if (p.life <= 0) it.remove();
        }
        if (particles.isEmpty()) hasActiveEffect = false;
    }
    
    public void render(WorldRenderContext context) {
        if (!hasActiveEffect || particles.isEmpty()) return;
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        for (EffectParticle p : particles) {
            float alpha = Math.min(1f, p.life / p.maxLife * 2);
            if (alpha <= 0) continue;
            
            double dx = p.x - camPos.x;
            double dy = p.y - camPos.y;
            double dz = p.z - camPos.z;
            
            matrices.push();
            matrices.translate(dx, dy, dz);
            
            // Billboard
            matrices.multiply(camera.getRotation());
            
            Matrix4f mat = matrices.peek().getPositionMatrix();
            float s = p.size * alpha;
            float r = p.color[0];
            float g = p.color[1];
            float b = p.color[2];
            
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(mat, -s, -s, 0).color(r, g, b, alpha).next();
            buffer.vertex(mat, -s, s, 0).color(r, g, b, alpha).next();
            buffer.vertex(mat, s, s, 0).color(r, g, b, alpha).next();
            buffer.vertex(mat, s, -s, 0).color(r, g, b, alpha).next();
            tessellator.draw();
            
            matrices.pop();
        }
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
    }
