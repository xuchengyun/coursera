import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Symbol> classSymbolTable;
    Map<String,Symbol> subroutineSymbolTable;
    public SymbolTable() {
        classSymbolTable = new HashMap<>();
        subroutineSymbolTable = new HashMap<>();
    }

    public void startSubroutine() {

    }

    public void define(String name, String type, Kind kind) {

    }

    public int varCount(Kind kind) {
        return 0;
    }

    public Kind kindOf(String name) {
        return null;
    }

    public String typeOf(String name) {
        return "";
    }

    public int indexOf(String name) {
        return 1;
    }

    public enum Kind {
        STATIC,
        FIELD,
        ARG,
        VAR
    }
}
