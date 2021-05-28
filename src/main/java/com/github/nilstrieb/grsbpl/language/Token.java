package com.github.nilstrieb.grsbpl.language;

import java.util.Objects;

public class Token {

    private final TokenType type;
    private final Object value;
    private final int lineNumber;
    private final int lineOffset;

    public Token(TokenType type, int lineNumber, int lineOffset) {
        this(type, null, lineNumber, lineOffset);
    }

    public Token(TokenType type, Object value, int lineNumber, int lineOffset) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.lineOffset = lineOffset;
    }

    public Token(TokenType type) {
        this(type, 0, 0);
    }

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        return (String) value;
    }

    public int getIntValue() {
        return (int) value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (lineNumber != token.lineNumber) return false;
        if (lineOffset != token.lineOffset) return false;
        if (type != token.type) return false;
        return Objects.equals(value, token.value);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value=" + value +
                ", lineNumber=" + lineNumber +
                ", lineOffset=" + lineOffset +
                '}';
    }
}
