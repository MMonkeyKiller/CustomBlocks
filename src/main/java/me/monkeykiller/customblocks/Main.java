package me.monkeykiller.customblocks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    PluginDescriptionFile pdfFile = this.getDescription();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled (v." + pdfFile.getVersion() + ")");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }
}
