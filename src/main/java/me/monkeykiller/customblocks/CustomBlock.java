package me.monkeykiller.customblocks;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomBlock {
    public static List<CustomBlock> REGISTRY = new ArrayList<>();

    public String id;
    public Instrument instrument;
    public int note;
    public boolean powered;

    public CustomBlock(@NotNull String id, @NotNull Instrument instrument, @NotNull int note, @NotNull boolean powered) {
        this.id = id;
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
        REGISTRY.add(this);
    }

    public static CustomBlock getCustomBlockbyId(String id) {
        for (CustomBlock CB : REGISTRY)
            if (CB.id.equals(id))
                return CB;
        return null;
    }

    public static CustomBlock getCustomBlockbyData(NoteBlock data) {
        for (CustomBlock CB : REGISTRY)
            if (CB.compareData(data))
                return CB;
        return null;
    }

    public void place(BlockPlaceEvent event) {
        Block block = event.getBlock();
        block.setType(Material.NOTE_BLOCK, false);
        if (!(block.getBlockData() instanceof NoteBlock))
            return;
        NoteBlock state = (NoteBlock) block.getBlockData();
        state.setInstrument(instrument);
        state.setNote(new Note(note));
        state.setPowered(powered);
        block.setBlockData(state);

    }

    public void mine(BlockBreakEvent event) {
    }

    public boolean compareData(NoteBlock data) {
        return data.getInstrument() == instrument && data.getNote().equals(new Note(note)) && data.isPowered() == powered;
    }

    // SERIALIZERS
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id);
        serialized.put("instrument", this.instrument.toString());
        serialized.put("note", this.note);
        serialized.put("powered", this.powered);
        return serialized;
    }

    public static CustomBlock deserialize(Map<String, Object> deserialize) {
        return new CustomBlock((String) deserialize.get("id"), Instrument.valueOf((String) deserialize.get("instrument")), (int) deserialize.get("note"), (boolean) deserialize.get("powered"));
    }
}
