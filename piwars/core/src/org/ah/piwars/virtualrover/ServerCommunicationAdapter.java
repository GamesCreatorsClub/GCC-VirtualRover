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
import org.ah.piwars.virtualrover.world.PiNoonAttachmentModelLink;
import org.ah.piwars.virtualrover.world.PlayerModelLink;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.message.ChatMessage;
import org.ah.themvsus.engine.common.message.Message;

import java.io.IOException;

public class ServerCommunicationAdapter extends CommonServerCommunicationAdapter<PiWarsGame> implements GameObjectAddedListener, GameObjectRemovedListener {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private Console console;

    private IntMap<VisibleObject> allVisibleObjects = new IntMap<VisibleObject>();

    protected PiWarsPlayerInputMessage playerOneInputMessage;
    protected PiWarsPlayerInputMessage playerTwoInputMessage;

    private int playerTwoId;
    private int gameMessageId;
    private int cameraAttachmentId;
    private int mineSweeperId;

    private AssetManager assetManager;
    private boolean local;

    private boolean makeCameraSnapshot;

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
    protected void processMessage(Message message) {
        if (message instanceof ServerRequestScreenshotMessage) {
            makeCameraSnapshot = true;
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


    @Override
    protected void processChatMessage(ChatMessage chatMessage) {
        String origin = chatMessage.getOrigin();
        if (origin.length() == 0) {
            origin = "System";
        }
        ChatColor color = ChatColor.BLACK;
        console.chat(origin, chatMessage.getLine(), color);
    }

    public boolean isLocal() {
        return local;
    }

    public void startEngine(String mapId, boolean local, boolean simulation) {
        sessionId = 0;
        playerTwoId = 0;
        gameMessageId = 0;
        cameraAttachmentId = 0;
        mineSweeperId = 0;

        this.local = local;
        PiWarsGame game = new PiWarsGame(mapId);
        game.setIsServer(local);
        game.init();
        PiWarsClientEngine engine = new PiWarsClientEngine(game, serverCommunication, playerOneInputMessage, logger, sessionId, playerTwoId);
        engine.setFollowOnly(simulation);
        this.engine = engine;

        engine.getGame().setGameObjectAddedListener(this);
        engine.getGame().setGameObjectRemovedListener(this);
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
        if (cameraAttachmentId == objectId) {
            cameraAttachmentId = 0;
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
            cameraAttachmentId = gameObject.getId();
        } else if (gameObject instanceof GameMessageObject) {
            gameMessageId = gameObject.getId();
        } else if (gameObject instanceof BarrelObject) {
            BarrelObject barrelObject = (BarrelObject)gameObject;
            BarrelModelLink barrelModelLink = new BarrelModelLink(engine.getGame(), barrelObject.getId(), barrelObject.getBarrelColour());
            allVisibleObjects.put(barrelObject.getId(), barrelModelLink);
            barrelModelLink.make(assetManager);
            barrelObject.setLinkBack(barrelModelLink);
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

    public CameraAttachment getCameraAttachment() {
        if (engine != null && cameraAttachmentId > 0) {
            CameraAttachment cameraAttachment = engine.getGame().getCurrentGameState().get(cameraAttachmentId);
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

    public boolean isMakeCameraSnapshot() {
        return makeCameraSnapshot;
    }

    public void makeCameraSnapshot(byte[] snapshotData) {
        makeCameraSnapshot = false;
        int packetPayloadAllowance;

        ClientScreenshotMessage clientScreenshotMessage =
                ((PiWarsMessageFactory)messageFactory).createClientScreenshotMessage(0, 0, EMPTY_ARRAY, 0, 0);
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
            clientScreenshotMessage = ((PiWarsMessageFactory)messageFactory).createClientScreenshotMessage(packetNo, totalPackets, snapshotData, packetNo * packetLen, thisPacketLen);
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
