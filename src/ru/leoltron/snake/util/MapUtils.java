package ru.leoltron.snake.util;

import java.util.Map;

public class MapUtils {
    public static <K, V> void fillMap(Map<K, V> destMap, Object... keyVals) {
        for (int i = 0; i < keyVals.length - 1; i += 2)
            destMap.put((K) keyVals[i], (V) keyVals[i + 1]);
    }
}
