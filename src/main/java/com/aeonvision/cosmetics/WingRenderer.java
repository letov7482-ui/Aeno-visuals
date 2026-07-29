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
        
        float bodyYaw = (float)Math.toRadians(-MC.player.getYaw() + 180);
        float flap = MathHelper.sin(flapTime) * 30f;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        
        matrices.push();
        matrices.translate(px, py, pz);
        
        drawWing(matrices, true, flap, bodyYaw);
        drawWing(matrices, false, flap, bodyYaw);
        
        matrices.pop();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private void drawWing(MatrixStack matrices, boolean isRight, float flap, float bodyYaw) {
        matrices.push();
        
        float dir = isRight ? 1 : -1;
        matrices.multiply(new Quaternionf().rotateY(bodyYaw));
        matrices.multiply(new Quaternionf().rotateZ((float)Math.toRadians(flap * dir)));
        matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(25 * dir)));
        
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float wingLength = 1.6f;
        float alpha = 0.65f;
        int r = 240, g = 240, b = 255;
        
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        
        int c1 = ((int)(alpha*255)<<24) | (r<<16) | (g<<8) | b;
        int c2 = ((int)(alpha*0.4f*255)<<24) | (r<<16) | (g<<8) | b;
        int c3 = ((int)(alpha*0.7f*255)<<24) | (r<<16) | (g<<8) | b;
        int c4 = ((int)(alpha*0.25f*255)<<24) | (r<<16) | (g<<8) | b;
        
        buffer.vertex(matrix, 0, 0.1f, 0).color(c1);
        buffer.vertex(matrix, -wingLength * dir, -0.2f, -0.4f * dir).color(c2);
        buffer.vertex(matrix, -wingLength * dir, 0.6f, -0.1f * dir).color(c3);
        
        buffer.vertex(matrix, 0, 0.1f, 0).color(c1);
        buffer.vertex(matrix, -wingLength * dir, 0.6f, -0.1f * dir).color(c3);
        buffer.vertex(matrix, -wingLength * 0.7f * dir, 0.9f, 0.15f * dir).color(c4);
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        matrices.pop();
    }
}
