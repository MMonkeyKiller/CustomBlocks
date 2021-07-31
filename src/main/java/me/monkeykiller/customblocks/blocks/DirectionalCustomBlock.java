package me.monkeykiller.customblocks.blocks;

import org.apache.commons.lang.Validate;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DirectionalCustomBlock extends CustomBlock implements Directional {
    private final HashMap<BlockFace, CustomBlockData> VARIANTS = new HashMap<>();

    public DirectionalCustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered, @NotNull HashMap<BlockFace, CustomBlockData> variants, @Nullable Material material) {
        super(id, itemModelData, instrument, note, powered, material);
        List<BlockFace> AVAILABLE_FACES = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
        AVAILABLE_FACES.forEach(f -> VARIANTS.put(f, getData()));
        Validate.isTrue(variants.size() >= 5, "Not enought face variants");
        variants.keySet().stream().filter(AVAILABLE_FACES::contains).forEach(f -> VARIANTS.put(f, variants.get(f)));
    }

    public DirectionalCustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered, @NotNull HashMap<BlockFace, CustomBlockData> variants) {
        this(id, itemModelData, instrument, note, powered, variants, null);
    }

    @Override
    public NoteBlock getFacingData(@NotNull NoteBlock data, @NotNull BlockFace face) {
        return VARIANTS.get(face).applyData(data);
    }

    @Override
    public boolean isVariant(@NotNull CustomBlockData data) {
        return VARIANTS.values().stream().anyMatch(d -> d.compareData(data));
    }

    @Override
    public BlockFace getFacingDirection(@NotNull Block block) {
        Validate.isTrue(block.getBlockData() instanceof NoteBlock, "Block isn't a noteblock!");
        for (BlockFace face : VARIANTS.keySet())
            if (VARIANTS.get(face).compareData((NoteBlock) block.getBlockData()))
                return face;
        return null;
    }

}
