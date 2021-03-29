package me.monkeykiller.customblocks;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.monkeykiller.customblocks.utils.config;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomBlock {
    public static List<CustomBlock> REGISTRY = new ArrayList<>();

    public String id;
    public int itemModelData;
    public Instrument instrument;
    public int note;
    public boolean powered;

    public CustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered) throws Exception {
        if (getCustomBlockbyId(id) != null)
            throw new Exception("CustomBlock " + id + " already exists!");

        this.id = id;
        this.itemModelData = itemModelData;
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
        REGISTRY.add(this);
    }

    public static CustomBlock getCustomBlockbyId(String id) {
        for (CustomBlock CB : REGISTRY)
            if (CB.id.equalsIgnoreCase(id)) return CB;
        return null;
    }

    public static CustomBlock getCustomBlockbyItem(@NotNull ItemStack item) {
        String itemId = Utils.Item.getItemId(item);
        if (itemId == null) return null;
        return getCustomBlockbyId(itemId);
    }

    public static CustomBlock getCustomBlockbyData(NoteBlock data) {
        return REGISTRY.stream()
                .filter(CB -> CB.compareData(data))
                .collect(Collectors.toList()).get(0);
    }

    public void place(BlockPlaceEvent event) {
        place(event.getBlock());
    }

    public void place(Block block) {
        block.setType(Material.NOTE_BLOCK, false);
        assert block.getBlockData() instanceof NoteBlock;

        NoteBlock state = (NoteBlock) block.getBlockData();
        state.setInstrument(instrument);
        state.setNote(new Note(note));
        state.setPowered(powered);
        block.setBlockData(state);
    }

    public void mine(BlockBreakEvent event) {
        mine(event.getBlock());
        event.setExpToDrop(0);
    }

    public void mine(@NotNull Block block) {
        block.getWorld().dropItemNaturally(block.getLocation(), this.getItemStack());
    }

    public boolean compareData(NoteBlock data) {
        return data.getInstrument() == instrument && data.getNote().equals(new Note(note)) && data.isPowered() == powered;
    }

    public ItemStack getItemStack() {
        NBTItem nbtc = new NBTItem(new ItemStack(config.cbiMaterial));
        nbtc.mergeCompound(new NBTContainer(String.format("{CustomModelData: %s, display:{Name:'{\"translate\":\"%s\", \"italic\": false}'}, ItemId:\"%s\"}", itemModelData, "customblocks.item." + id + ".name", id)));
        return nbtc.getItem();
    }

    public static boolean isCustomBlock(Block b) {
        assert b.getBlockData() instanceof NoteBlock;
        NoteBlock data = (NoteBlock) b.getBlockData();
        return !data.getNote().equals(new Note(0));
    }

    public static boolean isCustomBlock(String id) {
        return getCustomBlockbyId(id) != null;
    }

    // SERIALIZERS
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id);
        serialized.put("itemModelData", this.itemModelData);
        serialized.put("instrument", this.instrument.toString());
        serialized.put("note", this.note);
        serialized.put("powered", this.powered);
        return serialized;
    }

    public static void deserialize(Map<String, Object> deserialize) {
        try {
            new CustomBlock((String) deserialize.get("id"), (int) deserialize.get("itemModelData"), Instrument.valueOf((String) deserialize.get("instrument")), (int) deserialize.get("note"), (boolean) deserialize.get("powered"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
