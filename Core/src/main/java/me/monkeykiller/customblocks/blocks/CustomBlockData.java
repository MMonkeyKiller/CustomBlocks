package me.monkeykiller.customblocks.blocks;

import org.apache.commons.lang.Validate;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;

public class CustomBlockData {
    private final Instrument instrument;
    private final int note;
    private final boolean powered;

    public CustomBlockData(@NotNull Block block) {
        this((NoteBlock) block.getBlockData());
        Validate.isTrue(block.getType().equals(Material.NOTE_BLOCK), "The given block is not a noteblock");
    }

    public CustomBlockData(@NotNull NoteBlock data) {
        this.instrument = data.getInstrument();
        this.note = data.getNote().getId();
        this.powered = data.isPowered();
    }

    public CustomBlockData(@NotNull Instrument instrument, int note, boolean powered) {
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
    }

    public NoteBlock applyData(@NotNull NoteBlock data) {
        data.setInstrument(instrument);
        data.setNote(new Note(note));
        data.setPowered(powered);
        return data;
    }

    public boolean compareData(@NotNull NoteBlock data) {
        return compareData(data.getInstrument(), data.getNote().getId(), data.isPowered());
    }

    public boolean compareData(@NotNull CustomBlockData data) {
        return compareData(data.getInstrument(), data.getNote(), data.getPowered());
    }

    public boolean compareData(@NotNull Instrument instrument, int note, boolean powered) {
        return this.instrument == instrument && this.note == note && this.powered == powered;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public int getNote() {
        return note;
    }

    public boolean getPowered() {
        return powered;
    }

    @Override
    public String toString() {
        return String.format("CustomBlockData[instrument=%s, note=%s, powered=%s]", instrument, note, powered);
    }
}
