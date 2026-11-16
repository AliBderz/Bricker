package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * Represents the player-controlled paddle
 * responsible for reflecting the ball.
 */
public class Paddle extends GameObject {

    private static final float PADDLE_MOVEMENT_SPEED = 300f;
    private final UserInputListener userInputListener;
    private final Vector2 windowDimensions;

    /**
     * Constructs a new Paddle instance and sets up input listening and window constraints.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object.
     * @param userInputListener The listener for keyboard input to move the paddle.
     * @param windowDimensions The dimensions of the game window, used for clamping movement.
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions,
                  Renderable renderable,
                  UserInputListener userInputListener,
                  Vector2 windowDimensions) {
        super(topLeftCorner, dimensions, renderable);
        this.userInputListener = userInputListener;

        this.windowDimensions = windowDimensions;
    }


    /**
     * Reads player input, sets the paddle's velocity,
     * and clamps its position within the window edges.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir=Vector2.ZERO;
        if(userInputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if(userInputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(PADDLE_MOVEMENT_SPEED));
        Vector2 currentPosition = getTopLeftCorner();

        // Clamp left boundary
        if(currentPosition.x()<0){
            currentPosition = new Vector2(0,currentPosition.y());
            setTopLeftCorner(currentPosition);
        }

        // Clamp right boundary
        float rightLimit= windowDimensions.x()-getDimensions().x();
        if(currentPosition.x()>rightLimit){
            currentPosition = new Vector2(rightLimit,currentPosition.y());
            setTopLeftCorner(currentPosition);
        }
    }
}