import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompilationEngine {
    SymbolTable symbolTable;
    JackTokenizer tokenizer;
    BufferedWriter bufferWriter;
    VMWriter vmWriter;
    List<String> l = new ArrayList<>();
    String className;
    String functionName;
    int labelIndex;
    public CompilationEngine(JackTokenizer tokenizer, String xmlPath, String vmPath) throws IOException {
        this.tokenizer = tokenizer;
        bufferWriter = new BufferedWriter(new FileWriter(xmlPath));
        vmWriter = new VMWriter(vmPath);
        symbolTable = new SymbolTable();
        labelIndex = 0;
        className = "";
        functionName = "";
    }

    public void compileClass() throws JackCompilerException, IOException {
        println("<class>");
        eat("class");
        className = tokenizer.identifier();
        eat(Token.TokenType.IDENTIFIER);
        eat("{");
        while (isClassVarDec(tokenizer.currentToken) || isClassSubroutineDec(tokenizer.currentToken)) {
            if (isClassVarDec(tokenizer.currentToken)) {
                compileClassVarDec();
            } else if (isClassSubroutineDec(tokenizer.currentToken)) {
                compileSubroutineDec();
            }
        }
        eat("}");
        println("</class>");
        bufferWriter.close();
        vmWriter.close();
    }

    private boolean isClassVarDec(Token currentToken) {
        return "static".equals(currentToken.value) || "field".equals(currentToken.value);
    }

    private boolean isClassSubroutineDec(Token currentToken) {
        return "constructor".equals(currentToken.value) ||
                "function".equals(currentToken.value) ||
                "method".equals(currentToken.value);
    }

    private void compileClassVarDec() throws JackCompilerException, IOException {
        println("<classVarDec>");

        Symbol.Kind kind = null;
        String type = "";
        String name = "";
        switch (tokenizer.keyWord()) {
            case STATIC: kind = Symbol.Kind.STATIC; break;
            case FIELD: kind = Symbol.Kind.FIELD; break;
        }
        eat();
        if (isType(tokenizer.currentToken)) {
            type = tokenizer.currentToken.value;
            eat();
        } else {
            error("Token of Type");
        }
        eat(Token.TokenType.IDENTIFIER);
        while (tokenizer.symbol() == ',') {
            eat();
            name = tokenizer.currentToken.value;
            symbolTable.define(name, type, kind);
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
        println("</classVarDec>");
    }

    private boolean isType(Token currentToken) {
        return "int".equals(currentToken.value) ||
                "char".equals(currentToken.value) ||
                "boolean".equals(currentToken.value) ||
                Token.TokenType.IDENTIFIER.equals(currentToken.type);
    }

    private void compileSubroutineDec() throws JackCompilerException, IOException {
        println("<subroutineDec>");
        String keyword = tokenizer.tokenValue();
        eat();
        if ("void".equals(tokenizer.tokenValue()) || isType(tokenizer.currentToken)) {
            eat();
        } else {
            error("void|type");
        }
        functionName = tokenizer.tokenValue();
        eat(Token.TokenType.IDENTIFIER);
        eat("(");
        compileParameterList();
        eat(")");
        compileSubroutineBody(keyword);
        println("</subroutineDec>");
    }

    private void compileSubroutineBody(String keyword) throws JackCompilerException, IOException {
        println("<subroutineBody>");
        eat("{");
        symbolTable.startSubroutine();
        if ("method".equals(keyword)) {
            symbolTable.define("this", className, Symbol.Kind.ARG);
        }
        while ("var".equals(tokenizer.tokenValue())) {
            compileVarDec();
        }
        writeFunctionBody(keyword);
        compileStatements();
        eat("}");
        println("</subroutineBody>");
    }

    private void writeFunctionBody(String keyword) {
        vmWriter.writeFunction(functionName(), symbolTable.varCount(Symbol.Kind.VAR));
        //METHOD and CONSTRUCTOR need to load this pointer
        if ("method".equals(keyword)){
            //A Jack method with k arguments is compiled into a VM function that operates on k + 1 arguments.
            // The first argument (argument number 0) always refers to the this object.
            vmWriter.writePush(VMWriter.Segment.ARG, 0);
            vmWriter.writePop(VMWriter.Segment.POINTER, 0);

        }else if ("constructor".equals(keyword)){
            //A Jack function or constructor with k arguments is compiled into a VM function that operates on k arguments.
            vmWriter.writePush(VMWriter.Segment.CONST, symbolTable.varCount(Symbol.Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(VMWriter.Segment.POINTER,0);
        }
    }

    private void compileParameterList() throws JackCompilerException, IOException {
        println("<parameterList>");
        if (isType(tokenizer.currentToken)) {
            String type = tokenizer.currentToken.value;
            eat();
            symbolTable.define(tokenizer.identifier(), type, Symbol.Kind.ARG);
            eat(Token.TokenType.IDENTIFIER);
            while (tokenizer.symbol() == ',') {
                eat();
                if (isType(tokenizer.currentToken)) {
                    eat();
                    type = tokenizer.currentToken.value;
                } else {
                    error("type");
                }
                symbolTable.define(tokenizer.identifier(), type, Symbol.Kind.ARG);
                eat(Token.TokenType.IDENTIFIER);
            }
        }
        println("</parameterList>");
    }

    private void compileVarDec() throws JackCompilerException, IOException {
        println("<varDec>");
        eat("var");
        String type = "";
        if (isType(tokenizer.currentToken)) {
            type = tokenizer.tokenValue();
            eat();
        } else {
            error("type");
        }
        String name = tokenizer.tokenValue();
        eat(Token.TokenType.IDENTIFIER);
        symbolTable.define(name, type, Symbol.Kind.VAR);
        while (tokenizer.symbol() == ',') {
            eat();
            name = tokenizer.tokenValue();
            symbolTable.define(name, type, Symbol.Kind.VAR);
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
        println("</varDec>");
    }

    private void compileStatements() throws JackCompilerException, IOException {
        println("<statements>");
        while (isStatement(tokenizer.currentToken)) {
            compileStatement();
        }
        println("</statements>");
    }

    private void compileStatement() throws JackCompilerException, IOException {
        switch (tokenizer.tokenValue()) {
            case "let":
                compileLet();
                break;
            case "if":
                compileIf();
                break;
            case "while":
                compileWhile();
                break;
            case "do":
                compileDo();
                break;
            case "return":
                compileReturn();
                break;
        }
    }

    private void compileDo() throws JackCompilerException, IOException {
        println("<doStatement>");
        eat("do");
        eat(Token.TokenType.IDENTIFIER);
        if (tokenizer.symbol() == '(') {
            eat("(");
            compileExpressionList();
            eat(")");
        } else if (tokenizer.symbol() == '.') {
            eat(".");
            eat(Token.TokenType.IDENTIFIER);
            eat("(");
            compileExpressionList();
            eat(")");
        }
        eat(";");
        println("</doStatement>");
    }

    private void compileLet() throws JackCompilerException, IOException {
        println("<letStatement>");
        eat("let");
        String varName = tokenizer.identifier();
        eat(Token.TokenType.IDENTIFIER);

        boolean isArray = false;
        if (tokenizer.symbol() == '[') {
            isArray = true;
            eat("[");
            // push base address to stack
            vmWriter.writePush(getSeg(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));
            // push expression value to stack
            compileExpression();
            // base address + offset
            vmWriter.writeArithmetic(VMWriter.Command.ADD);
            eat("]");
        }
        eat("=");
        compileExpression();
        eat(";");
        if (isArray){
            //*(base+offset) = expression
            //pop expression value to temp
            vmWriter.writePop(VMWriter.Segment.TEMP,0);
            //pop base+index into 'that'
            vmWriter.writePop(VMWriter.Segment.POINTER,1);
            //pop expression value into *(base+index)
            vmWriter.writePush(VMWriter.Segment.TEMP,0);
            vmWriter.writePop(VMWriter.Segment.THAT,0);
        }else {
            //pop expression value directly
            vmWriter.writePop(getSeg(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));

        }
        println("</letStatement>");
    }

    private void compileWhile() throws JackCompilerException, IOException {
        String label1 = newLabel();
        String label2 = newLabel();
        println("<whileStatement>");
        vmWriter.writeLabel(label1);
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        vmWriter.writeArithmetic(VMWriter.Command.NOT);
        vmWriter.writeIf(label2);
        eat("{");
        compileStatements();
        eat("}");
        vmWriter.writeGoto(label1);
        vmWriter.writeLabel(label2);
        println("</whileStatement>");
    }

    private void compileReturn() throws JackCompilerException, IOException {
        println("<returnStatement>");
        eat("return");
        if (Token.TokenType.SYMBOL.equals(tokenizer.tokenType()) && tokenizer.symbol() == ';') {
            vmWriter.writePush(VMWriter.Segment.CONST,0);
            eat(";");
        } else {
            compileExpression();
            eat(";");
        }
        vmWriter.writeReturn();
        println("</returnStatement>");
    }

    private void compileIf() throws JackCompilerException, IOException {
        String label1 = newLabel();
        String label2 = newLabel();
        println("<ifStatement>");
        eat("if");
        eat("(");
        compileExpression();
        eat(")");
        vmWriter.writeArithmetic(VMWriter.Command.NOT);
        vmWriter.writeIf(label1);
        eat("{");
        compileStatements();
        eat("}");
        vmWriter.writeGoto(label2);
        vmWriter.writeLabel(label1);
        if ("else".equals(tokenizer.tokenValue())) {
            eat();
            eat("{");
            compileStatements();
            eat("}");
        }
        vmWriter.writeLabel(label2);
        println("</ifStatement>");
    }

//    private void compileSubroutineCall() throws JackCompilerException, IOException {
//        eat(Token.TokenType.IDENTIFIER);
//        if (tokenizer.symbol() == '(') {
//            eat("(");
//            compileExpressionList();
//            eat(")");
//        } else if (tokenizer.symbol() == '.') {
//            eat(".");
//            eat(Token.TokenType.IDENTIFIER);
//            eat("(");
//            compileExpressionList();
//            eat(")");
//        }
//    }

    private void compileExpression() throws IOException, JackCompilerException {
        println("<expression>");
        compileTerm();
        while (isOp(tokenizer.currentToken)) {
            eat();
            compileTerm();
        }
        println("</expression>");
    }


    private void compileTerm() throws IOException, JackCompilerException {
        println("<term>");
        if (tokenizer.tokenType().equals(Token.TokenType.INT_CONST)) {
            // Case: integerConstant
            vmWriter.writePush(VMWriter.Segment.CONST, tokenizer.intVal());
            eat();
        } else if (tokenizer.tokenType().equals(Token.TokenType.STRING_CONST)) {
            // Case: stringConstant
            eat();
        } else if (isKeywordConstant(tokenizer.currentToken)) {
            // Case: keywordConstant
            eat();
        } else if (isUnaryOp(tokenizer.currentToken)) {
            // Case: unaryOp term
            // unaryOp
            eat();
            // term
            compileTerm();
        } else if (tokenizer.tokenValue().equals("(")) {
            // Case: '(' expression ')'
            // '('
            eat("(");
            // expression
            compileExpression();
            // ')'
            eat(")");
        } else if (tokenizer.tokenType().equals(Token.TokenType.IDENTIFIER)) {
            String identifier = tokenizer.stringVal();
            vmWriter.writePush(getSeg(symbolTable.kindOf(identifier)), symbolTable.indexOf(identifier));
            eat();
            if (tokenizer.currentToken.value.equals("[")) {
                // Start an array
                eat("[");
                //push array variable,base address into stack
                vmWriter.writePush(getSeg(symbolTable.kindOf(identifier)),symbolTable.indexOf(identifier));
                compileExpression();
                eat("]");

                //base+offset
                vmWriter.writeArithmetic(VMWriter.Command.ADD);

                //pop into 'that' pointer
                vmWriter.writePop(VMWriter.Segment.POINTER,1);
                //push *(base+index) onto stack
                vmWriter.writePush(VMWriter.Segment.THAT,0);
            } else if (tokenizer.currentToken.value.equals("(") || tokenizer.currentToken.value.equals(".")) {
                // Case: subroutineCall
                if (tokenizer.symbol() == '(') {
                    eat("(");
                    // Push reference of current object this
                    vmWriter.writePush(VMWriter.Segment.POINTER, 0);

                    int argCnt = compileExpressionList() + 1;
                    eat(")");
                    // Call subroutine
                    vmWriter.writeCall(className + "." + identifier, argCnt);
                } else if (tokenizer.symbol() == '.') {
                    //(className|varName) '.' subroutineName '(' expressionList ')'
                    eat(".");
                    String subroutineName = tokenizer.identifier();
                    eat(Token.TokenType.IDENTIFIER);
                    eat("(");
                    compileExpressionList();
                    eat(")");
                }
            }
        } else {
            error("term");
        }
        println("</term>");

    }

    private VMWriter.Segment getSeg(Symbol.Kind kind) {
        switch (kind){
            case FIELD:return VMWriter.Segment.THIS;
            case STATIC:return VMWriter.Segment.STATIC;
            case VAR:return VMWriter.Segment.LOCAL;
            case ARG:return VMWriter.Segment.ARG;
            default:return null;
        }
    }

    private boolean isUnaryOp(Token currentToken) {
        return ("-").equals(currentToken.value) ||
                ("~").equals(currentToken.value);
    }


    public int compileExpressionList() throws JackCompilerException, IOException {
        int argCnt = 0;
        println("<expressionList>");
        if (tokenizer.symbol() != ')') {
            compileExpression();
            argCnt++;
            while (tokenizer.symbol() == ',') {
                eat(",");
                compileExpression();
                argCnt++;
            }
        }
        println("</expressionList>");
        return argCnt;
    }

    private boolean isKeywordConstant(Token token) {
        return token.value.equals("true") ||
                token.value.equals("false") ||
                token.value.equals("null") ||
                token.value.equals("this");
    }

    private boolean isStatement(Token currentToken) {
        return ("let").equals(currentToken.value) ||
                ("if").equals(currentToken.value) ||
                ("while").equals(currentToken.value) ||
                ("do").equals(currentToken.value) ||
                ("return").equals(currentToken.value);
    }

    private boolean isOp(Token currentToken) {
        return ("+").equals(currentToken.value) ||
                ("-").equals(currentToken.value) ||
                ("*").equals(currentToken.value) ||
                ("/").equals(currentToken.value) ||
                ("&").equals(currentToken.value) ||
                ("|").equals(currentToken.value) ||
                ("<").equals(currentToken.value) ||
                (">").equals(currentToken.value) ||
                ("=").equals(currentToken.value);
    }


    private void eat(String str) throws JackCompilerException, IOException {
        if (str == null) {
            throw new IllegalArgumentException("Parsing str of null to eat method");
        }
        if (str.equals(tokenizer.currentToken.value)) {
            if (tokenizer.tokenType().equals(Token.TokenType.SYMBOL) && Token.symbolMap.containsKey(tokenizer.symbol())) {
                println("<" + tokenizer.tokenType().getText() + "> " + Token.symbolMap.get(tokenizer.symbol()) + " </" + tokenizer.tokenType().getText() + ">");
            } else {
                println("<" + tokenizer.tokenType().getText() + "> " + tokenizer.currentToken.value + " </" + tokenizer.tokenType().getText() + ">");
            }
            tokenizer.advance();
        } else {
            error(str);
        }
    }

    private void eat(Token.TokenType type) throws JackCompilerException, IOException {
        if (tokenizer.tokenType().equals(type)) {
            if (tokenizer.tokenType().equals(Token.TokenType.SYMBOL) && Token.symbolMap.containsKey(tokenizer.symbol())) {
                println("<" + tokenizer.tokenType().getText() + "> " + Token.symbolMap.get(tokenizer.symbol()) + " </" + tokenizer.tokenType().getText() + ">");
            } else {
                println("<" + tokenizer.tokenType().getText() + "> " + tokenizer.currentToken.value + " </" + tokenizer.tokenType().getText() + ">");
            }
            tokenizer.advance();
        } else {
            error("type of " + type);
        }
    }

    private void eat() throws IOException {
        if (tokenizer.tokenType().equals(Token.TokenType.SYMBOL) && Token.symbolMap.containsKey(tokenizer.symbol())) {
            println("<" + tokenizer.tokenType().getText() + "> " + Token.symbolMap.get(tokenizer.symbol()) + " </" + tokenizer.tokenType().getText() + ">");
        } else {
            println("<" + tokenizer.tokenType().getText() + "> " + tokenizer.currentToken.value + " </" + tokenizer.tokenType().getText() + ">");
        }
        tokenizer.advance();
    }

    private String newLabel() {
        return "L" + labelIndex++;
    }

    private String functionName(){
        if (!className.isEmpty() && !functionName.isEmpty()){
            return className + "." + functionName;
        }
        return "";
    }

    private void println(String str) throws IOException {
        bufferWriter.write(str);
        bufferWriter.newLine();
        l.add(str);
    }

    public void close() throws IOException {
        bufferWriter.close();
    }


    private void error(String str) throws JackCompilerException {
        throw new JackCompilerException("Expect token: " + str + " But meet token: " + tokenizer.currentToken.toString() + ". Pos num:" + tokenizer.pos);
    }
}
