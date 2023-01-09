import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CodeWriter {

    BufferedWriter writer;
    String fileName;
    int eqNum;
    int gtNum;
    int ltNum;

    String functionName;
    int callNum;

    public CodeWriter(String outputFilePath, String fileName) throws IOException {
        this.fileName = fileName;
        this.eqNum = 0;
        this.gtNum = 0;
        this.ltNum = 0;
        this.callNum = 0;
        this.functionName = "";
//      this.temp = 5; // Initialize temp base address to 5
        writer = new BufferedWriter(new FileWriter(outputFilePath));
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }


    public void writeArithmetic(Command command) throws IOException {
        switch (command.command) {
            case "add":
                writeAdd();
                break;
            case "sub":
                writeSub();
                break;
            case "neg":
                writeNeg();
                break;
            case "eq":
                writeEq();
                break;
            case "gt":
                writeGt();
                break;
            case "lt":
                writeLt();
                break;
            case "and":
                writeAnd();
                break;
            case "or":
                writeOr();
                break;
            case "not":
                writeNot();
                break;
        }
    }

    public void writePushPop(Command command) throws VMSyntaxException, IOException {
        switch (command.arg1) {
            case "argument":
                writeArgument(command);
                break;
            case "local":
                writeLocal(command);
                break;
            case "static":
                writeStatic(command);
                break;
            case "constant":
                writeConstant(command);
                break;
            case "this":
                writeThis(command);
                break;
            case "that":
                writeThat(command);
                break;
            case "pointer":
                writePointer(command);
                break;
            case "temp":
                writeTemp(command);
                break;
        }
    }

    // init
    public void writeInit() throws IOException {
        writeWithNewLine("// init");
        writeWithNewLine("@256");
        writeWithNewLine("D=A");
        writeWithNewLine("@SP");
        writeWithNewLine("M=D");
        writeCall(new Command("call Sys.init 0", CommandType.C_CALL, "Sys.init", 0));
    }

    // label test
    public void writeLabel(Command command) throws IOException {
        writeWithNewLine("// " + command.command);
        writeWithNewLine("(" + getFullLabel(command.arg1) + ")");
    }

    // goto label
    public void writeGoto(Command command) throws IOException {
        writeWithNewLine("// " + command.command);
        writeWithNewLine("@" + getFullLabel(command.arg1));
        writeWithNewLine("0;JMP");
    }

    // if-goto label
    public void writeIf(Command command) throws IOException {
        writeWithNewLine("// " + command.command);
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@" + getFullLabel(command.arg1));
        writeWithNewLine("D;JNE");
    }

    private String getFullLabel(String label) {
        return functionName + "$" + label;
    }

    // call functionName nArgs
    public void writeCall(Command command) throws IOException {
        writeWithNewLine("//" + command.command);

        String retAddr = command.arg1 + "$ret" + callNum++;
        // push returnAddress
        writeWithNewLine("@" + retAddr);
        writeWithNewLine("D=A");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M");
        writeWithNewLine("M=D");
        writeWithNewLine("@SP");
        writeWithNewLine("M=M+1");

        List<String> l = new ArrayList<>();
        l.add("LCL");
        l.add("ARG");
        l.add("THIS");
        l.add("THAT");
        for (String arg : l) {
            writeCallPushTemplate(arg);
        }

        // Repositions ARG
        writeWithNewLine("@SP");
        writeWithNewLine("D=M");
        writeWithNewLine("@5");
        writeWithNewLine("D=D-A");
        writeWithNewLine("@" + command.arg2);
        writeWithNewLine("D=D-A");
        writeWithNewLine("@ARG");
        writeWithNewLine("M=D");

        // Repositions LCL
        writeWithNewLine("@SP");
        writeWithNewLine("D=M");
        writeWithNewLine("@LCL");
        writeWithNewLine("M=D");

        // goto functionName
        writeWithNewLine("@" + command.arg1);
        writeWithNewLine("0;JMP");

        //(returnAddress)
        writeWithNewLine("(" + retAddr + ")");
    }

    private void writeCallPushTemplate(String arg) throws IOException {
        writeWithNewLine("@" + arg);
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M");
        writeWithNewLine("M=D");
        writeWithNewLine("@SP");
        writeWithNewLine("M=M+1");
    }

    // return
    public void writeReturn() throws IOException {
        writeWithNewLine("// return");
        // 1. R13 <--- endFrame = LCL
        writeWithNewLine("@LCL");
        writeWithNewLine("D=M");
        writeWithNewLine("@R13");
        writeWithNewLine("M=D");

        // 2. R14 <--- retAddr = *(endFrame - 5)
        writeWithNewLine("@R13");
        writeWithNewLine("D=M");
        writeWithNewLine("@5");
        writeWithNewLine("A=D-A");
        writeWithNewLine("D=M");
        writeWithNewLine("@R14");
        writeWithNewLine("M=D");

        // 3. *ARG = pop()
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@ARG");
        writeWithNewLine("A=M");
        writeWithNewLine("M=D");

        // 4. SP = ARG + 1
        writeWithNewLine("@ARG");
        writeWithNewLine("D=M+1");
        writeWithNewLine("@SP");
        writeWithNewLine("M=D");

        // 5. THAT = *(endFrame - 1)
        writeWithNewLine("@R13");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@THAT");
        writeWithNewLine("M=D");

        // 6. THIS = *(endFrame - 2)
        writeWithNewLine("@R13");
        writeWithNewLine("D=M");
        writeWithNewLine("@2");
        writeWithNewLine("A=D-A");
        writeWithNewLine("D=M");
        writeWithNewLine("@THIS");
        writeWithNewLine("M=D");

        // 7. ARG = *(endFrame - 3)
        writeWithNewLine("@R13");
        writeWithNewLine("D=M");
        writeWithNewLine("@3");
        writeWithNewLine("A=D-A");
        writeWithNewLine("D=M");
        writeWithNewLine("@ARG");
        writeWithNewLine("M=D");

        // 8. LCL = *(endFrame - 4)
        writeWithNewLine("@R13");
        writeWithNewLine("D=M");
        writeWithNewLine("@4");
        writeWithNewLine("A=D-A");
        writeWithNewLine("D=M");
        writeWithNewLine("@LCL");
        writeWithNewLine("M=D");

        // 9. goto RET
        writeWithNewLine("@R14");
        writeWithNewLine("A=M");
        writeWithNewLine("0;JMP");
    }


    // function SimpleFunction.test 2
    public void writeFunction(Command command) throws IOException {
        setFunctionName(command.arg1);
        writeWithNewLine("//" + command.command);
        writeWithNewLine("(" + functionName + ")");
        for (int i = 0; i < command.arg2; i++) {
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=0");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        }
    }

    // add
    private void writeAdd() throws IOException {
        writeWithNewLine("// add");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=D+M");
        writeWithNewLine("M=D");
    }

    //sub
    private void writeSub() throws IOException {
        writeWithNewLine("// sub");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=M-D");
        writeWithNewLine("M=D");
    }

    // neg
    private void writeNeg() throws IOException {
        writeWithNewLine("// neg");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("M=-M");
    }

    // eq
    private void writeEq() throws IOException {
        writeWithNewLine("// eq");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=M-D");
        writeWithNewLine("M=-1");
        writeWithNewLine("@" + "EQ_" + eqNum);
        writeWithNewLine("D; JEQ");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("M=0");
        writeWithNewLine("(" + "EQ_" + eqNum + ")");
        eqNum++;
    }

    // gt
    private void writeGt() throws IOException {
        writeWithNewLine("// gt");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=M-D");
        writeWithNewLine("M=-1");
        writeWithNewLine("@" + "GT_" + gtNum);
        writeWithNewLine("D; JGT");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("M=0");
        writeWithNewLine("(" + "GT_" + gtNum + ")");
        gtNum++;
    }

    // lt
    private void writeLt() throws IOException {
        writeWithNewLine("// lt");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=M-D");
        writeWithNewLine("M=-1");
        writeWithNewLine("@" + "LT_" + ltNum);
        writeWithNewLine("D;JLT");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("M=0");
        writeWithNewLine("(" + "LT_" + ltNum + ")");
        ltNum++;
    }

    // not
    private void writeNot() throws IOException {
        writeWithNewLine("// neg");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("M=!M");
    }

    // or
    private void writeOr() throws IOException {
        writeWithNewLine("// or");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=D|M");
        writeWithNewLine("M=D");
    }

    // and
    private void writeAnd() throws IOException {
        writeWithNewLine("// sub");
        writeWithNewLine("@SP");
        writeWithNewLine("AM=M-1");
        writeWithNewLine("D=M");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M-1");
        writeWithNewLine("D=D&M");
        writeWithNewLine("M=D");
    }

    // push pointer 0/1
    private void writePointer(Command command) throws VMSyntaxException, IOException {
        if (command.arg2 != 0 && command.arg2 != 1) {
            throw new VMSyntaxException("Command:" + command.command + ".... pointer should only have index of 0 or 1");
        }
        String target = command.arg2 == 0 ? "THIS" : "THAT";
        writeWithNewLine("// " + command.command);
        if (CommandType.C_PUSH.equals(command.type)) {
            writeWithNewLine("@" + target);
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (CommandType.C_POP.equals(command.type)) {
            writeWithNewLine("@SP");
            writeWithNewLine("M=M-1");
//          writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("D=M");
            writeWithNewLine("@" + target);
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + ".... Command type is not pop or push");
        }
    }

    // push local i
    // addr=ARG+i, *SP=*addr, SP++
    // pop local i
    // addr=ARG+i, SP--, *addr=*SP
    private void writeArgument(Command command) throws IOException, VMSyntaxException {
        writeWithNewLine("// " + command.command);
        if (command.type.equals(CommandType.C_PUSH)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@ARG");
            writeWithNewLine("A=D+M");
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (command.type.equals(CommandType.C_POP)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@ARG");
            writeWithNewLine("D=D+M");
            writeWithNewLine("@R15");
            writeWithNewLine("M=D");

            writeWithNewLine("@SP");
            writeWithNewLine("AM=M-1");
            writeWithNewLine("D=M");

            writeWithNewLine("@R15");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + " is not push or pop operation");
        }
    }

    // push local i
    // addr=LCL+i, *SP=*addr, SP++
    // pop local i
    // addr=LCL+i, SP--, *addr=*SP
    private void writeLocal(Command command) throws IOException, VMSyntaxException {
        writeWithNewLine("// " + command.command);
        if (command.type.equals(CommandType.C_PUSH)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@LCL");
            writeWithNewLine("A=D+M");
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (command.type.equals(CommandType.C_POP)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@LCL");
            writeWithNewLine("D=D+M");
            writeWithNewLine("@R15");
            writeWithNewLine("M=D");

            writeWithNewLine("@SP");
            writeWithNewLine("AM=M-1");
            writeWithNewLine("D=M");

            writeWithNewLine("@R15");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + " is not push or pop operation");
        }
    }

    // push static i
    private void writeStatic(Command command) throws IOException, VMSyntaxException {
        writeWithNewLine("// " + command.command);
        String variableName = fileName.substring(0, fileName.indexOf('.')) + "." + command.arg2;
        if (command.type.equals(CommandType.C_PUSH)) {
            writeWithNewLine("@" + variableName);
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (command.type.equals(CommandType.C_POP)) {
            writeWithNewLine("@SP");
            writeWithNewLine("AM=M-1");
            writeWithNewLine("D=M");
            writeWithNewLine("@" + variableName);
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + " is not push or pop operation");
        }
    }

    // push constant i
    private void writeConstant(Command command) throws VMSyntaxException, IOException {
        if (command.type.equals(CommandType.C_POP)) {
            throw new VMSyntaxException("Command:" + command.command + ".... no pop constant operation");
        }
        writeWithNewLine("// " + command.command);
        writeWithNewLine("@" + command.arg2);
        writeWithNewLine("D=A");
        writeWithNewLine("@SP");
        writeWithNewLine("A=M");
        writeWithNewLine("M=D");
        writeWithNewLine("@SP");
        writeWithNewLine("M=M+1");
    }

    // push this i
    // addr=THIS+i, *SP=*addr, SP++
    // pop this i
    // addr=THIS+i, SP--, *addr=*SP
    private void writeThis(Command command) throws IOException, VMSyntaxException {
        writeWithNewLine("// " + command.command);
        if (command.type.equals(CommandType.C_PUSH)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@THIS");
            writeWithNewLine("A=D+M");
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (command.type.equals(CommandType.C_POP)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@THIS");
            writeWithNewLine("D=D+M");
            writeWithNewLine("@R15");
            writeWithNewLine("M=D");

            writeWithNewLine("@SP");
            writeWithNewLine("AM=M-1");
            writeWithNewLine("D=M");

            writeWithNewLine("@R15");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + " is not push or pop operation");
        }
    }

    // push that i
    // addr=THAT+i, *SP=*addr, SP++
    // pop that i
    // addr=THAT+i, SP--, *addr=*SP
    private void writeThat(Command command) throws IOException, VMSyntaxException {
        writeWithNewLine("// " + command.command);
        if (command.type.equals(CommandType.C_PUSH)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@THAT");
            writeWithNewLine("A=D+M");
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (command.type.equals(CommandType.C_POP)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@THAT");
            writeWithNewLine("D=D+M");
            writeWithNewLine("@R15");
            writeWithNewLine("M=D");

            writeWithNewLine("@SP");
            writeWithNewLine("AM=M-1");
            writeWithNewLine("D=M");

            writeWithNewLine("@R15");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + " is not push or pop operation");
        }
    }

    // push temp i
    // addr=5+i, *SP=*addr, SP++
    // pop temp i
    // addr=5+i, SP--, *addr=*SP
    private void writeTemp(Command command) throws IOException, VMSyntaxException {
        writeWithNewLine("// " + command.command);
        if (command.type.equals(CommandType.C_PUSH)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@5");
            writeWithNewLine("A=D+A");
            writeWithNewLine("D=M");
            writeWithNewLine("@SP");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
            writeWithNewLine("@SP");
            writeWithNewLine("M=M+1");
        } else if (command.type.equals(CommandType.C_POP)) {
            writeWithNewLine("@" + command.arg2);
            writeWithNewLine("D=A");
            writeWithNewLine("@5");
            writeWithNewLine("D=D+A");
            writeWithNewLine("@R15");
            writeWithNewLine("M=D");

            writeWithNewLine("@SP");
            writeWithNewLine("AM=M-1");
            writeWithNewLine("D=M");

            writeWithNewLine("@R15");
            writeWithNewLine("A=M");
            writeWithNewLine("M=D");
        } else {
            throw new VMSyntaxException("Command:" + command.command + " is not push or pop operation");
        }
    }

    private void writeWithNewLine(String string) throws IOException {
        writer.write(string);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}
