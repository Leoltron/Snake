package ru.leoltron.snake;

import lombok.val;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.gui.GameKeyListener;
import ru.leoltron.snake.gui.GamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import static ru.leoltron.snake.game.controller.SingleLevelGameController.getClassicGameController;

public class Main {
    private static void setFrame(JFrame frame,
                                 GamePanel gamePanel,
                                 int panelWidth,
                                 int panelHeight,
                                 KeyListener listener) {
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.getContentPane().setPreferredSize(new Dimension(panelWidth, panelHeight));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.addKeyListener(listener);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Snake");
        try {
            frame.setIconImage(ImageIO.read(new File(String.join(File.separator,
                    "resources", "textures", "snake", "head.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;
        val panelWidth = (int) (fieldWidth * 64 * scale);
        val panelHeight = (int) (fieldHeight * 64 * scale);

        val game = new Game(getClassicGameController(), fieldWidth, fieldHeight);
        /* val game = new Game(getPredefinedLevelGameController("" +
                "WWWWWWWWWW\n" +
                "W     W  W\n" +
                "W  W  W  W\n" +
                "W  W  W  W\n" +
                "W  W  W  W\n" +
                "W  W  W  W\n" +
                "W  W  W  W\n" +
                "W  W  W  W\n" +
                "W  W     W\n" +
                "WWWWWWWWWW\n"
        ), fieldWidth, fieldHeight);*/
        game.startNewGame();
        val gui = new GamePanel(fieldWidth, fieldHeight, game, scale);
        val frame = new JFrame();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_G)
                    gui.switchGridDrawMode();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        setFrame(frame, gui, panelWidth, panelHeight, new GameKeyListener(game));
        val period = 100;
        val timer = new Timer(period, actionEvent -> {
            game.tick();
            SwingUtilities.updateComponentTreeUI(frame);
        });
        timer.start();

    }
}