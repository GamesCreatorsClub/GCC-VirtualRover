package org.ah.piwars.fishtank.game;

import org.ah.piwars.fishtank.game.fish.AnchorObject;
import org.ah.piwars.fishtank.game.fish.BallObject;
import org.ah.piwars.fishtank.game.fish.BenchyObject;
import org.ah.piwars.fishtank.game.fish.SpadefishFish;
import org.ah.piwars.fishtank.game.fish.TetraFish;
import org.ah.piwars.fishtank.game.fish.TresureObject;
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

    public static FishtankGameTypeObject Anchor = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new AnchorObject(factory, -1); }
        @Override public String toString() { return "AnchorObjectType"; }
    };

    public static FishtankGameTypeObject Tresure = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new TresureObject(factory, -1); }
        @Override public String toString() { return "TresureObjectType"; }
    };

    public static FishtankGameTypeObject Benchy = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new BenchyObject(factory, -1); }
        @Override public String toString() { return "BenchyObjectType"; }
    };

    public static FishtankGameTypeObject Ball = new FishtankGameTypeObject() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new BallObject(factory, -1); }
        @Override public String toString() { return "BallType"; }
    };

    protected static GameObjectType[] DEFINED_TYPES = {
            CameraPosition, Spadefish, Tetrafish, Anchor, Tresure, Benchy, Ball
    };
}
