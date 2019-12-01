package org.ah.themvsus.engine.client;

import org.ah.themvsus.engine.client.logging.ClientLogging;

public class HeadlessClientServerCommunication extends UDPServerCommunication {

    protected HeadlessClientServerCommunication(ClientLogging logging) {
        super(new LocalClientLogging());
    }
}
