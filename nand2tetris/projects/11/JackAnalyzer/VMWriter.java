public class VMWriter {
    public VMWriter(String vmPath) {

    }

    public void writePush(Segment segment, int i) {


    }
    public void writePop(Segment segment, int i) {

    }
    public void writeArithmetic(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("command should not be null");
        }
        write(command.getValue());
    }

    private void write(String value) {
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
    public void writeCall(String s, int i) {

    }
    public void writeFunction(String name,int nLocals ) {

    }

    public void writeReturn() {
        write("return");
    }

    public void close() {

    }

    public enum Segment {
        CONST,
        ARG,
        LOCAL,
        STATIC,
        THIS,
        THAT,
        POINTER,
        TEMP
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
