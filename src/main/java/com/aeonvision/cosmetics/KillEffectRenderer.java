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

public class KillEffectRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private final List<EffectParticle> particles = new ArrayList<>();
    
    private static class EffectParticle {
        double x,y,z,vx,vy,vz; float life,maxLife,size; float[] color;
        EffectParticle(double x,double y,double z,int type){
            this.x=x;this.y=y;this.z=z; maxLife=0.5f+(float)Math.random()*1.5f; life=maxLife;
            size=0.05f+(float)Math.random()*0.15f;
            switch(type){
                case 0->{vx=(Math.random()-0.5)*0.3; vy=0.1+Math.random()*0.3; vz=(Math.random()-0.5)*0.3; color=new float[]{(float)Math.random(),(float)Math.random(),(float)Math.random()};}
                case 1->{float an=(float)(Math.random()*Math.PI*2),sp=0.2f+(float)Math.random()*0.3f; vx=Math.cos(an)*sp; vy=(Math.random()-0.5)*0.4; vz=Math.sin(an)*sp; color=new float[]{1,0.9f,0.3f};}
                case 2->{vx=(Math.random()-0.5)*0.05; vy=0.03+Math.random()*0.08; vz=(Math.random()-0.5)*0.05; color=new float[]{0.1f,0.1f,0.1f}; size=0.2f+(float)Math.random()*0.3f; maxLife=2f+(float)Math.random();}
                case 3->{vx=(Math.random()-0.5)*0.4; vy=0.1+Math.random()*0.4; vz=(Math.random()-0.5)*0.4; color=new float[]{0.4f,0.9f,1}; size=0.08f;}
                case 4->{color=new float[]{1,1,0.3f}; maxLife=0.15f; size=0.5f;}
                case 5->{vx=(Math.random()-0.5)*0.2; vy=0.15+Math.random()*0.5; vz=(Math.random()-0.5)*0.2; color=new float[]{0.8f,0.1f,0.1f};}
            }
        }
    }

    public void trigger(PlayerEntity p, KillEffectType t){
        int n=switch(t){case CONFETTI->40; case STAR_BURST->30; case SMOKE->20; case CRYSTAL->25; case LIGHTNING->10; case BLOOD->35; default->0;};
        int ti=switch(t){case CONFETTI->0; case STAR_BURST->1; case SMOKE->2; case CRYSTAL->3; case LIGHTNING->4; case BLOOD->5; default->0;};
        for(int i=0;i<n;i++)particles.add(new EffectParticle(p.getX(),p.getY()+1,p.getZ(),ti));
    }

    public void tick(){
        Iterator<EffectParticle> it=particles.iterator();
        while(it.hasNext()){EffectParticle p=it.next(); p.life-=0.016f; p.x+=p.vx; p.y+=p.vy; p.z+=p.vz; if(p.life<=0)it.remove();}
    }

    public void render(WorldRenderContext ctx){
        if(particles.isEmpty())return;
        MatrixStack ms=ctx.matrixStack(); Camera cam=ctx.camera(); Vec3d cp=cam.getPos();
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.depthMask(false); RenderSystem.disableCull();
        for(EffectParticle p:particles){
            float a=Math.min(1,p.life/p.maxLife*2); if(a<=0)continue;
            ms.push(); ms.translate(p.x-cp.x,p.y-cp.y,p.z-cp.z);
            ms.multiply(new Quaternionf().rotateY((float)Math.toRadians(-cam.getYaw())));
            ms.multiply(new Quaternionf().rotateX((float)Math.toRadians(-cam.getPitch())));
            Matrix4f m=ms.peek().getPositionMatrix(); float s=p.size*a;
            int col=((int)(a*255)<<24)|((int)(p.color[0]*255)<<16)|((int)(p.color[1]*255)<<8)|(int)(p.color[2]*255);
            BufferBuilder buf=Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS,VertexFormats.POSITION_COLOR);
            buf.vertex(m,-s,-s,0).color(col); buf.vertex(m,-s,s,0).color(col);
            buf.vertex(m,s,s,0).color(col); buf.vertex(m,s,-s,0).color(col);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            ms.pop();
        }
        RenderSystem.depthMask(true); RenderSystem.enableCull(); RenderSystem.disableBlend();
    }
            }
