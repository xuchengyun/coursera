import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SymbolTable {
    Map<String, Symbol> classSymbolTable;
    Map<String,Symbol> subroutineSymbolTable;
    Map<Symbol.Kind, Integer> indexMap;
    public SymbolTable() {
        classSymbolTable = new HashMap<>();
        subroutineSymbolTable = new HashMap<>();
        indexMap = new HashMap<>();
    }

    public void startSubroutine() {
        subroutineSymbolTable.clear();
        indexMap.put(Symbol.Kind.VAR, 0);
        indexMap.put(Symbol.Kind.ARG, 0);
    }

    public void define(String name, String type, Symbol.Kind kind) {
        if (kind == Symbol.Kind.ARG || kind == Symbol.Kind.VAR){
            int index = indexMap.getOrDefault(kind, 0);
            Symbol symbol = new Symbol(type,kind,index);
            indexMap.put(kind, index + 1);
            subroutineSymbolTable.put(name,symbol);
        }else if(kind == Symbol.Kind.STATIC || kind == Symbol.Kind.FIELD){
            int index = indexMap.getOrDefault(kind, 0);
            Symbol symbol = new Symbol(type, kind, index);
            indexMap.put(kind,index + 1);
            classSymbolTable.put(name,symbol);
        }
    }

    public int varCount(Symbol.Kind kind) {
        return indexMap.getOrDefault(kind, 0);
    }

    public Symbol.Kind kindOf(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol != null) return symbol.getKind();
        return Symbol.Kind.NONE;
    }

    public String typeOf(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol != null) return symbol.getType();
        return "";
    }

    public int indexOf(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol != null) return symbol.getIndex();
        return -1;
    }

    private Symbol getSymbol(String name){
        if (classSymbolTable.containsKey(name)) {
            return classSymbolTable.get(name);
        }
        if (subroutineSymbolTable.containsKey(name)) {
            return subroutineSymbolTable.get(name);
        }
        return null;
    }

}
