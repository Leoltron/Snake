package ru.leoltron.snake.game.field;

import lombok.val;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingGameField extends GameField {
    private Map<GamePoint, FieldObject> changes = new HashMap<>();

    public LoggingGameField(int fieldWidth, int fieldHeight) {
        super(fieldWidth, fieldHeight);
    }

    public List<Pair<GamePoint, FieldObject>> retrieveAllChanges() {
        val list = new ArrayList<Pair<GamePoint, FieldObject>>(changes.size());
        changes.forEach((gamePoint, object) -> list.add(Pair.create(gamePoint, object)));
        changes.clear();
        return list;
    }

    @Override
    public void addEntity(GamePoint coords, FieldObject object) {
        val oldEntity = getObjectAt(coords);
        super.addEntity(coords, object);
        val newEntity = getObjectAt(coords);
        if (!FieldObject.equals(oldEntity, newEntity))
            changes.put(coords, newEntity);
    }

    @Override
    public FieldObject removeEntityAt(GamePoint point) {
        if (!isFree(point))
            changes.put(point, null);
        return super.removeEntityAt(point);
    }

    @Override
    public void clear() {
        getFieldObjects().forEach(entry -> changes.put(entry.getKey(), entry.getValue()));
        super.clear();
    }
}
