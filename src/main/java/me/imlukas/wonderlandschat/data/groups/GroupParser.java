package me.imlukas.wonderlandschat.data.groups;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.storage.YMLBase;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GroupParser extends YMLBase {
    private final Map<String, String> formats = new HashMap<>();
    private final FileConfiguration config;
    private final Permission permission;

    public GroupParser(WonderlandsChatPlugin plugin) {
        super(plugin, "groups.yml");
        this.config = getConfiguration();
        this.permission = plugin.getPerms();
        parse();
    }


    private void parse() {
        ConfigurationSection groups = config.getConfigurationSection("groups");
        String[] groupsArray = permission.getGroups();
        System.out.println("[WonderlandsChat] Parsing groups...");
        System.out.println("[WonderlandsChat] Groups found: " + Arrays.toString(groupsArray));
        for (String key : groups.getKeys(false)) {
            if (Arrays.stream(groupsArray).noneMatch(key::equals)) {
                System.out.println(("[WonderlandsChat - ERROR] Group " + key + " not found! Skipping..."));
                continue;
            }
            String format = groups.getString(key + ".format");

            formats.put(key, format);
        }

        System.out.println("[WonderlandsChat] Parsed " + formats.size() + " groups.");
    }

    public void add(String group, String format) {
        formats.put(group, format);
    }

    public void remove(String group) {
        formats.remove(group);
    }

    public String getFormat(String group) {
        return formats.get(group);
    }

    public Map<String, String> getFormats() {
        return formats;
    }
}
