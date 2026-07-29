package com.aeonvision.cosmetics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import java.util.*;

public class FootprintRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class Footprint {
        double x,y,z; float yaw,life,maxLife; FootprintType type;
        Footprint(double x,double y,double z,float yaw,FootprintType t){
            this.x=x;this.y=y;this.z=z;this.yaw=yaw;this.type=t;
            this.maxLife=t==FootprintType.FIRE?2f:t==FootprintType.ICE?6f:4f;
            this.life=maxLife;
        }
    }
    
    private final List<Footprint> footprints = new ArrayList<>();
    private BlockPos lastPos = BlockPos.ORIGIN;

    public void addFootprint(PlayerEntity p, FootprintType t){
        BlockPos cp=p.getBlockPos(); if(cp.equals(lastPos))return; lastPos=cp;
        if(footprints.size()>=30)footprints.remove(0);
        footprints.add(new Footprint(p.getX(),Math.floor(p.getY())+0.01,p.getZ(),p.getYaw(),t));
    }

    public void tick(){
        Iterator<Footprint> it=footprints.iterator();
        while(it.hasNext()){Footprint f=it.next(); f.life-=0.016f; if(f.life<=0)it.remove();}
    }

    public void render(WorldRenderContext ctx){
        if(footprints.isEmpty())return;
        MatrixStack ms=ctx.matrixStack(); Camera cam=ctx.camera(); Vec3d cp=cam.getPos();
        RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.depthMask(false);
        for(Footprint f:footprints){
            float a=f.life/f.maxLife; if(a<=0)continue;
            ms.push(); ms.translate(f.x-cp.x,f.y-cp.y,f.z-cp.z);
            ms.multiply(new Quaternionf().rotateY((float)Math.toRadians(-f.yaw+90)));
            Matrix4f m=ms.peek().getPositionMatrix();
            float[] c=getColor(f.type); float s=0.2f;
            int c1=rgba(c[0],c[1],c[2],a), c2=rgba(c[0],c[1],c[2],a*0.5f);
            BufferBuilder buf=Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS,VertexFormats.POSITION_COLOR);
            buf.vertex(m,-s,0,-s*1.5f).color(c1); buf.vertex(m,-s*0.5f,0,-s*1.5f).color(c1);
            buf.vertex(m,-s*0.5f,0,s*0.5f).color(c2); buf.vertex(m,-s,0,s*0.5f).color(c2);
            buf.vertex(m,s*0.5f,0,-s*1.5f).color(c1); buf.vertex(m,s,0,-s*1.5f).color(c1);
            buf.vertex(m,s,0,s*0.5f).color(c2); buf.vertex(m,s*0.5f,0,s*0.5f).color(c2);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            ms.pop();
        }
        RenderSystem.depthMask(true); RenderSystem.disableBlend();
    }

    private float[] getColor(FootprintType t){return switch(t){case GLOWING->new float[]{1,1,0.9f};case FIRE->new float[]{1,0.5f,0.1f};case ICE->new float[]{0.5f,0.8f,1};case GHOST->new float[]{0.7f,0.7f,0.9f};case RUNIC->new float[]{0.6f,0.2f,1};default->new float[]{1,1,1};};}
    private int rgba(float r,float g,float b,float a){return((int)(a*255)<<24)|((int)(r*255)<<16)|((int)(g*255)<<8)|(int)(b*255);}
                }
