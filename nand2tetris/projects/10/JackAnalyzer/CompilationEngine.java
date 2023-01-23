public class CompilationEngine {
    JackTokenizer tokenizer;
    public CompilationEngine(JackTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void compileClass() {

    }

    public void compileClassVarDec() {

    }

    public void compileSubroutine() {

    }

    public void compileParameterList() {

    }

    public void compileVarDec() {

    }

    public void compileStatements() {
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

    public void compileReturn() {

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

    }

    public void compileTerm() {

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

}
