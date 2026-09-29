package pluginsfix.glowcasino.config;

import org.bukkit.Material;

public record CurrencySetting(
        boolean enabled,
        String name,
        Material item,
        double minBet,
        double maxBet,
        double step,
        double bigStep
) {
}
