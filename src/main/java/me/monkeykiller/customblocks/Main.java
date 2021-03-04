package me.monkeykiller.customblocks;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    PluginDescriptionFile pdfFile = this.getDescription();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled (v." + pdfFile.getVersion() + ")");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
