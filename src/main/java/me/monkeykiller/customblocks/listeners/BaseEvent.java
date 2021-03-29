package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.CustomBlocks;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class BaseEvent implements Listener {
    protected CustomBlocks plugin;
    public BaseEvent() {
        this.plugin = CustomBlocks.plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
