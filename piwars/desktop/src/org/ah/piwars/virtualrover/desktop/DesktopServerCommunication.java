package org.ah.piwars.virtualrover.desktop;

import org.ah.piwars.virtualrover.logging.GdxClientLoggingAdapter;
import org.ah.themvsus.engine.client.desktop.UDPServerCommunication;

public class DesktopServerCommunication extends UDPServerCommunication {

    protected DesktopServerCommunication() {
        super(GdxClientLoggingAdapter.getInstance());
    }
}
