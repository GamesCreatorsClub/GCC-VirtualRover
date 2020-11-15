package org.ah.piwars.fshtank.desktop;

import org.ah.piwars.fishtank.logging.GdxClientLoggingAdapter;
import org.ah.themvsus.engine.client.desktop.UDPServerCommunication;

public class DesktopServerCommunication extends UDPServerCommunication {

    protected DesktopServerCommunication() {
        super(GdxClientLoggingAdapter.getInstance());
    }
}
