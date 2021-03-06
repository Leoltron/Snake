package ru.leoltron.snake.gui;

import lombok.val;
import org.reflections.Reflections;
import ru.leoltron.snake.game.GameInfo;
import ru.leoltron.snake.game.LevelInfo;
import ru.leoltron.snake.game.MPClientGame;
import ru.leoltron.snake.game.entity.*;
import ru.leoltron.snake.gui.drawers.IDrawer;
import ru.leoltron.snake.gui.drawers.SnakeDrawer;
import ru.leoltron.snake.gui.drawers.StaticObjectDrawer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class GamePanel extends JPanel {

    private static final Color BG_COLOR = Color.WHITE;
    private final double scale;
    private int width;
    private int height;
    private GameInfo gameInfo;
    private HashMap<Class, IDrawer> drawers = new HashMap<>();

    private boolean drawGrid;

    public GamePanel(GameInfo gameInfo, double scale) throws IOException {
        this.gameInfo = gameInfo;
        this.width = gameInfo.getFieldWidth();
        this.height = gameInfo.getFieldHeight();
        this.scale = scale;
        registerDrawers();
        checkDrawersForAllFieldObjects();
    }

    private static void drawCenteredString(Graphics g, String text, int centerX, int centerY, Font font) {
        val metrics = g.getFontMetrics(font);
        int x = centerX - metrics.stringWidth(text) / 2;
        int y = (centerY - metrics.getHeight() / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    private void checkDrawersForAllFieldObjects() {
        val reflections = new Reflections("ru.leoltron.snake.gameInfo.entity");
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
        drawers.put(AppleEater.class, new StaticObjectDrawer("resources", "textures", "pufferfish.png"));
    }

    private Image getImageAt(int x, int y) {
        val fieldObject = gameInfo.getObjectAt(x, y);
        if (fieldObject == null)
            return null;
        val imgGetter = drawers.get(fieldObject.getClass());
        return imgGetter == null ? null : imgGetter.getImage(fieldObject, gameInfo.getTime())
                .getScaledInstance((int) (64 * scale), (int) (64 * scale), Image.SCALE_SMOOTH);
    }

    @Override
    public void paint(Graphics graphics) {
        drawBackground(graphics);
        if (drawGrid)
            drawGrid(graphics);
        drawFieldObjects(graphics);

        drawStateStrings(graphics);
    }

    private void drawStateStrings(Graphics graphics) {
        if (gameInfo.isGameOver())
            drawGameOverString(graphics);
        else if (gameInfo.getTempPauseTime() > 0)
            drawPausedTimeString(graphics, gameInfo.getTempPauseTime());
        else if (gameInfo.isPaused())
            drawPausedString(graphics);

        if (gameInfo instanceof LevelInfo) {
            val levelInfo = (LevelInfo) gameInfo;
            if (!levelInfo.isProvidingInfo()) return;
            drawLevelNumber(graphics, levelInfo.getLevelNumber());

            val s = String.format("Apples left: %d", levelInfo.getApplesLeftToEat());
            val font = getFont().deriveFont(32f);
            val metrics = graphics.getFontMetrics(font);
            graphics.setColor(Color.BLACK);
            graphics.setFont(font);
            graphics.drawString(s, getSize().width - 2 - metrics.stringWidth(s), getSize().height - 2);
        }
        if (gameInfo instanceof MPClientGame) {
            final MPClientGame mpClientGame = (MPClientGame) this.gameInfo;
            int delay = mpClientGame.getPacketTickDelay();
            val font = getFont().deriveFont(20f);
            val metrics = graphics.getFontMetrics(font);
            graphics.setColor(delay > 0 ? Color.RED : Color.BLACK);
            graphics.setFont(font);
            graphics.drawString(String.format("Delay(Ticks): %d", delay), 2, 2 + metrics.getHeight());

        }
    }

    private void drawLevelNumber(Graphics graphics, int levelNumber) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(getFont().deriveFont(32f));
        graphics.drawString("Level " + levelNumber, 0, getSize().height - 2);
    }

    private void drawBackground(Graphics graphics) {
        graphics.setColor(BG_COLOR);
        graphics.fillRect(0, 0, getSize().width, getSize().height);
    }

    private void drawFieldObjects(Graphics graphics) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                drawFieldObject(graphics, x, y);
    }

    private void drawGameOverString(Graphics graphics) {
        graphics.setColor(Color.RED);
        drawCenteredString(graphics, "Game Over",
                getWidth() / 2, getHeight() / 2,
                getFont().deriveFont(32f));
    }

    private void drawPausedString(Graphics graphics) {
        graphics.setColor(Color.BLUE);
        drawCenteredString(graphics, "Paused",
                getWidth() / 2, getHeight() / 2,
                getFont().deriveFont(32f));
    }

    private void drawPausedTimeString(Graphics graphics, int time) {
        graphics.setColor(Color.BLUE);
        drawCenteredString(graphics, String.valueOf(time),
                getWidth() / 2, getHeight() / 2,
                getFont().deriveFont(64f));
    }

    private void drawGrid(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        int lineX;
        int lineY = (int) (height * (64 * scale));
        for (int x = 0; x <= width; x++) {
            lineX = (int) (x * (64 * scale));
            graphics.drawLine(lineX - 1, -1, lineX - 1, lineY - 1);
            graphics.drawLine(lineX, 0, lineX, lineY);
        }
        lineX = (int) (width * (64 * scale));
        for (int y = 0; y <= height; y++) {
            lineY = (int) (y * (64 * scale));
            graphics.drawLine(-1, lineY - 1, lineX - 1, lineY - 1);
            graphics.drawLine(0, lineY, lineX, lineY);
        }
    }

    public void switchGridDrawMode() {
        drawGrid = !drawGrid;
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
}