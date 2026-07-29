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

public class WingRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private float flapTime = 0;
    private float flapSpeed = 0;
    private float targetFlapSpeed = 0;

    public void tick(PlayerEntity player, WingType type, float moveSpeed) {
        targetFlapSpeed = moveSpeed > 0.1f ? 0.15f : 0.04f;
        flapSpeed += (targetFlapSpeed - flapSpeed) * 0.1f;
        flapTime += flapSpeed;
    }

    public void render(WorldRenderContext context) {
        if (MC.player == null) return;
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        double px = MC.player.getX() - camPos.x;
        double py = MC.player.getY() + 1.2 - camPos.y;
        double pz = MC.player.getZ() - camPos.z;
        
        float bodyYaw = (float)Math.toRadians(-MC.player.interpolatedYaw + 180);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        matrices.push();
        matrices.translate(px, py, pz);
        matrices.multiply(new org.joml.Quaternionf().rotateY(bodyYaw));
        
        // Анимация взмаха
        float flap = MathHelper.sin(flapTime) * 30f;
        
        // Рисуем правое и левое крыло
        drawWing(matrices, buffer, tessellator, true, flap);
        drawWing(matrices, buffer, tessellator, false, flap);
        
        matrices.pop();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private void drawWing(MatrixStack matrices, BufferBuilder buffer, Tessellator tessellator, 
                         boolean isRight, float flap) {
        matrices.push();
        
        float dir = isRight ? 1 : -1;
        matrices.multiply(new org.joml.Quaternionf().rotateZ((float)Math.toRadians(flap * dir)));
        matrices.multiply(new org.joml.Quaternionf().rotateY((float)Math.toRadians(25 * dir)));
        
        Matrix4f mat = matrices.peek().getPositionMatrix();
        
        float wingLength = 1.6f;
        float alpha = 0.65f;
        float[] color = {0.95f, 0.95f, 1.0f}; // Ангельские (дефолт)
        
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        
        // Верхняя часть крыла
        buffer.vertex(mat, 0, 0.1f, 0).color(color[0], color[1], color[2], alpha).next();
        buffer.vertex(mat, -wingLength * dir, -0.2f, -0.4f * dir).color(color[0], color[1], color[2], alpha * 0.4f).next();
        buffer.vertex(mat, -wingLength * dir, 0.6f, -0.1f * dir).color(color[0], color[1], color[2], alpha * 0.7f).next();
        
        // Средняя часть
        buffer.vertex(mat, 0, 0.1f, 0).color(color[0], color[1], color[2], alpha).next();
        buffer.vertex(mat, -wingLength * dir, 0.6f, -0.1f * dir).color(color[0], color[1], color[2], alpha * 0.7f).next();
        buffer.vertex(mat, -wingLength * 0.7f * dir, 0.9f, 0.15f * dir).color(color[0], color[1], color[2], alpha * 0.25f).next();
        
        // Нижняя часть
        buffer.vertex(mat, 0, -0.1f, 0).color(color[0], color[1], color[2], alpha * 0.8f).next();
        buffer.vertex(mat, -wingLength * 0.9f * dir, -0.5f, -0.2f * dir).color(color[0], color[1], color[2], alpha * 0.3f).next();
        buffer.vertex(mat, -wingLength * 0.6f * dir, 0.0f, -0.3f * dir).color(color[0], color[1], color[2], alpha * 0.5f).next();
        
        tessellator.draw();
        
        // Перья (маленькие треугольники по краю)
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < 6; i++) {
            float t = (i + 1) / 7f;
            float featherX = -wingLength * dir * t;
            float featherY = 0.5f * t;
            float featherZ = -0.1f * dir * t;
            float featherSize = 0.15f * (1 - t * 0.7f);
            
            buffer.vertex(mat, featherX, featherY, featherZ).color(color[0], color[1], color[2], alpha * 0.8f).next();
            buffer.vertex(mat, featherX + 0.1f * dir, featherY + featherSize, featherZ).color(color[0], color[1], color[2], alpha * 0.2f).next();
            buffer.vertex(mat, featherX - 0.05f * dir, featherY - featherSize * 0.5f, featherZ + 0.1f * dir).color(color[0], color[1], color[2], alpha * 0.2f).next();
        }
        tessellator.draw();
        
        matrices.pop();
    }
}
