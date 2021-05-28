package com.github.nilstrieb.grsbpl;

import com.github.nilstrieb.grsbpl.language.Interpreter;
import com.github.nilstrieb.grsbpl.language.Lexer;
import com.github.nilstrieb.grsbpl.language.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {

    static Interpreter interpreter;

    static OutStream out;

    @BeforeEach
    void setup() {
        interpreter = new Interpreter();
        out = new OutStream();
        System.setOut(out);
    }

    @Test
    void arithmeticOperations() {
        String program = "1 1 * 2 +";
        assertEquals(3, run(program));
        String program2 = "10 5 /";
        assertEquals(2, run(program2));
    }

    @Test
    void bigNumbers() {
        String program = "1000 1234 +";
        assertEquals(2234, run(program));
    }

    @Test
    void comment() {
        String program = "1 # sdkfjsaf se9 83 252h43ui\n 2 # test 5 # +";
        assertEquals(3, run(program));
    }

    @Test
    void variables() {
        String program = "1 &one 2 &two 3 &three 8 @two +";
        assertEquals(10, run(program));
    }

    @Test
    void labels() {
        String program = "1 :first 2 0";
        assertEquals(0, run(program));
    }

    @Test
    void gotoBack() {
        String program = "10000000 &i \n" +
                ":start \n" +
                "@i nout '\n' out \n" +
                "@i 1 - &i \n" +
                "@i goto start \n" +
                " 0";
        int result = 0;
        assertEquals(result, run(program));
    }

    @Test
    void gotoSkip() {
        String program = "1 :first 0 goto first 1 goto skip 3754 78349758 :skip";
        int result = 1;
        assertEquals(result, run(program));
    }

    @Test
    void fizzBuzz() throws IOException, URISyntaxException {
        String program = Files.readString(Path.of(getClass().getClassLoader().getResource("fizzbuzz.grsbpl").toURI()));
        int result = 0;
        StringBuilder resultString = new StringBuilder();
        for (int i = 1; i < 100; i++) {
            if (i % 15 == 0) resultString.append("FizzBuzz\n");
            else if (i % 5 == 0) resultString.append("Buzz\n");
            else if (i % 3 == 0) resultString.append("Fizz\n");
            else resultString.append(i).append("\n");
        }
        assertEquals(result, run(program));
        assertEquals(resultString.toString(), out.getOut());
    }

    @Test
    void stackManipulationTest() {
        String program = "1 2 swap";
        assertEquals(1, run(program));

        String program2 = "0 not";
        assertEquals(1, run(program2));

        String program3 = "1 not";
        assertEquals(0, run(program3));

        String program4 = "5 dup pop";
        assertEquals(5, run(program4));

        String program5 = "1 2 pop";
        assertEquals(1, run(program5));
    }

    @Test
    void bitwise() {
        String p1 = "10 10 xor";
        assertEquals(0, run(p1));

        String p2 = "1 bnot";
        assertEquals(~1, run(p2));

        String p3 = 0xFF + " 1 and";
        assertEquals(1, run(p3));

        String p4 = 0b001 + " " + 0b101 + " or";
        assertEquals(0b101, run(p4));
    }

    @Test
    void functionTest() {
        String program = "" +
                "1 printNumber " +
                "2 printNumber " +
                "3 printNumber " +
                "1 goto end" +
                " " +
                "function printNumber 1 nout 0 return" +
                ":end 0";
        int result = 0;
        assertEquals(result, run(program));
        assertEquals("123", out.getOut());
    }

    @Test
    void factorial() throws URISyntaxException, IOException {
        String program0 = 0 + Files.readString(Path.of(getClass().getClassLoader().getResource("factorial.grsbpl").toURI()));
        String program1 = 1 + Files.readString(Path.of(getClass().getClassLoader().getResource("factorial.grsbpl").toURI()));
        String program10 = 10 + Files.readString(Path.of(getClass().getClassLoader().getResource("factorial.grsbpl").toURI()));

        assertEquals(1, run(program0));
        assertEquals(1, run(program1));
        assertEquals(3628800, run(program10));
    }

    @Test
    void outTest() {
        String program = "'\n' '!' 'd' 'l' 'r' 'o' 'w' ' ' 'o' 'l' 'l' 'e' 'h' out out out out out out out out out out out out out 0";

        assertEquals(0, run(program));
        assertEquals("hello world!\n", out.getOut());
    }
    
    @Test
    void strings() {
        String program = "\"hallo\" out 't' out";
        System.setOut(out);
        assertEquals(0, run(program));
        assertEquals("hallot", out.getOut());
    }

    static class OutStream extends PrintStream {
        private final StringBuilder builder = new StringBuilder();

        public OutStream() {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
        }

        @Override
        public void print(char c) {
            builder.append(c);
        }
        
        
        @Override
        public void print(String s) {
            builder.append(s);
        }


        @Override
        public void print(int i) {
            builder.append(i);
        }

        public String getOut() {
            return builder.toString();
        }
    }

    int run(String program) {
        List<Token> tokens = new Lexer().lex(program.toCharArray());
        return interpreter.run(tokens);
    }
}
