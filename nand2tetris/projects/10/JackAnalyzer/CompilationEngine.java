import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompilationEngine {
    JackTokenizer tokenizer;
    BufferedWriter writer;
    List<String> l = new ArrayList<>();
    public CompilationEngine(JackTokenizer tokenizer, String filePath) throws IOException {
        this.tokenizer = tokenizer;
        writer = new BufferedWriter(new FileWriter(filePath));
    }

    public void compileClass() throws JackCompilerException, IOException {
        println("<class>");
        eat("class");
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
        writer.close();
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
        eat();
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            error("Token of Type");
        }
        eat(Token.TokenType.IDENTIFIER);
        while (tokenizer.symbol() == ',') {
            eat();
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
        eat();
        if ("void".equals(tokenizer.tokenValue()) || isType(tokenizer.currentToken)) {
            eat();
        } else {
            error("void|type");
        }
        eat(Token.TokenType.IDENTIFIER);
        eat("(");
        compileParameterList();
        eat(")");
        compileSubroutineBody();
        println("</subroutineDec>");
    }

    private void compileSubroutineBody() throws JackCompilerException, IOException {
        println("<subroutineBody>");
        eat("{");
        while ("var".equals(tokenizer.tokenValue())) {
            compileVarDec();
        }
        compileStatements();
        eat("}");
        println("</subroutineBody>");
    }

    private void compileParameterList() throws JackCompilerException, IOException {
        println("<parameterList>");
        if (isType(tokenizer.currentToken)) {
            eat();
            eat(Token.TokenType.IDENTIFIER);
            while (tokenizer.symbol() == ',') {
                eat();
                if (isType(tokenizer.currentToken)) {
                    eat();
                } else {
                    error("type");
                }
                eat(Token.TokenType.IDENTIFIER);
            }
        }
        println("</parameterList>");
    }

    private void compileVarDec() throws JackCompilerException, IOException {
        println("<varDec>");
        eat("var");
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            error("type");
        }
        eat(Token.TokenType.IDENTIFIER);
        while (tokenizer.symbol() == ',') {
            eat();
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
        eat(Token.TokenType.IDENTIFIER);
        if (tokenizer.symbol() == '[') {
            eat("[");
            compileExpression();
            eat("]");
        }
        eat("=");
        compileExpression();
        eat(";");
        println("</letStatement>");
    }

    private void compileWhile() throws JackCompilerException, IOException {
        println("<whileStatement>");
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        println("</whileStatement>");
    }

    private void compileReturn() throws JackCompilerException, IOException {
        println("<returnStatement>");
        eat("return");
        if (Token.TokenType.SYMBOL.equals(tokenizer.tokenType()) && tokenizer.symbol() == ';') {
            eat(";");
        } else {
            compileExpression();
            eat(";");
        }
        println("</returnStatement>");
    }

    private void compileIf() throws JackCompilerException, IOException {
        println("<ifStatement>");
        eat("if");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        if ("else".equals(tokenizer.tokenValue())) {
            eat();
            eat("{");
            compileStatements();
            eat("}");
        }
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
            eat();
            if (tokenizer.currentToken.value.equals("[")) {
                eat("[");
                compileExpression();
                eat("]");
            } else if (tokenizer.currentToken.value.equals("(") || tokenizer.currentToken.value.equals(".")) {
                // Case: subroutineCall
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
            }
        } else {
            error("term");
        }
        println("</term>");

    }

    private boolean isUnaryOp(Token currentToken) {
        return ("-").equals(currentToken.value) ||
                ("~").equals(currentToken.value);
    }


    public void compileExpressionList() throws JackCompilerException, IOException {
        println("<expressionList>");
        if (tokenizer.symbol() != ')') {
            compileExpression();
            while (tokenizer.symbol() == ',') {
                eat(",");
                compileExpression();
            }
        }
        println("</expressionList>");
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

    private void println(String str) throws IOException {
        writer.write(str);
        writer.newLine();
        l.add(str);
    }

    public void close() throws IOException {
        writer.close();
    }

    private void error(String str) throws JackCompilerException {
        throw new JackCompilerException("Expect token: " + str + " But meet token: " + tokenizer.currentToken.toString() + ". Pos num:" + tokenizer.pos);
    }
}
