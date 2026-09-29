package pluginsfix.glowcasino.game.horseraces;

import org.bukkit.Color;
import org.bukkit.Material;

public enum HorseColor {
    WHITE(10, "&fБелый", Color.fromRGB(240, 240, 240), Material.WHITE_STAINED_GLASS_PANE),
    LIGHT_GRAY(11, "&7Светло-серый", Color.fromRGB(170, 170, 170), Material.LIGHT_GRAY_STAINED_GLASS_PANE),
    GRAY(12, "&8Серый", Color.fromRGB(85, 85, 85), Material.GRAY_STAINED_GLASS_PANE),
    BLACK(13, "&0Чёрный", Color.fromRGB(20, 20, 20), Material.BLACK_STAINED_GLASS_PANE),
    DARK_RED(14, "&4Тёмно-красный", Color.fromRGB(139, 0, 0), Material.RED_STAINED_GLASS_PANE),
    RED(15, "&cКрасный", Color.fromRGB(255, 0, 0), Material.RED_STAINED_GLASS_PANE),
    ORANGE(16, "&6Оранжевый", Color.fromRGB(255, 140, 0), Material.ORANGE_STAINED_GLASS_PANE),
    YELLOW(19, "&eЖёлтый", Color.fromRGB(255, 255, 0), Material.YELLOW_STAINED_GLASS_PANE),
    LIME(20, "&aЛаймовый", Color.fromRGB(50, 205, 50), Material.LIME_STAINED_GLASS_PANE),
    GREEN(21, "&2Зелёный", Color.fromRGB(0, 128, 0), Material.GREEN_STAINED_GLASS_PANE),
    CYAN(22, "&3Бирюзовый", Color.fromRGB(0, 139, 139), Material.CYAN_STAINED_GLASS_PANE),
    LIGHT_BLUE(23, "&bГолубой", Color.fromRGB(30, 144, 255), Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    BLUE(24, "&9Синий", Color.fromRGB(0, 0, 255), Material.BLUE_STAINED_GLASS_PANE),
    PURPLE(25, "&5Фиолетовый", Color.fromRGB(128, 0, 128), Material.PURPLE_STAINED_GLASS_PANE),
    PINK(30, "&dРозовый", Color.fromRGB(255, 105, 180), Material.PINK_STAINED_GLASS_PANE);

    private final int selectionSlot;
    private final String displayName;
    private final Color leatherColor;
    private final Material trackPane;

    HorseColor(int selectionSlot, String displayName, Color leatherColor, Material trackPane) {
        this.selectionSlot = selectionSlot;
        this.displayName = displayName;
        this.leatherColor = leatherColor;
        this.trackPane = trackPane;
    }

    public int getSelectionSlot() {
        return selectionSlot;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getLeatherColor() {
        return leatherColor;
    }

    public Material getTrackPane() {
        return trackPane;
    }

    public static HorseColor getBySlot(int slot) {
        for (HorseColor c : values()) {
            if (c.selectionSlot == slot) {
                return c;
            }
        }
        return null;
    }
}
