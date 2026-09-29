package pluginsfix.glowcasino.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderHook {

    private final boolean available;

    public PlaceholderHook() {
        this.available = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public boolean isAvailable() {
        return available;
    }

    public String setPlaceholders(Player player, String text) {
        if (available && player != null && text != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
