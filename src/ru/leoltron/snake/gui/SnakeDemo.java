package ru.leoltron.snake.gui;

import lombok.val;
import ru.leoltron.snake.gui.drawers.SnakeDrawer;

import javax.swing.*;
import java.awt.*;

public class SnakeDemo extends JComponent {
    private static final int SNAKE_LENGTH = 4;
    private double scale;
    private Image[] images;

    public SnakeDemo(int playerId, double scale) {
        this.scale = scale;
        val demoImages = SnakeDrawer.getDemo(playerId, SNAKE_LENGTH);
        images = new Image[SNAKE_LENGTH];
        for (int i = 0; i < images.length; i++)
            images[i] = demoImages[i].
                    getScaledInstance((int) (64 * scale), (int) (64 * scale), Image.SCALE_SMOOTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int x = getX();
        int y = getY() / 2;
        for (val image : images) {
            g.drawImage(image, x, y, null);
            x += (int) (64 * scale);
        }
    }

    @Override
    public int getWidth() {
        return (int) Math.ceil(64 * scale * images.length);
    }

    @Override
    public int getHeight() {
        return (int) Math.ceil(64 * scale);
    }
}
