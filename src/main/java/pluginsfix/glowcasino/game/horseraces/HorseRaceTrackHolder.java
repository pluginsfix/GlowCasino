package pluginsfix.glowcasino.game.horseraces;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.gui.CasinoHolder;
import pluginsfix.glowcasino.gui.ItemBuilder;
import pluginsfix.glowcasino.gui.MenuUtils;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.List;
import java.util.Map;

public final class HorseRaceTrackHolder implements CasinoHolder {

    private final Inventory inventory;
    private final Player player;
    private final HorseRaceGame game;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;

    public HorseRaceTrackHolder(Player player, HorseRaceGame game, CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.player = player;
        this.game = game;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
        this.inventory = Bukkit.createInventory(this, 54, messages.getComponent("titles.horse-race"));
        initBottomRow();
    }

    private void initBottomRow() {
        String paneTitle = config.decorativeGlassTitle();
        ItemStack darkPane = MenuUtils.createDecorativePane(Material.BROWN_STAINED_GLASS_PANE, paneTitle);

        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, darkPane);
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, darkPane);
        }

        ItemStack chestItem = new ItemBuilder(Material.CHEST)
                .name("&6Информация")
                .loreStrings(List.of(
                        "&7Следите за забегом своей лошадки!"
                ))
                .hideAllFlags()
                .build();
        inventory.setItem(48, chestItem);

        updateCollectButton();
    }

    public void updateTrack(List<HorseColor> horses, int[] positions) {
        String paneTitle = config.decorativeGlassTitle();

        for (int lane = 0; lane < 4; lane++) {
            HorseColor horse = horses.get(lane);
            int horsePos = positions[lane];
            int rowStart = lane * 9;

            ItemStack trackPane = MenuUtils.createDecorativePane(horse.getTrackPane(), paneTitle);
            ItemStack horseIcon = new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
                    .leatherColor(horse.getLeatherColor())
                    .name("&6Конь: " + horse.getDisplayName())
                    .hideAllFlags()
                    .build();

            for (int col = 0; col < 9; col++) {
                int slot = rowStart + col;
                if (col == horsePos) {
                    inventory.setItem(slot, horseIcon);
                } else {
                    inventory.setItem(slot, trackPane);
                }
            }
        }
        updateCollectButton();
    }

    public void updateCollectButton() {
        if (!game.isFinished()) {
            ItemStack disabledItem = new ItemBuilder(Material.GRAY_DYE)
                    .name(messages.getComponent("common.collect-button-disabled.name"))
                    .lore(messages.getComponentList("common.collect-button-disabled.lore"))
                    .hideAllFlags()
                    .build();
            inventory.setItem(49, disabledItem);
        } else {
            double winAmount = game.getWinAmount();
            CurrencyType curType = game.getCurrencyType();
            String curName = currencyManager.getCurrencyName(curType);

            if (winAmount > 0) {
                Map<String, String> placeholders = Map.of(
                        "win", Formatter.formatNumber(winAmount),
                        "currency", curName,
                        "multiplier", Formatter.formatNumber(game.getMultiplier())
                );
                ItemStack enabledItem = new ItemBuilder(Material.EMERALD)
                        .name(messages.getComponent("common.collect-button-enabled.name"))
                        .lore(messages.getComponentList("common.collect-button-enabled.lore", placeholders))
                        .hideAllFlags()
                        .build();
                inventory.setItem(49, enabledItem);
            } else {
                ItemStack disabledItem = new ItemBuilder(Material.GRAY_DYE)
                        .name(messages.getComponent("common.collect-button-disabled.name"))
                        .lore(messages.getComponentList("common.collect-button-disabled.lore"))
                        .hideAllFlags()
                        .build();
                inventory.setItem(49, disabledItem);
            }
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    public HorseRaceGame getGame() {
        return game;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
