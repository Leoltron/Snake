package ru.leoltron.snake.util;

import lombok.Value;

@Value
public class Pair<T1, T2> {
    private final T1 Item1;
    private final T2 Item2;

    private Pair(T1 item1, T2 item2) {
        Item1 = item1;
        Item2 = item2;
    }

    public static <T1, T2> Pair<T1, T2> create(T1 Item1, T2 Item2) {
        return new Pair<>(Item1, Item2);
    }
}