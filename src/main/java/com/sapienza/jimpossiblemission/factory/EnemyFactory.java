package com.sapienza.jimpossiblemission.factory;

import com.sapienza.jimpossiblemission.Direction;
import com.sapienza.jimpossiblemission.model.Enemy;
import com.sapienza.jimpossiblemission.model.PatrollingRobot;
import com.sapienza.jimpossiblemission.model.Position;

import java.util.List;
import java.util.stream.Collectors;

public class EnemyFactory {

    public static Enemy createEnemy(Position spawn) {
        return new PatrollingRobot(spawn.getX(), spawn.getY(), Direction.LEFT);
    }

    public static List<Enemy> createEnemies(List<Position> spawns) {
        return spawns.stream().map(EnemyFactory::createEnemy).collect(Collectors.toList());
    }
}
