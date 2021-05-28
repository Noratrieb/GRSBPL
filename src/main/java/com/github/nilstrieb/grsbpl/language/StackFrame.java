package com.github.nilstrieb.grsbpl.language;

import java.util.HashMap;
import java.util.Map;

public class StackFrame {
    private final IntStack stack;
    private final Map<String, Integer> variables;
    private int position;

    public StackFrame() {
        stack = new IntStack();
        variables = new HashMap<>();
    }

    public IntStack getStack() {
        return stack;
    }

    public Map<String, Integer> getVariables() {
        return variables;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
