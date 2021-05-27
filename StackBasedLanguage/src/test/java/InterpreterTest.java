import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {

    static Interpreter interpreter;

    @BeforeEach
    void setup() {
        interpreter = new Interpreter();
    }

    @Test
    void arithmeticOperations() {
        String program = "1 1 * 2 +";
        int result = 3;
        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void bigNumbers() {
        String program = "1000 1234 +";
        int result = 2234;

        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void comment() {
        String program = "1 # sdkfjsaf se9 83 252h43ui\n 2 # test 5 # +";
        int result = 3;

        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void variables() {
        String program = "1 &one 2 &two 3 &three 8 @two +";
        int result = 10;
        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void labels() {
        String program = "1 :first 2 0";
        int result = 0;
        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void gotoBack() {
        String program = "10 &i \n" +
                ":start \n" +
                "@i nout '\n' out \n" +
                "@i 1 - &i \n" +
                "@i goto start \n" +
                " 0";
        int result = 0;
        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void gotoSkip() {
        String program = "1 :first 0 goto first 1 goto skip 3754 78349758 :skip";
        int result = 1;
        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void fizzBuzz() throws IOException, URISyntaxException {
        String program = Files.readString(Path.of(getClass().getResource("fizzbuzz.grsbpl").toURI()));
        int result = 0;
        assertEquals(result, interpreter.run(program.toCharArray()));
    }

    @Test
    void stackManipulationTest() {
        String program = "1 2 swap";
        int result = 1;
        assertEquals(result, interpreter.run(program.toCharArray()));

        String program2 = "0 not";
        int result2 = 1;
        assertEquals(result2, interpreter.run(program2.toCharArray()));

        String program3 = "1 not";
        int result3 = 0;
        assertEquals(result3, interpreter.run(program3.toCharArray()));
    }

    @Test
    void outTest() {
        String program = "'\n' '!' 'd' 'l' 'r' 'o' 'w' ' ' 'o' 'l' 'l' 'e' 'h' out out out out out out out out out out out out out 0";
        int result = 0;

        OutStream o = null;
        try {
            o = new OutStream();
            System.setOut(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals(result, interpreter.run(program.toCharArray()));
        assertEquals("hello world!\n", o.getOut());
    }

    static class OutStream extends PrintStream {
        private final StringBuilder builder = new StringBuilder();

        public OutStream() throws FileNotFoundException {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
        }

        @Override
        public void print(boolean b) {
            builder.append(b);
        }

        @Override
        public void print(char c) {
            builder.append(c);
        }

        @Override
        public void print(int i) {
            builder.append(i);
        }

        @Override
        public void print(long l) {
            builder.append(l);
        }

        @Override
        public void print(float f) {
            builder.append(f);
        }

        @Override
        public void print(double d) {
            builder.append(d);
        }

        @Override
        public void print(char[] s) {
            builder.append(s);
        }

        @Override
        public void print(String s) {
            builder.append(s);
        }

        @Override
        public void print(Object obj) {
            super.print(obj);
        }

        public String getOut() {
            return builder.toString();
        }
    }
}