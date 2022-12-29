import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HackAssembler {

    private static Parser parser;
    private static SymbolTable st;

    public static void main(String[] args) throws HackSyntaxException {
        if (args.length > 1) {
            throw new IllegalArgumentException("There are more than 1 arguments");
        }
        if (args.length < 1) {
            System.out.println("Please provide file path to compile");
            return;
        }

        String fileName = args[0];
        st = new SymbolTable();
        firstPass(fileName);
        String output = secondPass(fileName);

        // Write to output file
        String outputFileName = fileName.substring(0, fileName.indexOf('.')) + ".hack";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName, false))) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String secondPass(String fileName) throws HackSyntaxException {
        StringBuilder code = new StringBuilder();
        try  {
            parser = new Parser(fileName);
            // Read the file line by line until the end
            while (parser.hasMoreCommands()) {
                parser.advance();
                switch (parser.commandType()) {
                    // Skip empty line and comments
                    case A_COMMAND:
                        String binary = Integer.toBinaryString(getNumberBySymbol(parser.symbol()));
                        code.append("0").append(String.format("%15s", binary).replace(' ', '0'));
                        code.append(System.lineSeparator());
                        break;
                    case C_COMMAND:
                        code.append("111").append(Code.comp(parser.comp())).append(Code.dest(parser.dest())).append(Code.jump(parser.jump()));
                        code.append(System.lineSeparator());
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new HackSyntaxException(e);
        } finally {
            try {
                parser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return code.toString();
    }

    private static void firstPass(String fileName) throws HackSyntaxException {
        try  {
            parser = new Parser(fileName);
            // Read the file line by line until the end
            int cnt = 0;
            while (parser.hasMoreCommands()) {
                parser.advance();
                switch (parser.commandType()) {
                    // Skip empty line and comments
                    case EMPTY:
                        continue;
                    case A_COMMAND:
                    case C_COMMAND:
                        cnt++;
                        break;
                    case L_COMMAND:
                        if (st.contains(parser.symbol())) {
                            throw new HackSyntaxException("Duplicate Symbol: " + parser.symbol());
                        }
                        st.addEntry(parser.symbol(), cnt);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new HackSyntaxException(e);
        } finally {
            try {
                parser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getNumberBySymbol(String symbol) {
        try {
            return Integer.parseInt(symbol);
        } catch (NumberFormatException e) {
            return st.getAddress(symbol);
        }
    }
}
