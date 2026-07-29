package com.aeonvision.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.PlayerScreenHandler;
import java.util.*;

public class InventorySorter {
    private final MinecraftClient MC = MinecraftClient.getInstance();

    // Категории для сортировки
    private static final Map<Class<?>, Integer> CATEGORY_ORDER = new LinkedHashMap<>();
    static {
        CATEGORY_ORDER.put(SwordItem.class, 0);       // Мечи
        CATEGORY_ORDER.put(BowItem.class, 1);          // Луки
        CATEGORY_ORDER.put(CrossbowItem.class, 1);     // Арбалеты
        CATEGORY_ORDER.put(TridentItem.class, 1);      // Трезубцы
        CATEGORY_ORDER.put(PickaxeItem.class, 2);      // Кирки
        CATEGORY_ORDER.put(AxeItem.class, 3);          // Топоры
        CATEGORY_ORDER.put(ShovelItem.class, 4);       // Лопаты
        CATEGORY_ORDER.put(HoeItem.class, 5);          // Мотыги
        CATEGORY_ORDER.put(ArmorItem.class, 6);        // Броня
        CATEGORY_ORDER.put(BlockItem.class, 7);        // Блоки
        CATEGORY_ORDER.put(FoodComponent.class, 8);    // Еда (проверяем компонент)
    }

    public void sortInventory() {
        if (MC.player == null) return;
        
        PlayerInventory inv = MC.player.getInventory();
        
        // Собираем все предметы из основного инвентаря (слоты 9-35) и хотбара (0-8)
        List<ItemStack> allItems = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                allItems.add(stack.copy());
                inv.setStack(i, ItemStack.EMPTY);
            }
        }
        
        // Сортируем
        allItems.sort((a, b) -> {
            int catA = getCategory(a);
            int catB = getCategory(b);
            if (catA != catB) return Integer.compare(catA, catB);
            
            // Внутри категории: по названию
            String nameA = a.getName().getString();
            String nameB = b.getName().getString();
            int nameCompare = nameA.compareTo(nameB);
            if (nameCompare != 0) return nameCompare;
            
            // По размеру стака (большие стаки первыми)
            return Integer.compare(b.getCount(), a.getCount());
        });
        
        // Объединяем одинаковые предметы и раскладываем обратно
        List<ItemStack> merged = mergeStacks(allItems);
        
        int slotIndex = 0;
        for (ItemStack stack : merged) {
            if (slotIndex >= 36) break;
            inv.setStack(slotIndex, stack);
            slotIndex++;
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
                    if (stack.isEmpty()) {
                        merged = true;
                        break;
                    }
                }
            }
            if (!merged && !stack.isEmpty()) {
                result.add(stack.copy());
            }
        }
        
        return result;
    }

    private int getCategory(ItemStack stack) {
        Item item =
