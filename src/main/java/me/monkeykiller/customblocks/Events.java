package me.monkeykiller.customblocks;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumHand;
import net.minecraft.server.v1_16_R3.ItemActionContext;
import net.minecraft.server.v1_16_R3.MovingObjectPositionBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Events implements Listener {

    private final Main plugin;

    public Events(Main plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockPhysics(BlockPhysicsEvent event) {
        Block b = event.getBlock();
        Block topBlock = b.getRelative(BlockFace.UP);       // Block (y + 1)
        Block bottomBlock = b.getRelative(BlockFace.DOWN);  // Block (y - 1)

        if (topBlock.getType() == Material.NOTE_BLOCK) {
            updateAndCheck(b.getLocation());
            if (Tag.DOORS.isTagged(b.getType()) && b.getBlockData() instanceof Door) {
                Door data = (Door) b.getBlockData();
                if (data.getHalf().equals(Bisected.Half.TOP))
                    bottomBlock.getState().update(true, false);
            }
            event.setCancelled(true);
        }
        if (b.getType() == Material.NOTE_BLOCK)
            event.setCancelled(true);
        if (!Tag.SIGNS.isTagged(b.getType()))
            b.getState().update(true, false);

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        customBlockPlaceCheck(event);
    }

    @EventHandler
    public void onPistonExtends(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks())
            if (b.getType().equals(Material.NOTE_BLOCK))
                event.setCancelled(true);

    }

    @EventHandler
    public void onPistonRestract(BlockPistonRetractEvent event) {
        for (Block b : event.getBlocks())
            if (b.getType().equals(Material.NOTE_BLOCK))
                event.setCancelled(true);

    }

    @EventHandler
    public void onNotePlay(NotePlayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) throws NullPointerException {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || !event.getClickedBlock().getType().equals(Material.NOTE_BLOCK))
            return;
        event.setCancelled(true);
        PlayerInventory inv = event.getPlayer().getInventory();
        ItemStack item = ItemUtils.getBlockInHand(inv);
        if (item == null)
            return;
        if (event.getPlayer().isSneaking())
            event.setCancelled(false);
        else {
            Block pblock = event.getClickedBlock().getRelative(event.getBlockFace());
            List<Material> REPLACE = Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR,
                    Material.GRASS, Material.WATER, Material.LAVA);

            net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            EnumHand hand = ItemUtils.getEquipmentSlot(inv, item) == EquipmentSlot.HAND ? EnumHand.MAIN_HAND
                    : EnumHand.OFF_HAND;

            Location eyeLoc = event.getPlayer().getEyeLocation();
            EntityPlayer human = ((CraftPlayer) event.getPlayer()).getHandle();

            MovingObjectPositionBlock MOPB = new MovingObjectPositionBlock(NMSUtils.LocToVec(eyeLoc),
                    human.getDirection(), NMSUtils.BlockToBlockPos(pblock), false);

            RayTraceResult rtr = pblock.getWorld().rayTraceBlocks(eyeLoc,
                    event.getPlayer().getLocation().getDirection(), 8, FluidCollisionMode.NEVER, true);

            Location interactionPoint = rtr.getHitPosition().subtract(rtr.getHitBlock().getLocation().toVector())
                    .toLocation(event.getPlayer().getWorld());

            if (Tag.STAIRS.isTagged(item.getType())) {
                if (!REPLACE.contains(pblock.getType()))
                    return;
                nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
                Stairs data = ((Stairs) pblock.getBlockData());
                data.setHalf(
                        (interactionPoint.getY() < 0.5d && interactionPoint.getY() >= 0.0d) ? Bisected.Half.BOTTOM : Bisected.Half.TOP);
                pblock.setBlockData(data);
            } else if (Tag.SLABS.isTagged(item.getType())
                    && (REPLACE.contains(pblock.getType()) || pblock.getType().equals(item.getType()))) {
                Slab.Type dataType = pblock.getType() == item.getType() ? Slab.Type.DOUBLE
                        : (interactionPoint.getY() > 0.0d && interactionPoint.getY() < 0.5d)
                        || interactionPoint.getY() == 1.0d ? Slab.Type.BOTTOM : Slab.Type.TOP;
                if (dataType != Slab.Type.DOUBLE)
                    nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
                Slab data = (Slab) pblock.getBlockData();
                data.setType(dataType);
                pblock.setBlockData(data);
            } else if (REPLACE.contains(pblock.getType()))
                nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);

        }

    }

    private void customBlockPlaceCheck(BlockPlaceEvent event) {
        ItemStack item = ItemUtils.getBlockInHand(event.getPlayer().getInventory());

        if (ItemUtils.isAirOrNull(item) || item.getType() != Material.BARRIER)
            return;
        if (!event.getBlock().getWorld().getNearbyEntities(event.getBlock().getBoundingBox()).isEmpty()) {
            event.setCancelled(true);
            return;
        }
        CustomBlock CB = CustomBlock.getCustomBlockbyId(ItemUtils.getItemId(item));
        if (CB == null)
            return;
        CB.place(event);
    }

    public void updateAndCheck(Location loc) {
        Block b = loc.getBlock().getRelative(BlockFace.UP);
        if (b.getType() == Material.NOTE_BLOCK)
            b.getState().update(true, true);
        Block nextBlock = b.getRelative(BlockFace.UP);
        if (nextBlock.getType() == Material.NOTE_BLOCK)
            updateAndCheck(b.getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.NOTE_BLOCK
                || event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        NoteBlock NBData = (NoteBlock) event.getBlock().getBlockData();
        if (!(event.getBlock().getBlockData() instanceof NoteBlock))
            return;
        CustomBlock CB = CustomBlock.getCustomBlockbyData(NBData);
        if (CB == null)
            return;
        event.setDropItems(false);
        CB.mine(event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        ArrayList<Block> blockList = new ArrayList<>(event.blockList());
        // A concurrent modification exception occurs when you edit a list whilst
        // looping through it

        for (Block b : blockList)
            if (b.getType() == Material.NOTE_BLOCK && isCustomBlock(b)) {
                event.blockList().remove(b);
                CustomBlock CB = CustomBlock.getCustomBlockbyData((NoteBlock) b.getBlockData());
                if (CB == null)
                    return;
                CB.mine(new BlockBreakEvent(b, null));
                // b.getDrops().add(new ItemStack(Material.DIAMOND));
            }
    }

    private boolean isCustomBlock(Block b) {
        if (!(b.getBlockData() instanceof NoteBlock))
            return false;
        NoteBlock data = (NoteBlock) b.getBlockData();
        return !data.getNote().equals(new Note(0));
    }
}
