package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GameMessageObject extends GameObject {

    private boolean flashing = false;
    private boolean inGame = false;
    private boolean waiting = false;
    private String message = "";

    public GameMessageObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public GameObjectType getType() { return GCCGameTypeObject.GameMessageObject; }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public void setMessage(String message, boolean flashing) {
        if (message == null) {
            message = "";
        }
        if (!message.equals(this.message) || flashing != this.flashing) {
            changed = true;
        }

        this.message = message;
        this.flashing = flashing;
    }

    public boolean isFlashing() {
        return flashing;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);

        serializer.serializeByte((flashing ? 1 : 0) + (inGame ? 2 : 0) + (waiting ? 4 : 0));
        serializer.serializeString(message);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);

        byte status = serializer.deserializeByte();
        boolean flashing = (status & 0x1b) != 0;
        boolean inGame = (status & 0x2b) != 0;
        boolean waiting = (status & 0x4b) != 0;
        String message = serializer.deserializeString();

        changed = changed || flashing != this.flashing;
        changed = changed || inGame != this.inGame;
        changed = changed || waiting != this.waiting;
        changed = changed || !this.message.equals(message);

        this.flashing = flashing;
        this.inGame = inGame;
        this.waiting = waiting;
        this.message = message;
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 1 + 2 + (message != null ? message.length() : 0);
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);
        GameMessageObject gameMessageObject = (GameMessageObject)newObject;
        gameMessageObject.flashing = flashing;
        gameMessageObject.inGame = inGame;
        gameMessageObject.waiting = waiting;
        gameMessageObject.message = message;
        return gameMessageObject;
    }

    @Override
    public String toString() {
        return "GameMessage" + (changed ? "*[" : "[") + id + ", ingame=" + inGame + ", waiting=" + waiting + ", msg=\"" + message + "\"]";
    }
}
