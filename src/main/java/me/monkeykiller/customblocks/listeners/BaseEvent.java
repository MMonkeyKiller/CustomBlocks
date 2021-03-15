package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class BaseEvent implements Listener {
    protected Main plugin;
    public BaseEvent() {
        this.plugin = Main.plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
