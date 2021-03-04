package me.monkeykiller.customblocks;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {
    public static boolean isAirOrNull(@NotNull ItemStack item) throws NullPointerException {
        try {
            return item.getType() == Material.AIR;
        } catch (NullPointerException err) {
            return true;
        }
    }

    public static ItemStack getBlockInHand(@NotNull PlayerInventory inv) {
        return inv.getItemInMainHand().getType().isBlock() && inv.getItemInMainHand().getType() != Material.AIR
                ? inv.getItemInMainHand()
                : inv.getItemInOffHand().getType().isBlock() && inv.getItemInOffHand().getType() != Material.AIR
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
        return getItemId(item) != null && getItemId(item).equalsIgnoreCase(id);
    }

    public static String getItemId(ItemStack item) {
        return new NBTItem(item).getString("ItemId");
    }
}
