package ru.leoltron.snake.gui;

import lombok.NonNull;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class HideParentWindowListener implements WindowListener {
    private JFrame frameToShow;

    public HideParentWindowListener(@NonNull JFrame frameToShow) {
        this.frameToShow = frameToShow;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        frameToShow.setVisible(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        frameToShow.setVisible(true);
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
}
