package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

import static java.util.Arrays.asList;

public class GCCGameObjectFactory extends GameObjectFactory {

    public GCCGameObjectFactory() {
    }

    @Override
    protected GameObject createNewObject(GameObjectType gameObjectType) {
        return super.createNewObject(gameObjectType);
    }

    @Override
    protected void collectTypes() {
        addTypes(asList(GCCGameTypeObject.DEFINED_TYPES));
    }
}
