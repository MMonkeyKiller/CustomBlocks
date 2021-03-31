package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.CBPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class BaseEvent implements Listener {
    protected CBPlugin plugin;
    public BaseEvent() {
        this.plugin = CBPlugin.plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
