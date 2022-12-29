import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Parser {

    Scanner scanner;
    Command command;

    public Parser(String path) throws IOException {
        this.scanner = new Scanner(new FileReader(path));
    }

    public boolean hasMoreCommands() throws IOException {
        return scanner.hasNextLine();
    }

    public void advance() throws IOException, HackSyntaxException {
        if (hasMoreCommands()) {
            command = parseLine(scanner.nextLine());
        }
    }

    public CommandType commandType() {
        return command.type;
    }

    public String symbol() {
        return command.symbol;
    }

    public String dest() {
        return command.dest;
    }

    public String comp() {
        return command.comp;
    }

    public String jump() {
        return command.jump;
    }

    // Method to close the reader when done
    public void close() throws IOException {
        scanner.close();
    }


    private Command parseLine(String line) throws HackSyntaxException {
        // Remove comments and empty space
        line = line.replaceAll("//.*", "").replaceAll("\\s+", "");
        return generateCommand(line);
    }

    private Command generateCommand(String line) throws HackSyntaxException {
        Command cmd = new Command();
        if (line == null || line.isBlank()) {
            cmd.type = CommandType.EMPTY;
            return cmd;
        }
        if (line.startsWith("@")) {
            cmd.type = CommandType.A_COMMAND;
            cmd.symbol = line.replaceAll("@", "");
        } else if (line.startsWith("(") && line.endsWith(")")) {
            cmd.type = CommandType.L_COMMAND;
            cmd.symbol = line.substring(1, line.length() - 1);
        } else if (line.contains("=") || line.contains(";")) {
            cmd.type = CommandType.C_COMMAND;
            String[] parts = line.split("[=;]");
            if (line.contains("=") && line.contains(";") && parts.length == 3) {
                cmd.dest = parts[0];
                cmd.comp = parts[1];
                cmd.jump = parts[2];
            } else if (line.contains("=") && parts.length == 2) {
                cmd.dest = parts[0];
                cmd.comp = parts[1];
            } else if (line.contains(";") && parts.length == 2) {
                cmd.comp = parts[0];
                cmd.jump = parts[1];
            } else {
                throw  new HackSyntaxException("Command " + line + " is illegal");
            }
        } else {
            throw  new HackSyntaxException("Command " + line + " is illegal");
        }
        return cmd;
    }
}
