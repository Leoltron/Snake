package ru.leoltron.snake.util;

import java.util.Map;

public class MapUtils<K, V>{

    private Class<K> keyClass;
    private Class<V> valueClass;
    public MapUtils(Class<K> keyClass, Class<V> valueClass){
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public void fillMap(Map<K, V> destMap, Object... keyVals) {
        for (int i = 0; i < keyVals.length - 1; i += 2) {
            destMap.put(keyClass.cast(keyVals[i]), valueClass.cast(keyVals[i + 1]));
        }
    }
}
