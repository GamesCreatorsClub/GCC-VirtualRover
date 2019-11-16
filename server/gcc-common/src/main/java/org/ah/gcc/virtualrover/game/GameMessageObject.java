package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GameMessageObject extends GameObject {

    private String message = "";

    public GameMessageObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public GameObjectType getType() { return GCCGameTypeObject.GameMessageObject; }

    public void setMessage(String message) {
        if (message == null) {
            message = "";
        }
        if (!message.equals(this.message)) {
            changed = true;
        }

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Message newlyCreatedObjectMessage(MessageFactory messageFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);

        serializer.serializeString(message);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);

        message = serializer.deserializeString();
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);
        GameMessageObject gameMessageObject = (GameMessageObject)newObject;
        gameMessageObject.setMessage(message);
        return gameMessageObject;
    }
}
