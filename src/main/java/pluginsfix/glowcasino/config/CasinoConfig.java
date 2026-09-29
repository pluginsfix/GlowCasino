package pluginsfix.glowcasino.config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CasinoConfig {

    private final Plugin plugin;
    private FileConfiguration config;
    private String decorativeGlassTitle;
    private final Map<CurrencyType, CurrencySetting> currencySettings = new EnumMap<>(CurrencyType.class);

    private int rouletteSpinTicks;
    private Sound rouletteSoundTick;
    private Sound rouletteSoundWin;
    private Sound rouletteSoundLose;
    private final List<RouletteReward> rouletteRewards = new ArrayList<>();

    private int minesweeperTotalTiles;
    private int minesweeperBombCount;
    private Sound minesweeperSoundReveal;
    private Sound minesweeperSoundExplode;
    private Sound minesweeperSoundWin;
    private final List<Double> minesweeperMultipliers = new ArrayList<>();

    private int horseTrackLength;
    private int horseTickInterval;
    private Sound horseSoundStep;
    private Sound horseSoundWin;
    private Sound horseSoundLose;
    private double horseFirstPlaceMult;
    private double horseSecondPlaceMult;
    private double horseThirdPlaceMult;
    private double horseFourthPlaceMult;

    public CasinoConfig(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        this.decorativeGlassTitle = config.getString("decorative-glass-title", "&6FunTime.me");

        currencySettings.clear();
        ConfigurationSection curSec = config.getConfigurationSection("currencies");
        if (curSec != null) {
            for (String key : curSec.getKeys(false)) {
                try {
                    CurrencyType type = CurrencyType.valueOf(key.toUpperCase());
                    ConfigurationSection s = curSec.getConfigurationSection(key);
                    if (s != null) {
                        boolean enabled = s.getBoolean("enabled", true);
                        String name = s.getString("name", key);
                        Material item = Material.matchMaterial(s.getString("item", "SUNFLOWER"));
                        if (item == null) item = Material.SUNFLOWER;
                        double minBet = s.getDouble("min-bet", 1.0);
                        double maxBet = s.getDouble("max-bet", 1000000.0);
                        double step = s.getDouble("step", 1.0);
                        double bigStep = s.getDouble("big-step", 10.0);
                        currencySettings.put(type, new CurrencySetting(enabled, name, item, minBet, maxBet, step, bigStep));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        this.rouletteSpinTicks = config.getInt("roulette.spin-ticks", 80);
        this.rouletteSoundTick = parseSound(config.getString("roulette.sound-tick"), Sound.BLOCK_NOTE_BLOCK_HAT);
        this.rouletteSoundWin = parseSound(config.getString("roulette.sound-win"), Sound.ENTITY_PLAYER_LEVELUP);
        this.rouletteSoundLose = parseSound(config.getString("roulette.sound-lose"), Sound.BLOCK_CHEST_CLOSE);

        rouletteRewards.clear();
        ConfigurationSection rItems = config.getConfigurationSection("roulette.items");
        if (rItems != null) {
            for (String key : rItems.getKeys(false)) {
                ConfigurationSection itemSec = rItems.getConfigurationSection(key);
                if (itemSec != null) {
                    Material mat = Material.matchMaterial(itemSec.getString("material", "DIAMOND"));
                    if (mat == null) mat = Material.DIAMOND;
                    String name = itemSec.getString("name", key);
                    double mult = itemSec.getDouble("multiplier", 1.0);
                    int weight = itemSec.getInt("weight", 10);
                    rouletteRewards.add(new RouletteReward(key, mat, name, mult, weight));
                }
            }
        }

        this.minesweeperTotalTiles = config.getInt("minesweeper.total-tiles", 25);
        this.minesweeperBombCount = config.getInt("minesweeper.bomb-count", 4);
        this.minesweeperSoundReveal = parseSound(config.getString("minesweeper.sound-reveal"), Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        this.minesweeperSoundExplode = parseSound(config.getString("minesweeper.sound-explode"), Sound.ENTITY_GENERIC_EXPLODE);
        this.minesweeperSoundWin = parseSound(config.getString("minesweeper.sound-win"), Sound.ENTITY_PLAYER_LEVELUP);

        minesweeperMultipliers.clear();
        List<Double> multList = config.getDoubleList("minesweeper.multipliers");
        if (!multList.isEmpty()) {
            minesweeperMultipliers.addAll(multList);
        } else {
            minesweeperMultipliers.addAll(List.of(1.2, 1.5, 2.0, 2.7, 3.8, 5.5, 8.0, 12.0, 18.0, 28.0, 45.0, 75.0, 130.0, 240.0));
        }

        this.horseTrackLength = config.getInt("horseraces.track-length", 9);
        this.horseTickInterval = config.getInt("horseraces.tick-interval", 4);
        this.horseSoundStep = parseSound(config.getString("horseraces.sound-step"), Sound.ENTITY_HORSE_GALLOP);
        this.horseSoundWin = parseSound(config.getString("horseraces.sound-win"), Sound.ENTITY_PLAYER_LEVELUP);
        this.horseSoundLose = parseSound(config.getString("horseraces.sound-lose"), Sound.ENTITY_VILLAGER_NO);
        this.horseFirstPlaceMult = config.getDouble("horseraces.multipliers.first-place", 2.0);
        this.horseSecondPlaceMult = config.getDouble("horseraces.multipliers.second-place", 1.0);
        this.horseThirdPlaceMult = config.getDouble("horseraces.multipliers.third-place", 0.5);
        this.horseFourthPlaceMult = config.getDouble("horseraces.multipliers.fourth-place", 0.0);
    }

    private Sound parseSound(String soundName, Sound fallback) {
        if (soundName == null || soundName.isEmpty()) return fallback;
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public String decorativeGlassTitle() {
        return decorativeGlassTitle;
    }

    public CurrencySetting getCurrencySetting(CurrencyType type) {
        return currencySettings.get(type);
    }

    public int rouletteSpinTicks() {
        return rouletteSpinTicks;
    }

    public Sound rouletteSoundTick() {
        return rouletteSoundTick;
    }

    public Sound rouletteSoundWin() {
        return rouletteSoundWin;
    }

    public Sound rouletteSoundLose() {
        return rouletteSoundLose;
    }

    public List<RouletteReward> rouletteRewards() {
        return rouletteRewards;
    }

    public int minesweeperTotalTiles() {
        return minesweeperTotalTiles;
    }

    public int minesweeperBombCount() {
        return minesweeperBombCount;
    }

    public Sound minesweeperSoundReveal() {
        return minesweeperSoundReveal;
    }

    public Sound minesweeperSoundExplode() {
        return minesweeperSoundExplode;
    }

    public Sound minesweeperSoundWin() {
        return minesweeperSoundWin;
    }

    public List<Double> minesweeperMultipliers() {
        return minesweeperMultipliers;
    }

    public int horseTrackLength() {
        return horseTrackLength;
    }

    public int horseTickInterval() {
        return horseTickInterval;
    }

    public Sound horseSoundStep() {
        return horseSoundStep;
    }

    public Sound horseSoundWin() {
        return horseSoundWin;
    }

    public Sound horseSoundLose() {
        return horseSoundLose;
    }

    public double horseFirstPlaceMult() {
        return horseFirstPlaceMult;
    }

    public double horseSecondPlaceMult() {
        return horseSecondPlaceMult;
    }

    public double horseThirdPlaceMult() {
        return horseThirdPlaceMult;
    }

    public double horseFourthPlaceMult() {
        return horseFourthPlaceMult;
    }
}
