import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Interpreter {

    private final Map<String, Runnable> KEYWORDS = Map.of(
            "out", this::out,
            "nout", this::nout,
            "in", this::in,
            "goto", this::condGoto,
            "not", this::not,
            "swap", this::swap
    );

    private IntStack stack;
    private HashMap<String, Integer> variables;
    private HashMap<String, Integer> labels;
    private char[] program;
    private int i;
    private int lineNumber;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("usage: <filename>");
            System.exit(1);
        }
        try {
            String s = Files.readString(Path.of(args[1]));
            Interpreter interpreter = new Interpreter();
            int exit = interpreter.run(s.toCharArray());
            System.exit(exit);
        } catch (IOException e) {
            System.err.println("File not found");
        }
    }

    public int run(char[] chars) {
        program = chars;
        stack = new IntStack();
        variables = new HashMap<>();
        labels = new HashMap<>();
        i = 0;
        lineNumber = 1;

        firstPass();
        i = 0;
        lineNumber = 1;

        while (hasNext()) {
            try {
                runStatement();
            } catch (Exception e) {
                System.err.println("Exception occurred on line: " + lineNumber);
                e.printStackTrace();
                return -1;
            }
        }

        return rest();
    }

    private int rest() {
        OptionalInt i = stack.tryPop();

        if (i.isEmpty()) {
            return 0;
        } else {
            return i.getAsInt();
        }
    }

    private void firstPass() {
        while (hasNext()) {
            if (advance() == ':') {
                label();
            }
        }
    }

    private void runStatement() {
        switch (advance()) {
            case '+' -> add();
            case '-' -> subtract();
            case '*' -> multiply();
            case '/' -> divide();
            case '%' -> modulo();
            case '\'' -> character();
            case '\n' -> lineNumber++;
            case ' ', '\t', '\r' -> {
            }
            case '#' -> comment();
            case '&' -> store();
            case '@' -> load();
            case ':' -> ignoreLabel();
            default -> {
                if (Character.isDigit(current())) {
                    number();
                } else {
                    keyword();
                }
            }
        }
    }

    private void add() {
        stack.performOn2(Integer::sum);
    }

    private void subtract() {
        stack.performOn2((i1, i2) -> i1 - i2);
    }

    private void multiply() {
        stack.performOn2((i1, i2) -> i1 * i2);
    }

    private void divide() {
        stack.performOn2((i1, i2) -> i1 / i2);
    }

    private void modulo() {
        stack.performOn2((i1, i2) -> i1 % i2);
    }


    private void character() {
        int value = advance();
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
                default -> {
                    System.err.println("Invalid escape sequence: \\" + escaped + " on line " + lineNumber);
                    System.exit(1);
                    throw new IllegalStateException("system exit failed");
                }
            };
        }
        stack.push(value);
        consume();
    }

    private void comment() {
        while (true) {
            char next = advance();
            if (next == '\n') {
                lineNumber++;
                break;
            } else if (next == '#') {
                break;
            }
        }
    }

    private void store() {
        whitespace();
        String name = ident();
        variables.put(name, stack.pop());
    }

    private void load() {
        whitespace();
        String name = ident();
        stack.push(variables.get(name));
    }

    private void label() {
        whitespace();
        String name = ident();
        labels.put(name, i);
    }

    // consume but don't use
    private void ignoreLabel() {
        whitespace();
        ident();
    }

    private void number() {
        String number = String.valueOf(current());
        while (Character.isDigit(peek())) {
            number += advance();
        }
        stack.push(Integer.parseInt(number));
    }

    private String ident() {
        String word = String.valueOf(current());

        while (Character.isAlphabetic(peek()) || Character.isDigit(peek()) || peek() == '_') {
            word += advance();
        }

        return word;
    }

    private void keyword() {
        String word = ident();

        Runnable r = KEYWORDS.get(word);

        if (r == null) {
            throw new RuntimeException("Invalid keyword: " + word);
        }
        r.run();
    }

    private void whitespace() {
        while (Character.isWhitespace(advance())) {
            if (current() == '\n') {
                lineNumber++;
            }
        }
    }

    // keywords
    private void out() {
        System.out.print((char) stack.pop());
    }

    private void nout() {
        System.out.print(stack.pop());
    }

    private void in() {
        try {
            stack.push(System.in.read());
        } catch (IOException e) {
            System.err.println("Error reading input on line number " + lineNumber);
            System.exit(1);
        }
    }

    private void condGoto() {
        whitespace();
        String label = ident();
        consume();
        if (stack.peek() != 0) {
            Integer index = labels.get(label);
            if (index == null) {
                System.err.println("Label :" + label + " not found on line number: " + lineNumber);
                System.exit(1);
            }
            i = index;
        }
    }

    private void not() {
        int value = stack.pop();
        if (value == 0) {
            stack.push(1);
        } else {
            stack.push(0);
        }
    }

    private void swap() {
        stack.swap();
    }

    // parsing
    private char current() {
        return program[i - 1];
    }

    private char advance() {
        if (i == program.length) {
            return '\0';
        }
        return program[i++];
    }

    private char peek() {
        if (i == program.length) {
            return '\0';
        }
        return program[i];
    }

    private void consume() {
        i++;
    }

    private boolean hasNext() {
        return i < program.length;
    }
}