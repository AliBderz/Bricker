package bricker.brick_strategies;

import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.util.Counter;

/**
 * Implements the chain-reaction behavior,
 * removing a brick and triggering collisions on all adjacent neighbors.
 */
public class ExplosiveStrategy implements CollisionStrategy{
    private final CollisionStrategy toBeDecorated;
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final SoundReader soundReader;

    /**
     * Constructs a new ExplosiveStrategy,
     * wrapping an existing collision strategy.
     */
    public ExplosiveStrategy(CollisionStrategy toBeDecorated,
                             GameObjectCollection gameObjects,
                             ImageReader imageReader,
                             SoundReader soundReader) {
        this.toBeDecorated = toBeDecorated;
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
    }

    /**
     * Executes the collision: removes the current brick,
     * prevents recursive loops, plays a sound,
     * and recursively triggers the collision logic for all adjacent neighbors.
     */
    @Override
    public void onCollision(GameObject object0, GameObject object1) {
        if (!(object0 instanceof Brick hitBrick)) return;

        toBeDecorated.onCollision(object0, object1);

        if(object0.getTag().equals("Exploded")){
            return;
        }
        object0.setTag("Exploded");


        soundReader.readSound("assets/explosion.wav").play();

        int hitRow = hitBrick.getRow();
        int hitCol = hitBrick.getCol();

        int[][] offsets = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        for (int[] offset : offsets) {
            int neighborRow = hitRow + offset[0];
            int neighborCol = hitCol + offset[1];

            GameObject neighbor = findBrickAt(neighborRow, neighborCol);
            if(neighbor instanceof Brick){
                ((Brick)neighbor).getCollisionStrategy().onCollision(neighbor, object1);
            }
        }
    }

    private GameObject findBrickAt(int row, int col) {
        for (GameObject obj : gameObjects.objectsInLayer(Layer.STATIC_OBJECTS)) {
            if (obj instanceof Brick b) {
                if (b.getRow() == row && b.getCol() == col) return b;
            }
        }
        return null;
    }
}