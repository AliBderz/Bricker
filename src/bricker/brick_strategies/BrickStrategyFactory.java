package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.Random;

/**
 * Responsible for creating and composing randomized,
 * decorator-wrapped collision strategies for bricks.
 */
public class BrickStrategyFactory {
    private final GameObjectCollection gameObjects;
    private final BrickerGameManager gameManager;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final WindowController windowController;
    private final Vector2 windowDimensions;
    private final Counter brickCounter;
    private final GameObject mainPaddle;
    private static final int MAX_STRATEGIES = 3;

    /**
     * Constructs a factory responsible for creating randomized,
     * decorator-wrapped collision strategies.
     */
    public BrickStrategyFactory(GameObjectCollection gameObjects,
                                BrickerGameManager gameManager,
                                ImageReader imageReader,
                                SoundReader soundReader,
                                UserInputListener inputListener,
                                WindowController windowController,
                                Vector2 windowDimensions,
                                Counter brickCounter,
                                GameObject mainPaddle) {
        this.gameObjects = gameObjects;
        this.gameManager = gameManager;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.windowDimensions = windowDimensions;
        this.brickCounter = brickCounter;
        this.mainPaddle = mainPaddle;
    }

    /**
     * Returns a randomized collision strategy for a new brick,
     * enforcing the 50/50 split between basic and special behaviors.
     */
    public CollisionStrategy getStrategy() {
        if(new Random().nextBoolean()){
            // 50% chance for the basic strategy
            return new BasicCollisionStrategy(gameObjects, brickCounter);
        }

        CollisionStrategy baseStrategy = new BasicCollisionStrategy(gameObjects, brickCounter);
        return buildStrategy(baseStrategy, MAX_STRATEGIES);
    }

    private CollisionStrategy buildStrategy(CollisionStrategy toBeDecorated, int maxStrategies) {
        if(maxStrategies <= 0){
            // The brick had 3 special strategies in it, what a lucky Brick
            return toBeDecorated;
        }
        int strategyType = new Random().nextInt(5);
        switch (strategyType){
            case 0:
                return new PuckStrategy(toBeDecorated,
                        imageReader, soundReader, gameObjects);
            case 1:
                return new ExtraPaddleStrategy(toBeDecorated,
                        imageReader, inputListener, windowDimensions, gameObjects);
            case 2:
                return new HeartStrategy(toBeDecorated, imageReader,
                        gameObjects, gameManager, mainPaddle, windowDimensions.y());
            case 3:
                return new ExplosiveStrategy(toBeDecorated,
                        gameObjects, imageReader, soundReader);
            case 4:
                CollisionStrategy double1 = buildStrategy(toBeDecorated,
                        maxStrategies-1);
                return buildStrategy(double1, maxStrategies-1);
            default:
                return toBeDecorated;
        }

    }
}