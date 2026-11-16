package bricker.brick_strategies;

import bricker.gameobjects.GraphicLifeCounter;
import bricker.gameobjects.Heart;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Implements the behavior to drop a Heart power-up
 * if the player has less than the maximum number of lives.
 */
public class HeartStrategy implements CollisionStrategy {
    private final CollisionStrategy toBeDecorated;
    private final ImageReader imageReader;
    private final GameObjectCollection gameObjects;
    private final BrickerGameManager gameManager;
    private final GameObject mainPaddle;
    private final float windowHeight;

    /**
     * Constructs a new HeartStrategy instance,
     * wrapping an existing collision strategy.
     */
    public HeartStrategy(CollisionStrategy toBeDecorated,
                         ImageReader imageReader,
                         GameObjectCollection gameObjects,
                         BrickerGameManager gameManager,
                         GameObject mainPaddle,
                         float windowHeight) {
        this.toBeDecorated = toBeDecorated;
        this.imageReader = imageReader;
        this.gameObjects = gameObjects;
        this.gameManager = gameManager;
        this.mainPaddle = mainPaddle;
        this.windowHeight = windowHeight;
    }

    /**
     * Executes the collision, delegates brick removal,
     * and spawns a Heart object if conditions are met.
     */
    @Override
    public void onCollision(GameObject object0, GameObject object1) {
        // remove the brick
        toBeDecorated.onCollision(object0, object1);

        if (gameManager.getCurrentLives() < GraphicLifeCounter.MAX_HEARTS
                && !thereIsFallingHeart()) {

            spawnHeart(object0.getCenter());
        }

    }

    private boolean thereIsFallingHeart() {
        for (GameObject obj : gameObjects.objectsInLayer(Layer.DEFAULT)) {
            if (obj instanceof Heart) return true;
        }
        return false;
    }

    private void spawnHeart(Vector2 center) {
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);
        Heart heart = new Heart(center.subtract(BrickerGameManager.HEART_SIZE.mult(0.5f)),
                BrickerGameManager.HEART_SIZE,
                heartImage,
                gameManager,
                mainPaddle,windowHeight,
                gameObjects);
        gameObjects.addGameObject(heart,Layer.DEFAULT);
    }
}