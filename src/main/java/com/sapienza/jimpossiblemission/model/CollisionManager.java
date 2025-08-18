package com.sapienza.jimpossiblemission.model;

import java.awt.*;
import java.util.List;

public class CollisionManager {

    public CollisionManager() {
    }

    public CollisionResult checkCollision(List<Rectangle> collisionObjects, Rectangle boundingBox, int dx, int dy) {
        boundingBox.translate(dx, dy);

        for (Rectangle rect : collisionObjects) {
            if (boundingBox.intersects(rect)) {
                Rectangle inter = boundingBox.intersection(rect);
                return new CollisionResult(true, rect, inter);
            }
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
