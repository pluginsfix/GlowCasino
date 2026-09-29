package pluginsfix.glowcasino.service;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.game.GameType;
import pluginsfix.glowcasino.game.horseraces.HorseColor;
import pluginsfix.glowcasino.game.horseraces.HorseRaceGame;
import pluginsfix.glowcasino.game.horseraces.HorseSelectionHolder;
import pluginsfix.glowcasino.game.minesweeper.MinesweeperGame;
import pluginsfix.glowcasino.game.roulette.RouletteGame;
import pluginsfix.glowcasino.gui.CurrencySelectHolder;
import pluginsfix.glowcasino.gui.GameSetupHolder;
import pluginsfix.glowcasino.gui.MainMenuHolder;
import pluginsfix.glowcasino.text.Messages;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CasinoService {

    private final Plugin plugin;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;

    private final Map<UUID, Object> activeGames = new ConcurrentHashMap<>();

    public CasinoService(Plugin plugin, CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
    }

    public void openMainMenu(Player player) {
        MainMenuHolder holder = new MainMenuHolder(config, messages);
        holder.open(player);
    }

    public void openGameSetup(Player player, GameType gameType) {
        GameSetupHolder holder = new GameSetupHolder(player, gameType, config, messages, currencyManager);
        holder.open();
    }

    public void openCurrencySelect(Player player, GameSetupHolder parentHolder) {
        CurrencySelectHolder holder = new CurrencySelectHolder(player, parentHolder, config, messages, currencyManager);
        holder.open();
    }

    public void openHorseSelection(Player player, GameSetupHolder setupHolder) {
        HorseSelectionHolder holder = new HorseSelectionHolder(player, setupHolder, config, messages, currencyManager);
        holder.open();
    }

    public void startRoulette(Player player, double bet) {
        CurrencyType curType = currencyManager.getSelectedCurrency(player.getUniqueId());
        RouletteGame game = new RouletteGame(plugin, player, bet, curType, config, messages, currencyManager);
        activeGames.put(player.getUniqueId(), game);
        game.start();
    }

    public void startMinesweeper(Player player, double bet) {
        CurrencyType curType = currencyManager.getSelectedCurrency(player.getUniqueId());
        MinesweeperGame game = new MinesweeperGame(plugin, player, bet, curType, config, messages, currencyManager);
        activeGames.put(player.getUniqueId(), game);
        game.start();
    }

    public void startHorseRace(Player player, HorseColor color, double bet) {
        CurrencyType curType = currencyManager.getSelectedCurrency(player.getUniqueId());
        HorseRaceGame game = new HorseRaceGame(plugin, player, color, bet, curType, config, messages, currencyManager);
        activeGames.put(player.getUniqueId(), game);
        game.start();
    }

    public void cleanup(UUID uuid) {
        activeGames.remove(uuid);
        currencyManager.cleanup(uuid);
    }

    public CasinoConfig getConfig() {
        return config;
    }

    public Messages getMessages() {
        return messages;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
}
