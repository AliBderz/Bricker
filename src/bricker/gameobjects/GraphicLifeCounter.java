package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

// Manages the visual display of the player's
// remaining lives using heart icons.
public class GraphicLifeCounter {
    private final GameObjectCollection gameObjects;
    private final GameObject[] hearts;
    private final Renderable renderable;
    private final Vector2 topLeftCorner;
    public static final int MAX_HEARTS = 4;

    // Constructs a new GraphicLifeCounter and sets the
    // initial number of displayed hearts.
    public GraphicLifeCounter(Vector2 topLeftCorner,
                              int numberOfLives,
                              GameObjectCollection gameObjects,
                              Renderable renderable) {
        this.gameObjects = gameObjects;
        this.renderable = renderable;
        this.topLeftCorner = topLeftCorner;

        // The maximum number of lives allowed in the game is 4
        this.hearts = new GameObject[MAX_HEARTS];

        setDisplayedLives(numberOfLives);
    }

    // Updates the number of hearts displayed on
    // the screen based on the current life count.
    public void setDisplayedLives(int lives) {
        // clear
        for (int i = 0; i < MAX_HEARTS; i++) {
            if (hearts[i] != null) {
                gameObjects.removeGameObject(hearts[i], Layer.UI);
                hearts[i] = null;
            }
        }

        // draw requested hearts
        for (int i = 0; i < lives; i++) {
            Vector2 pos = topLeftCorner.add(new Vector2(i * 30, 0));
            hearts[i] = new GameObject(pos, BrickerGameManager.HEART_SIZE, renderable);
            gameObjects.addGameObject(hearts[i], Layer.UI);
        }
    }









}
