package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.Direction;

import java.util.List;

public class PlatformGroup {
    private final List<Platform> platforms;
    private final List<Position> stops;

    public PlatformGroup(List<Platform> platforms, List<Position> stops) {
        this.platforms = platforms;
        this.stops = stops;
    }

    public void update() {
        stopPlatformWhenAtStop();
        platforms.forEach(Platform::update);
    }

    public void move(Direction direction) {
        if (canMove(direction)) {
            platforms.forEach(p -> p.setMoving(direction));
        }
    }

    public boolean canMove(Direction direction) {
        for (Platform platform : platforms) {
            int platformY = platform.getY();

            boolean hasStop;
            if (direction == Direction.UP) {
                hasStop = stops.stream().anyMatch(stop -> stop.getY() < platformY);
            } else if (direction == Direction.DOWN) {
                hasStop = stops.stream().anyMatch(stop -> stop.getY() > platformY);
            } else {
                hasStop = false;
            }

            if (!hasStop) {
                return false;
            }
        }

        return true;
    }

    private void stopPlatformWhenAtStop() {
        for (Platform platform : platforms) {
            Direction movingDirection = platform.getMovingDirection();
            if (movingDirection == null) continue;

            for (Position stop : stops) {
                if (movingDirection == Direction.DOWN && stop.getY() > platform.getY() &&
                        platform.getY() + platform.getSpeed() >= stop.getY()) {
                    platform.setY(stop.getY());
                    platform.stop();
                    break;
                }

                if (movingDirection == Direction.UP && stop.getY() < platform.getY() &&
                        platform.getY() - platform.getSpeed() <= stop.getY()) {
                    platform.setY(stop.getY());
                    platform.stop();
                    break;
                }
            }
        }
    }


    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<Position> getStops() {
        return stops;
    }
}
