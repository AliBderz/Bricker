# Bricker

A brick-breaker game in Java on the DanoGameLab engine, built for the OOP course at the
Hebrew University of Jerusalem. The interesting part is how special bricks are built:
**collision behaviours are composable decorators**, so one brick can carry up to three
effects at once without any class knowing about the others.

## How special bricks work

`BrickStrategyFactory.getStrategy()` gives each brick a `CollisionStrategy`:

- 50 % get the plain `BasicCollisionStrategy` (brick disappears, counter decrements).
- The other 50 % get the basic strategy **wrapped** in one or more decorators, chosen at random:
  - `PuckStrategy` — spawns extra bouncing pucks
  - `ExtraPaddleStrategy` — a second, temporary paddle in mid-screen
  - `HeartStrategy` — drops a falling heart; catch it with the paddle for an extra life
  - `ExplosiveStrategy` — chain reaction: triggers the collision of every adjacent brick
  - `Double` — recursively composes two more strategies (capped at 3 effects per brick)

Every decorator implements the same `CollisionStrategy` interface and delegates to the one it
wraps, so effects stack (Strategy + Decorator patterns).

## Other pieces

- `BrickerGameManager` — window, walls, paddle, ball, brick grid, lives, win/lose + restart
- `gameobjects/` — `Ball`, `Puck`, `Paddle`, `MockPaddle`, `Brick`, `Heart`,
  `GraphicLifeCounter` (hearts) and `NumericLifeCounter` (colour-coded number)

## Running

Requires Java 17+ and the course's `DanoGameLab.jar` (not included).

```bash
javac -cp DanoGameLab.jar -d out $(find src -name "*.java")
java  -cp DanoGameLab.jar:out:. bricker.main.BrickerGameManager
```

Controls: ← → move the paddle. Lose all lives and you are asked to play again.

## Authors

Ali Bder and Mohamed Zoabi (pair project).
