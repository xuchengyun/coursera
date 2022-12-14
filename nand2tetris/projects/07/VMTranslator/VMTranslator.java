import java.io.File;
import java.io.IOException;

public class VMTranslator {
    private static Parser parser;
    private static CodeWriter writer;

    public static void main(String[] args) throws VMSyntaxException {
        if (args.length > 1) {
            throw new IllegalArgumentException("There are more than 1 arguments");
        }
        if (args.length < 1) {
            System.out.println("Please provide file path to compile");
            return;
        }
        // String fileName = args[0];
        File file = new File(args[0]);
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            assert (listOfFiles != null);
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getPath().endsWith(".vm")) {
                    parse(listOfFiles[i].getName(), listOfFiles[i].getPath());
                }
            }
        } else {
            parse(file.getName(), file.getPath());
        }
    }

    private static void parse(String fileName, String filePath) throws  VMSyntaxException {
        try {
            parser = new Parser(filePath);
            String outputFilePath = filePath.substring(0, filePath.indexOf('.')) + ".asm";
            
            writer = new CodeWriter(outputFilePath, fileName);
            // Read the file line by line until the end and write parsed String to output file
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
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new VMSyntaxException(e);
        } finally {
            try {
                parser.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
