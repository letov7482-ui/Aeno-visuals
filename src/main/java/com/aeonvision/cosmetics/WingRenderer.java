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
    private float flapTime = 0, flapSpeed = 0;

    public void tick(PlayerEntity player, WingType type, float moveSpeed) {
        float target = moveSpeed > 0.1f ? 0.12f : 0.03f;
        flapSpeed += (target - flapSpeed) * 0.1f;
        flapTime += flapSpeed;
    }

    public void render(WorldRenderContext ctx) {
        if (MC.player == null) return;
        
        MatrixStack ms = ctx.matrixStack();
        Camera cam = ctx.camera();
        Vec3d cp = cam.getPos();
        
        double px = MC.player.getX() - cp.x;
        double py = MC.player.getY() + 1.0 - cp.y;
        double pz = MC.player.getZ() - cp.z;
        
        float bodyYaw = (float)Math.toRadians(-MC.player.getYaw() + 180);
        float flap = MathHelper.sin(flapTime) * 30f;
        
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        
        ms.push();
        ms.translate(px, py, pz);
        
        drawWing(ms, true, flap, bodyYaw);
        drawWing(ms, false, flap, bodyYaw);
        
        ms.pop();
        
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private void drawWing(MatrixStack ms, boolean isRight, float flap, float bodyYaw) {
        ms.push();
        float dir = isRight ? 1 : -1;
        ms.multiply(new Quaternionf().rotateY(bodyYaw));
        ms.multiply(new Quaternionf().rotateZ((float)Math.toRadians(flap * dir)));
        ms.multiply(new Quaternionf().rotateY((float)Math.toRadians(20 * dir)));
        
        Matrix4f m = ms.peek().getPositionMatrix();
        float len = 1.5f, alpha = 0.8f;
        
        int c1 = ((int)(alpha*255)<<24)|0xFFFFFF;      // Белый
        int c2 = ((int)(alpha*0.5f*255)<<24)|0xCCCCFF;  // Голубоватый
        int c3 = ((int)(alpha*0.3f*255)<<24)|0x8888CC;  // Тёмно-голубой
        
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        
        // Верхняя часть
        buf.vertex(m, 0, 0.1f, 0).color(c1);
        buf.vertex(m, -len*dir, 0.5f, -0.3f*dir).color(c2);
        buf.vertex(m, -len*0.7f*dir, 1.0f, 0).color(c3);
        
        // Нижняя часть
        buf.vertex(m, 0, -0.1f, 0).color(c1);
        buf.vertex(m, -len*0.8f*dir, -0.5f, -0.2f*dir).color(c2);
        buf.vertex(m, -len*0.5f*dir, 0.1f, -0.3f*dir).color(c3);
        
        // Средняя часть
        buf.vertex(m, 0, 0.1f, 0).color(c1);
        buf.vertex(m, -len*0.5f*dir, 0.1f, -0.3f*dir).color(c3);
        buf.vertex(m, -len*dir, 0.5f, -0.3f*dir).color(c2);
        
        BufferRenderer.drawWithGlobalProgram(buf.end());
        ms.pop();
    }
}
