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
        targetFlapSpeed = moveSpeed > 0.1f ? 0.15f : 0.03f;
        flapSpeed += (targetFlapSpeed - flapSpeed) * 0.1f;
        flapTime += flapSpeed;
    }

    public void render(WorldRenderContext context) {
        if (MC.player == null) return;
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        double px = MC.player.getX() - camPos.x;
        double py = MC.player.getY() + 1.2 - camPos.y; // Чуть выше центра
        double pz = MC.player.getZ() - camPos.z;
        
        float bodyYaw = (float)Math.toRadians(-MC.player.getYaw() + 180);
        
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
        
        float flap = MathHelper.sin(flapTime) * 25f;
        
        // Рисуем оба крыла
        drawWing(matrices, buffer, tessellator, true, flap);  // Правое
        drawWing(matrices, buffer, tessellator, false, flap); // Левое
        
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
        matrices.multiply(new org.joml.Quaternionf().rotateY((float)Math.toRadians(20 * dir)));
        
        Matrix4f mat = matrices.peek().getPositionMatrix();
        
        // Форма крыла (треугольники)
        float wingLength = 1.5f;
        float wingWidth = 0.6f;
        float alpha = 0.6f;
        
        float[] color = {0.9f, 0.9f, 1.0f}; // Белые (ангельские)
        
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        
        // Основная часть крыла
        buffer.vertex(mat, 0, 0, 0).color(color[0], color[1], color[2], alpha).next();
        buffer.vertex(mat, -wingLength * dir, -0.3f, -0.3f * dir).color(color[0], color[1], color[2], alpha * 0.5f).next();
        buffer.vertex(mat, -wingLength * dir, 0.5f, 0).color(color[0], color[1], color[2], alpha * 0.7f).next();
        
        buffer.vertex(mat, 0, 0, 0).color(color[0], color[1], color[2], alpha).next();
        buffer.vertex(mat, -wingLength * dir, 0.5f, 0).color(color[0], color[1], color[2], alpha * 0.7f).next();
        buffer.vertex(mat, -wingLength * dir * 0.7f, 0.8f, 0.1f * dir).color(color[0], color[1], color[2], alpha * 0.3f).next();
        
        // Нижняя часть
        buffer.vertex(mat, 0, -0.1f, 0).color(color[0], color[1], color[2], alpha).next();
        buffer.vertex(mat, -wingLength * 0.8f * dir, -
