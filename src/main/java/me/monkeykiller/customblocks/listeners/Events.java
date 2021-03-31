package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.ItemUtils;
import me.monkeykiller.customblocks.utils.NMSUtils;
import me.monkeykiller.customblocks.utils.Utils;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumHand;
import net.minecraft.server.v1_16_R3.ItemActionContext;
import net.minecraft.server.v1_16_R3.MovingObjectPositionBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Events extends BaseEvent {
    private final List<Material> REPLACE = Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR,
            Material.GRASS, Material.SEAGRASS, Material.SNOW, Material.WATER, Material.LAVA);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockPhysics(BlockPhysicsEvent event) {
        Block b = event.getBlock();
        Block topBlock = b.getRelative(BlockFace.UP);       // Block (y + 1)
        Block bottomBlock = b.getRelative(BlockFace.DOWN);  // Block (y - 1)

        if (topBlock.getType() == Material.NOTE_BLOCK) {
            updateAndCheck(b.getLocation());
            if (Tag.DOORS.isTagged(b.getType()) && b.getBlockData() instanceof Door) {
                Door data = (Door) b.getBlockData();
                if (!data.getHalf().equals(Bisected.Half.TOP)) return;
                // FIX: Door update issue
                Door d = (Door) bottomBlock.getBlockData();
                d.setOpen(data.isOpen());
                bottomBlock.setBlockData(d);
                //
                bottomBlock.getState().update(true, false);

            }
            event.setCancelled(true);
        }
        if (b.getType() == Material.NOTE_BLOCK) event.setCancelled(true);
        if (!Tag.SIGNS.isTagged(b.getType())) b.getState().update(true, false);

    }

    @EventHandler
    public void onPistonExtends(BlockPistonExtendEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> b.getType().equals(Material.NOTE_BLOCK)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPistonRestract(BlockPistonRetractEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> b.getType().equals(Material.NOTE_BLOCK)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onNotePlay(NotePlayEvent event) {
        event.setCancelled(true);
    }

    private void placeAndCheckCB(PlayerInteractEvent event) {
        try {
            if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

            Player p = event.getPlayer();
            PlayerInventory inv = p.getInventory();
            ItemStack item = ItemUtils.getMaterialInHand(inv, config.cbiMaterial);
            assert !ItemUtils.isAirOrNull(item) && event.getClickedBlock() != null;

            CustomBlock CB = CustomBlock.getCustomBlockbyItem(item);
            if (CB == null) return;
            event.setCancelled(true);


            if (config.clickable.contains(event.getClickedBlock().getType()) && !p.isSneaking()) {
                event.setCancelled(false);
                return;
            }

            Block placedBlock = event.getClickedBlock().getRelative(event.getBlockFace());
            Material replacedBlock = placedBlock.getType();
            EnumHand handSlot = NMSUtils.parseEnumHand(ItemUtils.getEquipmentSlot(inv, item));
            EntityPlayer human = NMSUtils.parseHuman(p);

            if (REPLACE.contains(event.getClickedBlock().getType()) || (event.getClickedBlock().getType().equals(Material.SNOW) && ((Snow) event.getClickedBlock().getBlockData()).getLayers() == 1))
                placedBlock = event.getClickedBlock();
            else if (!REPLACE.contains(placedBlock.getType()))
                return;
            placedBlock.setType(Material.BARRIER);

            BlockPlaceEvent e = new BlockPlaceEvent(placedBlock, placedBlock.getState(), event.getClickedBlock(), item, event.getPlayer(), true, Objects.requireNonNull(ItemUtils.getEquipmentSlot(inv, item)));
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) return;

            CB.place(e);
            if (!placedBlock.getWorld().getNearbyEntities(placedBlock.getBoundingBox(), entity -> !(entity instanceof Item)).isEmpty()) {
                placedBlock.setType(replacedBlock);
                return;
            }
            human.swingHand(handSlot, true);

            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0)
                item.setType(Material.AIR);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        assert event.getClickedBlock() != null;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || !event.getClickedBlock().getType().equals(Material.NOTE_BLOCK)) {
            placeAndCheckCB(event);
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack item = ItemUtils.getBlockOrCustomBlockInHand(inv);

        if (item == null) return;
        if (player.isSneaking()) {
            event.setCancelled(false);
            return;
        }

        Block pblock = event.getClickedBlock().getRelative(event.getBlockFace());

        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        EnumHand hand = NMSUtils.parseEnumHand(ItemUtils.getEquipmentSlot(inv, item));

        Location eyeLoc = player.getEyeLocation();
        EntityPlayer human = NMSUtils.parseHuman(player);

        MovingObjectPositionBlock MOPB = NMSUtils.getMOPB(player, pblock.getLocation(), false);
        Location interactionPoint = Utils.getInteractionPoint(eyeLoc, 8, true);
        assert interactionPoint != null;
        if (item.getType().equals(config.cbiMaterial))
            placeAndCheckCB(event);

        if (REPLACE.contains(event.getClickedBlock().getType()) || (event.getClickedBlock().getType().equals(Material.SNOW) && ((Snow) event.getClickedBlock().getBlockData()).getLayers() == 1))
            pblock = event.getClickedBlock();
        else if (!REPLACE.contains(pblock.getType()))
            return;

        if (Tag.STAIRS.isTagged(item.getType())) {
            nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
            Stairs data = ((Stairs) pblock.getBlockData());
            data.setHalf(
                    (interactionPoint.getY() < 0.5d && interactionPoint.getY() >= 0.0d) ? Bisected.Half.BOTTOM : Bisected.Half.TOP);
            pblock.setBlockData(data);
        } else if (Tag.SLABS.isTagged(item.getType()) || pblock.getType().equals(item.getType())) {
            Slab.Type dataType = pblock.getType() == item.getType() ? Slab.Type.DOUBLE
                    : (interactionPoint.getY() > 0.0d && interactionPoint.getY() < 0.5d)
                    || interactionPoint.getY() == 1.0d ? Slab.Type.BOTTOM : Slab.Type.TOP;
            if (dataType != Slab.Type.DOUBLE)
                nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
            Slab data = (Slab) pblock.getBlockData();
            data.setType(dataType);
            pblock.setBlockData(data);
        } else
            nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
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

        if (event.isCancelled() || !(event.getBlock().getBlockData() instanceof NoteBlock)) return;
        NoteBlock NBData = (NoteBlock) event.getBlock().getBlockData();
        CustomBlock CB = CustomBlock.getCustomBlockbyData(NBData);
        assert CB != null;
        event.setDropItems(false);
        event.setExpToDrop(0);
        CB.mine(event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        ArrayList<Block> blockList = new ArrayList<>(event.blockList());
        // A concurrent modification exception occurs when you edit a list whilst
        // looping through it

        blockList.stream()
                .filter(b -> b.getType() == Material.NOTE_BLOCK && CustomBlock.isCustomBlock(b))
                .forEach(b -> {
                    event.blockList().remove(b);
                    CustomBlock CB = CustomBlock.getCustomBlockbyData((NoteBlock) b.getBlockData());
                    if (CB != null)
                        CB.mine(b);
                    b.setType(Material.AIR);
                });
    }
}
