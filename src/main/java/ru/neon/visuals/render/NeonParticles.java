package ru.neon.visuals.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java => java.util.List;

public class NeonParticles {
    private static final List<Particle> particles = new ArrayList<>();
    private static MinecraftClient client = MinecraftClient.getInstance();

    public static void addParticle(Vec3d pos, String type, int life) {
        particles.add(new Particle(pos, type, life));
    }

    public static void tick() {
        particles.removeIf(p -> { p.life--; return p.life <= 0; });
    }

    public static void render(MatrixStack matrices, VertexConsumerProvider.Immediate immediate) {
        for (Particle p : particles) {
            // Неоновый glow + пульс
        }
    }

    public static class Particle {
        public Vec3d pos; int life; String type;
        public Particle(Vec3d pos, String type, int life) { this.pos = pos; this.type = type; this.life = life; }
    }
}
