package org.ah.piwars.fishtank.game;

import org.ah.piwars.fishtank.game.fish.SpadefishFish;
import org.ah.piwars.fishtank.game.fish.TetraFish;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

public abstract class FishtankGameTypeObject extends GameObjectType {

    public static FishtankGameTypeObject CameraPosition = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new CameraPositionObject(factory, -1); }
        @Override public String toString() { return "CameraPositionType"; }
    };

    public static FishtankGameTypeObject Spadefish = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new SpadefishFish(factory, -1); }
        @Override public String toString() { return "SpadefishType"; }
    };

    public static FishtankGameTypeObject Tetrafish = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new TetraFish(factory, -1); }
        @Override public String toString() { return "TetrafishType"; }
    };

    protected static GameObjectType[] DEFINED_TYPES = {
            CameraPosition, Spadefish, Tetrafish
    };
}
