package com.github.nilstrieb.grsbpl;

import com.github.nilstrieb.grsbpl.language.Lexer;
import com.github.nilstrieb.grsbpl.language.Token;
import com.github.nilstrieb.grsbpl.language.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.nilstrieb.grsbpl.language.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    Lexer lexer;

    @BeforeEach
    void setup() {
        lexer = new Lexer();
    }

    @Test
    void keywords() {
        String program = "out in nout xor or and not bnot pop dup swap goto function return not";
        List<TokenType> expected = List.of(OUT, IN, NOUT, XOR, OR, AND, NOT, BNOT, POP, DUP, SWAP, GOTO, FUNCTION, RETURN, NOT, EOF);
        List<TokenType> actual = getTypes(lex(program));

        assertEquals(expected, actual);
    }

    @Test
    void symbols() {
        String program = "+ & @ - % / * : +";
        List<TokenType> expected = List.of(PLUS, AMPERSAND, AT, MINUS, PERCENT, SLASH, STAR, COLUMN, PLUS, EOF);
        List<TokenType> actual = getTypes(lex(program));
        assertEquals(expected, actual);
    }

    @Test
    void identifiers() {
        String program = "out test xor hallo + stack";
        List<TokenType> expected = List.of(OUT, IDENTIFIER, XOR, IDENTIFIER, PLUS, IDENTIFIER, EOF);
        List<Token> actual = lex(program);
        assertEquals(expected, getTypes(actual));

        Token test = new Token(IDENTIFIER, "test", 1, 4);
        assertEquals(test, actual.get(1));

    }

    @Test
    void numbers() {
        String program = "out 347 test 64006 in";
        List<TokenType> expected = List.of(OUT, CHARACTER, IDENTIFIER, CHARACTER, IN, EOF);
        List<Token> actual = lex(program);
        assertEquals(expected, getTypes(actual));

        Token test = new Token(CHARACTER, 347, 1, 4);
        assertEquals(test, actual.get(1));

    }

    @Test
    void chars() {
        String program = "'h' '\\n' '\\r' '\\f' '\\\\' '\\b' '\\'' '\\0'";
        List<Character> expected = List.of('h', '\n', '\r', '\f', '\\', '\b', '\'', '\0');
        List<Token> actual = lex(program);
        assertEquals(expected, actual.stream()
                .map(Token::getValue)
                .filter(Objects::nonNull)
                .limit(8)
                .collect(Collectors.toUnmodifiableList()));
    }


    @Test
    void comments() {
        String program = "goto # hallo # goto #test\n goto";
        List<TokenType> expected = List.of(GOTO, GOTO, GOTO, EOF);
        List<TokenType> actual = getTypes(lex(program));

        assertEquals(expected, actual);
    }

    @Test
    void lineNumber() {
        String program = "goto \n \n goto \ngoto";
        List<TokenType> expected = List.of(GOTO, GOTO, GOTO, EOF);
        List<Token> actual = lex(program);
        assertEquals(expected, getTypes(actual));

        Token test = new Token(GOTO, null, 4, 0);
        assertEquals(test, actual.get(2));

    }

    @Test
    void identifierName() {
        String program = "test ABC g9tgq fe_53f";
        List<String> expected = List.of("test", "ABC", "g9tgq", "fe_53f");
        assertEquals(expected, getValues(lex(program)));
    }

    @Test
    void alternativeNumbers() {
        String withHex = "0xFFF 0xa4 0x10 1_000";
        List<Integer> expected = List.of(0xFFF, 0xA4, 0x10, 1000);
        assertEquals(expected, getValues(lex(withHex)));
    }

    @Test
    void string() {
        String strings = "\"hallo\" \"test\" 't' \"hallo\\\"test\\n\"";
        List<Token> tokens = lex(strings);
        List<TokenType> expected = List.of(STRING, STRING, CHAR, STRING, EOF);
        assertEquals(expected, getTypes(tokens));
        assertEquals("hallo", tokens.get(0).getStringValue());
        assertEquals("hallo\"test\n", tokens.get(3).getStringValue());
    }


    List<Token> lex(String program) {
        return lexer.lex(program.toCharArray());
    }

    List<Object> getValues(List<Token> tokens) {
        return tokens.stream()
                .map(Token::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }
    List<TokenType> getTypes(List<Token> tokens) {
        return tokens.stream()
                .map(Token::getType)
                .collect(Collectors.toUnmodifiableList());
    }
}
