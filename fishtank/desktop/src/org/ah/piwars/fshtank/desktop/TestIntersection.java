package org.ah.piwars.fshtank.desktop;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class TestIntersection {

    public static void main(String[] args) throws Exception {
        BoundingBox bb = new BoundingBox(new Vector3(-10, -10, -10), new Vector3(10, 10, 10));

        Ray ray = new Ray(new Vector3(1, 20, 1), new Vector3(0, -1, 0));

        Vector3 interesectionPoint = new Vector3();

        boolean doesIntersect = Intersector.intersectRayBounds(ray, bb, interesectionPoint);

        System.out.println("doesIntersect=" + doesIntersect);
        System.out.println(interesectionPoint);
    }

}
