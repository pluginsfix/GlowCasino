package pluginsfix.glowcasino.game.minesweeper;

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
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MinesweeperHolder implements CasinoHolder {

    public static final List<Integer> GRID_SLOTS = List.of(
            2, 3, 4, 5, 6,
            11, 12, 13, 14, 15,
            20, 21, 22, 23, 24,
            29, 30, 31, 32, 33,
            38, 39, 40, 41, 42
    );

    private final Inventory inventory;
    private final Player player;
    private final MinesweeperGame game;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;

    public MinesweeperHolder(Player player, MinesweeperGame game, CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.player = player;
        this.game = game;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
        this.inventory = Bukkit.createInventory(this, 54, messages.getComponent("titles.minesweeper-game"));
    }

    public void render() {
        String paneTitle = config.decorativeGlassTitle();
        ItemStack darkPane = new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE).name(paneTitle).hideAllFlags().build();
        ItemStack orangePane = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name(paneTitle).hideAllFlags().build();

        for (int i = 0; i < 54; i++) {
            if (!GRID_SLOTS.contains(i)) {
                if (i % 9 == 0 || i % 9 == 8 || i < 9 || i >= 45) {
                    inventory.setItem(i, darkPane);
                } else {
                    inventory.setItem(i, orangePane);
                }
            }
        }

        Map<Integer, MinesweeperTile> tiles = game.getTiles();
        for (int slot : GRID_SLOTS) {
            MinesweeperTile tile = tiles.get(slot);
            if (tile == null) continue;

            if (!tile.isRevealed()) {
                ItemStack sandItem = new ItemBuilder(Material.SUSPICIOUS_SAND)
                        .name("&eПесок &7(Нажмите, чтобы копать)")
                        .hideAllFlags()
                        .build();
                inventory.setItem(slot, sandItem);
            } else if (tile.isBomb()) {
                ItemStack tntItem = new ItemBuilder(Material.TNT)
                        .name("&cДинамит!")
                        .hideAllFlags()
                        .build();
                inventory.setItem(slot, tntItem);
            } else {
                ItemStack diamondItem = new ItemBuilder(Material.DIAMOND)
                        .name("&aКлад найден!")
                        .hideAllFlags()
                        .build();
                inventory.setItem(slot, diamondItem);
            }
        }

        updateCollectButton();
    }

    public void updateCollectButton() {
        int safeRevealed = game.getSafeRevealed();
        double currentMultiplier = game.getCurrentMultiplier();
        double currentWin = game.getBet() * currentMultiplier;
        CurrencyType curType = game.getCurrencyType();
        String curName = currencyManager.getCurrencyName(curType);

        if (safeRevealed == 0 || game.isGameOver()) {
            ItemStack disabledItem = new ItemBuilder(Material.GRAY_DYE)
                    .name(messages.getComponent("common.collect-button-disabled.name"))
                    .lore(messages.getComponentList("common.collect-button-disabled.lore"))
                    .hideAllFlags()
                    .build();
            inventory.setItem(49, disabledItem);
        } else {
            Map<String, String> placeholders = Map.of(
                    "win", Formatter.formatNumber(currentWin),
                    "currency", curName,
                    "multiplier", Formatter.formatNumber(currentMultiplier)
            );
            ItemStack enabledItem = new ItemBuilder(Material.EMERALD)
                    .name(messages.getComponent("common.collect-button-enabled.name"))
                    .lore(messages.getComponentList("common.collect-button-enabled.lore", placeholders))
                    .hideAllFlags()
                    .build();
            inventory.setItem(49, enabledItem);
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    public MinesweeperGame getGame() {
        return game;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
