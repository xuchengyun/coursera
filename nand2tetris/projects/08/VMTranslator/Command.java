public class Command {

    public CommandType type;
    public String command;
    public String arg1;
    public int arg2;

    public Command(String command, CommandType type, String arg1, int arg2) {
        this.command = command;
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public boolean IsArithmetic() {
        return CommandType.C_ARITHMETIC.equals(type);
    }

    public boolean IsPushPop() {
        return CommandType.C_POP.equals(type) || CommandType.C_PUSH.equals(type);
    }

}
