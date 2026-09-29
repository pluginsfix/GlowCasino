package pluginsfix.glowcasino.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.text.Messages;

import java.util.Set;

public final class MenuUtils {

    private static final Set<Integer> DARK_SLOTS_45 = Set.of(
            0, 1, 7, 8,
            9, 17,
            18, 26,
            27, 35,
            36, 37, 43, 44
    );

    private MenuUtils() {
    }

    public static ItemStack createDecorativePane(Material material, String title) {
        return new ItemBuilder(material)
                .name(title)
                .hideAllFlags()
                .build();
    }

    public static void fillStandardBackground(Inventory inventory, CasinoConfig config) {
        int size = inventory.getSize();
        String paneTitle = config.decorativeGlassTitle();
        ItemStack darkPane = createDecorativePane(Material.BROWN_STAINED_GLASS_PANE, paneTitle);
        ItemStack orangePane = createDecorativePane(Material.ORANGE_STAINED_GLASS_PANE, paneTitle);

        for (int i = 0; i < size; i++) {
            if (DARK_SLOTS_45.contains(i)) {
                inventory.setItem(i, darkPane);
            } else {
                inventory.setItem(i, orangePane);
            }
        }
    }

    public static void setBackButton(Inventory inventory, int slot, Messages messages) {
        ItemStack backItem = new ItemBuilder(Material.ARROW)
                .name(messages.getComponent("common.back-button.name"))
                .lore(messages.getComponentList("common.back-button.lore"))
                .hideAllFlags()
                .build();
        inventory.setItem(slot, backItem);
    }
}
