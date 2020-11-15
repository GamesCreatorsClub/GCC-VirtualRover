package org.ah.piwars.fishtank.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

import static java.util.Arrays.asList;

public class FishtankGameObjectFactory extends GameObjectFactory {

    public FishtankGameObjectFactory() {
    }

    @Override
    protected GameObject createNewObject(GameObjectType gameObjectType) {
        return super.createNewObject(gameObjectType);
    }

    @Override
    protected void collectTypes() {
        addTypes(asList(FishtankGameTypeObject.DEFINED_TYPES));
    }
}
