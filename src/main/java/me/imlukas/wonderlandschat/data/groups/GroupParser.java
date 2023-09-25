/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 */
package me.imlukas.wonderlandschat.data.groups;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.storage.YMLBase;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class GroupParser
extends YMLBase {
    private final Map<String, String> formats = new HashMap<String, String>();
    private final FileConfiguration config = this.getConfiguration();
    private final Permission permission;

    public GroupParser(WonderlandsChatPlugin plugin) {
        super(plugin, "groups.yml");
        this.permission = plugin.getPerms();
        this.parse();
    }

    private void parse() {
        ConfigurationSection groups = this.config.getConfigurationSection("groups");
        String[] groupsArray = this.permission.getGroups();
        System.out.println("[WonderlandsChat] Parsing groups...");
        System.out.println("[WonderlandsChat] Groups found: " + Arrays.toString(groupsArray));
        for (String key : groups.getKeys(false)) {
            if (Arrays.stream(groupsArray).noneMatch(key::equals)) {
                System.out.println("[WonderlandsChat - ERROR] Group " + key + " not found! Skipping...");
                continue;
            }
            String format = groups.getString(key + ".format");
            this.formats.put(key, format);
        }

        System.out.println("[WonderlandsChat] Parsed " + this.formats.size() + " groups.");
    }

    public void add(String group, String format) {
        this.formats.put(group, format);
    }

    public void remove(String group) {
        this.formats.remove(group);
    }

    public String getFormat(String group) {
        return this.formats.getOrDefault(group,this.formats.getOrDefault("default","&7%player%"));
    }

    public Map<String, String> getFormats() {
        return this.formats;
    }
}

