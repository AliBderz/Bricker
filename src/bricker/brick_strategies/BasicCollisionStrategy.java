package bricker.brick_strategies;

import
        bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * Implements the default behavior for a broken brick:
 * removes the brick and decrements the counter.
 */
public class BasicCollisionStrategy implements CollisionStrategy {

    private final GameObjectCollection gameObjects;
    private final Counter brickCounter;
    /**
     * Constructs the basic collision strategy,
     * which is responsible for removing the brick.
     */
    public BasicCollisionStrategy(GameObjectCollection gameObjects,
                                  Counter brickCounter) {
        this.gameObjects = gameObjects;
        this.brickCounter = brickCounter;
    }

    /**
     * Handles the collision by removing the brick from
     * the game and decrementing the brick counter.
     */
    @Override
    public void onCollision(GameObject object0, GameObject object1) {
        if(gameObjects.removeGameObject(object0, Layer.STATIC_OBJECTS)){
            brickCounter.decrement();
        }
    }

}