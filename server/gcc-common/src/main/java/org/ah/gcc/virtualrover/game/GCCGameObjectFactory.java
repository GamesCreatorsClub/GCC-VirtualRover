package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

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
        if (gameObjectType == GameObjectType.PlayerObject) {
            return new GCCPlayer(this, 0);
        }

        return super.createNewObject(gameObjectType);
    }
}
