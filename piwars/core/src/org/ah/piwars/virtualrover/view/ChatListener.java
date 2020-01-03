package org.ah.piwars.virtualrover.view;

import org.ah.themvsus.engine.common.game.Player;

public interface ChatListener {
    void onCommand(Player from, String cmdName, String[] args);
    void onChat(String playerName, String text);
    void onText(String text);

}
