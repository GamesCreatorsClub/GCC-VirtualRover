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

    private IntMap<VisibleObject> sprites = new IntMap<VisibleObject>();

    protected GCCPlayerInputMessage playerOneInputMessage;
    protected GCCPlayerInputMessage playerTwoInputMessage;

    private int playerTwoId;

    public ServerCommunicationAdapter(
            ServerCommunication serverCommunication,
            MessageFactory messageFactory,
            Console console) {

        super(new CommonServerCommunicationAdapter.LoggingCallback() {
                @Override public void error(String area, String msg, Throwable e) {
                    Gdx.app.error(area, msg, e);
                }
            },
            serverCommunication, messageFactory);

        this.console = console;

        serverCommunication.setReceiver(this);

        playerOneInputMessage = ((GCCMessageFactory)messageFactory).createPlayerInputCommand();
        playerTwoInputMessage = ((GCCMessageFactory)messageFactory).createPlayerInputCommand();
    }

    public void setLocalPlayerIds(int playerOneId, int playerTwoId) {
        this.sessionId = playerOneId;
        this.playerTwoId = playerTwoId;
    }

    public IntMap<VisibleObject> getSprites() {
        return sprites;
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
        if (local) {
            sessionId = 1;
            playerTwoId = 2;
        }
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
        sprites.remove(gameObject.getId());
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
            sprites.put(playerObject.getId(), playerModel);

//            VisibleObject sprite = new Tank(spriteTextures, gameObject.getId());
//
//            gameObject.setLinkBack(sprite);
//            if (gameObject.getLinkBack() == null) {
//                System.out.println("sprite == null");
//            }
//            sprites.put(gameObject.getId(), sprite);
        }
    }
}
