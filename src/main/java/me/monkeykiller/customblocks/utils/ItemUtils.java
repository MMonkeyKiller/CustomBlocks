package me.monkeykiller.customblocks.utils;

import de.tr7zw.nbtapi.NBTItem;
import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.config.config;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static ItemStack getBlockOrCustomBlockInHand(@NotNull PlayerInventory inv) {
        return (inv.getItemInMainHand().getType().isBlock() || CustomBlock.getCustomBlockbyItem(inv.getItemInMainHand()) != null) && inv.getItemInMainHand().getType() != Material.AIR
                ? inv.getItemInMainHand()
                : (inv.getItemInOffHand().getType().isBlock() || CustomBlock.getCustomBlockbyItem(inv.getItemInOffHand()) != null) && inv.getItemInOffHand().getType() != Material.AIR
                ? inv.getItemInOffHand()
                : null;
    }

    public static EquipmentSlot getEquipmentSlot(PlayerInventory inv, ItemStack item) {
        return inv.getItemInMainHand().equals(item) ? EquipmentSlot.HAND
                : inv.getItemInOffHand().equals(item) ? EquipmentSlot.OFF_HAND : null;
    }

    public static String getItemId(ItemStack item) {
        return item != null ? new NBTItem(item).getString("ItemId") : null;
    }

    public static ItemStack getFirstCustomBlockInHand(@NotNull PlayerInventory inv) {
        ItemStack[] hands = new ItemStack[]{inv.getItemInMainHand(), inv.getItemInOffHand()};

        for (ItemStack i : hands) {
            if (ItemUtils.isAirOrNull(i)) continue;
            CustomBlock CB = CustomBlock.getCustomBlockbyItem(i);
            if (CB == null) continue;
            Material expected = CB.getMaterial() != null ? CB.getMaterial() : config.cbiMaterial;
            if (i.getType() == expected) return i;
        }
        return null;
    }
}
