package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Defines the interface for all collision strategies,
 * decoupling the brick's reaction from its core class.
 */
public interface CollisionStrategy {

    /**
     * Defines the action to be taken when a collision occurs.
     */
    void onCollision(GameObject object0, GameObject object1);
}