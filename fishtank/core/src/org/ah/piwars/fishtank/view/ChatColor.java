package org.ah.piwars.fishtank.view;

import java.util.HashMap;
import java.util.Map;

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

    private static Map<String, ChatColor> cache = new HashMap<String, ChatColor>();

    public static ChatColor fromString(String s) {
        s = s.toUpperCase();
        ChatColor colour = cache.get(s);
        if (colour != null) {
            return colour;
        }
        try {
            colour = ChatColor.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            colour = ChatColor.BLACK;
        }
        cache.put(s, colour);
        return colour;
    }
}
