package bricker.brick_strategies;

import bricker.gameobjects.Puck;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.util.Vector2;

import static bricker.main.BrickerGameManager.BALL_RADIUS;

/**
 * Implements the behavior to spawn two small
 * Puck objects from the center of the broken brick.
 */
public class PuckStrategy implements CollisionStrategy {
    private final CollisionStrategy toBeDecorated;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final GameObjectCollection gameObjects;

    private static final float PUCK_DIAMETER = (BALL_RADIUS*((float) 3/4));

    /**
     * Constructs a new PuckStrategy instance, wrapping an
     * existing collision strategy.
     */
    public PuckStrategy(CollisionStrategy toBeDecorated,
                        ImageReader imageReader,
                        SoundReader soundReader,
                        GameObjectCollection gameObjects) {
        this.toBeDecorated = toBeDecorated;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.gameObjects = gameObjects;
    }

    /**
     * Executes the collision, delegating removal to
     * the wrapped strategy, and then spawning two pucks.
     */
    public void onCollision(GameObject object0, GameObject object1) {
        // 1. Delegate the removal and counting to the next strategy in the chain.
        toBeDecorated.onCollision(object0, object1);

        // 2. Perform the special effect: Spawn two pucks
        // (Pucks use the mockBall.png image and same sound as ball).
        Vector2 brickCenter = object0.getCenter();
        Vector2 puckSize = new  Vector2(PUCK_DIAMETER, PUCK_DIAMETER);

        for(int i=0; i<2; i++){
            Puck puck = new Puck(brickCenter,
                    puckSize,
                    imageReader.readImage("assets/mockBall.png", true),
                    soundReader.readSound("assets/blop.wav")
            );

            // Pucks are added to the default layer.
            gameObjects.addGameObject(puck, Layer.DEFAULT);
        }
    }
}