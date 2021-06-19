package me.monkeykiller.customblocks.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CBLoadEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    protected boolean reloaded;

    /**
     * It launches when the plugin config is loaded
     *
     * @param reloaded By /cb reload command
     */
    public CBLoadEvent(boolean reloaded) {
        this.reloaded = reloaded;
    }

    public boolean isReloaded() {
        return reloaded;
    }

    //

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
