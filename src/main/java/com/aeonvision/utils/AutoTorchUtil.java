package com.aeonvision.utils;

import com.aeonvision.AeonVisionMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

public class AutoTorchUtil {
    private final MinecraftClient MC = MinecraftClient.getInstance();
    
    private boolean enabled = false;
    private long lastTorchPlace = 0;
    private static final long PLACE_COOLDOWN = 250; // миллисекунды
    private static final int LIGHT_THRESHOLD = 7;
    
    // Предыдущая позиция для избежания спама
    private BlockPos lastCheckedPos = BlockPos.ORIGIN;

    public void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        
        BlockPos playerPos = client.player.getBlockPos();
        
        // Проверяем уровень освещения на позиции игрока
        int lightLevel = client.world.getLightLevel(LightType.BLOCK, playerPos);
        int skyLight = client.world.getLightLevel(LightType.SKY, playerPos);
        int totalLight = Math.max(lightLevel, skyLight - client.world.getAmbientDarkness());
        
        // Если темно и прошёл кулдаун
        if (totalLight <= LIGHT_THRESHOLD && 
            System.currentTimeMillis() - lastTorchPlace > PLACE_COOLDOWN &&
            !playerPos.equals(lastCheckedPos)) {
            
            lastCheckedPos = playerPos;
            
            // Ищем факел в инвентаре
            int torchSlot = findTorchInInventory();
            if (torchSlot == -1) return;
            
            // Проверяем, можно ли поставить факел под ногами или рядом
            BlockPos placePos = findPlacePosition(playerPos);
            if (placePos != null) {
                placeTorch(torchSlot, placePos);
                lastTorchPlace = System.currentTimeMillis();
            }
        }
    }

    private int findTorchInInventory() {
        PlayerInventory inv = MC.player.getInventory();
        
        // Сначала проверяем offhand
        if (inv.offHand.get(0).getItem() == Items.TORCH) {
            return 40; // offhand slot
        }
        
        // Потом хотбар (слоты 36-44, но в инвентаре это 0-8)
        for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).getItem() == Items.TORCH) {
                return i;
            }
        }
        
        // Потом весь инвентарь
        for (int i = 9; i < 36; i++) {
            if (inv.getStack(i).getItem() == Items.TORCH) {
                return i;
            }
        }
        
        return -1;
    }

    private BlockPos findPlacePosition(BlockPos playerPos) {
        // Пытаемся поставить под ногами
        BlockPos underFeet = playerPos.down();
        if (canPlaceTorch(underFeet)) {
            return underFeet;
        }
        
        // Пытаемся поставить на блоках вокруг
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction dir : directions) {
            BlockPos adjacent = playerPos.offset(dir);
            BlockPos placeOn = adjacent.down();
            if (canPlaceTorch(adjacent) && MC.world.getBlockState(placeOn).isSolid()) {
                return adjacent;
            }
        }
        
        return null;
    }

    private boolean canPlaceTorch(BlockPos pos) {
        BlockState state = MC.world.getBlockState(pos);
        BlockState below = MC.world.getBlockState(pos.down());
        
        // Можно ставить на твёрдый блок и если позиция свободна
        return (state.isAir() || state.isReplaceable()) && below.isSolid();
    }

    private void placeTorch(int slot, BlockPos pos) {
        // Сохраняем текущий выбранный слот
        int prevSlot = MC.player.getInventory().selectedSlot;
        
        // Переключаемся на слот с факелом если он в хотбаре
        if (slot >= 0 && slot < 9) {
            MC.player.getInventory().selectedSlot = slot;
        } else if (slot == 40) {
            // offhand — факел уже в левой руке, используем её
            // Ничего не меняем
        } else {
            // Факел в основном инвентаре — свапаем в хотбар
            swapToHotbar(slot);
            slot = findTorchInInventory();
            if (slot == -1 || slot >= 9) return;
            MC.player.getInventory().selectedSlot = slot;
        }
        
        // Используем предмет на блоке
        if (MC.interactionManager != null) {
            BlockHitResult hit = new BlockHitResult(
                net.minecraft.util.math.Vec3d.ofCenter(pos),
                Direction.UP,
                pos,
                false
            );
            MC.interactionManager.interactBlock(MC.player, 
                slot == 40 ? Hand.OFF_HAND : Hand.MAIN_HAND, hit);
        }
        
        // Возвращаем предыдущий слот
        MC.player.getInventory().selectedSlot = prevSlot;
    }

    private void swapToHotbar(int slot) {
        // Меняем местами слот из инвентаря с первым слотом хотбара
        PlayerInventory inv = MC.player.getInventory();
        ItemStack temp = inv.getStack(0).copy();
        inv.setStack(0, inv.getStack(slot).copy());
        inv.setStack(slot, temp);
    }

    public void toggle() {
        enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
