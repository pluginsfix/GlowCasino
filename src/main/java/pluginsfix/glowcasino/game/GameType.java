package pluginsfix.glowcasino.game;

public enum GameType {
    ROULETTE("roulette", "titles.roulette-menu", "rules.roulette"),
    MINESWEEPER("minesweeper", "titles.minesweeper-menu", "rules.minesweeper"),
    HORSERACES("horseraces", "titles.horseraces-menu", "rules.horseraces");

    private final String id;
    private final String titleKey;
    private final String ruleKey;

    GameType(String id, String titleKey, String ruleKey) {
        this.id = id;
        this.titleKey = titleKey;
        this.ruleKey = ruleKey;
    }

    public String getId() {
        return id;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public String getRuleKey() {
        return ruleKey;
    }
}
