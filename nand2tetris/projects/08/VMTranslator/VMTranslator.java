import java.io.File;
import java.io.IOException;

public class VMTranslator {
    private static Parser parser;
    private static CodeWriter writer;

    public static void main(String[] args) throws VMSyntaxException, IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("There are more than 1 arguments");
        }
        if (args.length < 1) {
            System.out.println("Please provide file path to compile");
            return;
        }
        File file = new File(args[0]);
        process(file);
    }

    private static void process(File file) throws VMSyntaxException, IOException {
        try {
            String outputFile = file.getPath().substring(0, file.getPath().indexOf('.')) + ".asm";
            writer = new CodeWriter(outputFile, "");
            if (file.isDirectory()) {
                writer.writeInit();
                File[] listOfFiles = file.listFiles();
                assert (listOfFiles != null);
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile() && listOfFile.getPath().endsWith(".vm")) {
                        parse(listOfFile.getName(), listOfFile.getPath());
                    }
                }
            } else {
                parse(file.getName(), file.getPath());
            }
        } catch (IOException | VMSyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                parser.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parse(String fileName, String filePath) throws IOException, VMSyntaxException {
        parser = new Parser(filePath);
        writer.setFileName(fileName);
        while (parser.hasMoreCommands()) {
            parser.advance();
            switch (parser.commandType()) {
                // Skip empty line and comments
                case C_ARITHMETIC:
                    writer.writeArithmetic(parser.command);
                    break;
                case C_PUSH:
                case C_POP:
                    writer.writePushPop(parser.command);
                    break;
                case C_LABEL:
                    writer.writeLabel(parser.command);
                    break;
                case C_GOTO:
                    writer.writeGoto(parser.command);
                    break;
                case C_IF:
                    writer.writeIf(parser.command);
                    break;
                case C_FUNCTION:
                    writer.writeFunction(parser.command);
                    break;
                case C_CALL:
                    writer.writeCall(parser.command);
                    break;
                case C_RETURN:
                    writer.writeReturn();
            }
        }
    }
}
