package pluginsfix.glowcasino.config;

import org.bukkit.Material;

public record RouletteReward(
        String id,
        Material material,
        String name,
        double multiplier,
        int weight
) {
}
