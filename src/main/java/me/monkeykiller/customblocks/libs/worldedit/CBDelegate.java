package me.monkeykiller.customblocks.libs.worldedit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.CustomBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class CBDelegate extends AbstractDelegateExtent {

    public WEListener listener;
    public EditSessionEvent event;
    public Player player;

    protected CBDelegate(WEListener listener, EditSessionEvent event) {
        super(event.getExtent());
        this.listener = listener;
        this.event = event;
        this.player = Bukkit.getPlayer(Objects.requireNonNull(event.getActor()).getUniqueId());
    }

    public BlockState getBlock(BlockVector3 pos) {
        return getExtent().getBlock(pos);
    }

    @SuppressWarnings("unchecked")
    public boolean setBlock(BlockVector3 pos, BlockStateHolder stateHolder) throws WorldEditException {
        World world = Bukkit.getWorld(Objects.requireNonNull(event.getWorld()).getName());
        Location loc = new Location(world, pos.getX(), pos.getY(), pos.getZ());

        if (stateHolder.getBlockType().equals(Parser.blockType)) {
            try {
                BaseBlock baseBlock = stateHolder.toBaseBlock();
                String id = "";
                try {
                    CompoundTag tag = (baseBlock.getNbtData());
                    assert tag != null;
                    Map<String, Tag> tags = tag.getValue();
                    assert tags.get("CBlock") instanceof CBTag;
                    id = tags.get("CBlock").getValue().toString();
                } catch (NullPointerException ignored) {
                }
                CustomBlock CB = CustomBlock.getCustomBlockbyId(id);
                if (CB == null) return true;
                CB.place(loc.getBlock());
                Bukkit.getScheduler().runTaskLater(CustomBlocks.plugin, () -> player.sendBlockChange(loc, loc.getBlock().getBlockData()), 5L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return getExtent().setBlock(pos, stateHolder);
    }
}
