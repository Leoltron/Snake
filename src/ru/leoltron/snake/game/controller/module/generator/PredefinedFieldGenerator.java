package ru.leoltron.snake.game.controller.module.generator;

import lombok.NonNull;
import lombok.val;
import ru.leoltron.snake.game.entity.Apple;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.Wall;
import ru.leoltron.snake.util.GamePoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PredefinedFieldGenerator extends GameFieldGenerator {

    private static Map<Character, FieldObject> charsToFieldObjects;

    static {
        charsToFieldObjects = new HashMap<>();
        charsToFieldObjects.put('W', new Wall());
        charsToFieldObjects.put('A', new Apple());
    }

    private Map<GamePoint, FieldObject> fieldObjects = new HashMap<>();
    private int fieldWidth;
    private int fieldHeight;

    public PredefinedFieldGenerator(@NonNull String text) {
        this(text.split("\\r?\\n"));
    }

    public PredefinedFieldGenerator(@NonNull String[] lines) {
        parseField(lines);
    }

    private void parseField(String[] lines) {
        fieldWidth = getMaxLength(lines);
        fieldHeight = lines.length;
        for (int y = 0; y < lines.length; y++)
            for (int x = 0; x < lines[y].length(); x++)
                tryAddFieldObject(x, y, lines[y].charAt(x));
    }

    private void tryAddFieldObject(int x, int y, char key) {
        if (charsToFieldObjects.containsKey(key))
            fieldObjects.put(new GamePoint(x, y), charsToFieldObjects.get(key));
    }

    private static int getMaxLength(@NonNull String[] strings) {
        int max = 0;
        for (val string : strings)
            max = Math.max(max, string.length());
        return max;
    }

    @Override
    protected Map<GamePoint, FieldObject> generateFieldObjects(int fieldWidth, int fieldHeight) {
        if (fieldWidth != this.fieldWidth || fieldHeight != this.fieldHeight)
            throw new IllegalArgumentException(String.format("Expected size: (%d, %d), got: (%d, %d)",
                    this.fieldWidth,
                    this.fieldHeight,
                    fieldWidth,
                    fieldHeight));
        val map = new HashMap<GamePoint, FieldObject>();
        copyFieldObjectsTo(map);
        return map;
    }

    private void copyFieldObjectsTo(HashMap<GamePoint, FieldObject> map) {
        for (val entry : fieldObjects.entrySet())
            map.put(entry.getKey(), entry.getValue().clone());
    }

    public static PredefinedFieldGenerator fromFile(String path) throws IOException {
        return new PredefinedFieldGenerator(Files.readAllLines(Paths.get(path)).toArray(new String[0]));
    }
}
