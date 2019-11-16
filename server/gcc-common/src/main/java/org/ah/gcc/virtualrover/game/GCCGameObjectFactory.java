package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

import java.util.List;

import static java.util.Arrays.asList;

public class GCCGameObjectFactory extends GameObjectFactory {

    public GCCGameObjectFactory() {
    }

    @Override
    public GCCPlayer newPlayer(int id, String alias) {
        GCCPlayer player = (GCCPlayer)super.newPlayer(id, alias);

        return player;
    }

    @Override
    protected GameObject createNewObject(GameObjectType gameObjectType) {
        return super.createNewObject(gameObjectType);
    }

    @Override
    protected void collectTypes() {
        List<GameObjectType> moreTypes = asList(GCCGameTypeObject.GameMessageObject);
        // Above - we need to instantiate GameMessageObject before super class accesses GameObjectType.PlayerObject
        super.collectTypes();
        addTypes(moreTypes);
    }
}
