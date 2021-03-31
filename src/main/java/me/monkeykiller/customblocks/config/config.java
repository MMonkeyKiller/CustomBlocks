package me.monkeykiller.customblocks.config;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class config {
    public static boolean debug_mode;
    public static class prefixes {
        public static String prefix;
        public static String warn;
    }
    public static String cbksGUITitle;
    public static Material cbiMaterial;
    public static List<Material> clickable = new ArrayList<>();
    public static List<Map<String, Object>> blocks = new ArrayList<>();
}
