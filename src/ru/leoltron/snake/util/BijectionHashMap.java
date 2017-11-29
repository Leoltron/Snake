package ru.leoltron.snake.util;

import lombok.val;

import java.util.HashMap;
import java.util.Map;

public class BijectionHashMap<V1, V2> {
    private HashMap<V1, V2> v1Tov2 = new HashMap<>();
    private HashMap<V2, V1> v2Tov1 = new HashMap<>();

    public void put(V1 v1, V2 v2) {
        v1Tov2.put(v1, v2);
        v2Tov1.put(v2, v1);
    }

    public V2 get(V1 key) {
        return v1Tov2.get(key);
    }

    public V1 getReverse(V2 key) {
        return v2Tov1.get(key);
    }

    public Map<V1, V2> toOneDirectionalMap() {
        val map = new HashMap<V1, V2>();
        for (val entry : v1Tov2.entrySet())
            map.put(entry.getKey(), entry.getValue());
        return map;
    }

    public Map<V2, V1> toReversedOneDirectionalMap() {
        val map = new HashMap<V2, V1>();
        for (val entry : v2Tov1.entrySet())
            map.put(entry.getKey(), entry.getValue());
        return map;
    }
}
