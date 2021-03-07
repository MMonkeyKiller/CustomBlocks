package me.monkeykiller.customblocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public final class Main extends JavaPlugin implements CommandExecutor {

    public PluginDescriptionFile pdfFile = this.getDescription();
    public FileConfiguration config = this.getConfig();
    public configData configData = new configData();

    public Commands commands = new Commands(this);

    public final class configData {
        public boolean debug_mode;
        public String prefix = colorify("&e" + pdfFile.getName() + " &8> &7");
        public Material cbiMaterial;
        public ArrayList<Map<String, Object>> blocks;

        @SuppressWarnings("unchecked")
        public void loadConfig() {
            this.debug_mode = config.getBoolean("debug_mode");
            if (debug_mode)
                getLogger().warning("Warning! debug_mode has been enabled.");
            cbiMaterial = Material.valueOf(config.getString("cb_item_material"));
            blocks = (ArrayList<Map<String, Object>>) config.getList("blocks");

            loadBlocks();
            Bukkit.getLogger().info(configData.prefix + "Loaded " + CustomBlock.REGISTRY.size() + " Custom Blocks");
            for (CustomBlock CB : CustomBlock.REGISTRY)
                Bukkit.getLogger().info(String.format(" - %s", CB.id));
            Bukkit.getLogger().info(configData.prefix + "Configuration loaded");
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
        Bukkit.getLogger().info(configData.prefix + "Plugin enabled (v." + pdfFile.getVersion() + ")");
        setupEvents();
        loadConfig();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(configData.prefix + "Plugin disabled");
    }

    public void loadConfig() {
        this.config = getConfig();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configData.loadConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return this.commands.onCommand(sender, command, label, args);
    }

    private void setupEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);
    }

    public static String colorify(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
