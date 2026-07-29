package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import java.util.*;

public class InventorySorter {
    private final MinecraftClient MC = MinecraftClient.getInstance();

    public void sortInventory() {
        if (MC.player == null) return;
        
        PlayerInventory inv = MC.player.getInventory();
        List<ItemStack> allItems = new ArrayList<>();
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                allItems.add(stack.copy());
                inv.setStack(i, ItemStack.EMPTY);
            }
        }
        
        allItems.sort((a, b) -> {
            int catA = getCategory(a);
            int catB = getCategory(b);
            if (catA != catB) return Integer.compare(catA, catB);
            int nameCompare = a.getName().getString().compareToIgnoreCase(b.getName().getString());
            if (nameCompare != 0) return nameCompare;
            return Integer.compare(b.getCount(), a.getCount());
        });
        
        List<ItemStack> merged = mergeStacks(allItems);
        int slot = 0;
        for (ItemStack stack : merged) {
            if (slot >= 36) break;
            inv.setStack(slot, stack);
            slot++;
        }
    }

    private List<ItemStack> mergeStacks(List<ItemStack> items) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : items) {
            boolean merged = false;
            for (ItemStack existing : result) {
                if (ItemStack.areItemsEqual(stack, existing) && 
                    existing.getCount() < existing.getMaxCount()) {
                    int space = existing.getMaxCount() - existing.getCount();
                    int toAdd = Math.min(stack.getCount(), space);
                    existing.increment(toAdd);
                    stack.decrement(toAdd);
                    if (stack.isEmpty()) { merged = true; break; }
                }
            }
            if (!merged && !stack.isEmpty()) result.add(stack.copy());
        }
        return result;
    }

    private int getCategory(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof SwordItem) return 0;
        if (item instanceof BowItem || item instanceof CrossbowItem) return 1;
        if (item instanceof PickaxeItem) return 2;
        if (item instanceof AxeItem) return 3;
        if (item instanceof ShovelItem) return 4;
        if (item instanceof HoeItem) return 5;
        if (item instanceof ArmorItem) return 6;
        if (item instanceof BlockItem) return 7;
        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food != null) return 8;
        if (item == Items.TORCH || item == Items.SOUL_TORCH) return 10;
        return 99;
    }
}
