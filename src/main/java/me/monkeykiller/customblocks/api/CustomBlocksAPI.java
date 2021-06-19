package me.monkeykiller.customblocks.api;

import me.monkeykiller.customblocks.CBPlugin;
import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.CustomBlocksGUI;
import org.bukkit.Instrument;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class CustomBlocksAPI {
    /**
     * Get the {@link CustomBlock} Registry
     *
     * @return The {@link CustomBlock} registry
     */
    public List<CustomBlock> getRegistry() {
        return CustomBlock.REGISTRY;
    }

    /**
     * Clears the {@link CustomBlock} Registry
     */
    public void clearRegistry() {
        CustomBlock.REGISTRY.clear();
    }

    /**
     * Adds the given {@link CustomBlock}(s) to the Registry
     *
     * @param cbks {@link CustomBlock}s separated by commas
     */
    public void register(@NotNull CustomBlock... cbks) {
        Collections.addAll(CustomBlock.REGISTRY, cbks);
    }

    /**
     * Removes the given {@link CustomBlock}(s) from the Registry
     *
     * @param cbks {@link CustomBlock}s separated by commas
     */
    public void unregister(@NotNull CustomBlock... cbks) {
        CustomBlock.REGISTRY.addAll(Arrays.asList(cbks));
    }

    /**
     * Check if a {@link CustomBlock} exists
     *
     * @param id The {@link CustomBlock} id
     * @return The result of the search
     */
    public boolean isCustomBlock(@NotNull String id) {
        return CustomBlock.isCustomBlock(id);
    }

    /**
     * Check if a {@link CustomBlock} exists
     *
     * @param block The NoteBlock block instance
     * @return The result of the search
     */
    public boolean isCustomBlock(@NotNull Block block) {
        return CustomBlock.isCustomBlock(block);
    }

    /**
     * Get the {@link CustomBlock} with the given params
     *
     * @param id The {@link CustomBlock} id
     * @return The corresponding {@link CustomBlock}
     */
    @Nullable
    public CustomBlock getCustomBlock(@NotNull String id) {
        return CustomBlock.getCustomBlockbyId(id);
    }

    /**
     * Get the {@link CustomBlock} with the given params
     *
     * @param item The {@link CustomBlock} corresponding item
     * @return The corresponding {@link CustomBlock}
     */
    @Nullable
    public CustomBlock getCustomBlock(@NotNull ItemStack item) {
        return CustomBlock.getCustomBlockbyItem(item);
    }

    /**
     * Get the {@link CustomBlock} with the given params
     *
     * @param data The {@link org.bukkit.block.data.BlockData} of a noteblock, corresponding to the {@link CustomBlock}
     * @return The corresponding {@link CustomBlock}
     */
    @Nullable
    public CustomBlock getCustomBlock(@NotNull NoteBlock data) {
        return CustomBlock.getCustomBlockbyData(data);
    }

    /**
     * Get the {@link CustomBlock} with the given params
     *
     * @param instrument The {@link Instrument} of the {@link CustomBlock}
     * @param note       The pitch note of the {@link CustomBlock}
     * @param powered    The powered status of the {@link CustomBlock}
     * @return The corresponding {@link CustomBlock}
     */
    @Nullable
    public CustomBlock getCustomBlock(@NotNull Instrument instrument, int note, boolean powered) {
        return CustomBlock.getCustomBlockbyData(instrument, note, powered);
    }

    /**
     * Opens the {@link CustomBlock}s GUI to an specific player
     *
     * @param player The player
     */
    public void openGUI(@NotNull Player player) {
        CustomBlocksGUI.open(player);
    }

    /**
     * Opens the {@link CustomBlock}s GUI to an specific player
     *
     * @param player The player
     * @param page   The page number
     * @apiNote If the page is invalid, it will be change to a valid one
     */
    public void openGUI(@NotNull Player player, int page) {
        CustomBlocksGUI.open(player, page);
    }

    /**
     * Reloads the plugin config
     *
     * @apiNote Launches the {@link CBLoadEvent} event
     */
    public void reload() {
        CBPlugin.configData.reloadConfig();
    }
}
