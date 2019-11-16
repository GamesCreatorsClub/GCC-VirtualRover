package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageFactory;

public class GameMessageObject extends GameObject {

    public GameMessageObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public Message newlyCreatedObjectMessage(MessageFactory messageFactory) {
        return null;
    }

    @Override
    public GameObjectType getType() {
        return null;
    }

}
