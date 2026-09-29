package pluginsfix.glowcasino.game.minesweeper;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.*;

public final class MinesweeperGame {

    private final Plugin plugin;
    private final Player player;
    private final double bet;
    private final CurrencyType currencyType;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;
    private final MinesweeperHolder holder;

    private final Map<Integer, MinesweeperTile> tiles = new HashMap<>();
    private int safeRevealed = 0;
    private boolean gameOver = false;

    public MinesweeperGame(Plugin plugin, Player player, double bet, CurrencyType currencyType,
                           CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.plugin = plugin;
        this.player = player;
        this.bet = bet;
        this.currencyType = currencyType;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;

        setupTiles();
        this.holder = new MinesweeperHolder(player, this, config, messages, currencyManager);
    }

    private void setupTiles() {
        List<Integer> slots = new ArrayList<>(MinesweeperHolder.GRID_SLOTS);
        Collections.shuffle(slots);

        int bombCount = Math.min(config.minesweeperBombCount(), slots.size() - 1);
        Set<Integer> bombSlots = new HashSet<>(slots.subList(0, bombCount));

        for (int slot : MinesweeperHolder.GRID_SLOTS) {
            boolean isBomb = bombSlots.contains(slot);
            tiles.put(slot, new MinesweeperTile(slot, isBomb));
        }
    }

    public void start() {
        if (!currencyManager.withdraw(player, currencyType, bet)) {
            messages.send(player, "errors.not-enough-funds", Map.of(
                    "amount", Formatter.formatNumber(bet),
                    "currency", currencyManager.getCurrencyName(currencyType)
            ));
            return;
        }

        holder.render();
        holder.open();
    }

    public void clickTile(int slot) {
        if (gameOver) return;

        MinesweeperTile tile = tiles.get(slot);
        if (tile == null || tile.isRevealed()) return;

        tile.setRevealed(true);

        if (tile.isBomb()) {
            gameOver = true;
            for (MinesweeperTile t : tiles.values()) {
                if (t.isBomb()) {
                    t.setRevealed(true);
                }
            }
            holder.render();
            player.playSound(player.getLocation(), config.minesweeperSoundExplode(), 1.0f, 0.8f);

            messages.send(player, "chat.minesweeper-explode", Map.of(
                    "bet", Formatter.formatNumber(bet),
                    "currency", currencyManager.getCurrencyName(currencyType)
            ));
            return;
        }

        safeRevealed++;
        player.playSound(player.getLocation(), config.minesweeperSoundReveal(), 1.0f, 1.2f + (safeRevealed * 0.05f));
        holder.render();

        int totalSafeTiles = MinesweeperHolder.GRID_SLOTS.size() - config.minesweeperBombCount();
        if (safeRevealed >= totalSafeTiles) {
            cashOut();
        }
    }

    public void cashOut() {
        if (gameOver || safeRevealed == 0) return;

        gameOver = true;
        double multiplier = getCurrentMultiplier();
        double winAmount = bet * multiplier;
        String curName = currencyManager.getCurrencyName(currencyType);

        currencyManager.deposit(player, currencyType, winAmount);
        player.playSound(player.getLocation(), config.minesweeperSoundWin(), 1.0f, 1.0f);

        messages.send(player, "chat.win", Map.of(
                "win", Formatter.formatNumber(winAmount),
                "currency", curName,
                "bet", Formatter.formatNumber(bet),
                "multiplier", Formatter.formatNumber(multiplier)
        ));

        player.closeInventory();
    }

    public double getCurrentMultiplier() {
        if (safeRevealed == 0) return 0.0;
        List<Double> mults = config.minesweeperMultipliers();
        int index = Math.min(safeRevealed - 1, mults.size() - 1);
        return mults.get(index);
    }

    public Map<Integer, MinesweeperTile> getTiles() {
        return tiles;
    }

    public int getSafeRevealed() {
        return safeRevealed;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public double getBet() {
        return bet;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public MinesweeperHolder getHolder() {
        return holder;
    }
}
