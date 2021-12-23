package me.monkeykiller.customblocks.config;

import me.monkeykiller.customblocks.CustomBlocksPlugin;
import me.monkeykiller.customblocks.api.CustomBlocksLoadEvent;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unchecked")
public class configData {
    protected static CustomBlocksPlugin plugin;

    public void setPlugin(CustomBlocksPlugin javaPlugin) {
        plugin = javaPlugin;
    }

    public void loadConfig() {
        loadConfig(false);
    }

    public void loadConfig(boolean reloaded) {
        try {
            config.prefixes.prefix = getColorizedConfig("prefixes.prefix");    // TODO: TEMPORAL, WAITING FOR messages.yml
            config.prefixes.warn = getColorizedConfig("prefixes.warn");
            config.cbksGUITitle = getColorizedConfig("gui_title");
            config.debug_mode = config().getBoolean("debug_mode");
            if (config.debug_mode)
                Utils.log(config.prefixes.warn + "debug_mode has been enabled.");
            config.cbiMaterial = Material.valueOf(config().getString("cb_item_material"));
            loadBlocks();
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.log(config.prefixes.warn + "An exception ocurred while loading config.yml, disabling plugin\n");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        Bukkit.getPluginManager().callEvent(new CustomBlocksLoadEvent(reloaded));
    }

    public void reloadConfig() {
        plugin.reloadConfig();  // reload config.yml file
        loadConfig(true);
    }

    public void loadBlocks() {
        try {
            config.blocks = (ArrayList<Map<String, Object>>) config().getList("blocks");
            CustomBlock.clear();
            assert config.blocks != null && !config.blocks.isEmpty();
            config.blocks.forEach(CustomBlock::deserialize);
            Utils.log(config.prefixes.prefix + "Loaded " + CustomBlock.getRegistry().size() + " Custom Blocks");
            CustomBlock.getRegistry().stream()
                    .map(cb -> " - " + cb.getId() + (cb.getMaterial() != null ? " (" + cb.getMaterial() + ")" : ""))
                    .forEach(Utils::log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBlocks() {
        config().set("blocks", config.blocks);
        plugin.saveConfig();
    }

    public static FileConfiguration config() {
        return plugin.getConfig();
    }

    public static String getColorizedConfig(@NotNull String configKey) throws ConfigException {
        if (!config().contains(configKey)) throw new ConfigException(configKey, "Config not found");
        if (!config().isString(configKey))
            throw new ConfigException(configKey, "Invalid config type, Expected: String");
        return Utils.colorize("" + config().getString(configKey));
    }

    public static class ConfigException extends Exception {
        public ConfigException(String key, String error) {
            super("Error while loading '" + key + "': " + error);
        }
    }
}
