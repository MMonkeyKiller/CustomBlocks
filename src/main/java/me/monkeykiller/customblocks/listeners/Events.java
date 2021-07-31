package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.CustomBlocksPlugin;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.blocks.CustomBlockData;
import me.monkeykiller.customblocks.blocks.Directional;
import me.monkeykiller.customblocks.blocks.Interactable;
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
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class Events implements Listener {
    private final List<Material> REPLACE = Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR,
            Material.GRASS, Material.SEAGRASS, Material.WATER, Material.LAVA);
    private final List<UUID> antiFastPlace = new ArrayList<>();


    @EventHandler(ignoreCancelled = true)
    private void onBlockPhysics(BlockPhysicsEvent event) {
        Block b = event.getBlock(),
                topBlock = b.getRelative(BlockFace.UP),       // Block (y + 1)
                bottomBlock = b.getRelative(BlockFace.DOWN);  // Block (y - 1)

        if (topBlock.getType() == Material.NOTE_BLOCK) {
            updateAndCheck(b.getLocation());
            if (Tag.DOORS.isTagged(b.getType()) && b.getBlockData() instanceof Door) {
                Door data = (Door) b.getBlockData();
                if (!data.getHalf().equals(Bisected.Half.TOP)) return;
                Door d = (Door) bottomBlock.getBlockData();
                d.setOpen(data.isOpen());
                bottomBlock.setBlockData(d);
                bottomBlock.getState().update(true, false);
            }
            event.setCancelled(true);
        }
        if (b.getType() == Material.NOTE_BLOCK) event.setCancelled(true);
        if (!Tag.SIGNS.isTagged(b.getType()) &&
                !b.getType().equals(Material.LECTERN))
            b.getState().update(true, false);
    }

    @EventHandler
    public void onNoteBlockPhysics(BlockPhysicsEvent event) {
        if (!event.getBlock().getType().equals(Material.NOTE_BLOCK)) return;
        CustomBlock cb = CustomBlock.getCustomBlockbyData(new CustomBlockData(event.getBlock()));
        if (cb != null) cb.onPhysics(event.getBlock(), event.getSourceBlock());
    }

    @EventHandler
    public void onPistonExtends(BlockPistonExtendEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> b.getType().equals(Material.NOTE_BLOCK)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> b.getType().equals(Material.NOTE_BLOCK)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onNotePlay(NotePlayEvent event) {
        event.setCancelled(true);
    }

    private boolean placeAndCheckCB(PlayerInteractEvent event) {
        try {
            if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return false;
            Player p = event.getPlayer();
            PlayerInventory inv = p.getInventory();
            ItemStack item = ItemUtils.getFirstCustomBlockInHand(inv);
            if (ItemUtils.isAirOrNull(item) || event.getClickedBlock() == null) return false;

            CustomBlock CB = CustomBlock.getCustomBlockbyItem(item);
            if (CB == null) return false;
            event.setCancelled(true);

            if ((event.getClickedBlock().getType().isInteractable() &&
                    !event.getClickedBlock().getType().equals(Material.NOTE_BLOCK)) && !p.isSneaking()) {
                event.setCancelled(false);
                return false;
            }

            Block placedBlock = event.getClickedBlock().getRelative(event.getBlockFace());
            EnumHand handSlot = NMSUtils.parseEnumHand(ItemUtils.getEquipmentSlot(inv, item));
            EntityPlayer human = NMSUtils.parseHuman(p);

            if (REPLACE.contains(event.getClickedBlock().getType()) || (event.getClickedBlock().getType().equals(Material.SNOW) && ((Snow) event.getClickedBlock().getBlockData()).getLayers() == 1))
                placedBlock = event.getClickedBlock();
            else if (!REPLACE.contains(placedBlock.getType()))
                return false;

            BlockPlaceEvent e = new BlockPlaceEvent(placedBlock, placedBlock.getState(), event.getClickedBlock(), item, event.getPlayer(), true, Objects.requireNonNull(ItemUtils.getEquipmentSlot(inv, item)));
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) return false;

            if (!Utils.getEntitiesOnBlock(e.getBlock(), en -> en instanceof LivingEntity).isEmpty())
                return false;

            if (CB instanceof Directional) {
                float pitch = event.getPlayer().getLocation().getPitch();
                BlockFace face = event.getPlayer().getFacing().getOppositeFace();
                if (pitch <= -45) face = BlockFace.DOWN;
                else if (pitch >= 45) face = BlockFace.UP;
                ((Directional) CB).place(e.getBlock(), face);
            } else CB.place(e);
            if (!Utils.getEntitiesOnBlock(placedBlock, entity -> !(entity instanceof Item || entity instanceof ItemFrame)).isEmpty())
                return false;

            human.swingHand(handSlot, true);

            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0)
                item.setType(Material.AIR);

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        assert event.getClickedBlock() != null;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.getClickedBlock().getType().equals(Material.NOTE_BLOCK)) {
            CBCooldown(event);
            return;
        }

        event.setCancelled(true);
        if (!antiFastPlace.contains(event.getPlayer().getUniqueId()) && CustomBlock.isCustomBlock(event.getClickedBlock()) && !(event.getPlayer().isSneaking() && !ItemUtils.isAirOrNull(event.getPlayer().getInventory().getItemInMainHand()))) {
            CustomBlock cb = CustomBlock.getCustomBlockbyData(new CustomBlockData((NoteBlock) event.getClickedBlock().getBlockData()));
            if (cb instanceof Interactable) {
                ((Interactable) cb).onInteract(event.getPlayer(), event.getClickedBlock());
                return;
            }
        }

        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack item = ItemUtils.getBlockOrCustomBlockInHand(inv);

        if (item == null) {
            if (player.isSneaking() && !ItemUtils.isAirOrNull(inv.getItemInMainHand()))
                event.setCancelled(false);
            return;
        }

        Block pblock = event.getClickedBlock().getRelative(event.getBlockFace());
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        EnumHand hand = NMSUtils.parseEnumHand(ItemUtils.getEquipmentSlot(inv, item));

        Location eyeLoc = player.getEyeLocation();
        EntityPlayer human = NMSUtils.parseHuman(player);

        MovingObjectPositionBlock MOPB = NMSUtils.getMOPB(player, pblock.getLocation(), false);
        Location point = Utils.getInteractionPoint(eyeLoc, 8, true);
        assert point != null;

        if (REPLACE.contains(event.getClickedBlock().getType()) || (event.getClickedBlock().getType().equals(Material.SNOW) && ((Snow) event.getClickedBlock().getBlockData()).getLayers() == 1))
            pblock = event.getClickedBlock();
        else if (!REPLACE.contains(pblock.getType())) return;

        if (ItemUtils.getFirstCustomBlockInHand(inv) != null) CBCooldown(event);
        if (Tag.STAIRS.isTagged(item.getType())) {
            nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
            Stairs data = ((Stairs) pblock.getBlockData());
            data.setHalf(point.getY() < .5d && point.getY() >= 0d
                    ? Bisected.Half.BOTTOM
                    : Bisected.Half.TOP);
            pblock.setBlockData(data);
        } else if (Tag.SLABS.isTagged(item.getType()) || pblock.getType().equals(item.getType())) {
            Slab.Type dataType;
            if (pblock.getType() == item.getType()) {
                dataType = Slab.Type.DOUBLE;
            } else {
                if ((point.getY() > 0d && point.getY() < .5d)
                        || point.getY() == 1d) {
                    dataType = Slab.Type.BOTTOM;
                } else dataType = Slab.Type.TOP;
                nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
            }

            Slab data = (Slab) pblock.getBlockData();
            data.setType(dataType);
            pblock.setBlockData(data);
        } else nmsItem.placeItem(new ItemActionContext(human, hand, MOPB), hand);
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
        if (event.isCancelled()) return;
        NoteBlock NBData = (NoteBlock) event.getBlock().getBlockData();
        CustomBlock CB = CustomBlock.getCustomBlockbyData(new CustomBlockData(NBData));
        if (CB == null) return;
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
                    CustomBlock CB = CustomBlock.getCustomBlockbyData(new CustomBlockData((NoteBlock) b.getBlockData()));
                    if (CB != null) CB.mine(b);
                    b.setType(Material.AIR);
                });
    }

    public void CBCooldown(PlayerInteractEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (antiFastPlace.contains(uuid) || !placeAndCheckCB(event)) return;
        antiFastPlace.add(uuid);
        Bukkit.getScheduler().runTaskLater(CustomBlocksPlugin.plugin, () -> antiFastPlace.remove(uuid), 2L);
    }
}
