package me.monkeykiller.customblocks.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;

public interface Directional {

    default void place(@NotNull Block block, @NotNull BlockFace facing) {
        block.setType(Material.NOTE_BLOCK, false);
        NoteBlock data = (NoteBlock) block.getBlockData();
        block.setBlockData(getFacingData(data, facing));
    }

    NoteBlock getFacingData(@NotNull NoteBlock data, @NotNull BlockFace face);

    boolean isVariant(@NotNull CustomBlockData data);

    BlockFace getFacingDirection(@NotNull Block block);
}
