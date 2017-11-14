package ru.leoltron.snake.game.controller;

import lombok.val;
import ru.leoltron.snake.game.controller.module.Level;
import ru.leoltron.snake.game.controller.module.generator.*;

import java.io.File;
import java.io.IOException;

public class AdaptedMultiLevelGameController extends MultiLevelGameController {

    private static GameFieldGenerator tryGetFieldGenFromFile(String levelFileName) {
        GameFieldGenerator fieldGenerator;
        try {
            fieldGenerator = PredefinedFieldGenerator.fromFile(String.join(
                    File.separator, "resources", "fields", levelFileName));

        } catch (IOException e) {
            e.printStackTrace();
            fieldGenerator = null;
        }
        return fieldGenerator;
    }

    @Override
    protected void initLevels() {

        //Счет уровней начинается с единицы
        setDefaultLevel(new RandomGameFieldGenerator(), new RandomApplesGenerator(3), 2);
        setLevel(1, new BorderGameFieldGenerator(), new RandomApplesGenerator());
        setLevelWithFieldFromFile(2, "field2.txt", new RandomApplesGenerator(10), 1);
        setLevel(3, new BorderGameFieldGenerator(), new RandomApplesGenerator(), 5);
        setLevelWithFieldFromFile(5, "field2.txt", new RandomApplesGenerator(1), 1);
        setLevelWithFieldFromFile(6, "field1.txt", new RandomApplesGenerator(10), 20);
    }

    private void setDefaultLevel(GameFieldGenerator fieldGenerator, AppleGenerator appleGenerator) {
        setDefaultLevel(new Level(fieldGenerator, appleGenerator));
    }

    private void setDefaultLevel(GameFieldGenerator fieldGenerator, AppleGenerator appleGenerator, int applesRequired) {
        setDefaultLevel(new Level(fieldGenerator, appleGenerator, applesRequired));
    }

    private void setLevelWithFieldFromFile(int levelNumber, String fieldFileName, AppleGenerator appleGenerator) {
        val fieldGenerator = tryGetFieldGenFromFile(fieldFileName);
        if (fieldGenerator != null)
            setLevel(levelNumber, fieldGenerator, appleGenerator);
    }

    private void setLevelWithFieldFromFile(int levelNumber, String fieldFileName, AppleGenerator appleGenerator, int applesRequired) {
        val fieldGenerator = tryGetFieldGenFromFile(fieldFileName);
        if (fieldGenerator != null)
            setLevel(levelNumber, fieldGenerator, appleGenerator, applesRequired);
    }

    private void setLevel(int levelNumber, GameFieldGenerator fieldGenerator, AppleGenerator appleGenerator) {
        setLevel(levelNumber, new Level(fieldGenerator, appleGenerator));
    }

    private void setLevel(int levelNumber, GameFieldGenerator fieldGenerator, AppleGenerator appleGenerator, int applesRequired) {
        setLevel(levelNumber, new Level(fieldGenerator, appleGenerator, applesRequired));
    }

}
