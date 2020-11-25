package org.ah.piwars.fishtank.game.fish;

import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

public class SpadefishFish extends Fish {

    public SpadefishFish(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public GameObjectType getType() { return FishtankGameTypeObject.Spadefish; }
}
