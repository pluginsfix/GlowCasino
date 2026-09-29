package pluginsfix.glowcasino.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencySetting;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.game.GameType;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.Map;

public final class GameSetupHolder implements CasinoHolder {

    private final Player player;
    private final GameType gameType;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;
    private final Inventory inventory;

    private double currentBet = 0.0;

    public GameSetupHolder(Player player, GameType gameType, CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.player = player;
        this.gameType = gameType;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
        this.inventory = Bukkit.createInventory(this, 45, messages.getComponent(gameType.getTitleKey()));
        render();
    }

    public void render() {
        MenuUtils.fillStandardBackground(inventory, config);

        CurrencyType currencyType = currencyManager.getSelectedCurrency(player.getUniqueId());
        CurrencySetting setting = config.getCurrencySetting(currencyType);
        double balance = currencyManager.getBalance(player, currencyType);
        String currencyName = currencyManager.getCurrencyName(currencyType);

        double step = setting != null ? setting.step() : 1.0;
        double bigStep = setting != null ? setting.bigStep() : 10.0;

        Map<String, String> betPlaceholders = Map.of(
                "bet", Formatter.formatNumber(currentBet),
                "balance", Formatter.formatNumber(balance),
                "currency", currencyName,
                "step", Formatter.formatNumber(step),
                "big_step", Formatter.formatNumber(bigStep)
        );

        ItemStack chestItem = new ItemBuilder(Material.CHEST)
                .name(messages.getComponent("common.bet-button.name"))
                .lore(messages.getComponentList("common.bet-button.lore", betPlaceholders))
                .hideAllFlags()
                .build();
        inventory.setItem(13, chestItem);

        ItemStack bookItem = new ItemBuilder(Material.BOOK)
                .name(messages.getComponent(gameType.getRuleKey() + ".name"))
                .lore(messages.getComponentList(gameType.getRuleKey() + ".lore"))
                .hideAllFlags()
                .build();
        inventory.setItem(21, bookItem);

        Material curMat = currencyManager.getItemMaterial(currencyType);
        Map<String, String> curPlaceholders = Map.of(
                "currency", currencyName,
                "balance", Formatter.formatNumber(balance)
        );
        ItemStack currencyItem = new ItemBuilder(curMat)
                .name(messages.getComponent("common.currency-button.name"))
                .lore(messages.getComponentList("common.currency-button.lore", curPlaceholders))
                .hideAllFlags()
                .build();
        inventory.setItem(23, currencyItem);

        ItemStack startItem;
        if (currentBet <= 0) {
            startItem = new ItemBuilder(Material.GRAY_DYE)
                    .name(messages.getComponent("common.start-button-disabled.name"))
                    .lore(messages.getComponentList("common.start-button-disabled.lore"))
                    .hideAllFlags()
                    .build();
        } else {
            Map<String, String> startPlaceholders = Map.of(
                    "bet", Formatter.formatNumber(currentBet),
                    "currency", currencyName
            );
            startItem = new ItemBuilder(Material.NETHER_STAR)
                    .name(messages.getComponent("common.start-button-enabled.name"))
                    .lore(messages.getComponentList("common.start-button-enabled.lore", startPlaceholders))
                    .hideAllFlags()
                    .build();
        }
        inventory.setItem(31, startItem);

        MenuUtils.setBackButton(inventory, 40, messages);
    }

    public void adjustBet(double delta) {
        CurrencyType currencyType = currencyManager.getSelectedCurrency(player.getUniqueId());
        CurrencySetting setting = config.getCurrencySetting(currencyType);
        double maxBet = setting != null ? setting.maxBet() : 1000000.0;
        double minBet = setting != null ? setting.minBet() : 0.0;
        double balance = currencyManager.getBalance(player, currencyType);

        double newBet = Math.max(0, currentBet + delta);
        if (newBet > maxBet) {
            newBet = maxBet;
        }
        if (newBet > balance) {
            newBet = balance;
        }
        this.currentBet = newBet;
        render();
    }

    public void setMaxBet() {
        CurrencyType currencyType = currencyManager.getSelectedCurrency(player.getUniqueId());
        CurrencySetting setting = config.getCurrencySetting(currencyType);
        double maxBet = setting != null ? setting.maxBet() : 1000000.0;
        double balance = currencyManager.getBalance(player, currencyType);
        this.currentBet = Math.min(balance, maxBet);
        render();
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public GameType getGameType() {
        return gameType;
    }

    public Player getPlayer() {
        return player;
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
