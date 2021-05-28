package com.github.nilstrieb.grsbpl.language;

import java.io.IOException;
import java.util.*;

import static com.github.nilstrieb.grsbpl.language.TokenType.*;

public class Interpreter {

    private static final int STACK_LIMIT = 1_000_000;

    private Stack<StackFrame> frames;
    private Map<String, Integer> labels;
    private Map<String, FunctionData> functions;
    private List<Token> program;
    private int position;

    public int run(List<Token> chars) {
        program = chars;
        frames = new Stack<>();
        frames.push(new StackFrame());
        functions = new HashMap<>();
        labels = new HashMap<>();
        position = 0;

        firstPass();
        position = 0;

        while (hasNext()) {
            executeNext();
        }

        return rest();
    }

    private IntStack stack() {
        return frames.peek().getStack();
    }

    private Map<String, Integer> variables() {
        return frames.peek().getVariables();
    }

    private int rest() {
        OptionalInt i = stack().tryPop();

        if (i.isEmpty()) {
            return 0;
        } else {
            return i.getAsInt();
        }
    }

    private void firstPass() {
        while (hasNext()) {
            TokenType type = advance().getType();
            if (type == COLUMN) {
                labels.put(expect(IDENTIFIER).getStringValue(), position);
            } else if (type == FUNCTION) {
                FunctionData fn = functionHeader();
                functions.put(fn.name, fn);
            }
        }
    }

    private FunctionData functionHeader() {
        String name = expect(IDENTIFIER).getStringValue();
        int paramCount = expect(CHARACTER).getIntValue();
        return new FunctionData(position, paramCount, name);
    }

    private void executeNext() {
        switch (peek().getType()) {
            // values
            case CHARACTER -> number();
            case CHAR -> character();
            case AMPERSAND -> store();
            case AT -> load();
            // binary operators
            case PLUS -> add();
            case MINUS -> subtract();
            case STAR -> multiply();
            case SLASH -> divide();
            case PERCENT -> modulo();
            case BNOT -> bnot();
            case AND -> and();
            case OR -> or();
            case XOR -> xor();
            // other operators
            case NOT -> not();
            case DUP -> dup();
            case SWAP -> swap();
            case POP -> pop();
            // io
            case OUT -> out();
            case NOUT -> nout();
            case IN -> in();
            case STRING -> string();
            // control flow
            case COLUMN -> ignoreLabel();
            case GOTO -> condGoto();
            case FUNCTION -> functionHeader();
            case IDENTIFIER -> callFunction();
            case RETURN -> returnFn();
        }
    }


    ///// values

    private void number() {
        Token number = advance();
        stack().push(number.getIntValue());
    }

    private void character() {
        Token character = advance();
        stack().push((char) character.getValue());
    }

    private void store() {
        consume(); // &
        Token variable = expect(IDENTIFIER);
        String name = variable.getStringValue();
        variables().put(name, stack().pop());
    }

    private void load() {
        consume(); // @
        Token variable = expect(IDENTIFIER);
        String name = variable.getStringValue();
        stack().push(variables().get(name));
    }

    ///// binary operators

    private void add() {
        consume();
        stack().apply2(Integer::sum);
    }

    private void subtract() {
        consume();
        stack().apply2((i1, i2) -> i1 - i2);
    }

    private void multiply() {
        consume();
        stack().apply2((i1, i2) -> i1 * i2);
    }

    private void divide() {
        consume();
        stack().apply2((i1, i2) -> i1 / i2);
    }

    private void modulo() {
        consume();
        stack().apply2((i1, i2) -> i1 % i2);
    }

    private void bnot() {
        consume();
        int value = ~stack().pop();
        stack().push(value);
    }

    private void and() {
        consume();
        stack().apply2((i1, i2) -> i1 & i2);
    }

    private void or() {
        consume();
        stack().apply2((i1, i2) -> i1 | i2);
    }

    private void xor() {
        consume();
        stack().apply2((i1, i2) -> i1 ^ i2);
    }

    ///// other operators

    private void not() {
        consume();
        int value = stack().pop();
        if (value == 0) {
            stack().push(1);
        } else {
            stack().push(0);
        }
    }

    private void dup() {
        consume();
        int value = stack().peek();
        stack().push(value);
    }

    private void swap() {
        consume();
        stack().swap();
    }

    private void pop() {
        consume();
        if (stack().isEmpty()) {
            throw runException("Cannot pop empty stack");
        }
        stack().pop(); // checked pop
    }

    ///// IO

    private void out() {
        consume();
        if (stack().isEmpty()) {
            throw runException("Cannot pop empty stack");
        }
        System.out.print((char) stack().pop());
    }

    private void nout() {
        consume();
        System.out.print(stack().pop());
    }

    private void in() {
        consume();
        try {
            stack().push(System.in.read());
        } catch (IOException e) {
            throw runException("[VM] - Error reading input");
        }
    }
    
    private void string() {
        String s = advance().getStringValue();
        expect(OUT, "String can only be used together with out");
        System.out.print(s);
    }

    ///// control flow

    private void ignoreLabel() {
        consume();
        expect(IDENTIFIER);
    }

    private void condGoto() {
        consume();
        String label = expect(IDENTIFIER).getStringValue();
        if (stack().peek() != 0) {
            Integer index = labels.get(label);
            if (index == null) {
                throw runException("Label '" + label + "' not found");
            }
            position = index;
        }
    }

    private void callFunction() {
        String name = advance().getStringValue();
        FunctionData p = functions.get(name);
        if (p != null) {
            call(p);
        } else {
            throw runException("Function '" + name + "' not found");
        }
    }

    private void call(FunctionData fn) {
        if (frames.size() > STACK_LIMIT) {
            throw runException("Stackoverflow, limit of " + STACK_LIMIT + " stack frames reached.");
        }

        frames.peek().setPosition(position);
        position = fn.index;
        IntStack temp = new IntStack();
        for (int i = 0; i < fn.paramCount; i++) {
            temp.push(stack().pop());
        }
        frames.push(new StackFrame());
        for (int i = 0; i < fn.paramCount; i++) {
            stack().push(temp.pop());
        }
    }

    private void returnFn() {
        consume();
        OptionalInt returnValue = frames.pop().getStack().tryPop();
        if (returnValue.isEmpty()) {
            throw runException("Function has to return some value, but no value was found on the stack");
        }
        if (frames.isEmpty()) {
            throw runException("Tried to return outside of function, probably forgot to skip a function");
        }
        stack().push(returnValue.getAsInt());
        position = frames.peek().getPosition();
    }

    ///// parsing helper methods

    private Token expect(TokenType type) {
        return expect(type, "Excepted token '" + type + "' but found '" + peek().getType() + "'");
    }

    private Token expect(TokenType type, String message) {
        if (peek().getType() == type) {
            return advance();
        } else {
            throw runException(message);
        }
    }

    private Token advance() {
        if (position == program.size()) {
            return new Token(EOF);
        }
        return program.get(position++);
    }

    private Token peek() {
        if (position == program.size()) {
            return new Token(EOF);
        }
        return program.get(position);
    }

    private void consume() {
        position++;
    }

    private boolean hasNext() {
        return position < program.size() - 1; // last token is EOF
    }

    private RunException runException(String message) {
        Token last = program.get(position - 1);
        int length;
        if (peek().getLineNumber() == last.getLineNumber()) {
            length = peek().getLineOffset() - last.getLineOffset();
        } else {
            // length cannot be known, make it -1, the error message writer with access to the source code will figure it out
            length = -1;
        }
        return new RunException(message, last.getLineNumber(), last.getLineOffset(), length);
    }


    /**
     * The values for a function
     */
    static class FunctionData {
        private final String name;
        public int index;
        public int paramCount;

        public FunctionData(int i, int paramCount, String name) {
            this.index = i;
            this.paramCount = paramCount;
            this.name = name;
        }
    }
}
