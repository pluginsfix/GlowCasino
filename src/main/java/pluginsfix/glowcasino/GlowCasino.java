package pluginsfix.glowcasino;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pluginsfix.glowcasino.command.CasinoCommand;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.hook.PlaceholderHook;
import pluginsfix.glowcasino.hook.VaultHook;
import pluginsfix.glowcasino.listener.InventoryListener;
import pluginsfix.glowcasino.service.CasinoService;
import pluginsfix.glowcasino.text.Messages;

public final class GlowCasino extends JavaPlugin {

    private CasinoConfig casinoConfig;
    private Messages messages;
    private VaultHook vaultHook;
    private PlaceholderHook placeholderHook;
    private CurrencyManager currencyManager;
    private CasinoService casinoService;

    @Override
    public void onEnable() {
        this.casinoConfig = new CasinoConfig(this);
        this.messages = new Messages(this);
        this.vaultHook = new VaultHook(this);
        this.placeholderHook = new PlaceholderHook();
        this.currencyManager = new CurrencyManager(casinoConfig, vaultHook);
        this.casinoService = new CasinoService(this, casinoConfig, messages, currencyManager);

        getServer().getPluginManager().registerEvents(new InventoryListener(casinoService), this);

        PluginCommand command = getCommand("casino");
        if (command != null) {
            CasinoCommand executor = new CasinoCommand(casinoService, messages);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    @Override
    public void onDisable() {
    }

    public CasinoConfig getCasinoConfig() {
        return casinoConfig;
    }

    public Messages getMessages() {
        return messages;
    }

    public CasinoService getCasinoService() {
        return casinoService;
    }
}
