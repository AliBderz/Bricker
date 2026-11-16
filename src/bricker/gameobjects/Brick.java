package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a standard brick in the
 * game grid that must be broken by the ball.
 */
public class Brick extends GameObject {
    private final CollisionStrategy collisionStrategy;
    private int  row;
    private int col;

    /**
     * Constructs a new Brick instance,
     * associating it with a specific collision strategy and grid position.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     * the GameObject will not be rendered.
     * @param collisionStrategy The strategy defining how this brick reacts to a collision.
     * @param row           The row index of the brick in the grid.
     * @param col           The column index of the brick in the grid.
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions,
                 Renderable renderable,
                 CollisionStrategy collisionStrategy,  int row, int col) {
        super(topLeftCorner, dimensions, renderable);

        this.collisionStrategy = collisionStrategy;
        this.row = row;
        this.col = col;

    }

    /**
     * Delegates the collision handling logic to the associated CollisionStrategy.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        //System.out.println("Brick : ("+row+","+col+")");
        collisionStrategy.onCollision(this, other);
    }

    /**
     * Returns the collision strategy associated with this brick.
     * This is required for special behaviors like the Explosive Brick.
     */
    public CollisionStrategy getCollisionStrategy() {
        return collisionStrategy;
    }

    /**
     * Returns the row index of the brick in the grid.
     */
    public int getRow(){
        return row;
    }

    /**
     * Returns the column index of the brick in the grid.
     */
    public int getCol(){
        return col;
    }

    /**
     * Sets the row index of the brick.
     */
    public void setRow(int row){
        this.row = row;
    }

    /**
     * Sets the column index of the brick.
     */
    public void setCol(int col){
        this.col = col;
    }

}