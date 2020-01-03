package org.ah.themvsus.server;

import org.ah.piwars.virtualrover.desktop.DesktopServerCommunication;

import static org.ah.themvsus.engine.common.debug.Debug.DEBUG;

public class TestDesktopServerCommunication extends DesktopServerCommunication {

    @Override
    public void pause() {
        if (DEBUG.isPauseAllowed()) { DEBUG.pause(); }
    }

    @Override
    public void unpause() {
        if (DEBUG.isPauseAllowed()) { DEBUG.unpause(); }
    }
}
