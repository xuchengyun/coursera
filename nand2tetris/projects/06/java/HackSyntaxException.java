public class HackSyntaxException extends Exception {
    public HackSyntaxException(String message) {
        super(message);
    }

    public HackSyntaxException(Exception e) {
        super(e);
    }
}
