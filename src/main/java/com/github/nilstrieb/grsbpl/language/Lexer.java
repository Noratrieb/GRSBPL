package com.github.nilstrieb.grsbpl.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.nilstrieb.grsbpl.language.TokenType.*;

/**
 * The Lexer lexes the input and transforms it into tokens that the interpreter can then run
 * Makes everything a lot easier
 */
public class Lexer {
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    private char[] program;
    private List<Token> tokens;
    private int position;
    private int lineNumber;
    private int lineOffset;
    private int offsetLock;

    static {
        KEYWORDS.put("out", OUT);
        KEYWORDS.put("nout", NOUT);
        KEYWORDS.put("in", IN);
        KEYWORDS.put("goto", GOTO);
        KEYWORDS.put("not", NOT);
        KEYWORDS.put("swap", SWAP);
        KEYWORDS.put("bnot", BNOT);
        KEYWORDS.put("and", AND);
        KEYWORDS.put("or", OR);
        KEYWORDS.put("xor", XOR);
        KEYWORDS.put("dup", DUP);
        KEYWORDS.put("pop", POP);
        KEYWORDS.put("function", FUNCTION);
        KEYWORDS.put("return", RETURN);
    }


    public List<Token> lex(char[] chars) {
        program = chars;
        position = 0;
        tokens = new ArrayList<>();
        lineOffset = 0;
        lineNumber = 1;

        while (hasNext()) {
            try {
                next();
            } catch (LexException e) {
                throw e;
            } catch (Exception e) {
                throw lexException("Unkown Syntax Error. " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
        add(EOF);
        return tokens;
    }

    private void next() {
        lockOffset();
        char next = advance();
        switch (next) {
            case '+' -> add(PLUS);
            case '-' -> add(MINUS);
            case '*' -> add(STAR);
            case '/' -> add(SLASH);
            case '%' -> add(PERCENT);
            case '&' -> add(AMPERSAND);
            case '@' -> add(AT);
            case ':' -> add(COLUMN);
            case '\'' -> character();
            case ' ', '\t', '\r', '\n' -> {
            }
            case '#' -> comment();
            default -> {
                if (Character.isDigit(next)) {
                    number();
                } else {
                    ident();
                }
            }
        }
    }

    private void character() {
        char value = advance();
        if (value == '\\') {
            char escaped = advance();
            value = switch (escaped) {
                case 'n' -> '\n';
                case 'r' -> '\r';
                case '\\' -> '\\';
                case '0' -> '\0';
                case '\'' -> '\'';
                case 'b' -> '\b';
                case 'f' -> '\f';
                default -> throw new LexException("Invalid escape sequence: \\" + escaped, lineNumber, offsetLock, lineOffset - offsetLock);
            };
        }
        add(CHAR, value);
        consume();
    }

    private void comment() {
        while (true) {
            char next = advance();
            if (next == '\n') {
                break;
            } else if (next == '#') {
                break;
            }
        }
    }

    private void ident() {
        StringBuilder text = new StringBuilder(String.valueOf(last()));
        while (isAlphaNumeric(peek())) {
            text.append(advance());
        }
        TokenType type = KEYWORDS.get(text.toString());
        if (type == null) {
            add(IDENTIFIER, text.toString());
        } else {
            add(type);
        }
    }

    private void number() {
        StringBuilder number = new StringBuilder(String.valueOf(last()));
        while (Character.isDigit(peek())) {
            number.append(advance());
        }
        try {
            int value = Integer.parseInt(number.toString());
            add(CHARACTER, value);
        } catch (NumberFormatException e) {
            throw lexException("Value not an integer: " + number);
        }
    }

    private boolean isAlphaNumeric(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }

    private boolean hasNext() {
        return position < program.length;
    }

    private void consume() {
        advance();
    }

    private char last() {
        return program[position - 1];
    }

    private char peek() {
        if (hasNext()) {
            return program[position];
        } else {
            return '\0';
        }
    }

    private char advance() {
        lineOffset++;
        char c = program[position++];
        if (c == '\n') {
            lineNumber++;
            lineOffset = 0;
        }
        return c;
    }

    private void lockOffset() {
        offsetLock = lineOffset;
    }

    private LexException lexException(String message) {
        throw new LexException(message, lineNumber, offsetLock, lineOffset - offsetLock);
    }

    private void add(TokenType tokenType) {
        tokens.add(new Token(tokenType, lineNumber, offsetLock));
    }

    private void add(TokenType tokenType, Object value) {
        tokens.add(new Token(tokenType, value, lineNumber, offsetLock));
    }
}
