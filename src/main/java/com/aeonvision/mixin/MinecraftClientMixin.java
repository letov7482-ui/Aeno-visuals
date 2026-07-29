package com.aeonvision.mixin;

import com.aeonvision.AeonVisionMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
    }

    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    private void onResolutionChanged(CallbackInfo ci) {
    }
}
