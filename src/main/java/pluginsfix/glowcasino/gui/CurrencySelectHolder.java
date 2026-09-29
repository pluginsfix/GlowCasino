package pluginsfix.glowcasino.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.List;
import java.util.Map;

public final class CurrencySelectHolder implements CasinoHolder {

    private final Player player;
    private final GameSetupHolder parentHolder;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;
    private final Inventory inventory;

    public CurrencySelectHolder(Player player, GameSetupHolder parentHolder, CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.player = player;
        this.parentHolder = parentHolder;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
        this.inventory = Bukkit.createInventory(this, 45, messages.getComponent("titles.currency-menu"));
        render();
    }

    public void render() {
        MenuUtils.fillStandardBackground(inventory, config);

        CurrencyType currentSelected = currencyManager.getSelectedCurrency(player.getUniqueId());

        setCurrencyItem(13, CurrencyType.DIAMONDS, currentSelected);
        setCurrencyItem(21, CurrencyType.SILVER, currentSelected);
        setCurrencyItem(23, CurrencyType.VAULT, currentSelected);
        setCurrencyItem(31, CurrencyType.NETHERITE, currentSelected);

        MenuUtils.setBackButton(inventory, 40, messages);
    }

    private void setCurrencyItem(int slot, CurrencyType type, CurrencyType currentSelected) {
        Material mat = currencyManager.getItemMaterial(type);
        double balance = currencyManager.getBalance(player, type);
        boolean isSelected = (type == currentSelected);

        Map<String, String> placeholders = Map.of("balance", Formatter.formatNumber(balance));

        String nameKey = "currency-selection." + type.getMessageKey() + ".name";
        String loreKey = isSelected ? "currency-selection.selected-lore" : "currency-selection.unselected-lore";

        ItemStack item = new ItemBuilder(mat)
                .name(messages.getComponent(nameKey))
                .lore(messages.getComponentList(loreKey, placeholders))
                .hideAllFlags()
                .build();
        inventory.setItem(slot, item);
    }

    public void select(CurrencyType type) {
        currencyManager.setSelectedCurrency(player.getUniqueId(), type);
        parentHolder.render();
        parentHolder.open();
    }

    public GameSetupHolder getParentHolder() {
        return parentHolder;
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
