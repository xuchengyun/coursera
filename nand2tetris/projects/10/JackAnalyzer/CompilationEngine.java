public class CompilationEngine {
    JackTokenizer tokenizer;

    public CompilationEngine(JackTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void compileClass() throws JackCompilerException {
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
    }

    private boolean isClassVarDec(Token currentToken) {
        return "static".equals(currentToken.value) || "field".equals(currentToken.value);
    }
    private boolean isClassSubroutineDec(Token currentToken) {
        return "constructor".equals(currentToken.value) ||
                "function".equals(currentToken.value) ||
                "method".equals(currentToken.value);
    }

    private void compileClassVarDec() throws JackCompilerException {
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

    private void compileSubroutineDec() throws JackCompilerException {
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

    private void compileSubroutineBody() throws JackCompilerException {
        eat("{");
        while("var".equals(tokenizer.tokenValue())) {
            compileVarDec();
        }
        compileStatements();
        eat("}");
    }

    private void compileParameterList() throws JackCompilerException {
        if (isType(tokenizer.currentToken)) {
            eat();
            eat(Token.TokenType.IDENTIFIER);
            while (tokenizer.symbol()== ',') {
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

    private void compileVarDec() throws JackCompilerException {
        eat("var");
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.tokenValue());
        }
        eat(Token.TokenType.IDENTIFIER);
        while(tokenizer.symbol() == ',') {
            eat();
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
    }

    private void compileStatements() throws JackCompilerException {
        while (isStatement(tokenizer.currentToken)) {
            compileStatement();
        }
    }

    private void compileStatement() throws JackCompilerException {
        switch (tokenizer.tokenValue()) {
            case "let" : compileLet(); break;
            case "if" : compileIf(); break;
            case "while" : compileWhile(); break;
            case "do" : compileDo(); break;
            case "return" : compileReturn(); break;
        }
    }

    private void compileDo() throws JackCompilerException {
        eat("do");
        compileSubroutineCall();
    }

    private void compileLet() throws JackCompilerException {
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

    private void compileWhile() throws JackCompilerException {
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
    }

    private void compileReturn() throws JackCompilerException {
        eat("return");
        if (Token.TokenType.SYMBOL.equals(tokenizer.tokenType()) && tokenizer.symbol()== ';') {
            eat(";");
        } else {
            compileExpression();
        }
    }

    private void compileIf() throws JackCompilerException {
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

    private void compileSubroutineCall() throws JackCompilerException {
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

    private void compileExpression() {
        compileTerm();
        while (isOp(tokenizer.currentToken)) {
            eat();
            compileTerm();
        }
    }


    private void compileTerm() {

    }

    public void compileExpressionList() throws JackCompilerException {
        if (tokenizer.symbol() != ')'){
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


    private void eat(String str) throws JackCompilerException {
        if (str == null) {
            throw new IllegalArgumentException("Parsing null value to eat method");
        }
        if (str.equals(tokenizer.currentToken.value)) {
            tokenizer.advance();
        } else {
            throw new JackCompilerException("Unexpected Token: " + str);
        }
    }

    private void eat(Token.TokenType type) throws JackCompilerException {
        if (tokenizer.tokenType().equals(type)) {
            tokenizer.advance();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
    }

    private void eat() {
        tokenizer.advance();
    }
}
