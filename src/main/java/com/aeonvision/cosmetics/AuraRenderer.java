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
import org.joml.Quaternionf;

public class AuraRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private float time = 0;
    private int auraSegments = 64;
    private float auraRadius = 0.8f;

    public void tick(PlayerEntity player, AuraType type, float[] color) {
        time += 0.016f;
    }

    public void render(WorldRenderContext context) {
        if (MC.player == null) return;
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        double px = MC.player.getX() - camPos.x;
        double py = MC.player.getY() + MC.player.getHeight() / 2 - camPos.y;
        double pz = MC.player.getZ() - camPos.z;
        
        float[] color = {0.0f, 0.8f, 1.0f};
        float alpha = 0.3f + MathHelper.sin(time * 2f) * 0.1f;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        
        matrices.push();
        matrices.translate(px, py, pz);
        matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(-camera.getYaw())));
        matrices.multiply(new Quaternionf().rotateX((float)Math.toRadians(-camera.getPitch())));
        
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Горизонтальное кольцо
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        
        for (int i = 0; i <= auraSegments; i++) {
            float angle = (float)(i * Math.PI * 2 / auraSegments);
            float cos = MathHelper.cos(angle);
            float sin = MathHelper.sin(angle);
            float r = auraRadius + MathHelper.sin(angle * 3 + time) * 0.1f;
            
            int c1 = ((int)(alpha * 255) << 24) | ((int)(color[0]*255)<<16) | ((int)(color[1]*255)<<8) | (int)(color[2]*255);
            int c2 = ((int)(alpha * 0.5f * 255) << 24) | ((int)(color[0]*255)<<16) | ((int)(color[1]*255)<<8) | (int)(color[2]*255);
            
            buffer.vertex(matrix, cos * r, -0.1f, sin * r).color(c1);
            buffer.vertex(matrix, cos * r, 0.1f, sin * r).color(c2);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        matrices.pop();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
                          }
