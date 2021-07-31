package me.monkeykiller.customblocks.libs.worldedit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Parser extends InputParser {

    public static BlockType blockType = BlockTypes.NOTE_BLOCK;
    List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

    public Parser(WorldEdit we) {
        super(we);
    }

    public Stream getSuggestions(String s) {
        return s.isEmpty() ? Stream.empty() : CustomBlock.getRegistry().stream()
                .map(CustomBlock::getId)
                .filter(CB -> CB.contains(s.toLowerCase()))
                .sorted(String::compareToIgnoreCase);
    }

    public BaseBlock parseFromInput(String input, ParserContext context) {
        if (CustomBlock.getCustomBlockbyId(input) == null) return null;

        try {
            Constructor BSConstructor = BlockState.class.getDeclaredConstructors()[0];
            BSConstructor.setAccessible(true);
            BlockState blockState = blockType.getDefaultState();

            Constructor BBConstructor = BaseBlock.class.getDeclaredConstructors()[1];
            BBConstructor.setAccessible(true);

            HashMap<String, Tag> tags = new HashMap<>();
            tags.put("CBlock", new CBTag(this, input));

            return (BaseBlock) BBConstructor.newInstance(blockState, new CompoundTag(tags));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private BaseItemStack getBaseItemStackInHand(Actor actor, HandSide hand) throws Exception {
        if (actor instanceof Player)
            return ((Player) actor).getItemInHand(hand);
        throw new Exception("The user is not a player!");
    }
}
