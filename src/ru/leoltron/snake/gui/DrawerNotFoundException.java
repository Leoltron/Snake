package ru.leoltron.snake.gui;

public class DrawerNotFoundException extends RuntimeException {
    public final Class objClass;

    public DrawerNotFoundException(Class objClass) {
        super(String.format("We don't have drawer for %s", objClass.toString()));
        this.objClass = objClass;
    }
}
