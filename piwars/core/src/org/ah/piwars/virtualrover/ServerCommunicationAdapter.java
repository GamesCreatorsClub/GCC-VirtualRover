package org.ah.piwars.virtualrover;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.engine.client.PiWarsClientEngine;
import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.MineSweeperStateObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.piwars.virtualrover.game.objects.BarrelObject;
import org.ah.piwars.virtualrover.game.objects.FishTowerObject;
import org.ah.piwars.virtualrover.game.objects.GolfBallObject;
import org.ah.piwars.virtualrover.game.objects.ToyCubeObject;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.input.PiWarsPlayerInput;
import org.ah.piwars.virtualrover.logging.GdxClientLoggingAdapter;
import org.ah.piwars.virtualrover.message.ClientScreenshotMessage;
import org.ah.piwars.virtualrover.message.PiWarsMessageFactory;
import org.ah.piwars.virtualrover.message.PiWarsPlayerInputMessage;
import org.ah.piwars.virtualrover.message.ServerRequestScreenshotMessage;
import org.ah.piwars.virtualrover.view.ChatColor;
import org.ah.piwars.virtualrover.view.Console;
import org.ah.piwars.virtualrover.world.BarrelModelLink;
import org.ah.piwars.virtualrover.world.FishTowerModelLink;
import org.ah.piwars.virtualrover.world.GolfBallModelLink;
import org.ah.piwars.virtualrover.world.PiNoonAttachmentModelLink;
import org.ah.piwars.virtualrover.world.PlayerModelLink;
import org.ah.piwars.virtualrover.world.ToyCubeModelLink;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.ServerClientAuthenticatedMessage;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ServerCommunicationAdapter extends CommonServerCommunicationAdapter<PiWarsGame> implements GameObjectAddedListener, GameObjectRemovedListener {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private Console console;

    private IntMap<VisibleObject> allVisibleObjects = new IntMap<VisibleObject>();

    protected PiWarsPlayerInputMessage playerOneInputMessage;
    protected PiWarsPlayerInputMessage playerTwoInputMessage;

    private int playerTwoId;
    private int gameMessageId;
    private Set<Integer> cameraAttachmentIds = new LinkedHashSet<Integer>();
    private int mineSweeperId;

    private AssetManager assetManager;
    private boolean local;

    private Set<Integer> makeCameraSnapshot = new LinkedHashSet<Integer>();

    public ServerCommunicationAdapter(
            ServerCommunication serverCommunication,
            Console console,
            AssetManager assetManager) {

        super(GdxClientLoggingAdapter.getInstance(), serverCommunication);

        PiWarsMessageFactory messageFactory = new PiWarsMessageFactory();
        messageFactory.init();
        setMessageFactory(messageFactory);

        this.console = console;
        this.assetManager = assetManager;

        serverCommunication.setReceiver(this);

        playerOneInputMessage = messageFactory.createPlayerInputCommand();
        playerTwoInputMessage = messageFactory.createPlayerInputCommand();
    }

    @Override
    protected void processServerClientAuthenticateMessage(ServerClientAuthenticatedMessage serverInternalMessage) {
        super.processServerClientAuthenticateMessage(serverInternalMessage);
        playerOneInputMessage.setSessionId(sessionId);
    }

    @Override
    protected void processMessage(Message message) {
        if (message instanceof ServerRequestScreenshotMessage) {
            int cameraId = ((ServerRequestScreenshotMessage)message).getCameraId();
            if (!makeCameraSnapshot.contains(cameraId)) {
                makeCameraSnapshot.add(cameraId);
            }
            message.free();
        } else {
            super.processMessage(message);
        }
    }

    public void setLocalPlayerIds(int playerOneId) {
        setLocalPlayerIds(playerOneId, -1);
    }

    public void setLocalPlayerIds(int playerOneId, int playerTwoId) {
        this.sessionId = playerOneId;
        this.playerTwoId = playerTwoId;
        ((PiWarsClientEngine)this.engine).setLocalPlayerIds(playerOneId, playerTwoId);
    }

    public IntMap<VisibleObject> getVisibleObjects() {
        return allVisibleObjects;
    }

    public void setPlayerOneInput(PiWarsPlayerInput playerInput) {
        getEngine().updateInput(playerInput);

        if (serverCommunication.isConnected()) {
            engine.sendPlayerInput();
        }
    }

    public void setPlayerTwoInput(PiWarsPlayerInput playerInput) {
        // getEngine().updateInput(playerInput); -- for second player: see next line
        playerTwoInputMessage.addInput(playerTwoId, getEngine().getGame().getCurrentFrameId(), -1, playerInput);
    }

    public PlayerInputs getPlayerTwoInputs() {
        return playerTwoInputMessage.getInputs();
    }

    @Override
    protected void notifyUser(String origin, String message, String colour) {
        console.chat(origin, message, ChatColor.fromString(colour));
    }

    public boolean isLocal() {
        return local;
    }

    public void startEngine(String mapId, int playerId, boolean local, boolean simulation) {
        sessionId = 0;
        this.mapId = mapId;
        this.playerId = playerId;
        playerTwoId = 0;
        gameMessageId = 0;
        cameraAttachmentIds.clear();
        mineSweeperId = 0;

        this.local = local;
        PiWarsGame game = new PiWarsGame(mapId);
        game.setIsServer(local);
        game.init();
        PiWarsClientEngine engine = new PiWarsClientEngine(game, serverCommunication, playerOneInputMessage, logger, sessionId, playerTwoId);
        engine.setFollowOnly(simulation);
        this.engine = engine;

        engine.getGame().addGameObjectAddedListener(this);
        engine.getGame().addGameObjectRemovedListener(this);
        engine.setPlayerInputs(playerOneInputMessage.getInputs());
        engine.setPlayerTwoInputs(playerTwoInputMessage.getInputs());
        if (!local) {
            sendClientReady();
        }

        fireGameReady();
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        gameObject.setLinkBack(null);
        int objectId = gameObject.getId();
        allVisibleObjects.remove(objectId);
        if (sessionId == objectId) {
            sessionId = 0;
        }
        if (playerTwoId == objectId) {
            playerTwoId = 0;
        }
        if (gameMessageId == objectId) {
            gameMessageId = 0;
        }
        if (cameraAttachmentIds.contains(objectId)) {
            cameraAttachmentIds.remove(objectId);
        }
        if (mineSweeperId == objectId) {
            mineSweeperId = 0;
        }
    }

    @Override
    public void gameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            Rover rover = (Rover)gameObject;

            PlayerModelLink playerModel = new PlayerModelLink(engine.getGame(), rover.getRoverType(), gameObject.getId(), rover.getAlias());
            playerModel.setRoverColour(rover);
            allVisibleObjects.put(rover.getId(), playerModel);
            playerModel.makeRobot(assetManager);
            rover.setLinkBack(playerModel);

            if (rover.getId() != sessionId && playerTwoId <= 0) {
                playerTwoId = rover.getId();
            }

        } else if (gameObject instanceof PiNoonAttachment) {
            PiNoonAttachment piNoonAttachment = (PiNoonAttachment)gameObject;
            PlayerModelLink playerModel = (PlayerModelLink)allVisibleObjects.get(piNoonAttachment.getParentId());

            PiNoonAttachmentModelLink piNoonAttachmentModel = new PiNoonAttachmentModelLink(engine.getGame(), playerModel.getColour(), piNoonAttachment, playerModel);
            piNoonAttachmentModel.makeModel(assetManager);
            piNoonAttachment.setLinkBack(piNoonAttachmentModel);

            allVisibleObjects.put(gameObject.getId(), piNoonAttachmentModel);
        } else if (gameObject instanceof CameraAttachment) {
            cameraAttachmentIds.add(gameObject.getId());
        } else if (gameObject instanceof GameMessageObject) {
            gameMessageId = gameObject.getId();
        } else if (gameObject instanceof BarrelObject) {
            BarrelObject barrelObject = (BarrelObject)gameObject;
            BarrelModelLink barrelModelLink = new BarrelModelLink(engine.getGame(), barrelObject.getId(), barrelObject.getColour());
            allVisibleObjects.put(barrelObject.getId(), barrelModelLink);
            barrelModelLink.make(assetManager);
            barrelObject.setLinkBack(barrelModelLink);
        } else if (gameObject instanceof ToyCubeObject) {
            ToyCubeObject toyCubeObject = (ToyCubeObject)gameObject;
            ToyCubeModelLink toyCubeModelLink = new ToyCubeModelLink(engine.getGame(), toyCubeObject.getId(), toyCubeObject.getColour());
            allVisibleObjects.put(toyCubeObject.getId(), toyCubeModelLink);
            toyCubeModelLink.make(assetManager);
            toyCubeObject.setLinkBack(toyCubeModelLink);
        } else if (gameObject instanceof ToyCubeObject) {
            ToyCubeObject toyCubeObject = (ToyCubeObject)gameObject;
            ToyCubeModelLink toyCubeModelLink = new ToyCubeModelLink(engine.getGame(), toyCubeObject.getId(), toyCubeObject.getColour());
            allVisibleObjects.put(toyCubeObject.getId(), toyCubeModelLink);
            toyCubeModelLink.make(assetManager);
            toyCubeObject.setLinkBack(toyCubeModelLink);
        } else if (gameObject instanceof GolfBallObject) {
            GolfBallObject golfBallObject = (GolfBallObject)gameObject;
            GolfBallModelLink golfBallModelLink = new GolfBallModelLink(engine.getGame(), golfBallObject.getId());
            allVisibleObjects.put(golfBallObject.getId(), golfBallModelLink);
            golfBallModelLink.make(assetManager);
            golfBallObject.setLinkBack(golfBallModelLink);
        } else if (gameObject instanceof FishTowerObject) {
            FishTowerObject fishTowerObject = (FishTowerObject)gameObject;
            FishTowerModelLink fishTowerModelLink = new FishTowerModelLink(engine.getGame(), fishTowerObject.getId());
            allVisibleObjects.put(fishTowerObject.getId(), fishTowerModelLink);
            fishTowerModelLink.make(assetManager);
            fishTowerObject.setLinkBack(fishTowerModelLink);
        } else if (gameObject instanceof MineSweeperStateObject) {
            mineSweeperId = gameObject.getId();
        }
    }

    public boolean hasPlayerOne() {
        return sessionId != 0;
    }

    public boolean hasPlayerTwo() {
        return playerTwoId != 0;
    }

    public Rover getPlayerOne() {
        if (engine != null && sessionId > 0) {
            return getEngine().getGame().getCurrentGameState().get(sessionId);
        }
        return null;
    }

    public Rover getPlayerTwo() {
        if (engine != null && playerTwoId > 0) {
            return getEngine().getGame().getCurrentGameState().get(playerTwoId);
        }
        return null;
    }
    public PlayerModelLink getPlayerOneVisualObject() {
        if (engine != null && sessionId > 0) {
            Rover player = getEngine().getGame().getCurrentGameState().get(sessionId);
            if (player != null) {
                return player.getLinkBack();
            }
        }
        return null;
    }

    public PlayerModelLink getPlayerTwoVisualObject() {
        if (engine != null && playerTwoId > 0) {
            Rover player = getEngine().getGame().getCurrentGameState().get(playerTwoId);
            if (player != null) {
                return player.getLinkBack();
            }
        }
        return null;
    }

    public GameMessageObject getGameMessageObject() {
        if (engine != null && gameMessageId > 0) {
            GameMessageObject gameMessageObject = engine.getGame().getCurrentGameState().get(gameMessageId);
            return gameMessageObject;
        }
        return null;
    }

    public CameraAttachment firstCameraAttachment() {
        if (engine != null && cameraAttachmentIds.size() > 0) {
            CameraAttachment cameraAttachment = engine.getGame().getCurrentGameState().get(cameraAttachmentIds.iterator().next());
            return cameraAttachment;
        }
        return null;
    }

    public CameraAttachment getCameraAttachment(int cameraId) {
        if (engine != null && cameraAttachmentIds.size() > 0 && cameraAttachmentIds.contains(cameraId)) {
            CameraAttachment cameraAttachment = engine.getGame().getCurrentGameState().get(cameraId);
            return cameraAttachment;
        }
        return null;
    }

    public MineSweeperStateObject getMineSweeperStateObject() {
        if (engine != null && mineSweeperId > 0) {
            MineSweeperStateObject mineSweeperStateObject = engine.getGame().getCurrentGameState().get(mineSweeperId);
            return mineSweeperStateObject;
        }
        return null;
    }

    public boolean hasMakeCameraSnapshotRequest() {
        return makeCameraSnapshot.size() > 0;
    }

    public int getCameraIdForCameraSnapshotRequest() {
        return makeCameraSnapshot.size() > 0 ? makeCameraSnapshot.iterator().next() : -1;
    }

    public void makeCameraSnapshot(int cameraId, byte[] snapshotData) {
        makeCameraSnapshot.remove(cameraId);
        int packetPayloadAllowance;

        ClientScreenshotMessage clientScreenshotMessage =
                ((PiWarsMessageFactory)messageFactory).createClientScreenshotMessage(cameraId, 0, 0, EMPTY_ARRAY, 0, 0);
        try {
            packetPayloadAllowance = clientScreenshotMessage.size();
        } finally {
            clientScreenshotMessage.free();
        }

        int packetNo = 0;
        int packetLen = getServerCommmunication().getNoHeaderMTU() - packetPayloadAllowance;
        int totalPackets = snapshotData.length / packetLen;
        if (snapshotData.length % packetLen != 0) {
            totalPackets += 1;
        }

        while (packetNo < totalPackets) {
            int thisPacketLen = packetLen;
            if (packetNo * packetLen + thisPacketLen > snapshotData.length) {
                thisPacketLen = snapshotData.length - packetNo * packetLen;
            }
            clientScreenshotMessage = ((PiWarsMessageFactory)messageFactory).createClientScreenshotMessage(cameraId, packetNo, totalPackets, snapshotData, packetNo * packetLen, thisPacketLen);
            try {
                serverCommunication.send(clientScreenshotMessage);
            } catch (IOException e) {
                logger.error("Network", "Error sending ClientScreenshotMessage", e);
            } finally {
                clientScreenshotMessage.free();
            }
            packetNo++;
        }
    }

    public void reset() {
        for (VisibleObject visibleObject : allVisibleObjects.values()) {
            visibleObject.dispose();
        }

        allVisibleObjects.clear();
    }
}
