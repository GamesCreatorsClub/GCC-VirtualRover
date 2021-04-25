package org.ah.piwars.fishtank;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.fishtank.engine.client.FishtankClientEngine;
import org.ah.piwars.fishtank.game.CameraPositionObject;
import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.piwars.fishtank.input.FishtankPlayerInput;
import org.ah.piwars.fishtank.logging.GdxClientLoggingAdapter;
import org.ah.piwars.fishtank.message.FishtankMessageFactory;
import org.ah.piwars.fishtank.message.FishtankPlayerInputMessage;
import org.ah.piwars.fishtank.view.ChatColor;
import org.ah.piwars.fishtank.view.Console;
import org.ah.piwars.fishtank.world.CameraPositionLink;
import org.ah.piwars.fishtank.world.FishModelLink;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.ServerClientAuthenticatedMessage;

import java.util.LinkedHashSet;
import java.util.Set;

public class ServerCommunicationAdapter extends CommonServerCommunicationAdapter<FishtankGame> implements GameObjectAddedListener, GameObjectRemovedListener {

    private Console console;

    private IntMap<VisibleObject> allVisibleObjects = new IntMap<VisibleObject>();

    private Set<Integer> cameraAttachmentIds = new LinkedHashSet<Integer>();
    private int mineSweeperId;

    private AssetManager assetManager;
    private boolean local;

    private Set<Integer> makeCameraSnapshot = new LinkedHashSet<Integer>();

    private CameraPositionLink cameraPositionModel;

    private FishtankPlayerInputMessage cameraPositionInputMessage;

    public ServerCommunicationAdapter(
            ServerCommunication serverCommunication,
            Console console,
            AssetManager assetManager) {

        super(GdxClientLoggingAdapter.getInstance(), serverCommunication);

        FishtankMessageFactory messageFactory = new FishtankMessageFactory();
        messageFactory.init();
        setMessageFactory(messageFactory);

        this.console = console;
        this.assetManager = assetManager;

        serverCommunication.setReceiver(this);

        cameraPositionInputMessage = messageFactory.createPlayerInputCommand();
    }

    @Override
    protected void processServerClientAuthenticateMessage(ServerClientAuthenticatedMessage serverInternalMessage) {
        super.processServerClientAuthenticateMessage(serverInternalMessage);
        cameraPositionInputMessage.setSessionId(sessionId);
    }

    @Override
    protected void processMessage(Message message) {
        super.processMessage(message);
    }

    public IntMap<VisibleObject> getVisibleObjects() {
        return allVisibleObjects;
    }

    public void setCameraPositionInput(FishtankPlayerInput cameraPositionInput) {
        getEngine().updateInput(cameraPositionInput);

        if (serverCommunication.isConnected()) {
            engine.sendPlayerInput();
        }
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
        cameraAttachmentIds.clear();
        mineSweeperId = 0;

        this.local = local;
        FishtankGame game = new FishtankGame(mapId);
        game.setIsServer(local);
        game.init();
        FishtankClientEngine engine = new FishtankClientEngine(game, serverCommunication, cameraPositionInputMessage, logger);
        engine.setFollowOnly(simulation);
        this.engine = engine;

        engine.getGame().addGameObjectAddedListener(this);
        engine.getGame().addGameObjectRemovedListener(this);
        engine.setPlayerInputs(cameraPositionInputMessage.getInputs());
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
        if (cameraAttachmentIds.contains(objectId)) {
            cameraAttachmentIds.remove(objectId);
        }
        if (mineSweeperId == objectId) {
            mineSweeperId = 0;
        }
    }

    @Override
    public void gameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof CameraPositionObject) {
            setCameraPositionModel(new CameraPositionLink(engine.getGame(), gameObject.getId()));
            gameObject.setLinkBack(getCameraPositionModel());
        } else if (gameObject instanceof Fish) {
            Fish fish = (Fish)gameObject;

            FishModelLink playerModel = new FishModelLink(engine.getGame(), gameObject.getId(), (FishtankGameTypeObject)fish.getType());
            allVisibleObjects.put(gameObject.getId(), playerModel);
            playerModel.makeObject(assetManager);
            fish.setLinkBack(playerModel);
        }
    }

    public boolean hasMakeCameraSnapshotRequest() {
        return makeCameraSnapshot.size() > 0;
    }

    public int getCameraIdForCameraSnapshotRequest() {
        return makeCameraSnapshot.size() > 0 ? makeCameraSnapshot.iterator().next() : -1;
    }

    public void reset() {
        for (VisibleObject visibleObject : allVisibleObjects.values()) {
            visibleObject.dispose();
        }

        allVisibleObjects.clear();
    }

    public CameraPositionLink getCameraPositionModel() {
        return cameraPositionModel;
    }

    public void setCameraPositionModel(CameraPositionLink cameraPositionModel) {
        this.cameraPositionModel = cameraPositionModel;
    }
}
