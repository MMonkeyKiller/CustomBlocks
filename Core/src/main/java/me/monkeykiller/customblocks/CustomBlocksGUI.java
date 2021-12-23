package me.monkeykiller.customblocks;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class CustomBlocksGUI {
    public static Map<UUID, Integer> savedPages = new HashMap<>();

    public static void open(@NotNull Player player) {
        open(player, savedPages.getOrDefault(player.getUniqueId(), 1));
    }

    public static void open(@NotNull Player player, int page) {
        int cbksCount = CustomBlock.getRegistry().size(), totalPages = getTotalPages();

        page = Math.max(1, Math.min(page, totalPages)); // Set page range to 1 >= page >= totalPages

        Inventory inv = Bukkit.createInventory(null, 54, config.cbksGUITitle + Utils.colorize(" [" + page + "/" + totalPages + "]"));

        for (int i = (9 * 5); i < (9 * 6); i++) {
            ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET + "");
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        for (int invSlot = 0, cbksIndex = (page - 1) * 45;
             invSlot < 45 && cbksIndex < cbksCount; invSlot++, cbksIndex++)
            inv.setItem(invSlot, CustomBlock.getRegistry().get(cbksIndex).getItemStack(true));

        inv.setItem(45, getBackBtn());
        inv.setItem(49, getInfoBtn(page, totalPages));
        inv.setItem(53, getNextBtn());

        player.openInventory(inv);
        savedPages.put(player.getUniqueId(), page);
    }

    public static int getTotalPages() {
        int cbksCount = CustomBlock.getRegistry().size(),
                totalPages = cbksCount / (9 * 5);    // 45 -> The max blocks per page
        if (cbksCount % (9 * 5) > 0) ++totalPages;   // extra page for the last items
        return totalPages;
    }

    private static ItemStack getBackBtn() {
        String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1MmUyYjkzNmNhODAyNmJkMjg2NTFkN2M5ZjI4MTlkMmU5MjM2OTc3MzRkMThkZmRiMTM1NTBmOGZkYWQ1ZiJ9fX0=";
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        assert item.getItemMeta() != null;
        SkullMeta meta = getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
        meta.setDisplayName(Utils.colorize("&aBack"));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getInfoBtn(int page, int totalPages) {
        String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUzZjUwYmE2MmNiZGEzZWNmNTQ3OWI2MmZlZGViZDYxZDc2NTg5NzcxY2MxOTI4NmJmMjc0NWNkNzFlNDdjNiJ9fX0=";
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        assert item.getItemMeta() != null;
        SkullMeta meta = getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
        meta.setDisplayName(Utils.colorize("&aPage " + page + "/" + totalPages));
        meta.setLore(Collections.singletonList(Utils.colorize("&a&lLeft Click &r&8Go back to page 1")));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getNextBtn() {
        String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzYjhmNjgxZGFhZDhiZjQzNmNhZThkYTNmZTgxMzFmNjJhMTYyYWI4MWFmNjM5YzNlMDY0NGFhNmFiYWMyZiJ9fX0=";
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        assert item.getItemMeta() != null;
        SkullMeta meta = getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
        meta.setDisplayName(Utils.colorize("&aNext"));
        item.setItemMeta(meta);
        return item;
    }

    private static SkullMeta getSkullMetaTextureByB64(@NotNull SkullMeta meta, @NotNull String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meta;
    }
}





