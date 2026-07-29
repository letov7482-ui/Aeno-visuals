package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import java.util.*;

public class TrailRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class TrailParticle {
        double x, y, z; float size, life, maxLife, velocityY, randomOffset;
        float[] color; TrailType type;
        TrailParticle(double x, double y, double z, TrailType t, float[] c) {
            this.x=x; this.y=y; this.z=z; this.type=t; this.color=c.clone();
            this.maxLife=1f+(float)Math.random()*0.5f; this.life=maxLife;
            this.size=0.1f+(float)Math.random()*0.15f;
            this.velocityY=0.02f+(float)Math.random()*0.05f;
            this.randomOffset=(float)Math.random()*0.3f;
        }
    }
    
    private final List<TrailParticle> particles = new ArrayList<>();
    private long lastSpawn = 0;

    public void addParticle(PlayerEntity p, float s, TrailType t, float[] c) {
        long n = System.currentTimeMillis();
        int rate = switch(t){case FIRE->40; case RAINBOW->30; case ELECTRIC->60; default->50;};
        if(n-lastSpawn<rate)return; lastSpawn=n;
        if(particles.size()>=150)particles.remove(0);
        float yaw=(float)Math.toRadians(-p.getYaw());
        double bx=-Math.sin(yaw)*0.3, bz=Math.cos(yaw)*0.3;
        TrailParticle tp=new TrailParticle(
            p.getX()+bx+(Math.random()-0.5)*0.4,
            p.getY()+0.1+Math.random()*0.5,
            p.getZ()+bz+(Math.random()-0.5)*0.4,t,c);
        switch(t){
            case FIRE->{tp.velocityY=0.1f; tp.color=new float[]{1,0.6f,0};}
            case SAKURA->{tp.velocityY=-0.02f; tp.size=0.08f; tp.maxLife=2.5f; tp.color=new float[]{1,0.7f,0.85f};}
            case ELECTRIC->{tp.maxLife=0.2f; tp.size=0.05f; tp.color=new float[]{0.4f,0.8f,1};}
            case ENDER->{tp.color=new float[]{0.6f,0.2f,1};}
            case AQUA->{tp.velocityY=-0.03f; tp.color=new float[]{0.2f,0.6f,1};}
            case SCULK->{tp.color=new float[]{0.1f,0.3f,0.2f}; tp.maxLife=1.5f; tp.size=0.2f;}
            default->{}
        }
        particles.add(tp);
    }

    public void tick() {
        Iterator<TrailParticle> it = particles.iterator();
        while(it.hasNext()){
            TrailParticle p=it.next();
            p.life-=0.016f; p.y+=p.velocityY;
            if(p.type==TrailType.SAKURA){p.x+=Math.sin(p.life*5+p.randomOffset)*0.02; p.z+=Math.cos(p.life*5+p.randomOffset)*0.02;}
            if(p.type==TrailType.ELECTRIC){p.x+=(Math.random()-0.5)*0.3; p.z+=(Math.random()-0.5)*0.3;}
            if(p.life<=0)it.remove();
        }
    }

    public void render(WorldRenderContext ctx) {
        if(particles.isEmpty())return;
        MatrixStack ms = ctx.matrixStack();
        Camera cam = ctx.camera();
        Vec3d cp = cam.getPos();
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.depthMask(false); RenderSystem.disableCull();
        for(TrailParticle p:particles){
            float a=p.life/p.maxLife; if(a<=0)continue;
            float r=p.color[0],g=p.color[1],b=p.color[2];
            if(p.type==TrailType.RAINBOW){
                int rgb=java.awt.Color.HSBtoRGB((System.currentTimeMillis()%3000)/3000f+p.randomOffset,1,1);
                r=((rgb>>16)&255)/255f; g=((rgb>>8)&255)/255f; b=(rgb&255)/255f;
            }
            ms.push();
            ms.translate(p.x-cp.x, p.y-cp.y, p.z-cp.z);
            ms.multiply(new Quaternionf().rotateY((float)Math.toRadians(-cam.getYaw())));
            ms.multiply(new Quaternionf().rotateX((float)Math.toRadians(-cam.getPitch())));
            Matrix4f m = ms.peek().getPositionMatrix();
            float s=p.size*a;
            int col=((int)(a*255)<<24)|((int)(r*255)<<16)|((int)(g*255)<<8)|(int)(b*255);
            BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buf.vertex(m,-s,-s,0).color(col); buf.vertex(m,-s,s,0).color(col);
            buf.vertex(m,s,s,0).color(col); buf.vertex(m,s,-s,0).color(col);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            ms.pop();
        }
        RenderSystem.depthMask(true); RenderSystem.enableCull(); RenderSystem.disableBlend();
    }
        }
