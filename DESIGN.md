# Bricker — design notes

Answers to the course's design questions, kept here as the architecture explanation.

1)“Which design did you choose in section 1.7 to allow removing bricks,
and what are the advantages and disadvantages of this design?”

Our Design:
We chose to implement the Strategy design pattern together with composition.
Each Brick receives a CollisionStrategy object in its constructor.
When a brick is hit, it calls:
collisionStrategy.OnCollision(this, other);
The brick does not implement removal logic or special behavior by itself.
Instead, the behavior is delegated to the strategy object.

Advantages:
1. Open–Closed Principle
The Brick class never changes.
To add new behaviors (explosive brick, extra paddle, puck, heart, or multiple behaviors),
 we only create new strategy classes.
2. Extensibility
The system easily supports future additions:
new brick behaviors,
decorations stacking (double/triple strategies),
random behavior selection in the factory.
No code inside Brick or GameManager must be modified.
3. Clear Separation of Responsibilities
Brick = only detects collision
Strategy = defines what happens
Factory = decides which strategy a brick gets
This keeps the code modular and easier to understand.
4. Reusability
Strategies can be reused among many bricks.
Decorators can wrap each other to create “multiple behaviors” cleanly.
5. Cleaner BrickerGameManager
BrickerGameManager creates bricks without knowing their internal behavior.
All behavior is encapsulated inside strategies.
Disadvantages

1. More Classes / Higher Complexity
Using many strategies and decorators creates more files and more layers of logic.
Beginners may find it harder to track behavior flow.
2. Indirect Control
Behavior is distributed across many objects.
Debugging is more complex, especially when behaviors stack (double/triple).
3. Must Manage Dependencies Carefully
Strategies need access to:
GameObjectCollection,
GameManager,
Paddle,
BrickCounter,
ImageReader/SoundReader,
Window dimensions.
This required a dedicated BrickStrategyFactory to inject everything correctly.
Summary

The Strategy pattern was chosen because it fits perfectly with the
requirement to extend brick behavior without modifying the Brick class.

2)Explain how you implemented the life display (graphical + numeric)
 and describe briefly the role of each class.

Overview:

The life system is implemented with two dedicated UI components:
GraphicLifeCounter — displays a row of heart icons.
NumericLifeCounter — displays the remaining number of lives with dynamic color changes.
Both are updated by the game logic inside BrickerGameManager,
specifically through the gainLife() and loseLife() methods.
Classes and Their Responsibilities:

1. GraphicLifeCounter
Purpose:
Shows the remaining lives as heart icons on the screen.
How it works :
Receives topLeftCorner, initialLives, the GameObjectCollection, and the heart Renderable.
Keeps an internal array hearts[] of size MAX_HEARTS = 4.
Uses a single method setDisplayedLives(lives) to fully update the UI when lives change.
setDisplayedLives does two things:
Fully clears any previously shown hearts from the UI layer.
Rebuilds exactly the required number of hearts, each placed at an offset from topLeftCorner.
This makes the class extremely simple:
No incremental add/remove logic.
The UI always shows a correct, synchronized picture of the number of lives.
Design benefit:
It is very robust — regardless of how lives increase or decrease,
the UI always resets and redraws correctly.

2. NumericLifeCounter
Purpose:
Displays the remaining number of lives as text, with color indicating danger level.
How it works:
Uses a TextRenderable instance to display the number.
Adds a text GameObject to the UI layer in the constructor.
Exposes a method Update(lives) that:
updates the displayed number using setString()
changes the color using setColor(lives)
Color rules (as required):
3 or more lives → Green
2 lives → Yellow
1 life → Red
The class is responsible only for text display and color.

3. BrickerGameManager (life control)
Purpose:
Stores the actual life count and tells both counters when to update.
Relevant methods:
loseLife()
Decrements the heart count.
Calls:
graphicLifeCounter.setDisplayedLives(hearts)
numericLifeCounter.Update(hearts)
Checks if the game is over and shows the "You lost! Play again?" dialog if needed.
gainLife()
Increases hearts up to MAX_HEARTS.
Calls:
graphicLifeCounter.setDisplayedLives(hearts)
numericLifeCounter.Update(hearts)
Responsibility separation:
BrickerGameManager only manages the number of lives.
GraphicLifeCounter and NumericLifeCounter handle how lives look on screen.

Summary:

GraphicLifeCounter → redraws the correct number of heart icons using setDisplayedLives().
NumericLifeCounter → displays the life count as color-coded text.
BrickerGameManager → maintains the life value and updates both counters.
This achieves clean separation of concerns, simple updates,
and a flexible UI system that supports both decreasing and increasing
life counts exactly as required in section 1.8.

3)Explanation of How Each Special Behavior Was Implemented (excluding Double Strategy)
Below is a brief explanation of how each special behavior (strategy) was implemented in the project,
 and the role of any helper classes created to support it.

BasicCollisionStrategy:

Purpose: The default behavior.
Implementation:
Removes the brick from the game using gameObjects.removeGameObject(…).
Decrements the global brick counter.
Contains no additional effects.
This serves as the “base” behavior for all decorated strategies.

PuckStrategy:

Purpose: Spawns an additional ball (“puck”) when the brick is hit.
Implementation:
Calls toBeDecorated.OnCollision() to remove the brick.
Creates a new ball object using the image and sound systems.
Sets a random direction and adds the puck to the game world.
Helper class used:
The extra puck is simply a GameObject; no special helper class was needed beyond reading assets.
ExtraPaddleStrategy:
Purpose: Adds a temporary second paddle (“MockPaddle”) in the middle of the screen.
Implementation:
Calls the decorated behavior to remove the brick.
Checks a static flag MockPaddle.isInstantiated to prevent duplicates.
Creates a new MockPaddle object and adds it to the game.
Helper class:
MockPaddle:
A full class responsible for:
Movement identical to the main paddle.
Automatic timed removal using the game clock.
Maintaining the static flag to ensure only one instance exists.
HeartStrategy:
Purpose: Spawns a falling heart that grants the player an extra life.
Implementation:
Calls the decorated behavior to remove the brick.
Creates a Heart object at the brick’s location and adds it to the DEFAULT layer.
Only spawns a heart if the player currently has fewer than MAX_HEARTS.
Helper class:
Heart
Handles:
Falling motion.
Detecting collision with the main paddle.
Calling gameManager.gainLife() when collected.
Self-removal when leaving the screen.
ExplosiveStrategy
Purpose: Breaks not only the brick hit by the ball,
but also the 4 orthogonal neighbors (up, down, left, right).
Implementation:
Prevents infinite loops using setTag("Exploded").
Plays the explosion sound.
Calls the decorated behavior to remove the brick.
Searches the STATIC_OBJECTS layer for neighboring bricks using their (row, col) fields.
Triggers each neighbor’s CollisionStrategy to break them in the same manner.
Helper functionality:
Uses a helper method findBrickAt(row, col) to look up neighbors.
Uses recursion via neighbor.getCollisionStrategy().OnCollision(neighbor, ball).
Decorator Pattern (excluding Double Strategy)
All behaviors except "double behavior" use the decorator pattern:
Every strategy wraps a toBeDecorated strategy.
Calling toBeDecorated.OnCollision() ensures the basic brick-removal logic always happens.
Additional behavior is layered on top without modifying the existing strategies.

4) How We Implemented “Double Behavior” (התנהגות כפולה) According to Section 2.2.5
In part 2.2.5, the instructions describe double behavior:
A brick may include more than one special behavior,
randomly chosen from the 5 available behaviors.
A brick must have no more than 3 special behaviors,
on top of the base behavior.
We implemented this requirement using a recursive Decorator design inside our BrickStrategyFactory.
Design Explanation
Our implementation follows the decorator pattern:
Every brick begins with a BasicCollisionStrategy, which performs the standard brick removal.
Then, with some probability, we wrap this basic strategy with one or more other behaviors.
The “double behavior” is implemented using a recursive call:
case 4:
    CollisionStrategy double1 = buildStrategy(toBeDecorated, maxStrategies - 1);
    return buildStrategy(double1, maxStrategies - 1);
This means that “double behavior” simply applies two additional random behaviors to the brick,
each of which can itself be decorated again.

How We Enforced the Limit of 3 Behaviors
We strictly enforced the limit using a depth counter (maxStrategies), initialized to 3:
return buildStrategy(baseStrategy, MAX_STRATEGIES);
Each recursive call reduces the counter:
buildStrategy(toBeDecorated, maxStrategies - 1)
Once maxStrategies == 0, recursion stops immediately:
if (maxStrategies <= 0)
    return toBeDecorated;
This ensures:
✔ A brick never receives more than 3 special behaviors
✔ Behavior combinations are fully random
✔ Double behavior cannot exceed the maximum
✔ No infinite recursion

Summary of Design Advantages
Fully supports all 5 behaviors, including recursive double behaviors.
Ensures correctness using a clean, deterministic recursion depth.
Maintains modularity through decorators (no strategy knows anything about others).
Produces random combinations while automatically obeying the 3-behavior limit.

5) API Changes from Part 1 → Part 2 and Why They Were Needed
In Part 1, the game only included a single behavior for bricks (BasicCollisionStrategy).
The Brick class simply received a single strategy object and called its onCollision() method.
The API was minimal because there was no need to combine behaviors.
In Part 2, the instructions required adding four more behaviors
 (Puck, Extra Paddle, Heart, Explosive), as well as double-behavior combinations.
To support this, several API changes were necessary:
Changes We Made
1. CollisionStrategy became a “decoratable” interface
In Part 1, a strategy was flat and simple.
In Part 2, we needed to combine multiple strategies together.
To do this we modified the API so that every strategy receives another
 strategy in its constructor:
public class PuckStrategy implements CollisionStrategy {
    private final CollisionStrategy toBeDecorated;
    ...
}
This enabled the Decorator Pattern, which was required to
support multi-behavior and double-behavior bricks (§2.2.5).
2. BrickStrategyFactory was introduced
Part 1 created bricks manually with only one behavior.
Part 2 required:
random selection between 5 behaviors
enforcing a maximum of 3 behaviors
recursive double behavior
This required a new API component:
BrickStrategyFactory.getStrategy()
This factory encapsulates:
random selection
recursive decoration
probability rules
max strategies limit
This was impossible to implement cleanly using the Part 1 API.
3. Strategy classes required extra dependencies
Each new behavior required additional game objects or tools:
PuckStrategy → needs ImageReader, SoundReader, GameObjectCollection
ExtraPaddleStrategy → needs input listener & window size
HeartStrategy → needs reference to the game manager to add life
ExplosiveStrategy → needs access to all bricks
Therefore the constructors of strategies in Part 2 had additional parameters compared to Part 1.
This API expansion was necessary to keep the logic encapsulated inside
 the strategies themselves (SOLID: Single Responsibility).
4. Brick did not change — by design
One required outcome of the new API was:
✔ No changes to the Brick class
✔ All new behaviors entered the system without modifying Brick
This proves that the Strategy + Decorator design was correct.
Why These Changes Were Necessary
The changes were required because:
Part 1’s API supported only one behavior per brick.
Part 2 explicitly required up to three behaviors and double behavior combinations.
Behavior composition must be determined at runtime, not hard-coded.
The new behaviors require game-wide resources (GameObjectCollection,
sounds, image loading, UI, etc.)
The instructions demanded that:
ההתנהגות הכפולה היא בעצם התנהגות מיוחדת חדשה ש"עוטפת" שתי התנהגויות אחרות
This is only possible with a decorator-friendly API.
Therefore, updating the API between Part 1 and Part 2 was not
 optional—it was a direct requirement for implementing the new game mechanics.
