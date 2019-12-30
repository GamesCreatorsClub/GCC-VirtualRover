package org.ah.themvsus.engine.common.debug;

import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.Engine;
import org.ah.themvsus.server.engine.ClientSession;
import org.ah.themvsus.server.engine.ServerEngine;

public class Debug {

    public static final Debug DEBUG = new Debug();

    private boolean pauseAllowed;
    private boolean isPaused;
    private ServerEngine<?> serverEngine;
    private ClientEngine<?> clientEngine;

    private Debug() {}

    public boolean isPauseAllowed() {
        return pauseAllowed;
    }

    public void setPauseIsAllowed(boolean pauseIsAllowed) {
        pauseAllowed = pauseIsAllowed;
    }

    public void pause() {
        isPaused = true;
        if (serverEngine != null) {
            serverEngine.setPaused(true);
        }
        if (clientEngine != null) {
            clientEngine.setPaused(true);
        }
    }

    public void unpause() {
        isPaused = false;
        if (serverEngine != null) {
            serverEngine.setPaused(false);
        }
        if (clientEngine != null) {
            clientEngine.setPaused(false);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setServerEngine(ServerEngine<?> engine) { serverEngine = engine; }

    public void setClientEngine(ClientEngine<?> engine) { clientEngine = engine; }

    // public Engine<?> getServerEngine() { return serverEngine; }

    public Engine<?> getClientEngine() { return clientEngine; }

    public ClientSession<?> getClientSessionByAlias(String alias) {
        return serverEngine.getClientSessionByAlias(alias);
    }
}
