package pluginsfix.glowcasino.currency;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencySetting;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.hook.VaultHook;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CurrencyManager {

    private final CasinoConfig config;
    private final VaultHook vaultHook;
    private final Map<UUID, CurrencyType> selectedCurrencies = new ConcurrentHashMap<>();

    public CurrencyManager(CasinoConfig config, VaultHook vaultHook) {
        this.config = config;
        this.vaultHook = vaultHook;
    }

    public CurrencyType getSelectedCurrency(UUID uuid) {
        return selectedCurrencies.getOrDefault(uuid, CurrencyType.VAULT);
    }

    public void setSelectedCurrency(UUID uuid, CurrencyType type) {
        selectedCurrencies.put(uuid, type);
    }

    public void cleanup(UUID uuid) {
        selectedCurrencies.remove(uuid);
    }

    public double getBalance(Player player, CurrencyType type) {
        if (type == CurrencyType.VAULT) {
            Optional<Economy> economy = vaultHook.getEconomy();
            if (economy.isPresent()) {
                return economy.get().getBalance(player);
            }
            return 0.0;
        }

        Material itemMaterial = getItemMaterial(type);
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == itemMaterial && !item.hasItemMeta()) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public boolean has(Player player, CurrencyType type, double amount) {
        return getBalance(player, type) >= amount;
    }

    public boolean withdraw(Player player, CurrencyType type, double amount) {
        if (!has(player, type, amount)) {
            return false;
        }

        if (type == CurrencyType.VAULT) {
            Optional<Economy> economy = vaultHook.getEconomy();
            if (economy.isEmpty()) {
                return false;
            }
            return economy.get().withdrawPlayer(player, amount).transactionSuccess();
        }

        Material itemMaterial = getItemMaterial(type);
        int toRemove = (int) Math.round(amount);
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == itemMaterial && !item.hasItemMeta()) {
                int itemAmount = item.getAmount();
                if (itemAmount <= toRemove) {
                    toRemove -= itemAmount;
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(itemAmount - toRemove);
                    toRemove = 0;
                    break;
                }
            }
            if (toRemove <= 0) {
                break;
            }
        }
        player.updateInventory();
        return true;
    }

    public void deposit(Player player, CurrencyType type, double amount) {
        if (amount <= 0) return;

        if (type == CurrencyType.VAULT) {
            Optional<Economy> economy = vaultHook.getEconomy();
            economy.ifPresent(eco -> eco.depositPlayer(player, amount));
            return;
        }

        Material itemMaterial = getItemMaterial(type);
        int toAdd = (int) Math.round(amount);
        int maxStack = itemMaterial.getMaxStackSize();

        while (toAdd > 0) {
            int stackAmount = Math.min(toAdd, maxStack);
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(itemMaterial, stackAmount));
            if (!leftover.isEmpty()) {
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
            }
            toAdd -= stackAmount;
        }
        player.updateInventory();
    }

    public Material getItemMaterial(CurrencyType type) {
        CurrencySetting setting = config.getCurrencySetting(type);
        if (setting != null && setting.item() != null) {
            return setting.item();
        }
        return type.getDefaultMaterial();
    }

    public String getCurrencyName(CurrencyType type) {
        CurrencySetting setting = config.getCurrencySetting(type);
        if (setting != null && setting.name() != null) {
            return setting.name();
        }
        return type.getDefaultName();
    }
}
