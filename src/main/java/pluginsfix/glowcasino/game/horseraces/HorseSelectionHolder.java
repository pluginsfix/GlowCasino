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
import pluginsfix.glowcasino.gui.GameSetupHolder;
import pluginsfix.glowcasino.gui.ItemBuilder;
import pluginsfix.glowcasino.gui.MenuUtils;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.Map;

public final class HorseSelectionHolder implements CasinoHolder {

    private final Player player;
    private final GameSetupHolder setupHolder;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;
    private final Inventory inventory;

    public HorseSelectionHolder(Player player, GameSetupHolder setupHolder, CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.player = player;
        this.setupHolder = setupHolder;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
        this.inventory = Bukkit.createInventory(this, 45, messages.getComponent("titles.horse-selection"));
        render();
    }

    public void render() {
        MenuUtils.fillStandardBackground(inventory, config);
        MenuUtils.setBackButton(inventory, 4, messages);

        double bet = setupHolder.getCurrentBet();
        CurrencyType curType = currencyManager.getSelectedCurrency(player.getUniqueId());
        String curName = currencyManager.getCurrencyName(curType);

        for (HorseColor horseColor : HorseColor.values()) {
            Map<String, String> placeholders = Map.of(
                    "color_name", horseColor.getDisplayName(),
                    "bet", Formatter.formatNumber(bet),
                    "currency", curName
            );

            ItemStack horseItem = new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
                    .leatherColor(horseColor.getLeatherColor())
                    .name(messages.getComponent("horse-selection.horse-item.name", placeholders))
                    .lore(messages.getComponentList("horse-selection.horse-item.lore", placeholders))
                    .hideAllFlags()
                    .build();

            inventory.setItem(horseColor.getSelectionSlot(), horseItem);
        }
    }

    public void open() {
        player.openInventory(inventory);
    }

    public GameSetupHolder getSetupHolder() {
        return setupHolder;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
