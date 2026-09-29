package pluginsfix.glowcasino.gui;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.text.Messages;

public final class MainMenuHolder implements CasinoHolder {

    private final Inventory inventory;
    private final CasinoConfig config;
    private final Messages messages;

    public MainMenuHolder(CasinoConfig config, Messages messages) {
        this.config = config;
        this.messages = messages;
        this.inventory = Bukkit.createInventory(this, 45, messages.getComponent("titles.main-menu"));
        render();
    }

    private void render() {
        MenuUtils.fillStandardBackground(inventory, config);

        ItemStack rouletteItem = new ItemBuilder(Material.DIAMOND)
                .name(messages.getComponent("main-menu.roulette.name"))
                .lore(messages.getComponentList("main-menu.roulette.lore"))
                .hideAllFlags()
                .build();
        inventory.setItem(21, rouletteItem);

        ItemStack minesweeperItem = new ItemBuilder(Material.TNT)
                .name(messages.getComponent("main-menu.minesweeper.name"))
                .lore(messages.getComponentList("main-menu.minesweeper.lore"))
                .hideAllFlags()
                .build();
        inventory.setItem(22, minesweeperItem);

        ItemStack horseRacesItem = new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
                .leatherColor(Color.fromRGB(139, 69, 19))
                .name(messages.getComponent("main-menu.horseraces.name"))
                .lore(messages.getComponentList("main-menu.horseraces.lore"))
                .hideAllFlags()
                .build();
        inventory.setItem(23, horseRacesItem);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
