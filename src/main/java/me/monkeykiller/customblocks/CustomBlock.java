package me.monkeykiller.customblocks;

import de.tr7zw.nbtapi.NBTItem;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.ItemUtils;
import me.monkeykiller.customblocks.utils.Utils;
import org.apache.commons.lang.Validate;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class CustomBlock {
    public static List<CustomBlock> REGISTRY = new ArrayList<>();

    private final String id;
    private final int itemModelData, note;
    private final Instrument instrument;
    private final boolean powered;
    private final Material material;

    public CustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered, @Nullable Material material) throws Exception {
        Validate.isTrue(getCustomBlockbyId(id) == null, String.format("CustomBlock with id \"%s\" already exists!", id));
        //Validate.isTrue(getCustomBlockbyData(instrument, note, powered) == null, "CustomBlock with id \"" + id + "\" has the same data as \"" + Objects.requireNonNull(getCustomBlockbyData(instrument, note, powered)).getId() + "\"");

        this.id = id;
        this.itemModelData = itemModelData;
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
        this.material = material;
    }

    public CustomBlock(@NotNull String id, int itemModelData, @NotNull Instrument instrument, int note, boolean powered) throws Exception {
        this(id, itemModelData, instrument, note, powered, null);
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
        block.getWorld().dropItemNaturally(block.getLocation(), this.getItemStack(false));
    }

    public boolean compareData(NoteBlock data) {
        return data.getInstrument() == instrument && data.getNote().equals(new Note(note)) && data.isPowered() == powered;
    }

    public boolean compareData(@NotNull Instrument instrument, int note, boolean powered) {
        return instrument == this.instrument && new Note(note).equals(new Note(this.note)) && powered == this.powered;
    }

    public ItemStack getItemStack() {
        return getItemStack(false);
    }

    public ItemStack getItemStack(boolean visibleBlockId) {
        NBTItem nbtc = new NBTItem(new ItemStack(material != null ? material : config.cbiMaterial));
        nbtc.setString("ItemId", id);
        nbtc.addCompound("display")
                .setString("Name", String.format("{\"translate\":\"%s\", \"italic\": false}", "customblocks.item." + id + ".name"));

        ItemStack item = nbtc.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(itemModelData);
            if (visibleBlockId) meta.setLore(Collections.singletonList(Utils.colorize("&r&8" + id)));
        }
        item.setItemMeta(meta);
        return item;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public int getNote() {
        return note;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public boolean isPowered() {
        return powered;
    }

    ////

    public static CustomBlock getCustomBlockbyId(String id) {
        for (CustomBlock CB : REGISTRY)
            if (CB.id.equalsIgnoreCase(id)) return CB;
        return null;
    }

    public static CustomBlock getCustomBlockbyItem(@NotNull ItemStack item) {
        String itemId = ItemUtils.getItemId(item);
        if (itemId != null) return getCustomBlockbyId(itemId);
        return null;
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

    public static boolean isCustomBlock(Block b) {
        if (!(b.getBlockData() instanceof NoteBlock)) return false;
        return !((NoteBlock) b.getBlockData()).getNote().equals(new Note(0));
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

        if (this.material != null)
            serialized.put("cbItem", material);
        return serialized;
    }

    public static void deserialize(Map<String, Object> deserialize) {
        try {
            REGISTRY.add(new CustomBlock(
                    (String) deserialize.get("id"),
                    (int) deserialize.get("itemModelData"),
                    Instrument.valueOf((String) deserialize.get("instrument")),
                    (int) deserialize.get("note"),
                    (boolean) deserialize.get("powered"),
                    deserialize.get("cbItem") != null ? Material.valueOf((String) deserialize.get("cbItem")) : null
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
