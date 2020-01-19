package org.ah.piwars.virtualrover.game.challenge;

import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.GameObject;

public abstract class CameraAbstractChallenge extends AbstractChallenge {

    protected int cameraId;

    protected CameraAbstractChallenge(PiWarsGame piwarsGame, String name) {
        super(piwarsGame, name);
    }

    @Override
    public void beforeGameObjectAdded(GameObject gameObject) {
    }

    @Override
    public void afterGameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            if (piwarsGame.isServer()) {
                CameraAttachment cameraAttachment = piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.CameraAttachment, piwarsGame.newId());
                cameraAttachment.attachToRover((Rover)gameObject);
                piwarsGame.addNewGameObjectImmediately(cameraAttachment);
                cameraId = cameraAttachment.getId();
            }
        }
        super.afterGameObjectAdded(gameObject);
        if (gameObject instanceof CameraAttachment) {
            CameraAttachment cameraAttachment = (CameraAttachment)gameObject;
            cameraId = cameraAttachment.getId();

            Rover rover = piwarsGame.getCurrentGameState().get(cameraAttachment.getParentId());
            if (rover != null) {
                cameraAttachment.attachToRover(rover);
            } else {
                // TODO add error here!!!
            }
        }
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        super.gameObjectRemoved(gameObject);
        if (gameObject instanceof Rover) {
            Rover rover = (Rover)gameObject;
            cameraId = 0;
            if (rover.getCameraId() != 0) {
                piwarsGame.removeGameObject(rover.getAttachemntId());
            }
        }
    }


    protected CameraAttachment getCameraAttachment() {
        Rover rover = getRover();
        if (rover != null) {
            CameraAttachment cameraAttachment = piwarsGame.getCurrentGameState().get(rover.getCameraId());
            return cameraAttachment;
        }
        return null;
    }
}
