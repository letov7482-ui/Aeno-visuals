package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import java.util.*;

public class HitParticleRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class HitParticle {
        double x, y, z, vx, vy, vz;
        float life, maxLife, size;
        
        HitParticle(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
            float angle = (float)(Math.random() * Math.PI * 2);
            float speed = 0.1f + (float)Math.random() * 0.3f;
            this.vx = Math.cos(angle) * speed;
            this.vy = 0.1f + (float)Math.random() * 0.4f;
            this.vz = Math.sin(angle) * speed;
            this.maxLife = 0.5f + (float)Math.random() * 1f;
            this.life = maxLife;
            this.size = 0.08f + (float)Math.random() * 0.15f;
        }
    }
    
    private final List<HitParticle> particles = new ArrayList<>();
    
    public void spawn(double x, double y, double z) {
        for (int i = 0; i < 25; i++) {
            particles.add(new HitParticle(x, y + 1, z));
        }
    }
    
    public void tick() {
        Iterator<HitParticle> it = particles.iterator();
        while (it.hasNext()) {
            HitParticle p = it.next();
            p.life -= 0.016f;
            p.x += p.vx; p.y += p.vy; p.z += p.vz;
            p.vy -= 0.005f;
            if (p.life <= 0) it.remove();
        }
    }
    
    public void render(WorldRenderContext ctx) {
        if (particles.isEmpty()) return;
        
        MatrixStack ms = ctx.matrixStack();
        Camera cam = ctx.camera();
        Vec3d cp = cam.getPos();
        
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        
        for (HitParticle p : particles) {
            float alpha = p.life / p.maxLife;
            if (alpha <= 0) continue;
            
            ms.push();
            ms.translate(p.x - cp.x, p.y - cp.y, p.z - cp.z);
            ms.multiply(new Quaternionf().rotateY((float)Math.toRadians(-cam.getYaw())));
            ms.multiply(new Quaternionf().rotateX((float)Math.toRadians(-cam.getPitch())));
            
            Matrix4f m = ms.peek().getPositionMatrix();
            float s = p.size * alpha;
            
            // Яркая жёлто-золотая звезда
            float r = 1f, g = 0.9f, b = 0.2f;
            int col = ((int)(alpha*255)<<24) | ((int)(r*255)<<16) | ((int)(g*255)<<8) | (int)(b*255);
            
            BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buf.vertex(m, -s, -s, 0).color(col);
            buf.vertex(m, -s, s, 0).color(col);
            buf.vertex(m, s, s, 0).color(col);
            buf.vertex(m, s, -s, 0).color(col);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            
            ms.pop();
        }
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
          }
