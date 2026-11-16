package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a temporary, extra paddle granted as a power-up,
 * which disappears after a fixed number of hits.
 */
public class MockPaddle extends Paddle {
    public static boolean isInstantiated = false;
    private final GameObjectCollection gameObjects;
    private int collisionCounter;
    private static final int MAX_COLLISIONS = 4;

    /**
     * Constructs a new MockPaddle instance and marks the extra paddle as active.
     */
    public MockPaddle(Vector2 topLeftCorner,
                      Vector2 dimensions,
                      Renderable renderable,
                      UserInputListener userInputListener,
                      GameObjectCollection gameObjects,
                      Vector2 windowDimensions) {
        super(topLeftCorner, dimensions, renderable,
                userInputListener,windowDimensions);
        this.gameObjects = gameObjects;
        collisionCounter = 0;
        isInstantiated = true;
    }

    /**
     * Handles collision events; tracks the number of
     * collisions and removes the paddle after 4 hits.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        collisionCounter++;
        if (collisionCounter >= MAX_COLLISIONS) {
            gameObjects.removeGameObject(this, Layer.DEFAULT);
            isInstantiated = false;
        }
    }
}