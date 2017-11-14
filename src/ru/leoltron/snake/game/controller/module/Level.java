package ru.leoltron.snake.game.controller.module;

import lombok.NonNull;
import ru.leoltron.snake.game.controller.module.generator.AppleGenerator;
import ru.leoltron.snake.game.controller.module.generator.GameFieldGenerator;

public class Level {
    public final GameFieldGenerator fieldGenerator;
    public final AppleGenerator appleGenerator;
    public final int applesInLevel;

    public Level(GameFieldGenerator fieldGenerator, AppleGenerator appleGenerator) {
        this(fieldGenerator, appleGenerator, 4);
    }

    public Level(@NonNull GameFieldGenerator fieldGenerator, @NonNull AppleGenerator appleGenerator, int applesInLevel) {
        this.fieldGenerator = fieldGenerator;
        this.appleGenerator = appleGenerator;
        this.applesInLevel = applesInLevel;
    }
}
