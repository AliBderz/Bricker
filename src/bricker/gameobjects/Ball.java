package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.Sound;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents the main ball or puck in the game,
 * handling physics and sound upon collision.
 */
public class Ball extends GameObject {

    private final Sound collisionSound;

    /**
     * Constructs a new Ball instance, setting its sound effect for collisions.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object.
     * @param collisionSound The sound to play when the ball collides.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
    }

    /**
     * Handles collision events by playing the collision
     * sound and reflecting the ball's velocity.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.collisionSound.play();
        Vector2 flippedVelocity = getVelocity().flipped(collision.getNormal());
        this.setVelocity(flippedVelocity);
    }
}