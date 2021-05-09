package me.monkeykiller.customblocks.utils;

import de.tr7zw.nbtapi.NBTItem;
import me.monkeykiller.customblocks.CustomBlock;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemUtils {
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static ItemStack getBlockInHand(@NotNull PlayerInventory inv) {
        return inv.getItemInMainHand().getType().isBlock() && inv.getItemInMainHand().getType() != Material.AIR
                ? inv.getItemInMainHand()
                : inv.getItemInOffHand().getType().isBlock() && inv.getItemInOffHand().getType() != Material.AIR
                ? inv.getItemInOffHand()
                : null;
    }

    public static ItemStack getMaterialInHand(@NotNull PlayerInventory inv, @NotNull Material mat) {
        return inv.getItemInMainHand().getType() == mat
                ? inv.getItemInMainHand()
                : inv.getItemInOffHand().getType() == mat
                ? inv.getItemInOffHand()
                : null;
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

    public static boolean hasSilkTouch(ItemStack item) {
        return item.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    public static boolean checkItemId(ItemStack item, String id) {
        return getItemId(item) != null && Objects.requireNonNull(getItemId(item)).equalsIgnoreCase(id);
    }

    public static String getItemId(ItemStack item) {
        return item != null ? new NBTItem(item).getString("ItemId") : null;
    }
}