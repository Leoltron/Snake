package ru.leoltron.snake.gui;

import lombok.val;
import org.reflections.Reflections;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.entity.Apple;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.game.entity.Wall;
import ru.leoltron.snake.gui.drawers.IDrawer;
import ru.leoltron.snake.gui.drawers.SnakeDrawer;
import ru.leoltron.snake.gui.drawers.StaticObjectDrawer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class GamePanel extends JPanel {

    private int width;
    private int height;
    private Game game;
    private HashMap<Class, IDrawer> drawers = new HashMap<>();


    public GamePanel(int width, int height, Game game) throws IOException {
        this.game = game;
        this.width = width;
        this.height = height;
        registerDrawers();
        checkDrawersForAllFieldObjects();
    }

    private void checkDrawersForAllFieldObjects() {
        val reflections = new Reflections("ru.leoltron.snake.game.entity");
        val allClasses = reflections.getSubTypesOf(FieldObject.class);
        for (val foClass : allClasses) {
            if (Modifier.isAbstract(foClass.getModifiers())) continue;
            if (!drawers.containsKey(foClass))
                throw new DrawerNotFoundException(foClass);
        }
    }

    private void registerDrawers() throws IOException {
        drawers.put(Apple.class, new StaticObjectDrawer("resources", "textures", "apple.png"));
        drawers.put(Wall.class, new StaticObjectDrawer("resources", "textures", "brick.png"));
        drawers.put(SnakePart.class, new SnakeDrawer());
    }

    private Image getImageAt(int x, int y) {
        val fieldObject = game.getObjectAt(x, y);
        if (fieldObject == null)
            return null;
        val imgGetter = drawers.get(fieldObject.getClass());
        return imgGetter == null ? null : imgGetter.getImage(fieldObject, game.getTime());
    }

    @Override
    public void paint(Graphics graphics) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                drawFieldObject(graphics, x, y);
        if (game.isGameOver())
            drawCenteredString(graphics, "Game Over",
                    getWidth() / 2, getHeight() / 2,
                    getFont());
    }

    private void drawFieldObject(Graphics graphics, int fieldObjX, int fieldObjY) {
        val img = getImageAt(fieldObjX, fieldObjY);
        if (img == null)
            return;
        graphics.drawImage(img,
                fieldObjX * img.getWidth(null),
                fieldObjY * img.getHeight(null),
                null);
    }

    private static void drawCenteredString(Graphics g, String text, int centerX, int centerY, Font font) {
        val metrics = g.getFontMetrics(font);
        int x = centerX - metrics.stringWidth(text) / 2;
        int y = (centerY - metrics.getHeight() / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }
}