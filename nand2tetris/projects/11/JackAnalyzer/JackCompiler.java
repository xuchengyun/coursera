import java.io.File;
import java.io.IOException;

public class JackCompiler {

    public static void main(String[] args) throws JackCompilerException, IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("There are more than 1 arguments");
        }
        if (args.length < 1) {
            System.out.println("Please provide file path to compile");
            return;
        }
        File file = new File(args[0]);
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            assert (listOfFiles != null);
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile() && listOfFile.getPath().endsWith(".jack")) {
                    compile(listOfFile);
                }
            }
        } else {
            compile(file);
        }
    }

    private static void compile(File file) throws IOException, JackCompilerException {
        String xmlPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".xml";
        String vmPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".vm";

        String tokenFileOutPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + "T.xml";

        JackTokenizer tokenizer = new JackTokenizer(file, tokenFileOutPath);
        CompilationEngine compilationEngine = new CompilationEngine(tokenizer, xmlPath, vmPath);
        compilationEngine.compileClass();
    }

}
