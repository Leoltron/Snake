package ru.leoltron.snake.game;

import lombok.val;
import ru.leoltron.snake.game.controller.GameController;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.field.LoggingGameField;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MPServerGame extends Game {

    private Map<Integer, String> changes = new HashMap<>();
    private LoggingGameField loggingGameField;

    public MPServerGame(GameController gameController, int fieldWidth, int fieldHeight) {
        super(gameController, fieldWidth, fieldHeight);
        gameField = loggingGameField = new LoggingGameField(fieldWidth, fieldHeight);
    }

    private static String createChangePacket(Collection<Pair<GamePoint, FieldObject>> changes) {
        val result = new StringBuilder(String.valueOf(changes.size()) + "\n");
        for (val change : changes) {
            val point = change.getItem1();
            val fo = change.getItem2();
            val isEmpty = fo == null;
            result.append(String.format("%d:%d:%s:%s\n",
                    point.x, point.y,
                    isEmpty ? "null" : fo.getClass(),
                    isEmpty ? "" : fo.serializeToString()));
        }
        return result.toString();
    }

    public String getChangePacket(int time) {
        val curTime = getTime();
        if (time < curTime) {
            if (!changes.containsKey(time)) {
                System.err.println("Have not found change description for time " + String.valueOf(time));
                return null;
            } else {
                return changes.get(time);
            }
        } else if (time > curTime) {
            System.err.println(String.format("Cannot have future changes description! " +
                    "(Current time: %d, requested: %d)", curTime, time));
            return null;
        } else {
            val changePacket = createChangePacket(loggingGameField.retrieveAllChanges());
            changes.put(time, changePacket);
            return changePacket;
        }
    }
}
