package ru.leoltron.snake.gui;

public class DrawerNotFoundException extends Exception {
    public final Class objClass;

    public DrawerNotFoundException(Class objClass) {
        super(String.format("We don't have drawer for %s", objClass.toString()));
        this.objClass = objClass;
    }
}
