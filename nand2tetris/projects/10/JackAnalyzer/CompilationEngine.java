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
                compileSubroutine();
            }
        }
    }

    private boolean isClassVarDec(Token currentToken) {
        return "static".equals(tokenizer.currentToken.value) || "field".equals(tokenizer.currentToken.value);
    }
    private boolean isClassSubroutineDec(Token currentToken) {
        return "constructor".equals(tokenizer.currentToken.value) ||
                "function".equals(tokenizer.currentToken.value) ||
                "method".equals(tokenizer.currentToken.value);
    }

    public void compileClassVarDec() throws JackCompilerException {
        eat();
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
        eat(Token.TokenType.IDENTIFIER);
        while (tokenizer.currentToken.symbol == ',') {
            eat();
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
    }

    private boolean isType(Token currentToken) {
        return "int".equals(tokenizer.currentToken.value) ||
                "char".equals(tokenizer.currentToken.value) ||
                "boolean".equals(tokenizer.currentToken.value) ||
                Token.TokenType.IDENTIFIER.equals(currentToken.type);
    }

    public void compileSubroutine() throws JackCompilerException {
        eat();
        if ("void".equals(tokenizer.currentToken.value) || isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
        eat(Token.TokenType.IDENTIFIER);
        eat("(");
        compileParameterList();
        eat(")");
        compileSubroutineBody();
    }

    public void compileSubroutineBody() throws JackCompilerException {
        eat("{");
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
        eat(Token.TokenType.IDENTIFIER);
        while(tokenizer.currentToken.symbol == ',') {
            eat();
            eat(Token.TokenType.IDENTIFIER);
        }
    }

    public void compileParameterList() throws JackCompilerException {
        if (isType(tokenizer.currentToken)) {
            eat();
            eat(Token.TokenType.IDENTIFIER);
            while (tokenizer.currentToken.symbol == ',') {
                eat();
                if (isType(tokenizer.currentToken)) {
                    eat();
                } else {
                    throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
                }
                eat(Token.TokenType.IDENTIFIER);
            }
        }
    }

    public void compileVarDec() throws JackCompilerException {
        eat("var");
        if (isType(tokenizer.currentToken)) {
            eat();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
        eat(Token.TokenType.IDENTIFIER);
        while(tokenizer.currentToken.symbol == ',') {
            eat();
            eat(Token.TokenType.IDENTIFIER);
        }
        eat(";");
    }

    public void compileStatements() throws JackCompilerException {
        while (isStatement(tokenizer.currentToken)) {
            compileStatement();
        }
    }

    private void compileStatement() throws JackCompilerException {
        switch (tokenizer.currentToken.value) {
            case "let" : compileLet(); break;
            case "if" : compileIf(); break;
            case "while" : compileWhile(); break;
            case "do" : compileDo(); break;
            case "return" : compileReturn(); break;
        }
    }

    public void compileDo() {

    }

    public void compileLet() throws JackCompilerException {
        eat("let");
        compileVarDec();
        eat("=");
        compileExpression();
        eat(";");
    }

    public void compileWhile() throws JackCompilerException {
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
    }

    public void compileReturn() throws JackCompilerException {
        eat("return");
        if (Token.TokenType.SYMBOL.equals(tokenizer.currentToken.type) && tokenizer.currentToken.symbol == ';') {
            eat(";");
        } else {
            compileExpression();
        }
    }

    public void compileIf() throws JackCompilerException {
        eat("if");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
    }

    public void compileExpression() {
        compileTerm();
        while (isOp(tokenizer.currentToken)) {
            eat();
            compileTerm();
        }
    }


    public void compileTerm() {

    }

    public void compileExpressionList() {

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
        if (tokenizer.currentToken.type.equals(type)) {
            tokenizer.advance();
        } else {
            throw new JackCompilerException("Unexpected Token: " + tokenizer.currentToken.value);
        }
    }

    private void eat() {
        tokenizer.advance();
    }
}
