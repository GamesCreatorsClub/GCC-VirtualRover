package org.ah.piwars.fishtank.game;

import org.ah.piwars.fishtank.game.fish.SpadefishFish;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

public abstract class FishtankGameTypeObject extends GameObjectType {

    public static FishtankGameTypeObject Spadefish = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new SpadefishFish(factory, -1); }
        @Override public String toString() { return "SpadefishType"; }
    };

    protected static GameObjectType[] DEFINED_TYPES = {
            Spadefish
    };
}
