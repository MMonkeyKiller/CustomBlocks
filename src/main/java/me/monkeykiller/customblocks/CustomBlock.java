package me.monkeykiller.customblocks;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.ItemUtils;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomBlock {
    public static List<CustomBlock> REGISTRY = new ArrayList<>();

    public String id;
    public int itemModelData;
    public Instrument instrument;
    public int note;
    public boolean powered;

    public Material cbItem = null;

    public CustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered) throws Exception {
        if (getCustomBlockbyId(id) != null)
            throw new Exception("CustomBlock with id \"" + id + "\" already exists!");
        if (getCustomBlockbyData(instrument, note, powered) != null)
            throw new Exception("CustomBlock with id \"" + id + "\" has the same data as \"" + Objects.requireNonNull(getCustomBlockbyData(instrument, note, powered)).id + "\"");

        this.id = id;
        this.itemModelData = itemModelData;
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
        REGISTRY.add(this);
    }

    public CustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered, @Nullable Material cbItem) throws Exception {
        this(id, itemModelData, instrument, note, powered);
        if (cbItem != null)
            this.cbItem = cbItem;
    }

    public static CustomBlock getCustomBlockbyId(String id) {
        for (CustomBlock CB : REGISTRY)
            if (CB.id.equalsIgnoreCase(id)) return CB;
        return null;
    }

    public static CustomBlock getCustomBlockbyItem(@NotNull ItemStack item) {
        String itemId = ItemUtils.getItemId(item);
        if (itemId == null) return null;
        return getCustomBlockbyId(itemId);
    }

    public static CustomBlock getCustomBlockbyData(NoteBlock data) {
        for (CustomBlock CB : REGISTRY)
            if (CB.compareData(data))
                return CB;
        return null;
    }

    public static CustomBlock getCustomBlockbyData(@NotNull Instrument instrument, int note, boolean powered) {
        for (CustomBlock CB : REGISTRY)
            if (CB.compareData(instrument, note, powered))
                return CB;
        return null;
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

    public boolean compareData(@NotNull Instrument instrument, int note, boolean powered) {
        return instrument == this.instrument && new Note(note).equals(new Note(this.note)) && powered == this.powered;
    }

    public ItemStack getItemStack() {
        NBTItem nbtc = new NBTItem(new ItemStack(cbItem != null ? cbItem : config.cbiMaterial));
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

        if (this.cbItem != null) serialized.put("cbItem", cbItem);
        return serialized;
    }

    public static void deserialize(Map<String, Object> deserialize) {
        try {
            new CustomBlock(
                    (String) deserialize.get("id"),
                    (int) deserialize.get("itemModelData"),
                    Instrument.valueOf((String) deserialize.get("instrument")),
                    (int) deserialize.get("note"),
                    (boolean) deserialize.get("powered"),
                    deserialize.get("cbItem") != null ? Material.valueOf((String) deserialize.get("cbItem")) : null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
