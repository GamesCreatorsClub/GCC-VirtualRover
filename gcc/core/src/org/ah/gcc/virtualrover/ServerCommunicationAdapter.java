package org.ah.gcc.virtualrover;

import com.badlogic.gdx.utils.IntMap;

import org.ah.gcc.virtualrover.engine.client.GCCClientEngine;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GameMessageObject;
import org.ah.gcc.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.gcc.virtualrover.game.objects.BarrelObject;
import org.ah.gcc.virtualrover.game.rovers.Rover;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.logging.GdxClientLoggingAdapter;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.gcc.virtualrover.message.GCCPlayerInputMessage;
import org.ah.gcc.virtualrover.view.ChatColor;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.gcc.virtualrover.world.BarrelModelLink;
import org.ah.gcc.virtualrover.world.PiNoonAttachmentModelLink;
import org.ah.gcc.virtualrover.world.PlayerModelLink;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.message.ChatMessage;

public class ServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> implements GameObjectAddedListener, GameObjectRemovedListener {

    private Console console;

    private IntMap<VisibleObject> allVisibleObjects = new IntMap<VisibleObject>();

    protected GCCPlayerInputMessage playerOneInputMessage;
    protected GCCPlayerInputMessage playerTwoInputMessage;

    private int playerTwoId;
    private int gameMessageId;

    private ModelFactory modelFactory;
    private boolean local;

    public ServerCommunicationAdapter(
            ServerCommunication serverCommunication,
            Console console,
            ModelFactory modelFactory) {

        super(GdxClientLoggingAdapter.getInstance(), serverCommunication);

        GCCMessageFactory messageFactory = new GCCMessageFactory();
        messageFactory.init();
        setMessageFactory(messageFactory);

        this.console = console;
        this.modelFactory = modelFactory;

        serverCommunication.setReceiver(this);

        playerOneInputMessage = messageFactory.createPlayerInputCommand();
        playerTwoInputMessage = messageFactory.createPlayerInputCommand();
    }

    public void setLocalPlayerIds(int playerOneId) {
        setLocalPlayerIds(playerOneId, -1);
    }

    public void setLocalPlayerIds(int playerOneId, int playerTwoId) {
        this.sessionId = playerOneId;
        this.playerTwoId = playerTwoId;
        ((GCCClientEngine)this.engine).setLocalPlayerIds(playerOneId, playerTwoId);
    }

    public IntMap<VisibleObject> getVisibleObjects() {
        return allVisibleObjects;
    }

    public void setPlayerOneInput(GCCPlayerInput playerInput) {
        getEngine().updateInput(playerInput);

        if (serverCommunication.isConnected()) {
            engine.sendPlayerInput();
        }
    }

    public void setPlayerTwoInput(GCCPlayerInput playerInput) {
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
        this.local = local;
        GCCGame game = new GCCGame(mapId);
        game.setIsServer(local);
        game.init();
        GCCClientEngine engine = new GCCClientEngine(game, serverCommunication, playerOneInputMessage, logger, sessionId, playerTwoId);
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
    }

    @Override
    public void gameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            Rover rover = (Rover)gameObject;

            PlayerModelLink playerModel = new PlayerModelLink(engine.getGame(), rover.getRoverType(), gameObject.getId(), rover.getAlias());
            playerModel.setRoverColour(rover);
            allVisibleObjects.put(rover.getId(), playerModel);
            playerModel.makeRobot(modelFactory);
            rover.setLinkBack(playerModel);

            if (rover.getId() != sessionId && playerTwoId <= 0) {
                playerTwoId = rover.getId();
            }

        } else if (gameObject instanceof PiNoonAttachment) {
            PiNoonAttachment piNoonAttachment = (PiNoonAttachment)gameObject;
            PlayerModelLink playerModel = (PlayerModelLink)allVisibleObjects.get(piNoonAttachment.getParentId());

            PiNoonAttachmentModelLink piNoonAttachmentModel = new PiNoonAttachmentModelLink(engine.getGame(), playerModel.getColour(), piNoonAttachment, playerModel);
            piNoonAttachmentModel.makeModel(modelFactory);
            piNoonAttachment.setLinkBack(piNoonAttachmentModel);

            allVisibleObjects.put(gameObject.getId(), piNoonAttachmentModel);
        } else if (gameObject instanceof GameMessageObject) {
            gameMessageId = gameObject.getId();
        } else if (gameObject instanceof BarrelObject) {
            BarrelObject barrelObject = (BarrelObject)gameObject;
            BarrelModelLink barrelModelLink = new BarrelModelLink(engine.getGame(), barrelObject.getId(), barrelObject.getBarrelColour());
            allVisibleObjects.put(barrelObject.getId(), barrelModelLink);
            barrelModelLink.make(modelFactory);
            barrelObject.setLinkBack(barrelModelLink);
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
}
