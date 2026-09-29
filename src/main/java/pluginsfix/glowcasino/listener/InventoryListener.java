package pluginsfix.glowcasino.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pluginsfix.glowcasino.config.CurrencySetting;
import pluginsfix.glowcasino.config.CurrencyType;
import pluginsfix.glowcasino.game.GameType;
import pluginsfix.glowcasino.game.horseraces.HorseColor;
import pluginsfix.glowcasino.game.horseraces.HorseRaceTrackHolder;
import pluginsfix.glowcasino.game.horseraces.HorseSelectionHolder;
import pluginsfix.glowcasino.game.minesweeper.MinesweeperHolder;
import pluginsfix.glowcasino.game.roulette.RouletteHolder;
import pluginsfix.glowcasino.gui.CasinoHolder;
import pluginsfix.glowcasino.gui.CurrencySelectHolder;
import pluginsfix.glowcasino.gui.GameSetupHolder;
import pluginsfix.glowcasino.gui.MainMenuHolder;
import pluginsfix.glowcasino.service.CasinoService;

public final class InventoryListener implements Listener {

    private final CasinoService service;

    public InventoryListener(CasinoService service) {
        this.service = service;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof CasinoHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getInventory())) {
            return;
        }

        int slot = event.getSlot();
        CasinoHolder holder = (CasinoHolder) event.getInventory().getHolder();

        if (holder instanceof MainMenuHolder) {
            handleMainMenuClick(player, slot);
            return;
        }

        if (holder instanceof GameSetupHolder setupHolder) {
            handleSetupClick(player, setupHolder, slot, event.getClick());
            return;
        }

        if (holder instanceof CurrencySelectHolder curHolder) {
            handleCurrencySelectClick(player, curHolder, slot);
            return;
        }

        if (holder instanceof MinesweeperHolder msHolder) {
            handleMinesweeperClick(msHolder, slot);
            return;
        }

        if (holder instanceof HorseSelectionHolder horseSelHolder) {
            handleHorseSelectionClick(player, horseSelHolder, slot);
            return;
        }

        if (holder instanceof HorseRaceTrackHolder raceHolder) {
            handleRaceTrackClick(player, raceHolder, slot);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof CasinoHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        service.cleanup(event.getPlayer().getUniqueId());
    }

    private void handleMainMenuClick(Player player, int slot) {
        switch (slot) {
            case 21 -> service.openGameSetup(player, GameType.ROULETTE);
            case 22 -> service.openGameSetup(player, GameType.MINESWEEPER);
            case 23 -> service.openGameSetup(player, GameType.HORSERACES);
            default -> {}
        }
    }

    private void handleSetupClick(Player player, GameSetupHolder setupHolder, int slot, ClickType clickType) {
        CurrencyType curType = service.getCurrencyManager().getSelectedCurrency(player.getUniqueId());
        CurrencySetting setting = service.getConfig().getCurrencySetting(curType);
        double step = setting != null ? setting.step() : 1.0;
        double bigStep = setting != null ? setting.bigStep() : 10.0;

        if (slot == 13) {
            if (clickType == ClickType.LEFT) {
                setupHolder.adjustBet(step);
            } else if (clickType == ClickType.RIGHT) {
                setupHolder.adjustBet(-step);
            } else if (clickType == ClickType.SHIFT_LEFT) {
                setupHolder.adjustBet(bigStep);
            } else if (clickType == ClickType.SHIFT_RIGHT) {
                setupHolder.adjustBet(-bigStep);
            } else if (clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP) {
                setupHolder.setMaxBet();
            }
            return;
        }

        if (slot == 23) {
            service.openCurrencySelect(player, setupHolder);
            return;
        }

        if (slot == 31) {
            double bet = setupHolder.getCurrentBet();
            if (bet <= 0) return;

            GameType gType = setupHolder.getGameType();
            if (gType == GameType.ROULETTE) {
                service.startRoulette(player, bet);
            } else if (gType == GameType.MINESWEEPER) {
                service.startMinesweeper(player, bet);
            } else if (gType == GameType.HORSERACES) {
                service.openHorseSelection(player, setupHolder);
            }
            return;
        }

        if (slot == 40) {
            service.openMainMenu(player);
        }
    }

    private void handleCurrencySelectClick(Player player, CurrencySelectHolder curHolder, int slot) {
        switch (slot) {
            case 13 -> curHolder.select(CurrencyType.DIAMONDS);
            case 21 -> curHolder.select(CurrencyType.SILVER);
            case 23 -> curHolder.select(CurrencyType.VAULT);
            case 31 -> curHolder.select(CurrencyType.NETHERITE);
            case 40 -> curHolder.getParentHolder().open();
            default -> {}
        }
    }

    private void handleMinesweeperClick(MinesweeperHolder msHolder, int slot) {
        if (MinesweeperHolder.GRID_SLOTS.contains(slot)) {
            msHolder.getGame().clickTile(slot);
            return;
        }

        if (slot == 49) {
            msHolder.getGame().cashOut();
        }
    }

    private void handleHorseSelectionClick(Player player, HorseSelectionHolder holder, int slot) {
        if (slot == 4) {
            holder.getSetupHolder().open();
            return;
        }

        HorseColor color = HorseColor.getBySlot(slot);
        if (color != null) {
            double bet = holder.getSetupHolder().getCurrentBet();
            service.startHorseRace(player, color, bet);
        }
    }

    private void handleRaceTrackClick(Player player, HorseRaceTrackHolder raceHolder, int slot) {
        if (slot == 49 && raceHolder.getGame().isFinished()) {
            player.closeInventory();
        }
    }
}
