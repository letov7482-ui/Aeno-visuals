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

public class AuraRenderer {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    private float time = 0;
    private int auraSegments = 64;
    private float auraRadius = 0.8f;

    public void tick(PlayerEntity player, AuraType type, float[] color) {
        time += 0.016f;
    }

    public void render(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        
        if (MC.player == null) return;
        
        double px = MC.player.getX() - camPos.x;
        double py = MC.player.getY() + MC.player.getHeight() / 2 - camPos.y;
        double pz = MC.player.getZ() - camPos.z;
        
        CosmeticManager cm = ((com.aeonvision.AeonVisionClient) 
            net.fabricmc.loader.api.FabricLoader.getInstance()
            .getEntrypointContainers("client", net.fabricmc.api.ClientModInitializer.class)
            .stream().findFirst().orElse(null)).COSMETICS;
        
        // Заглушка для статического доступа
        // В реальном коде нужно получить через AeonVisionClient.COSMETICS
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        // Рисуем кольца ауры
        float[] color = {0.0f, 0.8f, 1.0f}; // Дефолт, берём из менеджера
        float alpha = 0.3f + MathHelper.sin(time * 2f) * 0.1f;
        
        matrices.push();
        matrices.translate(px, py, pz);
        
        // Горизонтальное кольцо
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= auraSegments; i++) {
            float angle = (float)(i * Math.PI * 2 / auraSegments);
            float cos = MathHelper.cos(angle);
            float sin = MathHelper.sin(angle);
            float r = auraRadius + MathHelper.sin(angle * 3 + time) * 0.1f;
            
            Matrix4f mat = matrices.peek().getPositionMatrix();
            buffer.vertex(mat, cos * r, -0.1f, sin * r).color(color[0], color[1], color[2], alpha).next();
            buffer.vertex(mat, cos * r, 0.1f, sin * r).color(color[0], color[1], color[2], alpha * 0.5f).next();
        }
        tessellator.draw();
        
        // Вертикальное кольцо
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= auraSegments; i++) {
            float angle = (float)(i * Math.PI * 2 / auraSegments);
            float cos = MathHelper.cos(angle);
            float sin = MathHelper.sin(angle);
            
            Matrix4f mat = matrices.peek().getPositionMatrix();
            buffer.vertex(mat, cos * auraRadius, sin * auraRadius, 0).color(color[0], color[1], color[2], alpha * 0.7f).next();
            buffer.vertex(mat, cos * (auraRadius - 0.05f), sin * (auraRadius - 0.05f), 0).color(color[0], color[1], color[2], 0).next();
        }
        tessellator.draw();
        
        // Частицы ауры
        for (int i = 0; i < 12; i++) {
            float angle = (float)(i * Math.PI * 2 / 12) + time;
            float cos = MathHelper.cos(angle);
            float sin = MathHelper.sin(angle);
            float particleAlpha = 0.5f + MathHelper.sin(time * 3 + i) * 0.3f;
            
            matrices.push();
            matrices.translate(cos * auraRadius, MathHelper.sin(time * 2 + i * 0.5f) * 0.5f, sin * auraRadius);
            
            Matrix4f mat = matrices.peek().getPositionMatrix();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            float s = 0.05f;
            buffer.vertex(mat, -s, -s, 0).color(color[0], color[1], color[2], particleAlpha).next();
            buffer.vertex(mat, -s, s, 0).color(color[0], color[1], color[2], particleAlpha).next();
            buffer.vertex(mat, s, s, 0).color(color[0], color[1], color[2], particleAlpha).next();
            buffer.vertex(mat, s, -s, 0).color(color[0], color[1], color[2], particleAlpha).next();
            tessellator.draw();
            
            matrices.pop();
        }
        
        matrices.pop();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
              }
