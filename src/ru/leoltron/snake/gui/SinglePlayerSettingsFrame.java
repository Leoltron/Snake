package ru.leoltron.snake.gui;

import lombok.val;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.controller.AdaptedMultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SimpleAISnakeController;
import ru.leoltron.snake.game.controller.snake.SnakeController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SinglePlayerSettingsFrame extends JFrame implements ActionListener {
    private static final int MAX_PLAYERS = 4;

    private ArrayList<JRadioButton> noButtons = new ArrayList<>();
    private ArrayList<JRadioButton> playerButtons = new ArrayList<>();
    private ArrayList<JRadioButton> aiButtons = new ArrayList<>();

    public SinglePlayerSettingsFrame() {
        this(null);
    }

    public SinglePlayerSettingsFrame(JFrame parentFrame) {
        super("Одиночная игра");

        try {
            this.setIconImage(ImageIO.read(new File(String.join(File.separator,
                    "resources", "textures", "snake", "blue", "head.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (parentFrame != null)
            this.addWindowListener(new HideParentWindowListener(parentFrame));
        val btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(MAX_PLAYERS + 1, 1, 5, 5));
        for (int i = 0; i < MAX_PLAYERS; i++)
            btnPanel.add(generateBtnGroup());
        playerButtons.get(0).setSelected(true);
        for (int i = 1; i < MAX_PLAYERS; i++)
            noButtons.get(i).setSelected(true);
        val startButton = new JButton("Старт");
        startButton.addActionListener(e -> startGame());
        btnPanel.add(startButton);
        setContentPane(btnPanel);
        pack();
        setMinimumSize(getSize());
        this.setLocationRelativeTo(null);
        updateButtons();
    }

    private JComponent generateBtnGroup() {
        val group = new ButtonGroup();
        val radioPanel = new JPanel(new GridLayout(1, 3));
        val btnN = new JRadioButton("Отключен");
        setListenerAndAdd(btnN, group, radioPanel);
        noButtons.add(btnN);
        val btnP = new JRadioButton("Игрок");
        setListenerAndAdd(btnP, group, radioPanel);
        playerButtons.add(btnP);
        val btnA = new JRadioButton("ИИ");
        setListenerAndAdd(btnA, group, radioPanel);
        aiButtons.add(btnA);
        return radioPanel;
    }

    private void setAllNButtonsBlocked(boolean isBlocked) {
        for (val btn : noButtons)
            btn.setEnabled(!isBlocked);
    }


    private void setAllAIButtonsBlocked(boolean isBlocked) {
        for (val btn : aiButtons)
            btn.setEnabled(!isBlocked);
    }


    private void setAllPlayerButtonsBlocked(boolean isBlocked) {
        for (val btn : playerButtons)
            btn.setEnabled(!isBlocked);
    }

    private void setListenerAndAdd(JRadioButton btn, ButtonGroup group, JPanel panel) {
        btn.addActionListener(this);
        group.add(btn);
        panel.add(btn);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        updateButtons();
    }

    private void updateButtons() {
        int ais = getAIsAmount();
        int players = getPlayersAmount();
        int totalSnakes = ais + players;
        setAllNButtonsBlocked(totalSnakes <= 1);
        setAllPlayerButtonsBlocked(players >= GameKeyListener.getTotalKeyBinds());

    }

    private int getPlayersAmount() {
        int sum = 0;
        for (val btn : playerButtons)
            if (btn.isSelected())
                sum++;
        return sum;
    }

    private int getAIsAmount() {
        int sum = 0;
        for (val btn : aiButtons)
            if (btn.isSelected())
                sum++;
        return sum;
    }

    private boolean isAI(int playerId) {
        return aiButtons.get(playerId).isSelected();
    }

    private boolean isPlayer(int playerId) {
        return playerButtons.get(playerId).isSelected();
    }

    private boolean isNonePlayer(int playerId) {
        return noButtons.get(playerId).isSelected();
    }


    private void startGame() {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;

        int playerI = 0;
        val controllers = new ArrayList<SnakeController>();
        val keyListeners = new ArrayList<KeyListener>();
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (isPlayer(i)) {
                val controller = new SnakeController(i);
                controllers.add(controller);
                keyListeners.add(new GameKeyListener(controller, playerI));
                playerI++;
            } else if (isAI(i)) {
                val controller = new SimpleAISnakeController(i);
                controllers.add(controller);
            }
        }

        val game = new Game(new AdaptedMultiLevelGameController(controllers), fieldWidth, fieldHeight);
        GameFrame frame;
        try {
            frame = new GameFrame(game, scale, keyListeners.toArray(new KeyListener[keyListeners.size()]));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        frame.addWindowListener(new HideParentWindowListener(this));
        game.startNewGame();

        frame.addKeyListener(SelectModeFrame.getLevelKeyListener(game, frame));

        val period = 300;
        val timer = new Timer(period, actionEvent -> SelectModeFrame.tickGameAndUpdate(game, frame, false));
        timer.start();
    }
}
