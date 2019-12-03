package org.ah.gcc.virtualrover.desktop;

import org.ah.gcc.virtualrover.logging.GdxClientLoggingAdapter;
import org.ah.themvsus.engine.client.desktop.UDPServerCommunication;

public class DesktopServerCommunication extends UDPServerCommunication {

    protected DesktopServerCommunication() {
        super(GdxClientLoggingAdapter.getInstance());
    }
}
