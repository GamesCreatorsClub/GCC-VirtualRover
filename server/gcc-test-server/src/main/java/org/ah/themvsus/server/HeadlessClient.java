package org.ah.themvsus.server;

import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.AuthenticatedCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameMapCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.DesktopServerCommunication;
import org.ah.themvsus.engine.client.HeadlessClientServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeadlessClient implements Runnable, AuthenticatedCallback, GameReadyCallback, GameMapCallback {

    static enum State {
        Nothing, Authenticate, StartEngine, InGame;
    }

    private static final Logger LOGGER = Logger.getLogger("HeadlessClient");

    private DesktopServerCommunication serverCommunication;
    private HeadlessClientServerCommunicationAdapter serverCommunicationAdapter;
    private Properties config;
    private String alias;
    private String pass;
    private String mapId;
    private State state = State.Nothing;

    public HeadlessClient(String alias, String pass, Properties config) {
        this.config = config;
        this.alias = alias;
        this.pass = pass;
    }

    public void init() {
        serverCommunication = new DesktopServerCommunication();

        Thread thread = new Thread(this);
        thread.setName("Headless client; " + alias);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        try {
            serverCommunicationAdapter = new HeadlessClientServerCommunicationAdapter(serverCommunication);
            serverCommunicationAdapter.setAuthenticatedCallback(this);
            serverCommunicationAdapter.setGameMapCallback(this);
            serverCommunicationAdapter.addGameReadyCallback(this);

            String udpPortString = config.getProperty("udp.port", "7454");
            int udpPort = Integer.parseInt(udpPortString);

            serverCommunicationAdapter.connectToServer("127.0.0.1", udpPort, new ServerConnectionCallback() {
                @Override public void successful() {
                    LOGGER.info("Connected for " + alias);
                    setState(State.Authenticate);
                }

                @Override public void failed(String msg) {
                    LOGGER.warning("Failed to connect for " + alias);
                }
            });

            while (true) {
                try {
                    synchronized (state) {
                        state.wait(1000);
                    }
                } catch (InterruptedException ignore) { }
                if (state == State.Authenticate) {
                    state = State.Nothing;
                    LOGGER.info("Authenticating for " + alias);
                    setupToReadSignInUsername();
                } else if (state == State.StartEngine) {
                    state = State.Nothing;
                    LOGGER.info("Starting engine at map " + mapId + " for " + alias);
                    serverCommunicationAdapter.startEngine(mapId);
                }
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Failed in headless client's loop", t);
        }
    }

    private void setupToReadSignInUsername() {
        serverCommunicationAdapter.authenticate(alias, pass);
    }

    @Override
    public void gameMap(String mapId) {
        this.mapId = mapId;
        LOGGER.info("Got map id " + mapId + " for " + alias);
        setState(State.StartEngine);
    }

    @Override
    public void gameReady() {
        LOGGER.info("Game ready player " + alias);
        setState(State.InGame);
    }

    @Override
    public void authenticated() {
        LOGGER.info("Player authenticated " + alias);
    }

    private void setState(State state) {
        synchronized (state) {
            this.state = state;
            this.state.notifyAll();
        }
    }
}
