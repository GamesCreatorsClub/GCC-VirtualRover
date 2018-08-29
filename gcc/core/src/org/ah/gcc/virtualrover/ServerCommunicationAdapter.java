package org.ah.gcc.virtualrover;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.view.ChatColor;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.Player;
import org.ah.themvsus.engine.common.message.ChatMessage;
import org.ah.themvsus.engine.common.message.MessageFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;

public class ServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> implements GameObjectAddedListener, GameObjectRemovedListener {

    private Console console;

    private IntMap<VisibleObject> sprites = new IntMap<VisibleObject>();

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
    }

    public IntMap<VisibleObject> getSprites() {
        return sprites;
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

    @Override
    public void startEngine(String mapId) {
        GCCGame game = new GCCGame();
        game.init();
        engine = new ClientEngine<GCCGame>(game, sessionId);

        engine.getGame().setGameObjectAddedListener(this);
        engine.getGame().setGameObjectRemovedListener(this);
        engine.setPlayerInputs(playerInputMessage.getInputs());
        sendClientReady();

        fireGameReady();
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        gameObject.setLinkBack(null);
        sprites.remove(gameObject.getId());
        System.out.println("removedObject");
    }

    @Override
    public void gameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Player) {
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
