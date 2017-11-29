package ru.leoltron.snake.util;

import lombok.Getter;
import lombok.val;

public class SimpleQueue<E> {
    private QueueElement<E> first;
    private QueueElement<E> last;
    @Getter
    private int size = 0;

    public boolean isEmpty() {
        return size == 0;
    }

    public void enqueue(E element) {
        val qElement = new QueueElement<E>(element);
        if (last == null)
            first = last = qElement;
        else {
            last.next = qElement;
            last = qElement;
        }

        size++;
    }

    public E dequeue() {
        if (isEmpty()) throw new NullPointerException();
        val returnValue = first.value;

        if (size == 1)
            first = last = null;
        else
            first = first.next;

        size--;

        return returnValue;
    }

    private static class QueueElement<T> {
        T value;
        QueueElement<T> next;

        QueueElement(T value) {
            this.value = value;
        }
    }
}
