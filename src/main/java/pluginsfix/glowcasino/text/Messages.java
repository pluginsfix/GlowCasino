package pluginsfix.glowcasino.text;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Messages {

    private final Plugin plugin;
    private FileConfiguration config;

    public Messages(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getRaw(String key) {
        return config.getString(key, "");
    }

    public List<String> getRawList(String key) {
        List<String> list = config.getStringList(key);
        if (list.isEmpty() && config.isString(key)) {
            return List.of(config.getString(key));
        }
        return list;
    }

    public Component getComponent(String key, Map<String, String> placeholders) {
        String text = getRaw(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return Formatter.format(text);
    }

    public Component getComponent(String key) {
        return getComponent(key, Collections.emptyMap());
    }

    public List<Component> getComponentList(String key, Map<String, String> placeholders) {
        List<String> rawList = getRawList(key);
        List<Component> components = new ArrayList<>(rawList.size());
        for (String line : rawList) {
            String processed = line;
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    processed = processed.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            }
            components.add(Formatter.format(processed));
        }
        return components;
    }

    public List<Component> getComponentList(String key) {
        return getComponentList(key, Collections.emptyMap());
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        if (config.isList(key)) {
            List<Component> lines = getComponentList(key, placeholders);
            for (Component line : lines) {
                sender.sendMessage(line);
            }
        } else {
            Component component = getComponent(key, placeholders);
            sender.sendMessage(component);
        }
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Collections.emptyMap());
    }
}
