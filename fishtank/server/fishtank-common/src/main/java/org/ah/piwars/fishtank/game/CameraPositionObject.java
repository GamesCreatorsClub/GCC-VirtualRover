package org.ah.piwars.fishtank.game;

import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;

public class CameraPositionObject extends GameObjectWithPositionAndOrientation {

    public CameraPositionObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public GameObjectType getType() { return FishtankGameTypeObject.CameraPosition; }
}
