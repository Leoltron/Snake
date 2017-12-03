package ru.leoltron.snake.gui;

import lombok.val;
import ru.leoltron.snake.game.GameInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

public class GameFrame extends JFrame {

    private static final int FIELD_CELL_WIDTH = 64;
    private static final int FIELD_CELL_HEIGHT = 64;
    public GamePanel gamePanel;

    public GameFrame(GameInfo gameInfo, double scale, KeyListener... listeners) throws IOException {
        val panelWidth = (int) (gameInfo.getFieldWidth() * FIELD_CELL_WIDTH * scale);
        val panelHeight = (int) (gameInfo.getFieldHeight() * FIELD_CELL_HEIGHT * scale) + 29;

        gamePanel = new GamePanel(gameInfo, scale);
        this.add(gamePanel);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.add(gamePanel);
        this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        for (val listener : listeners)
            this.addKeyListener(listener);
        this.setLocationRelativeTo(null);
        this.setTitle("Snake");
        try {
            this.setIconImage(ImageIO.read(new File(String.join(File.separator,
                    "resources", "textures", "snake", "green", "head.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParentFrame(JFrame frame) {
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}
