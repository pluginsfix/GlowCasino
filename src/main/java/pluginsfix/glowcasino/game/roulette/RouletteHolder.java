package pluginsfix.glowcasino.game.roulette;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.RouletteReward;
import pluginsfix.glowcasino.gui.CasinoHolder;
import pluginsfix.glowcasino.gui.ItemBuilder;
import pluginsfix.glowcasino.gui.MenuUtils;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.List;

public final class RouletteHolder implements CasinoHolder {

    private final Inventory inventory;
    private final Player player;
    private final CasinoConfig config;
    private final Messages messages;
    private boolean spinning = true;

    public RouletteHolder(Player player, CasinoConfig config, Messages messages) {
        this.player = player;
        this.config = config;
        this.messages = messages;
        this.inventory = Bukkit.createInventory(this, 45, messages.getComponent("titles.roulette-spin"));
        initLayout();
    }

    private void initLayout() {
        MenuUtils.fillStandardBackground(inventory, config);

        ItemStack pointerDown = new ItemBuilder(Material.HOPPER)
                .name("&e▼ &6Указатель &e▼")
                .hideAllFlags()
                .build();
        inventory.setItem(13, pointerDown);

        ItemStack pointerUp = new ItemBuilder(Material.REDSTONE_TORCH)
                .name("&e▲ &6Указатель &e▲")
                .hideAllFlags()
                .build();
        inventory.setItem(31, pointerUp);

        ItemStack marker = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .name("&eУказатель")
                .hideAllFlags()
                .build();
        inventory.setItem(4, marker);
        inventory.setItem(40, marker);
    }

    public void updateDisplay(List<RouletteReward> windowItems) {
        for (int i = 0; i < 9 && i < windowItems.size(); i++) {
            RouletteReward reward = windowItems.get(i);
            int slot = 18 + i;
            ItemStack item = new ItemBuilder(reward.material())
                    .name(reward.name())
                    .loreStrings(List.of(
                            "&7Множитель: &e" + reward.multiplier() + "x",
                            (slot == 22 ? "&a▶ Выигрышный слот ◀" : "")
                    ))
                    .hideAllFlags()
                    .build();
            inventory.setItem(slot, item);
        }
    }

    public void setSpinning(boolean spinning) {
        this.spinning = spinning;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
