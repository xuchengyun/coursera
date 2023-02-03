import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {
    private final BufferedWriter bufferWriter;

    public VMWriter(String vmPath) throws IOException {
        bufferWriter = new BufferedWriter(new FileWriter(vmPath));
    }

    public void writePush(Segment segment, int i) {
        write("push " + segment.getValue() + " " + i);

    }
    public void writePop(Segment segment, int i) {
        write("pop " + segment.getValue() + " " + i);
    }

    public void writeArithmetic(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("command should not be null");
        }
        write(command.getValue());
    }

    public void write(String str) {
        try {
            bufferWriter.write(str);
            bufferWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLabel(String label) {
        write("label " + label);
    }
    public void writeGoto(String label) {
        write("goto " + label);
    }
    public void writeIf(String label) {
        write("if-goto " + label);
    }
    public void writeCall(String name, int nArgs) {
        write("call " + name + " " + nArgs);
    }
    public void writeFunction(String name,int nLocals ) {
        write("function " + name + " " + nLocals);
    }

    public void writeReturn() {
        write("return");
    }

    public void close() {
        try {
            bufferWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Segment {
        CONST("constant"),
        ARG("argument"),
        LOCAL("local"),
        STATIC("static"),
        THIS("this"),
        THAT("that"),
        POINTER("pointer"),
        TEMP("temp");
        private final String value;
        Segment(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }

    public enum Command {
        ADD("add"),
        SUB("sub") ,
        NEG("neg"),
        EQ("eq"),
        GT("gt"),
        LT("lt"),
        AND("and"),
        OR("or"),
        NOT("not");

        private final String value;
        Command(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }

    }
}
