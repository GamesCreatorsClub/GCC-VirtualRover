package org.ah.gcc.virtualrover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntMap;

import org.ah.gcc.virtualrover.engine.client.GCCClientEngine;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.gcc.virtualrover.message.GCCPlayerInputMessage;
import org.ah.gcc.virtualrover.view.ChatColor;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.gcc.virtualrover.world.PlayerModel;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.message.ChatMessage;
import org.ah.themvsus.engine.common.message.MessageFactory;

public class ServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> implements GameObjectAddedListener, GameObjectRemovedListener {

    private Console console;

    private IntMap<VisibleObject> allVisibleObjects = new IntMap<VisibleObject>();

    protected GCCPlayerInputMessage playerOneInputMessage;
    protected GCCPlayerInputMessage playerTwoInputMessage;

    private int playerTwoId;

    private ModelFactory modelFactory;

    public ServerCommunicationAdapter(
            ServerCommunication serverCommunication,
            MessageFactory messageFactory,
            Console console,
            ModelFactory modelFactory) {

        super(new CommonServerCommunicationAdapter.LoggingCallback() {
                @Override public void error(String area, String msg, Throwable e) {
                    Gdx.app.error(area, msg, e);
                }
            },
            serverCommunication, messageFactory);

        this.console = console;
        this.modelFactory = modelFactory;

        serverCommunication.setReceiver(this);

        playerOneInputMessage = ((GCCMessageFactory)messageFactory).createPlayerInputCommand();
        playerTwoInputMessage = ((GCCMessageFactory)messageFactory).createPlayerInputCommand();
    }

    public void setLocalPlayerIds(int playerOneId, int playerTwoId) {
        this.sessionId = playerOneId;
        this.playerTwoId = playerTwoId;
        ((GCCClientEngine)this.engine).setLocalPlayerIds(playerOneId, playerTwoId);
    }

    public IntMap<VisibleObject> getVisibleObjects() {
        return allVisibleObjects;
    }

    public void setPlayerOneInput(int currentFrameNo, GCCPlayerInput playerInput) {
        playerOneInputMessage.addInput(sessionId, currentFrameNo, playerInput);

        if (serverCommunication.isConnected()) {
            sendPlayerInput(playerOneInputMessage);
        } else {
            // Allow shortcut with inputs before
            engine.receiveMessage(playerOneInputMessage);
        }
    }

    public void setPlayerTwoInput(int currentFrameNo, GCCPlayerInput playerInput) {
        playerTwoInputMessage.addInput(sessionId, currentFrameNo, playerInput);

        if (serverCommunication.isConnected()) {
            sendPlayerInput(playerTwoInputMessage);
        } else {
            // Allow shortcut with inputs before
            engine.receiveMessage(playerTwoInputMessage);
        }
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

    public void startEngine(String mapId, boolean local) {
        GCCGame game = new GCCGame(mapId);
        game.init();
        GCCClientEngine engine = new GCCClientEngine(game, sessionId, playerTwoId);
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
        allVisibleObjects.remove(gameObject.getId());
    }

    @Override
    public void gameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof GCCPlayer) {
            GCCPlayer playerObject = (GCCPlayer)gameObject;

            String alias = playerObject.getAlias();
            Color playerColor = Color.WHITE;
            if ("Blue".equals(alias)) {
                playerColor = Color.BLUE;
            } else if ("Green".equals(alias)) {
                playerColor = Color.GREEN;
            }

            PlayerModel playerModel = new PlayerModel(engine.getGame(), playerObject.getRoverType(), gameObject.getId(), playerObject.getAlias(), playerColor);
            allVisibleObjects.put(playerObject.getId(), playerModel);
            playerModel.makeRobot(modelFactory);
            playerObject.setLinkBack(playerModel);

//            VisibleObject sprite = new Tank(spriteTextures, gameObject.getId());
//
//            gameObject.setLinkBack(sprite);
//            if (gameObject.getLinkBack() == null) {
//                System.out.println("sprite == null");
//            }
//            sprites.put(gameObject.getId(), sprite);
        }
    }

    public boolean hasPlayerOne() {
        return sessionId != 0;
    }

    public boolean hasPlayerTwo() {
        return playerTwoId != 0;
    }

    public GCCPlayer getPlayerOne() {
        if (engine != null && sessionId != 0) {
            return (GCCPlayer)engine.getGame().getCurrentGameState().get(sessionId);
        }
        return null;
    }

    public GCCPlayer getPlayerTwo() {
        if (engine != null && playerTwoId != 0) {
            return (GCCPlayer)getEngine().getGame().getCurrentGameState().get(playerTwoId);
        }
        return null;
    }
    public PlayerModel getPlayerOneVisualObject() {
        if (engine != null && sessionId != 0) {
            GCCPlayer player = (GCCPlayer)engine.getGame().getCurrentGameState().get(sessionId);
            if (player != null) {
                return player.getLinkBack();
            }
        }
        return null;
    }

    public PlayerModel getPlayerTwoVisualObject() {
        if (engine != null && playerTwoId != 0) {
            GCCPlayer player = (GCCPlayer)engine.getGame().getCurrentGameState().get(playerTwoId);
            if (player != null) {
                return player.getLinkBack();
            }
        }
        return null;
    }
}
