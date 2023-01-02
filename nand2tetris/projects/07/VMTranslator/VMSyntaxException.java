public class VMSyntaxException extends Exception{
    public VMSyntaxException(String message) {
        super(message);
    }

    public VMSyntaxException(Exception e) {
        super(e);
    }
}