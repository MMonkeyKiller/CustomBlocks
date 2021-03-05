package me.monkeykiller.customblocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Map;

public final class Main extends JavaPlugin {

    public PluginDescriptionFile pdfFile = this.getDescription();
    public FileConfiguration config = this.getConfig();
    public configData configData = new configData();

    public final class configData {
        public boolean debug_mode;
        public Material cbiMaterial;
        public ArrayList<Map<String, Object>> blocks;

        @SuppressWarnings("unchecked")
        public void loadConfig() {
            this.debug_mode = config.getBoolean("debug_mode");
            if (debug_mode)
                getLogger().warning("Warning! debug_mode has been enabled.");
            cbiMaterial = Material.valueOf(config.getString("cb_item_material"));
            blocks = (ArrayList<Map<String, Object>>) config.getList("blocks");//Objects.requireNonNull(config.getConfigurationSection("blocks")).getKeys(false);

            loadBlocks();
            getLogger().info("Loaded " + CustomBlock.REGISTRY.size() + " Custom Blocks");
            for (CustomBlock CB : CustomBlock.REGISTRY)
                Bukkit.getLogger().info(String.format(" - %s", CB.id));
            getLogger().info("Configuration loaded");
        }

        public void loadBlocks() throws NullPointerException {
            try {
                CustomBlock.REGISTRY.clear();
                for (Map<String, Object> map : blocks)
                    CustomBlock.deserialize(map);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled (v." + pdfFile.getVersion() + ")");
        setupEvents();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configData.loadConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    private void setupEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);
    }
}
