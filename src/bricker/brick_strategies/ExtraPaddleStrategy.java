package bricker.brick_strategies;

import bricker.gameobjects.MockPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import static bricker.main.BrickerGameManager.PADDLE_HEIGHT;
import static bricker.main.BrickerGameManager.PADDLE_WIDTH;

/**
 * Implements the behavior to spawn a temporary
 * extra paddle if one is not already present.
 */
public class ExtraPaddleStrategy implements CollisionStrategy {
    private final CollisionStrategy toBeDecorated;
    private final ImageReader imageReader;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final GameObjectCollection gameObjects;

    /**
     * Constructs a new ExtraPaddleStrategy instance,
     * wrapping an existing collision strategy.
     */
    public ExtraPaddleStrategy(CollisionStrategy toBeDecorated,
                               ImageReader imageReader,
                               UserInputListener inputListener,
                               Vector2 windowDimensions,
                               GameObjectCollection gameObjects) {
        this.toBeDecorated = toBeDecorated;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
    }

    /**
     * Executes the collision, delegates brick removal,
     * and spawns an extra paddle if one isn't already active.
     */
    @Override
    public void onCollision(GameObject object0, GameObject object1) {
        toBeDecorated.onCollision(object0, object1);

        if(!MockPaddle.isInstantiated){
            float centerX = windowDimensions.x()/2;
            float centerY = windowDimensions.y()/2;

            // Create the paddle position at the center of the screen
            Vector2 paddlePos = new Vector2(centerX-(PADDLE_WIDTH/2), centerY-(PADDLE_HEIGHT/2));
            Vector2 paddleSize =  new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT);

            Renderable paddleImage = imageReader.readImage("assets/paddle.png", true);
            MockPaddle extraPaddle = new MockPaddle(paddlePos,
                    paddleSize,
                    paddleImage,
                    inputListener,
                    gameObjects,
                    windowDimensions);

            // Add the extra paddle to the default layer
            gameObjects.addGameObject(extraPaddle, Layer.DEFAULT);
        }
    }
}