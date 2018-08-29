package org.ah.gcc.virtualrover.view;

public enum ChatColor {
    RED("[RED]"),
    ORANGE("[ORANGE]"),
    YELLOW("[YELLOW]"),
    GREEN("[GREEN]"),
    BLUE("[BLUE]"),
    INDIGO("[#0200BB]"),
    PURPLE("[PURPLE]"),
    BLACK("[BLACK]"),
    WHITE("[WHITE]"),
    GRAY("[GRAY]");

    private final String text;
    private ChatColor(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }

}
