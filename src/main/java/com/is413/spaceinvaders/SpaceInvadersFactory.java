package com.is413.spaceinvaders;

import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.is413.spaceinvaders.SpaceInvaders.Entities;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;


public class SpaceInvadersFactory implements EntityFactory {

    static final int missleSpeed = 1000;                //Travel speed of fired missiles


    @Spawns("SHIP")                                     //Spawns ship entity controlled by player controls
    public Entity newShip(SpawnData data) {
        return entityBuilder().from(data).type(Entities.SHIP).viewWithBBox("ship.png").collidable().build();

    }

    @Spawns("MISSILE_EXPLOSION")                                            //Missile explosion entity spawned whenever a missile contacts an enemy ship
    public Entity newMissileExplosion(SpawnData data) {
        Duration animTime = new Duration(700);                           //animTime duration defines the duration of explosion animation
        var explode = new AnimationChannel(List.of(                         //animTime also defines the duration of MISSILE_EXPLOSION entity lifetime
                image("explode/Bomb_3_Explosion_002.png"),        //So this entity will be removed once the animation is complete
                image("explode/Bomb_3_Explosion_003.png"),
                image("explode/Bomb_3_Explosion_004.png"),
                image("explode/Bomb_3_Explosion_005.png"),
                image("explode/Bomb_3_Explosion_006.png"),
                image("explode/Bomb_3_Explosion_007.png"),
                image("explode/Bomb_3_Explosion_008.png")
        ), animTime);
        Point2D pos = (Point2D) data.get("pos");                           //Grab the position previously stored in SpawnData object to be spawn position of this entity
        return entityBuilder().view(new AnimatedTexture(explode).play()).with(new ExpireCleanComponent(animTime)).scale(0.15,0.15).anchorFromCenter().at(pos.add(100, -50)).build();

    }

    @Spawns("MISSILE")                                      //Missile entity spawn whenever player presses fire button
    public Entity newMissile(SpawnData data) {
        Entity ship = getGameWorld().getSingleton(Entities.SHIP);           //Get ship entity reference
        Point2D missileVector = ship.getCenter().subtract(0, ship.getY()-100);      //Calculate vector from position of ship
        Point2D dir = missileVector.subtract(ship.getCenter());                         //Direction of vector is straight up from ship

        return entityBuilder().from(data).type(Entities.MISSILE).viewWithBBox("missile.png").scale(0.25, 0.25).anchorFromCenter()
                .collidable().with(new ProjectileComponent(dir, missleSpeed)).with(new OffscreenCleanComponent()).build();      //Return projectile component traveling straight up from ship at predefined speed
    }

    @Spawns("ENEMY_EXPLOSION")                                      //Enemy ship explosion entity, same as missile explosion
    public Entity newEnemyExplosion(SpawnData data) {
        Duration animTime = new Duration(700);
        var explode = new AnimationChannel(List.of(
                image("enemy_explosion/enemy_explosion (1).png"),
                image("enemy_explosion/enemy_explosion (2).png"),
                image("enemy_explosion/enemy_explosion (3).png"),
                image("enemy_explosion/enemy_explosion (4).png"),
                image("enemy_explosion/enemy_explosion (5).png"),
                image("enemy_explosion/enemy_explosion (6).png"),
                image("enemy_explosion/enemy_explosion (7).png"),
                image("enemy_explosion/enemy_explosion (8).png"),
                image("enemy_explosion/enemy_explosion (9).png")
                ), animTime);
        Point2D pos = (Point2D) data.get("pos");
        return entityBuilder().view(new AnimatedTexture(explode).play()).with(new ExpireCleanComponent(animTime)).scale(0.15,0.15).anchorFromCenter().at(pos.add(100, -50)).build();
    }
    @Spawns("LVLSTAGE")                                                 //LVLSTAGE is just a background image created as a passive entity
    public Entity newLvlStage(SpawnData data) {
        return entityBuilder().view("lv1.jpeg").anchorFromCenter().scale(1.1,1.1).at(0,0).build();
    }

    @Spawns("ENEMY")
    public Entity newEnemy(SpawnData data) {        //Enemy ship entity

        Duration animTime = new Duration(1500);         //Enemy ship is animated with a looping animation
        var enemy = new AnimationChannel(List.of(
                image("enemy/enemy (1).png"),
                image("enemy/enemy (2).png"),
                image("enemy/enemy (3).png"),
                image("enemy/enemy (4).png"),
                image("enemy/enemy (5).png"),
                image("enemy/enemy (6).png"),
                image("enemy/enemy (7).png"),
                image("enemy/enemy (8).png"),
                image("enemy/enemy (9).png"),
                image("enemy/enemy (10).png")
                ), animTime);



        return entityBuilder()                                          //Returns an enemy ship with movement path defined by waypoints in enemyWaypoints arrayList
                .from(data)
                .type(Entities.ENEMY)
                .viewWithBBox(new AnimatedTexture(enemy).loop())
                .scale(0.2,0.2)
                .collidable()
                .with(new WaypointMoveComponent(Double.valueOf((int) data.get("moveSpeed")), SpaceInvaders.enemyWaypoints))
                .build();
    }
}
