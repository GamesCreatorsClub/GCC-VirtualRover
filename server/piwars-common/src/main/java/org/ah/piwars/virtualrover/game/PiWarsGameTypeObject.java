package org.ah.piwars.virtualrover.game;

import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.piwars.virtualrover.game.objects.BarrelObject;
import org.ah.piwars.virtualrover.game.rovers.CBISRover;
import org.ah.piwars.virtualrover.game.rovers.GCCRoverM16;
import org.ah.piwars.virtualrover.game.rovers.GCCRoverM18;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

public abstract class PiWarsGameTypeObject extends GameObjectType {
    public static GameObjectType GameMessageObject = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new GameMessageObject(factory, 0); }
        @Override public String toString() { return "GameMessageObjectType"; }
    };

    public static GameObjectType BarrelObject = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new BarrelObject(factory, 0); }
        @Override public String toString() { return "BarrelObjectType"; }
    };

    public static GameObjectType MineSweeperStateObject = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new MineSweeperStateObject(factory, 0); }
        @Override public String toString() { return "MineSweeperStateObjectType"; }
    };

    public static GameObjectType PiNoonAttachment = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new PiNoonAttachment(factory, 0); }
        @Override public String toString() { return "PiNoonAttachmentType"; }
    };

    public static GameObjectType CameraAttachment = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new CameraAttachment(factory, 0); }
        @Override public String toString() { return "CameraAttachmentType"; }
    };

    public static GameObjectType GCCRoverM16 = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new GCCRoverM16(factory, -1); }
        @Override public String toString() { return "GCCRoverM16Type"; }
    };

    public static GameObjectType GCCRoverM18 = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new GCCRoverM18(factory, -1); }
        @Override public String toString() { return "GCCRoverM18Type"; }
    };

    public static GameObjectType CBISRover = new GameObjectType() {
        @Override public GameObject newObject(GameObjectFactory factory) { return new CBISRover(factory, -1); }
        @Override public String toString() { return "CBISRoverType"; }
    };

    protected static GameObjectType[] DEFINED_TYPES = {
            GameMessageObject,
            BarrelObject,
            MineSweeperStateObject,
            PiNoonAttachment,
            CameraAttachment,
            GCCRoverM16,
            GCCRoverM18,
            CBISRover
    };
}
