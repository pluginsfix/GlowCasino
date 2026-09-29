package pluginsfix.glowcasino.game.horseraces;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pluginsfix.glowcasino.config.CasinoConfig;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.currency.CurrencyManager;
import pluginsfix.glowcasino.text.Formatter;
import pluginsfix.glowcasino.text.Messages;

import java.util.*;

public final class HorseRaceGame {

    private final Plugin plugin;
    private final Player player;
    private final HorseColor playerHorse;
    private final double bet;
    private final CurrencyType currencyType;
    private final CasinoConfig config;
    private final Messages messages;
    private final CurrencyManager currencyManager;

    private final List<HorseColor> laneHorses = new ArrayList<>(4);
    private final int[] positions = new int[4];
    private final List<Integer> finishOrder = new ArrayList<>();
    private final Random random = new Random();

    private HorseRaceTrackHolder holder;
    private boolean finished = false;
    private int playerPlace = 4;
    private double multiplier = 0.0;
    private double winAmount = 0.0;

    public HorseRaceGame(Plugin plugin, Player player, HorseColor playerHorse, double bet, CurrencyType currencyType,
                          CasinoConfig config, Messages messages, CurrencyManager currencyManager) {
        this.plugin = plugin;
        this.player = player;
        this.playerHorse = playerHorse;
        this.bet = bet;
        this.currencyType = currencyType;
        this.config = config;
        this.messages = messages;
        this.currencyManager = currencyManager;

        setupLanes();
        this.holder = new HorseRaceTrackHolder(player, this, config, messages, currencyManager);
    }

    private void setupLanes() {
        List<HorseColor> allColors = new ArrayList<>(List.of(HorseColor.values()));
        allColors.remove(playerHorse);
        Collections.shuffle(allColors);

        int playerLane = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            if (i == playerLane) {
                laneHorses.add(playerHorse);
            } else {
                laneHorses.add(allColors.remove(0));
            }
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

        holder.updateTrack(laneHorses, positions);
        holder.open();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                boolean anyAdvanced = false;
                for (int i = 0; i < 4; i++) {
                    if (positions[i] < 8) {
                        if (random.nextDouble() < 0.65) {
                            positions[i]++;
                            anyAdvanced = true;
                            if (positions[i] == 8 && !finishOrder.contains(i)) {
                                finishOrder.add(i);
                            }
                        }
                    }
                }

                if (anyAdvanced) {
                    player.playSound(player.getLocation(), config.horseSoundStep(), 0.7f, 0.9f + (random.nextFloat() * 0.4f));
                    holder.updateTrack(laneHorses, positions);
                }

                if (finishOrder.size() == 4 || allFinished()) {
                    for (int i = 0; i < 4; i++) {
                        if (!finishOrder.contains(i)) {
                            finishOrder.add(i);
                        }
                    }
                    cancel();
                    finishRace();
                }
            }
        }.runTaskTimer(plugin, 10L, Math.max(1, config.horseTickInterval()));
    }

    private boolean allFinished() {
        for (int pos : positions) {
            if (pos < 8) return false;
        }
        return true;
    }

    private void finishRace() {
        this.finished = true;
        int playerLane = laneHorses.indexOf(playerHorse);
        int placeIndex = finishOrder.indexOf(playerLane);
        this.playerPlace = placeIndex + 1;

        this.multiplier = switch (playerPlace) {
            case 1 -> config.horseFirstPlaceMult();
            case 2 -> config.horseSecondPlaceMult();
            case 3 -> config.horseThirdPlaceMult();
            default -> config.horseFourthPlaceMult();
        };

        this.winAmount = bet * multiplier;
        String curName = currencyManager.getCurrencyName(currencyType);

        if (winAmount > 0) {
            currencyManager.deposit(player, currencyType, winAmount);
            player.playSound(player.getLocation(), config.horseSoundWin(), 1.0f, 1.0f);

            messages.send(player, "chat.win", Map.of(
                    "win", Formatter.formatNumber(winAmount),
                    "currency", curName,
                    "bet", Formatter.formatNumber(bet),
                    "multiplier", Formatter.formatNumber(multiplier)
            ));
        } else {
            player.playSound(player.getLocation(), config.horseSoundLose(), 1.0f, 0.8f);

            messages.send(player, "chat.horse-lose", Map.of(
                    "bet", Formatter.formatNumber(bet),
                    "currency", curName
            ));
        }

        holder.updateCollectButton();
    }

    public boolean isFinished() {
        return finished;
    }

    public double getWinAmount() {
        return winAmount;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public HorseRaceTrackHolder getHolder() {
        return holder;
    }
}
