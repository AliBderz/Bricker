package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a falling power-up that grants the player
 * an extra life if collected by the paddle.
 */
public class Heart extends GameObject{
    private final static float FALL_SPEED_OF_HEART=100;
    private final BrickerGameManager gameManager;
    private final GameObject originalPaddle;
    private final float windowHeight;
    private final GameObjectCollection gameObjects;
    private boolean collected=false;


    /**
     * Constructs a new Heart instance that falls downwards.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object.
     * @param gameManager   The game manager instance to gain life.
     * @param originalPaddle The main paddle object, used for collision detection.
     * @param windowHeight  The height of the game window,
     *                     used for determining when the heart falls off screen.
     * @param gameObjects   The collection of game objects for adding/removing.
     */
    public Heart(Vector2 topLeftCorner
            , Vector2 dimensions
            , Renderable renderable, BrickerGameManager gameManager,

                 GameObject originalPaddle,
                 float windowHeight, GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable);
        this.gameManager = gameManager;
        this.originalPaddle = originalPaddle;
        this.windowHeight = windowHeight;
        this.gameObjects = gameObjects;
        // falling down
        this.setVelocity(new Vector2(0, FALL_SPEED_OF_HEART));
    }

    /**
     * Updates the heart's position and removes it if it falls below the screen.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        // remove the heart if it falls below the screen
        if(getTopLeftCorner().y()>windowHeight){
            gameObjects.removeGameObject(this, Layer.DEFAULT);

        }
    }


    /**
     * Specifies that the heart should only collide with the main paddle.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        // collides only with the original paddle
        return (!collected&&other==originalPaddle);
    }

    /**
     * Handles collision with the paddle by granting a life and removing the heart.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if(other==originalPaddle && !collected){
            collected=true;
            gameManager.gainLife();
            gameObjects.removeGameObject(this, Layer.DEFAULT);


        }
    }
}