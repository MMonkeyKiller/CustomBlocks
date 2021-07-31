package me.monkeykiller.customblocks.api;

import me.monkeykiller.customblocks.CustomBlocksGUI;
import me.monkeykiller.customblocks.CustomBlocksPlugin;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.blocks.CustomBlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class CustomBlocksAPI {
    public static List<CustomBlock> getRegistry() {
        return CustomBlock.getRegistry();
    }

    public static void clearRegistry() {
        CustomBlock.clear();
    }

    public static void register(@NotNull CustomBlock... cbks) {
        register(false, cbks);
    }

    public static void register(boolean replaceIfExists, @NotNull CustomBlock... cbks) {
        Arrays.stream(cbks).forEach(cb -> CustomBlock.register(cb, replaceIfExists));
    }

    public static void unregister(@NotNull CustomBlock... cbks) {
        Arrays.stream(cbks).map(CustomBlock::getId).forEach(CustomBlock::unregister);
    }

    public static CustomBlock getCustomBlock(@NotNull String id) {
        return CustomBlock.getCustomBlockbyId(id);
    }

    public static CustomBlock getCustomBlock(@NotNull ItemStack item) {
        return CustomBlock.getCustomBlockbyItem(item);
    }

    public static CustomBlock getCustomBlock(@NotNull CustomBlockData data) {
        return CustomBlock.getCustomBlockbyData(data);
    }

    public static void openGUI(@NotNull Player player) {
        CustomBlocksGUI.open(player);
    }

    public static void openGUI(@NotNull Player player, int page) {
        CustomBlocksGUI.open(player, page);
    }

    public static void reload() {
        CustomBlocksPlugin.configData.reloadConfig();
    }
}
