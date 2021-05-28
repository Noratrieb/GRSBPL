package com.github.nilstrieb.grsbpl;

import com.github.nilstrieb.grsbpl.language.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class GrsbplRunner {

    private List<String> program;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: <filename>");
            System.exit(1);
        }

        try {
            String s = Files.readString(Path.of(args[0]));
            GrsbplRunner runner = new GrsbplRunner();
            int exit = runner.run(s);
            System.exit(exit);
        } catch (IOException e) {
            System.err.println("File not found");
        }
    }

    private int run(String program) {
        this.program = program.lines().collect(Collectors.toUnmodifiableList());
        try {
            List<Token> tokens = new Lexer().lex(program.toCharArray());
            Interpreter interpreter = new Interpreter();
            return interpreter.run(tokens);
        } catch (LexException e) {
            showError(e.getMessage(), e.getLineNumber(), e.getLineOffset(), e.getLineLength());
        } catch (RunException e) {
            showError(e.getMessage(), e.getLineNumber(), e.getLineOffset(), e.getLineLength());
        }
        return 1;
    }

    private void showError(String message, int line, int offset, int length) {
        if (length == -1) {
            length = program.get(line - 1).length() - offset + 1;
        }
        System.err.println();

        System.err.println("[GRSBPL Runtime Execution Error]");
        System.err.println();
        if (line - 1 > 0) {
            System.err.println("  " + (line - 1) + " | " + program.get(line - 2));
        }
        System.err.println("  " + line + " | " + program.get(line - 1));
        System.err.println("  " + s(len(line)) + "   " + s(offset) + "^".repeat(length - 1));
        System.err.println("  " + s(len(line)) + "   " + s(offset) + message);
        System.err.println();
        if (program.size() > line + 1) {
            System.err.println("  " + (line + 1) + " | " + program.get(line));
        }
        if (program.size() > line + 2) {
            System.err.println("  " + (line + 2) + " | " + program.get(line + 1));
        }
    }

    private String s(int length) {
        return " ".repeat(length);
    }

    private int len(int i) {
        return String.valueOf(i).length();
    }
}
