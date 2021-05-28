package com.github.nilstrieb.grsbpl.language;

/**
 * The different types of tokens
 */
public enum TokenType {

    // values
    CHARACTER, CHAR, AMPERSAND("&"), AT("@"),

    // binary operators
    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), PERCENT("%"),
    BNOT, AND, OR, XOR,

    // other operators
    NOT, DUP, SWAP, POP,

    // io
    OUT, NOUT, IN,

    // control flow
    COLUMN(":"), GOTO, FUNCTION, IDENTIFIER, RETURN,

    // end
    EOF;

    private final String display;

    TokenType() {
        this.display = super.toString();
    }

    TokenType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
