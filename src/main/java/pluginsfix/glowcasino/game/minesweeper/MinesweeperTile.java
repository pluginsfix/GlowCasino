package pluginsfix.glowcasino.game.minesweeper;

public final class MinesweeperTile {

    private final int slot;
    private final boolean bomb;
    private boolean revealed = false;

    public MinesweeperTile(int slot, boolean bomb) {
        this.slot = slot;
        this.bomb = bomb;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isBomb() {
        return bomb;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}
