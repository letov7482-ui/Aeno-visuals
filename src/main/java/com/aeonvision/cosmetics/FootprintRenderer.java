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
import java.util.*;

public class FootprintRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class Footprint {
        double x, y, z;
        float yaw;
        float life;
        float maxLife;
        FootprintType type;
        
        Footprint(double x, double y, double z, float yaw, FootprintType type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.type = type;
            this.maxLife = type == FootprintType.FIRE ? 2f : 
                           type == FootprintType.ICE ? 6f : 4f;
            this.life = maxLife;
        }
    }
    
    private final List<Footprint> footprints = new ArrayList<>();
    private static final int MAX_FOOTPRINTS = 30;
    private BlockPos lastFootprintBlock = BlockPos.ORIGIN;
    
    public void addFootprint(PlayerEntity player, FootprintType type) {
        BlockPos currentBlock = player.getBlockPos();
        if (currentBlock.equals(lastFootprintBlock)) return;
        lastFootprintBlock = currentBlock;
        
        if (footprints.size() >= MAX_FOOTPRINTS) {
            footprints.remove(0);
        }
        
        // Позиция на земле
        double x = player.getX();
        double y = Math.floor(player.getY()) + 0.01;
        double z = player.getZ();
        
        footprints.add(new Footprint(x, y, z, player.getYaw(), type));
    }
    
    public void tick() {
        Iterator<Footprint> it = footprints.iterator();
        while (it.hasNext()) {
            Footprint fp = it.next();
            fp.life -= 0.016f;
            if (fp.life <= 0) it.remove();
        }
    }
    
    public void render(WorldRenderContext context) {
        if (footprints.isEmpty()) return;
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        for (Footprint fp : footprints) {
            float alpha = fp.life / fp.maxLife;
            if (alpha <= 0) continue;
            
            double dx = fp.x - camPos.x;
            double dy = fp.y - camPos.y;
            double dz = fp.z - camPos.z;
            
            matrices.push();
            matrices.translate(dx, dy, dz);
            matrices.multiply(new org.joml.Quaternionf().rotateY((float)Math.toRadians(-fp.yaw + 90)));
            
            Matrix4f mat = matrices.peek().getPositionMatrix();
            
            float[] color = getColorForType(fp.type);
            float size = 0.2f;
            
            // Рисуем отпечаток (форма подошвы)
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            
            // Левая сторона
            buffer.vertex(mat, -size, 0, -size * 1.5f).color(color[0], color[1], color[2], alpha).next();
            buffer.vertex(mat, -size * 0.5f, 0, -size * 1.5f).color(color[0], color[1], color[2], alpha * 0.7f).next();
            buffer.vertex(mat, -size * 0.5f, 0, size * 0.5f).color(color[0], color[1], color[2], alpha * 0.3f).next();
            buffer.vertex(mat, -size, 0, size * 0.5f).color(color[0], color[1], color[2], alpha * 0.5f).next();
            
            // Правая сторона
            buffer.vertex(mat, size * 0.5f, 0, -size * 1.5f).color(color[0], color[1], color[2], alpha * 0.7f).next();
            buffer.vertex(mat, size, 0, -size * 1.5f).color(color[0], color[1], color[2], alpha).next();
            buffer.vertex(mat, size, 0, size * 0.5f).color(color[0], color[1], color[2], alpha * 0.5f).next();
            buffer.vertex(mat, size * 0.5f, 0, size * 0.5f).color(color[0], color[1], color[2], alpha * 0.3f).next();
            
            tessellator.draw();
            
            matrices.pop();
        }
        
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    
    private float[] getColorForType(FootprintType type) {
        return switch(type) {
            case GLOWING -> new float[]{1f, 1f, 0.9f};
            case FIRE -> new float[]{1f, 0.5f, 0.1f};
            case ICE -> new float[]{0.5f, 0.8f, 1f};
            case GHOST -> new float[]{0.7f, 0.7f, 0.9f};
            case RUNIC -> new float[]{0.6f, 0.2f, 1f};
            default -> new float[]{1f, 1f, 1f};
        };
    }
                          }
