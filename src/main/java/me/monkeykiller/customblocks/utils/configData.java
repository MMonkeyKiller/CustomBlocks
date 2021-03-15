package me.monkeykiller.customblocks.utils;

import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class configData {
    public Main plugin;
    //public FileConfiguration config;

    public boolean debug_mode;
    public String prefix;
    public String cbksGUITitle;
    public Material cbiMaterial;
    public ArrayList<Material> clickable = new ArrayList<>();
    public ArrayList<Map<String, Object>> blocks;

    public configData(Main plugin) {
        this.plugin = plugin;
        //this.config = plugin.getConfig();
        //loadConfig();
    }

    public void loadConfig() {
        loadConfig(plugin.getConfig());
        Bukkit.getLogger().info(prefix + "Configuration loaded");
    }

    public void loadConfig(FileConfiguration config) throws NullPointerException {
        try {
            //config = plugin.getConfig();
            this.prefix = Main.colorify("&a" + plugin.pdfFile.getName() + "&8> &7");    // TEMPORAL, WAITING FOR messages.yml
            this.cbksGUITitle = Main.colorify("&6Custom Blocks GUI");
            debug_mode = plugin.getConfig().getBoolean("debug_mode");
            if (debug_mode) plugin.getLogger().warning("Warning! debug_mode has been enabled.");

            cbiMaterial = Material.valueOf(plugin.getConfig().getString("cb_item_material"));

            loadClickableMaterials();
            loadBlocks();

        } catch (Exception ex) {
            plugin.getLogger().warning("An exception ocurred while loading config.yml, disabling plugin\n");
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig(plugin.getConfig());
        Bukkit.getLogger().info(prefix + "Configuration reloaded");
    }

    public void loadBlocks() throws Exception {
        try {
            blocks = (ArrayList<Map<String, Object>>) plugin.getConfig().getList("blocks");
            CustomBlock.REGISTRY.clear();
            assert blocks != null;
            for (Map<String, Object> map : blocks)
                CustomBlock.deserialize(map);
            Bukkit.getLogger().info(prefix + "Loaded " + CustomBlock.REGISTRY.size() + " Custom Blocks");
            for (CustomBlock CB : CustomBlock.REGISTRY)
                Bukkit.getLogger().info(" - " + CB.id);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void saveBlocks() {
        plugin.getConfig().set("blocks", blocks);
        plugin.saveConfig();
    }

    public void loadClickableMaterials() {
        clickable.clear();
        for (String material : (List<String>) Objects.requireNonNull(plugin.getConfig().getList("clickable_materials")))
            clickable.add(Material.valueOf(material));
        Bukkit.getLogger().info(prefix + "Loaded " + clickable.size() + " Clickable Materials");
    }
}
