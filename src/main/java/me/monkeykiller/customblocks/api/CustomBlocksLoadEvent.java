package me.monkeykiller.customblocks.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CustomBlocksLoadEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    protected boolean reloaded;

    public CustomBlocksLoadEvent(boolean reloaded) {
        this.reloaded = reloaded;
    }

    public boolean isReloaded() {
        return reloaded;
    }

    //

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
