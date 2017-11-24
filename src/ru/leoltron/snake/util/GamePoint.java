package ru.leoltron.snake.util;

import ru.leoltron.snake.game.Direction;

import java.awt.*;

public class GamePoint extends Point {

    public GamePoint(Point p) {
        super(p);
    }

    public GamePoint(int x, int y) {
        super(x, y);
    }

    public GamePoint reversed() {
        return new GamePoint(-x, -y);
    }

    public GamePoint normalized() {
        return new GamePoint(sign(x), sign(y));
    }

    private static int sign(int a) {
        return Integer.compare(a, 0);
    }

    public GamePoint translated(Direction direction) {
        return new GamePoint(x + direction.dx, y + direction.dy);
    }

    public GamePoint subtract(Point point) {
        return new GamePoint(x - point.x, y - point.y);
    }

    public GamePoint add(GamePoint point)  {
        return new GamePoint(x + point.x, y + point.y);
    }

    public int manhattanDistanceTo(Point point) {
        return Math.abs(this.x - point.x) + Math.abs(this.y - point.y);
    }

    public double euclideanDistanceTo(Point point) {
        return Math.sqrt(Math.pow(this.x - point.x, 2) + Math.pow(this.y - point.y, 2));
    }
}
