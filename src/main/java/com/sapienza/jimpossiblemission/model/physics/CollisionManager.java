package com.sapienza.jimpossiblemission.model.physics;

import java.awt.*;
import java.util.List;

public class CollisionManager {

    public CollisionManager() {
    }

    public CollisionResult checkCollision(List<Rectangle> collisionObjects, Rectangle boundingBox, int dx, int dy) {
        // TODO CHECK: in Java, Rectangle è un oggetto passato per riferimento. Se chiami checkCollision, la boundingBox che passi come argomento verrà effettivamente spostata. Se la tua intenzione era solo "simulare" il movimento senza modificare l'oggetto originale, dovresti creare una copia prima di traslare:
        boundingBox.translate(dx, dy);

        for (Rectangle rect : collisionObjects) {
            if (boundingBox.intersects(rect)) {
                Rectangle inter = boundingBox.intersection(rect);
                return new CollisionResult(true, rect, inter);
            }
        }
        return new CollisionResult(false, null, null);
    }

    public CollisionResult checkCollision(Rectangle collisionObject, Rectangle boundingBox, int dx, int dy) {
        return checkCollision(List.of(collisionObject), boundingBox, dx, dy);
    }

    public CollisionResult checkCollision(Rectangle collisionObject, Rectangle boundingBox) {
        return checkCollision(List.of(collisionObject), boundingBox, 0, 0);
    }

    public CollisionResult checkPointCollision(int pointX, int pointY, Rectangle boundingBox) {
        if (boundingBox.contains(pointX, pointY)) {
            return new CollisionResult(true, boundingBox, new Rectangle(pointX, pointY, 10, 200));
        }
        return new CollisionResult(false, null, null);
    }



    public static class CollisionResult {
        public final boolean collided;
        public final Rectangle collider;
        public final Rectangle intersection;

        public CollisionResult(boolean collided, Rectangle collider, Rectangle intersection) {
            this.collided = collided;
            this.collider = collider;
            this.intersection = intersection;
        }
    }

}
