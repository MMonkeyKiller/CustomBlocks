package me.monkeykiller.customblocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Interactable {
    public void onInteract(@NotNull Player player, @NotNull Block block);
}
