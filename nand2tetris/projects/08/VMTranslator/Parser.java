import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Parser {

    Scanner scanner;
    Command command;

    Set<String> arithmeticCommands;
    Set<String> segments;


    public Parser(String path) throws IOException {
        this.scanner = new Scanner(new FileReader(path));
        arithmeticCommands = new HashSet<>(Arrays.
                asList("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"));
        segments = new HashSet<>(Arrays.
                asList("argument", "local", "static", "constant", "this", "that", "pointer", "temp"));
    }

    public boolean hasMoreCommands() throws IOException {
        return scanner.hasNextLine();
    }

    public void advance() throws IOException, VMSyntaxException {
        if (hasMoreCommands()) {
            String line = scanner.nextLine();
            line = line.replaceAll("//.*", "").trim();
            if (line.isBlank()) {
                advance();
            } else {
                command = parseLine(line);
            }
        }
    }

    public CommandType commandType() {
        return command.type;
    }


    // Method to close the reader when done
    public void close() throws IOException {
        scanner.close();
    }


    private Command parseLine(String line) throws VMSyntaxException {
        return generateCommand(line);
    }

    private Command generateCommand(String line) throws VMSyntaxException {
        if (isArithmetic(line)) {
            return new Command(line, CommandType.C_ARITHMETIC, "", 0);
        }

        String[] parts = line.split(" ");
        if (parts.length != 3 || !isPopPush(parts[0]) || !isSegments(parts[1]) || !isInteger(parts[2])) {
            throw new VMSyntaxException("Assembler.Command " + line + " is illegal");
        }
        if ("push".equals(parts[0])) {
            return new Command(line, CommandType.C_PUSH, parts[1], Integer.parseInt(parts[2]));
        } else if ("pop".equals(parts[0])) {
            return new Command(line, CommandType.C_POP, parts[1], Integer.parseInt(parts[2]));
        } else {
            throw new VMSyntaxException("");
        }
    }

    public String arg1() {
        return command.arg1;
    }

    public int arg2() {
        return command.arg2;
    }

    private boolean isArithmetic(String line) {
        return arithmeticCommands.contains(line);
    }

    private boolean isSegments(String segment) {
        return segments.contains(segment);
    }

    private boolean isInteger(String index) {
        try {
            Integer.parseInt(index);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPopPush(String s) {
        return "push".equals(s) || "pop".equals(s);
    }
}