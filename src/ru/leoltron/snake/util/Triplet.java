package ru.leoltron.snake.util;

import lombok.Value;

@Value
public class Triplet<T1, T2, T3> {
    private final T1 Item1;
    private final T2 Item2;
    private final T3 Item3;

    private Triplet(T1 item1, T2 item2, T3 item3) {
        Item1 = item1;
        Item2 = item2;
        Item3 = item3;
    }

    public static <T1, T2, T3> Triplet<T1, T2, T3> create(T1 Item1, T2 Item2, T3 Item3) {
        return new Triplet<>(Item1, Item2, Item3);
    }
}