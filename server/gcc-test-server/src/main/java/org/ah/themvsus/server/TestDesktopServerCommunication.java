package org.ah.themvsus.server;

import static org.ah.themvsus.engine.common.debug.Debug.DEBUG;

import org.ah.gcc.virtualrover.desktop.DesktopServerCommunication;

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
