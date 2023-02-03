public class JackCompilerException extends Exception{
    public JackCompilerException(String message) {
        super(message);
    }

    public JackCompilerException(Exception e) {
        super(e);
    }
}
