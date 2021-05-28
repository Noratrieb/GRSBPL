package com.github.nilstrieb.grsbpl.language;

public class RunException extends RuntimeException {
    private final int lineNumber;
    private final int lineOffset;
    private final int lineLength;

    public RunException(String message, int lineNumber, int lineOffset, int lineLength) {
        super(message);
        this.lineNumber = lineNumber;
        this.lineOffset = lineOffset;
        this.lineLength = lineLength;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public int getLineLength() {
        return lineLength;
    }
}
