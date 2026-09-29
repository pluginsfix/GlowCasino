package pluginsfix.glowcasino.game.roulette;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.config.RouletteReward;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class RouletteGame {

    private final Plugin plugin;
    private final Player player;
    private final double bet;
    private final CurrencyType currencyType;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;
    private final RouletteHolder holder;
    private final Random random = new Random();

    private final List<RouletteReward> rewardList = new ArrayList<>();
    private int currentIndex = 0;

    public RouletteGame(Plugin plugin, Player player, double bet, CurrencyType currencyType,
                        CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.plugin = plugin;
        this.player = player;
        this.bet = bet;
        this.currencyType = currencyType;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;
        this.holder = new RouletteHolder(player, config, messages);

        prepareRewards();
    }

    private void prepareRewards() {
        List<RouletteReward> pool = config.rouletteRewards();
        if (pool.isEmpty()) return;

        int totalWeight = 0;
        for (RouletteReward r : pool) {
            totalWeight += Math.max(1, r.weight());
        }

        for (int i = 0; i < 60; i++) {
            int rand = random.nextInt(totalWeight);
            int sum = 0;
            RouletteReward chosen = pool.get(0);
            for (RouletteReward r : pool) {
                sum += Math.max(1, r.weight());
                if (rand < sum) {
                    chosen = r;
                    break;
                }
            }
            rewardList.add(chosen);
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

        holder.open();

        new BukkitRunnable() {
            private int step = 0;
            private final int totalSteps = 45;
            private int delay = 1;
            private int elapsedTicks = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (elapsedTicks % delay == 0) {
                    currentIndex++;
                    if (currentIndex + 9 > rewardList.size()) {
                        prepareRewards();
                    }

                    List<RouletteReward> window = rewardList.subList(currentIndex, currentIndex + 9);
                    holder.updateDisplay(window);

                    player.playSound(player.getLocation(), config.rouletteSoundTick(), 0.7f, 1.2f + (step * 0.01f));

                    step++;
                    if (step > 25) {
                        delay = 2;
                    }
                    if (step > 35) {
                        delay = 3;
                    }
                    if (step > 40) {
                        delay = 5;
                    }

                    if (step >= totalSteps) {
                        cancel();
                        finishGame();
                    }
                }
                elapsedTicks++;
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void finishGame() {
        holder.setSpinning(false);
        RouletteReward winningReward = rewardList.get(currentIndex + 4);
        double multiplier = winningReward.multiplier();
        double winAmount = bet * multiplier;

        String currencyName = currencyManager.getCurrencyName(currencyType);

        if (winAmount > 0) {
            currencyManager.deposit(player, currencyType, winAmount);
            player.playSound(player.getLocation(), config.rouletteSoundWin(), 1.0f, 1.0f);

            messages.send(player, "chat.win", Map.of(
                    "win", Formatter.formatNumber(winAmount),
                    "currency", currencyName,
                    "bet", Formatter.formatNumber(bet),
                    "multiplier", Formatter.formatNumber(multiplier)
            ));
        } else {
            player.playSound(player.getLocation(), config.rouletteSoundLose(), 1.0f, 0.8f);

            messages.send(player, "chat.lose", Map.of(
                    "bet", Formatter.formatNumber(bet),
                    "currency", currencyName
            ));
        }
    }
}
