package org.ah.gcc.virtualrover.game;

import org.ah.gcc.virtualrover.game.attachments.CameraAttachment;
import org.ah.gcc.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.gcc.virtualrover.game.objects.BarrelObject;
import org.ah.gcc.virtualrover.game.rovers.CBISRover;
import org.ah.gcc.virtualrover.game.rovers.GCCRover;
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

    public static GameObjectType PiNoonAttachment = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new PiNoonAttachment(factory, 0); }
        @Override public String toString() { return "PiNoonAttachmentType"; }
    };

    public static GameObjectType CameraAttachment = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new CameraAttachment(factory, 0); }
        @Override public String toString() { return "CameraAttachmentType"; }
    };

    public static GameObjectType GCCRover = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new GCCRover(factory, -1); }
        @Override public String toString() { return "GCCRoverType"; }
    };

    public static GameObjectType CBISRover = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new CBISRover(factory, -1); }
        @Override public String toString() { return "CBISRoverType"; }
    };

    protected static GameObjectType[] DEFINED_TYPES = {
            GameMessageObject,
            BarrelObject,
            PiNoonAttachment,
            GCCRover,
            CBISRover
    };
}
