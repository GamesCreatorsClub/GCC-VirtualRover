package org.ah.themvsus.engine.client;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntSet;

import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.input.PiWarsPlayerInput;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.AuthenticatedCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameListCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameMapCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

import java.util.Properties;

public class HeadlessClient
    implements
        Runnable,
        AuthenticatedCallback,
        GameReadyCallback,
        GameMapCallback,
        GameListCallback {

    static enum State {
        Nothing, Authenticate, StartEngine, InGame;
    }

    private HeadlessClientServerCommunication serverCommunication;
    private HeadlessClientServerCommunicationAdapter serverCommunicationAdapter;
    private Properties config;
    private String alias;
    private String pass;
    private String mapId;
    private int playerId;
    private State state = State.Nothing;
    private LocalClientLogging logger;
    private PiWarsPlayerInput roverInput;
    private IntSet unknownObjectIds = new IntSet();
    private boolean runLocalPredictiveEngine = false;

    public HeadlessClient(String alias, String pass, Properties config) {
        this.config = config;
        this.alias = alias;
        this.pass = pass;
        this.logger = new LocalClientLogging();
    }

    public void init() {
        serverCommunication = new HeadlessClientServerCommunication(logger);
        roverInput = (PiWarsPlayerInput)PiWarsPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?

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
            serverCommunicationAdapter.setGameListCallback(this);
            serverCommunicationAdapter.setGameReadyCallback(this);

            String udpPortString = config.getProperty("udp.port", "7454");
            int udpPort = Integer.parseInt(udpPortString);

            serverCommunicationAdapter.connectToServer("127.0.0.1", udpPort, new ServerConnectionCallback() {
                @Override public void successful() {
                    logger.info("Connected for " + alias);
                    setState(State.Authenticate);
                }

                @Override public void failed(String msg) {
                    logger.warning("Failed to connect for " + alias);
                }
            });

            while (true) {
                try {
                    synchronized (state) {
                        state.wait(40); // 24 times a second
                    }
                } catch (InterruptedException ignore) { }
                if (state == State.Authenticate) {
                    state = State.Nothing;
                    logger.info("Authenticating for " + alias);
                    setupToReadSignInUsername();
                } else if (state == State.StartEngine) {
                    state = State.Nothing;
                    logger.info("Starting engine at map " + mapId + " for " + alias);
                    serverCommunicationAdapter.startEngine(mapId);
                } else if (state == State.InGame) {

                    if (runLocalPredictiveEngine) {
                        serverCommunicationAdapter.setPlayerInput(roverInput);

                        ClientEngine<PiWarsGame> engine = serverCommunicationAdapter.getEngine();
                        if (engine != null) {
                            long now = System.currentTimeMillis();
                            engine.progressEngine(now, unknownObjectIds);
                            if (unknownObjectIds.size > 0) {
                                serverCommunicationAdapter.requestFullUpdate(unknownObjectIds);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Failed in headless client's loop", t);
        }
    }

    private void setupToReadSignInUsername() {
        serverCommunicationAdapter.authenticate(alias, pass);
    }

    @Override
    public void gameMap(String mapId, int playerId) {
        this.mapId = mapId;
        this.playerId = playerId;
        logger.info("Got map id " + mapId + " for " + alias);
        setState(State.StartEngine);
    }

    @Override
    public void gameReady() {
        logger.info("Game ready player " + alias);
        setState(State.InGame);
    }

    @Override
    public void authenticated() {
        logger.info("Player authenticated " + alias);
        serverCommunicationAdapter.sendRequestGameList();
    }

    @Override
    public void gameListCallback(int seq, Array<String> gameIds) {
        if (Math.abs(seq) == 1 && gameIds.size > 0) {
            logger.info("Got game list. Joining first game.");
            serverCommunicationAdapter.sendJoinGame(gameIds.get(0));
        } else {
            logger.info("Got game list, but there are no games.");
        }
    }

    private void setState(State state) {
        synchronized (state) {
            this.state = state;
            this.state.notifyAll();
        }
    }
}
