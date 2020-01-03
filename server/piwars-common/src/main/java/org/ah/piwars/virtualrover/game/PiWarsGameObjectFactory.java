package org.ah.piwars.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

import static java.util.Arrays.asList;

public class PiWarsGameObjectFactory extends GameObjectFactory {

    public PiWarsGameObjectFactory() {
    }

    @Override
    protected GameObject createNewObject(GameObjectType gameObjectType) {
        return super.createNewObject(gameObjectType);
    }

    @Override
    protected void collectTypes() {
        addTypes(asList(PiWarsGameTypeObject.DEFINED_TYPES));
    }
}
