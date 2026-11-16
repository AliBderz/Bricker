package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * Manages the textual display of the player's
 * remaining lives, including color changes based on life count.
 */
public class NumericLifeCounter {
    private final TextRenderable textRenderable;

    /**
     * Constructs a new numeric life counter and adds it to the UI layer.
     */
    public NumericLifeCounter(Vector2 topLeftCorner,
                              Vector2 dimensions,
                              GameObjectCollection gameObjects,
                              int initiateLives){
        this.textRenderable = new TextRenderable(
                String.valueOf(initiateLives));
        setColor(initiateLives);
        GameObject lifeText = new GameObject(topLeftCorner,
                dimensions, textRenderable);
        gameObjects.addGameObject(lifeText, Layer.UI);
    }

    /**
     * Updates the displayed life count and changes
     * the text color based on the remaining lives.
     */
    public void Update(int lives){
        textRenderable.setString(String.valueOf(lives));
        setColor(lives);
    }

    private void setColor(int lives){
        if(lives >= 3){
            textRenderable.setColor(Color.GREEN);
        }else if(lives == 2){
            textRenderable.setColor(Color.YELLOW);
        }else{
            textRenderable.setColor(Color.RED);
        }
    }
}