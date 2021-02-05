package org.ah.piwars.fishtank.game.fish;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;

public class TetraFish extends Fish {

    public static BoundingBox BOUNDING_BOX = new BoundingBox(
            new Vector3(-2.3f, -12.8f, -8.2f),
            new Vector3(2.3f, 12.7f, 7.7f));

    public TetraFish(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BOUNDING_BOX;
    }

    @Override
    public GameObjectType getType() { return FishtankGameTypeObject.Tetrafish; }
}
