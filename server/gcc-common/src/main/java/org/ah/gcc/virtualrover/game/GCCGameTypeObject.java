package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

public abstract class GCCGameTypeObject extends GameObjectType {
    public static GameObjectType GameMessageObject = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new GameMessageObject(factory, 0); }
        @Override public String toString() { return "GameMessageObjectType"; }
    };

    public static GameObjectType BarrelObject = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new BarrelObject(factory, 0); }
        @Override public String toString() { return "BarrelObjectType"; }
    };

    static { // Override with concrete object!
        PlayerObject = new GameObjectType() {
            @Override public GameObject newObject(GameObjectFactory factory) { return new GCCPlayer(factory, -1); }
            @Override public String toString() { return "PlayerObjectType"; }
        };
    }
}
