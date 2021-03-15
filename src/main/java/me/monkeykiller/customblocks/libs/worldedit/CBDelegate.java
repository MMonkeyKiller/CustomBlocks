package me.monkeykiller.customblocks.libs.worldedit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
                CompoundTag tag = (baseBlock.getNbtData());
                assert tag != null;
                Map<String, Tag> tags = tag.getValue();
                assert tags.get("CBlock") instanceof CBTag;
                String id = ((CBTag) tags.get("CBlock")).getValue();

                CustomBlock CB = CustomBlock.getCustomBlockbyId(id);
                if (CB == null) return true;
                CB.place(loc.getBlock());
                Bukkit.getScheduler().runTaskLater(Main.plugin, () -> player.sendBlockChange(loc, loc.getBlock().getBlockData()), 5L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            HashMap hm = new HashMap();

            for (Object o : stateHolder.getStates().entrySet()) {
                Map.Entry e = (Map.Entry) o;
                hm.put(BlockFace.valueOf(((Property) e.getKey()).getName().toUpperCase()), e.getValue());
            }
            BaseBlock baseBlock = stateHolder.toBaseBlock();
            baseBlock.getStates().forEach((key, value) -> Bukkit.getLogger().info(key.toString() + " -- " + value.toString()));
        }
        return getExtent().setBlock(pos, stateHolder);
    }
}
