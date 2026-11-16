package bricker.gameobjects;

import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

import static bricker.main.BrickerGameManager.BALL_SPEED;

/**
 * Represents a small secondary ball spawned by a special brick,
 * inheriting properties from the main Ball.
 */
public class Puck extends Ball{

    /**
     * Constructs a new Puck instance with a random initial velocity.
     *
     * @param topLeftCorner  Position of the object, in window coordinates (pixels).
     * @param dimensions     Width and height in window coordinates.
     * @param renderable     The renderable representing the object.
     * @param collisionSound The sound to play when the puck collides.
     */
    public Puck(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        setRandomVelocity();
    }

    private void setRandomVelocity() {
        Random random = new Random();
        double angle = random.nextDouble() * Math.PI;

        float velocityX = (float) Math.cos(angle) * BALL_SPEED;
        float velocityY = (float) Math.sin(angle) * BALL_SPEED;

        this.setVelocity(new Vector2(velocityX, velocityY));
    }

}