package com.github.nilstrieb.grsbpl.language;

import java.util.OptionalInt;
import java.util.function.BiFunction;

/**
 * A stack that exclusively holds integers
 */
public class IntStack {
    private int[] values;
    private int pointer;

    private static final int INITIAL_CAPACITY = 64;

    public IntStack() {
        this(INITIAL_CAPACITY);
    }

    public IntStack(int initialCapacity) {
        values = new int[initialCapacity];
        pointer = -1;
    }

    public void push(int value) {
        checkResize();
        values[++pointer] = value;
    }

    public int pop() {
        if (pointer == -1) {
            throw new IndexOutOfBoundsException("Cannot pop below zero");
        }
        return values[pointer--];
    }

    public boolean isEmpty() {
        return pointer < 0;
    }

    public int peek() {
        return values[pointer];
    }

    public OptionalInt tryPop() {
        if (pointer == -1) {
            return OptionalInt.empty();
        } else {
            return OptionalInt.of(pop());
        }
    }

    public void apply2(BiFunction<Integer, Integer, Integer> function) {
        int val2 = pop();
        int val1 = pop();
        push(function.apply(val1, val2));
    }

    private void checkResize() {
        if (pointer == values.length - 1) {
            int[] newValues = new int[values.length * 2];
            System.arraycopy(values, 0, newValues, 0, values.length);
            this.values = newValues;
        }
    }

    public void swap() {
        int val1 = pop();
        int val2 = pop();
        push(val1);
        push(val2);
    }
}
