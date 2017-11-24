package ru.leoltron.snake.gui.drawers;

import lombok.NonNull;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.util.Pair;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static java.awt.image.AffineTransformOp.TYPE_BILINEAR;
import static ru.leoltron.snake.game.Direction.*;

public class SnakeDrawer implements IDrawer {
    public enum SnakeColor {
        GREEN, RED, BLUE, PURPLE
    }

    private static final BufferedImage[] BEND = new BufferedImage[SnakeColor.values().length];
    private static final BufferedImage[][] HEADS = new BufferedImage[SnakeColor.values().length][];
    private static final BufferedImage[] STRAIGHT = new BufferedImage[SnakeColor.values().length];
    private static final BufferedImage[][] TAILS = new BufferedImage[SnakeColor.values().length][];


    static {
        for (val color : SnakeColor.values()) {
            int i = color.ordinal();
            BEND[i] = tryGetSnakeImage(color, "bend.png");
            HEADS[i] = new BufferedImage[]{
                    tryGetSnakeImage(color, "head.png"),
                    tryGetSnakeImage(color, "headTongue.png")
            };
            STRAIGHT[i] = tryGetSnakeImage(color, "straight.png");
            TAILS[i] = new BufferedImage[]{
                    tryGetSnakeImage(color, "tailRight.png"),
                    tryGetSnakeImage(color, "tail.png"),
                    tryGetSnakeImage(color, "tailLeft.png"),
                    tryGetSnakeImage(color, "tail.png")
            };
        }
    }

    @Override
    public BufferedImage getImage(FieldObject fieldObject, int time) {
        val snakePart = (SnakePart) fieldObject;
        val colorId = snakePart.getSnakeOwnerId();
        val nextDirection = snakePart.getNextPartDirection();
        val prevDirection = snakePart.getPrevPartDirection();
        BufferedImage img;
        if (snakePart.isHead())
            img = HEADS[colorId][time % HEADS[colorId].length];
        else if (snakePart.isTail())
            img = TAILS[colorId][time % TAILS[colorId].length];
        else if (nextDirection.reversed() == prevDirection)
            img = STRAIGHT[colorId];
        else
            img = BEND[colorId];
        return rotateSnakeImage(img, prevDirection, nextDirection);
    }

    private static BufferedImage getImage(String... path) throws IOException {
        return ImageIO.read(new File(String.join(File.separator, path)));
    }

    private static BufferedImage tryGetSnakeImage(SnakeColor color, String imageName) {
        try {
            return getImage("resources", "textures", "snake", color.name().toLowerCase(), imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage rotateSnakeImage(@NonNull BufferedImage image, Direction dirPrev, Direction dirNext) {
        if (dirNext == null && dirPrev == null)
            return image;

        val imgCenterX = image.getWidth(null) / 2;
        val imgCenterY = image.getHeight(null) / 2;
        dirNext = dirNext != null ? dirNext : dirPrev.reversed();
        dirPrev = dirPrev != null ? dirPrev : dirNext.reversed();
        val angle = getSnakeImagesRotation(dirNext, dirPrev);

        val tx = AffineTransform.getRotateInstance(angle, imgCenterX, imgCenterY);
        val op = new AffineTransformOp(tx, TYPE_BILINEAR);
        return op.filter(image, null);
    }

    private static HashMap<Pair<Direction, Direction>, Double> angleToRotateByDirections = new HashMap<>();

    static {
        angleToRotateByDirections.put(Pair.create(DOWN, LEFT), Math.PI);
        angleToRotateByDirections.put(Pair.create(LEFT, DOWN), Math.PI);
        angleToRotateByDirections.put(Pair.create(DOWN, UP), Math.PI);
        angleToRotateByDirections.put(Pair.create(RIGHT, DOWN), Math.PI / 2);
        angleToRotateByDirections.put(Pair.create(DOWN, RIGHT), Math.PI / 2);
        angleToRotateByDirections.put(Pair.create(RIGHT, LEFT), Math.PI / 2);
        angleToRotateByDirections.put(Pair.create(LEFT, UP), 3 * Math.PI / 2);
        angleToRotateByDirections.put(Pair.create(UP, LEFT), 3 * Math.PI / 2);
        angleToRotateByDirections.put(Pair.create(LEFT, RIGHT), 3 * Math.PI / 2);
    }

    private static double getSnakeImagesRotation(Direction direction1, Direction direction2) {
        return angleToRotateByDirections.getOrDefault(Pair.create(direction1, direction2), 0d);
    }
}
