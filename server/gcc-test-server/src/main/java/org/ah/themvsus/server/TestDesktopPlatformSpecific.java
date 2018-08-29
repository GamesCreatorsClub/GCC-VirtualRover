package org.ah.themvsus.server;

import org.ah.gcc.virtualrover.desktop.DesktopPlatformSpecific;

public class TestDesktopPlatformSpecific extends DesktopPlatformSpecific {

    public TestDesktopPlatformSpecific() {
        super(new TestDesktopServerCommunication());
    }
}
