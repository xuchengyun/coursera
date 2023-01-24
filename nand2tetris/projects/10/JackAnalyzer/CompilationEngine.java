import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    JackTokenizer tokenizer;
    BufferedWriter writer;

    public CompilationEngine(JackTokenizer tokenizer, String filePath) throws IOException {
        this.tokenizer = tokenizer;
        writer = new BufferedWriter(new FileWriter(filePath));
    }

    public void compileClass() throws JackCompilerException, IOException {
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
        eat();
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.tokenValue());
        }
        eat(Token.TokenType.IDENTIFIER);
        while (tokenizer.symbol() == ',') {
            eat();
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
    }

    private boolean isType(Token currentToken) {
        return "int".equals(currentToken.value) ||
                "char".equals(currentToken.value) ||
                "boolean".equals(currentToken.value) ||
                Token.TokenType.IDENTIFIER.equals(currentToken.type);
    }

    private void compileSubroutineDec() throws JackCompilerException, IOException {
        eat();
        if ("void".equals(tokenizer.tokenValue()) || isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.tokenValue());
        }
        eat(Token.TokenType.IDENTIFIER);
        eat("(");
        compileParameterList();
        eat(")");
        compileSubroutineBody();
    }

    private void compileSubroutineBody() throws JackCompilerException, IOException {
        eat("{");
        while ("var".equals(tokenizer.tokenValue())) {
            compileVarDec();
        }
        compileStatements();
        eat("}");
    }

    private void compileParameterList() throws JackCompilerException, IOException {
        if (isType(tokenizer.currentToken)) {
            eat();
            eat(Token.TokenType.IDENTIFIER);
            while (tokenizer.symbol() == ',') {
                eat();
                if (isType(tokenizer.currentToken)) {
                    eat();
                } else {
                    throw new JackCompilerException("Unexpected Token: " + tokenizer.tokenValue());
                }
                eat(Token.TokenType.IDENTIFIER);
            }
        }
    }

    private void compileVarDec() throws JackCompilerException, IOException {
        eat("var");
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.tokenValue());
        }
        eat(Token.TokenType.IDENTIFIER);
        while (tokenizer.symbol() == ',') {
            eat();
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
    }

    private void compileStatements() throws JackCompilerException, IOException {
        while (isStatement(tokenizer.currentToken)) {
            compileStatement();
        }
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
        eat("do");
        compileSubroutineCall();
    }

    private void compileLet() throws JackCompilerException, IOException {
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
    }

    private void compileWhile() throws JackCompilerException, IOException {
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
    }

    private void compileReturn() throws JackCompilerException, IOException {
        eat("return");
        if (Token.TokenType.SYMBOL.equals(tokenizer.tokenType()) && tokenizer.symbol() == ';') {
            eat(";");
        } else {
            compileExpression();
        }
    }

    private void compileIf() throws JackCompilerException, IOException {
        eat("if");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        if ("else".equals(tokenizer.tokenValue())) {
            eat("{");
            compileExpression();
            eat("}");
        }
    }

    private void compileSubroutineCall() throws JackCompilerException, IOException {
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
    }

    private void compileExpression() throws IOException {
        compileTerm();
        while (isOp(tokenizer.currentToken)) {
            eat();
            compileTerm();
        }
    }


    private void compileTerm() {

    }

    public void compileExpressionList() throws JackCompilerException, IOException {
        if (tokenizer.symbol() != ')') {
            compileExpression();
            while (tokenizer.symbol() == ',') {
                eat(",");
                compileExpression();
            }
        }
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
            println("<" + tokenizer.tokenType().getText() + "> " + tokenizer.currentToken.value + " </" + tokenizer.tokenType().getText() + ">");
            tokenizer.advance();
        } else {
            throw new JackCompilerException("Unexpected Token: " + str);
        }
    }

    private void eat(Token.TokenType type) throws JackCompilerException, IOException {
        if (tokenizer.tokenType().equals(type)) {
            println("<" + tokenizer.tokenType().getText() + "> " + tokenizer.currentToken.value + " </" + tokenizer.tokenType().getText() + ">");
            tokenizer.advance();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
    }

    private void eat() throws IOException {
        println("<" + tokenizer.tokenType().getText() + "> " + tokenizer.currentToken.value + " </" + tokenizer.tokenType().getText() + ">");
        tokenizer.advance();
    }

    private void println(String str) throws IOException {
        writer.write(str);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}
