package pluginsfix.glowcasino.config;

import org.bukkit.Material;

public enum CurrencyType {
    VAULT(Material.SUNFLOWER, "Монеты", "coins"),
    DIAMONDS(Material.DIAMOND, "Алмазы", "diamond"),
    SILVER(Material.IRON_NUGGET, "Серебро", "silver"),
    NETHERITE(Material.NETHERITE_INGOT, "Незерит", "netherite");

    private final Material defaultMaterial;
    private final String defaultName;
    private final String messageKey;

    CurrencyType(Material defaultMaterial, String defaultName, String messageKey) {
        this.defaultMaterial = defaultMaterial;
        this.defaultName = defaultName;
        this.messageKey = messageKey;
    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
