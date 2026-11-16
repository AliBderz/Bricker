package bricker.main;

import bricker.brick_strategies.*;
import bricker.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Manages the main game loop, initialization,
 * collision layers, and high-level win/loss conditions.
 */
public class BrickerGameManager extends GameManager {
    // --- 1. GAME CONSTANTS  ---
    private static final float BORDER_WIDTH = 10f;
    public static final float BALL_RADIUS = 20f;
    private static final float DOWN_BORDER_HEIGHT = 40f;
    public static final float PADDLE_HEIGHT = 15f;
    public static final float PADDLE_WIDTH = 100f;
    public static final float BALL_SPEED = 200f;
    private static final float BRICK_HEIGHT = 15f;
    private static final float BRICK_PADDING_WIDTH = 5f;
    private static final float BRICK_PADDING_HEIGHT = 2f;
    private static final int INITIAL_LIVES = 3;
    private static final int DEFAULT_BRICK_ROWS = 7;
    private static final int DEFAULT_BRICK_COLS = 8;
    private static final int WINDOW_DIMENSION_X = 700;
    private static final int WINDOW_DIMENSION_Y = 600;
    public static final Vector2 HEART_SIZE=new Vector2(20,20);

    // --- 2. ASSET PATHS ---
    private static final String PATH_BALL_SOUND = "assets/blop.wav";
    private static final String PATH_BALL_IMAGE = "assets/ball.png";
    private static final String PATH_PADDLE_IMAGE = "assets/paddle.png";
    private static final String PATH_BRICK_IMAGE = "assets/brick.png";
    private static final String PATH_HEART_IMAGE = "assets/heart.png";
    private static final String PATH_BG_IMAGE = "assets/DARK_BG2_small.jpeg";
    private static final String PATH_EXPLOSION_SOUND= "assets/explosion.wav";
    private static final float WALL_THICKNESS = 5;
    private final int brickCols;
    private final int brickRows;
    private int hearts;
    private GameObject paddle;
    private Renderable brickImage;
    private Renderable heartImage;
    private GameObject ball;
    private Vector2 windowDimensions;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private WindowController windowController;
    private GraphicLifeCounter graphicLifeCounter;
    private NumericLifeCounter numericLifeCounter;
    private Counter brickCounter;
    private UserInputListener inputListener;

    /**
     * Constructs a new BrickerGameManager with the
     * specified window dimensions and brick grid size.
     */
    public BrickerGameManager(String windowTitle,
                              Vector2 windowDimensions,
                              int brickCols, int brickRows) {
        super(windowTitle, windowDimensions);
        this.brickCols = brickCols;
        this.brickRows = brickRows;
    }

    /**
     * Initializes game resources, creates objects (ball, paddle, walls, bricks), and sets up life counters.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader,
                inputListener, windowController);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.windowController = windowController;
        MockPaddle.isInstantiated = false;
        float wallThickness = BORDER_WIDTH;
        this.hearts = INITIAL_LIVES;
        int totalBricks = brickCols * brickRows;
        this.brickCounter = new Counter(totalBricks);
        windowDimensions = windowController.getWindowDimensions();
        // Creating Renders
        loadAssets();
        //create Walls
        createWalls();
        //create paddle
        createPaddle();
        // create background
        createBackground();
        // Creating the Ball
        createBall();
        // Creating the factory
        BrickStrategyFactory strategyFactory = new BrickStrategyFactory(
                gameObjects(),
                this,
                imageReader,
                soundReader,
                inputListener,
                windowController,
                windowDimensions,
                brickCounter,
                paddle
        );
        float availableWidth = windowDimensions.x() - (2*wallThickness);
        float totalPaddingWidth = (brickCols-1)*BRICK_PADDING_WIDTH;
        float brickWidth = (availableWidth-totalPaddingWidth)/brickCols;
        float brickHeight = BRICK_HEIGHT;
        for(int row = 0; row < brickRows; row++){
            for(int col = 0; col < brickCols; col++){
                CollisionStrategy strategy = strategyFactory.getStrategy();

                float brickX = wallThickness + (col*(brickWidth+BRICK_PADDING_WIDTH));
                float brickY = wallThickness + (row*(brickHeight+BRICK_PADDING_HEIGHT));

                GameObject brick = new Brick(new Vector2(brickX,brickY),
                        new Vector2(brickWidth, brickHeight),
                        brickImage, strategy, row, col);
                brick.setTag("Brick");
                gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
            }
        }
        //Creating the hearts
        createLifeUI();
    }


    private void createLifeUI() {
        Vector2 heartPos = new Vector2(20, windowDimensions.y() - DOWN_BORDER_HEIGHT);

        graphicLifeCounter =
                new GraphicLifeCounter(heartPos, hearts, gameObjects(), heartImage);

        Vector2 numericPos =
                new Vector2(20 + (GraphicLifeCounter.MAX_HEARTS * 30) + 20,
                        windowDimensions.y() - DOWN_BORDER_HEIGHT);

        numericLifeCounter =
                new NumericLifeCounter(numericPos,
                        new Vector2(20, 20), gameObjects(), hearts);
    }
    private void loadAssets() {
        brickImage = imageReader.readImage(PATH_BRICK_IMAGE, false);
        heartImage = imageReader.readImage(PATH_HEART_IMAGE, true);
    }

    private void createBackground() {
        Renderable backgroundImage = imageReader.readImage(PATH_BG_IMAGE,
                false);
        GameObject background = new GameObject(Vector2.ZERO,
                windowDimensions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);



    }

    private void createPaddle() {
        Renderable paddleImage = imageReader.readImage(PATH_PADDLE_IMAGE,
                true);
        paddle = new Paddle(Vector2.ZERO,
                new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT),
                paddleImage, inputListener,windowDimensions);
        paddle.setCenter(new Vector2(windowDimensions.x()/2,
                windowDimensions.y()- DOWN_BORDER_HEIGHT));
        gameObjects().addGameObject(paddle, Layer.DEFAULT);


    }


    /**
     * Updates the game state every frame, checking for win or loss conditions.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        checkWinCondition();
        checkLoseCondition();

    }

    private void checkWinCondition(){
        if(brickCounter.value() <= 0 ||
                inputListener.isKeyPressed(KeyEvent.VK_W))
        {
            if(windowController.openYesNoDialog("You won! Play again ?"))
            {
                windowController.resetGame();
            }else
            {
                windowController.closeWindow();
            }
        }
    }

    private void checkLoseCondition(){
        float ballHeight = ball.getCenter().y();
        if(ballHeight > windowDimensions.y()){

            loseLife();


            gameObjects().removeGameObject(ball);
            createBall();

            if(hearts == 0)
            {
                if(windowController.openYesNoDialog("You lost! Play again ?"))
                {
                    windowController.resetGame();
                }else
                {
                    windowController.closeWindow();
                }
            }

        }
    }

    /**
     * Increments the life counter if the player has fewer than the maximum allowed lives.
     */
    public void gainLife() {
        if (hearts < GraphicLifeCounter.MAX_HEARTS) {
            hearts++;
            graphicLifeCounter.setDisplayedLives(hearts);
            numericLifeCounter.Update(hearts);
        }
    }

    /**
     * Decrements the life counter and updates the UI if the player has lives remaining.
     */
    public void loseLife() {
        if (hearts > 0) {
            hearts--;
            graphicLifeCounter.setDisplayedLives(hearts);
            numericLifeCounter.Update(hearts);

        }
    }

    private void createBall(){

        Renderable ballImage = imageReader.readImage(
                PATH_BALL_IMAGE, true);
        Sound ballCollisionSound = soundReader.readSound(PATH_BALL_SOUND);

        ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS),
                ballImage, ballCollisionSound);

        ball.setCenter(windowDimensions.mult(0.5f));

        //Random start for the ball
        this.randomVelocity();

        gameObjects().addGameObject(ball,  Layer.DEFAULT);
    }
    private GameObject createWall(Vector2 topLeftCorner,Vector2 dimensions){
        return new GameObject(topLeftCorner,dimensions, null);
    }
    private void createWalls(){
        //left wall
        GameObject leftWall= createWall(Vector2.ZERO,new Vector2(WALL_THICKNESS,
                windowDimensions.y()));
        //right wall
        GameObject rightWall= createWall(new Vector2(windowDimensions.x(),0),
                new Vector2(WALL_THICKNESS, windowDimensions.y()));
        //upper wall
        GameObject upperWall= createWall(Vector2.ZERO,
                new Vector2(windowDimensions.x(),WALL_THICKNESS));
        gameObjects().addGameObject(leftWall);
        gameObjects().addGameObject(rightWall);
        gameObjects().addGameObject(upperWall);
    }
    private void randomVelocity(){
        float velocityY=BALL_SPEED;
        float velocityX=BALL_SPEED;
        Random rand=new Random();
        if(rand.nextBoolean()){
            velocityY*=-1;
        }
        if(rand.nextBoolean()){
            velocityX*=-1;
        }


        ball.setVelocity(new Vector2(velocityX,velocityY));
    }

    /**
     * The entry point of the game; parses arguments and runs the game manager.
     */
    public static void main(String[] args) {
        int brickCols = DEFAULT_BRICK_COLS;
        int brickRows = DEFAULT_BRICK_ROWS;

        if(args.length == 2)
        {
            brickCols = Integer.parseInt(args[0]);
            brickRows = Integer.parseInt(args[1]);
        }

        BrickerGameManager gameManager =
                new BrickerGameManager("GAME",
                        new Vector2(WINDOW_DIMENSION_X,
                                WINDOW_DIMENSION_Y),
                        brickCols, brickRows);
        gameManager.run();
    }

    /**
     * Returns the current number of lives the player has.
     */
    public int getCurrentLives() {
        return hearts;

    }
}