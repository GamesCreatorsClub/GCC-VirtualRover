package org.ah.piwars.fishtank.game.fish;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.themvsus.engine.common.game.GameObjectFactory;

public class SpadefishFish extends Fish {

    public SpadefishFish(GameObjectFactory factory, int id) {
        super(factory, id);

        this.cameraPosition = new Vector3(-40f, 0f, 110f);
        this.cameraOrientation = new Quaternion();
        this.cameraOrientation.setEulerAngles(22.5f, 0f, 0f); // positive yaw is down
        this.cameraAngle = 45f;
    }
}
