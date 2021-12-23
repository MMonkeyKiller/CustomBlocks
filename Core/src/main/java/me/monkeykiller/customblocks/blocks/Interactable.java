package me.monkeykiller.customblocks.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Interactable {
    void onInteract(@NotNull Player player, @NotNull Block block);
}
