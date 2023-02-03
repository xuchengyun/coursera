import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token {
    public static final List<String> keywords =
            Arrays.asList("class", "constructor", "function",
                    "method", "field", "static", "var",
                    "int", "char", "boolean", "void", "true",
                    "false", "null", "this", "let", "do",
                    "if", "else", "while", "return");

    public static final List<String> symbols =
            Arrays.asList("{", "}", "(", ")", "[", "]", ".",
                    ",", ";", "+", "-", "*", "/", "&",
                    "|", "<", ">", "=", "~");
    public static final Map<Character, String> symbolMap;
    static {
        symbolMap = new HashMap<>();
        symbolMap.put('<', "&lt;");
        symbolMap.put('>', "&gt;");
        symbolMap.put('"', "&quot;");
        symbolMap.put('&', "&amp;");
    }

    String identifier;
    char symbol;
    int intVal;
    String stringVal;

    String value;

    public enum TokenType {
        KEYWORD("keyword"),
        SYMBOL("symbol"),
        INT_CONST("integerConstant"),
        STRING_CONST("stringConstant"),
        IDENTIFIER("identifier");
        private final String text;
        TokenType(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }

    public enum KeyWord {
        CLASS,
        METHOD,
        FUNCTION,
        CONSTRUCTOR,
        INT,
        BOOLEAN,
        CHAR,
        VOID,
        VAR,
        STATIC,
        FIELD,
        LET,
        DO,
        IF,
        ELSE,
        WHILE,
        RETURN,
        TRUE,
        FALSE,
        NULL,
        THIS
    }


    TokenType type;
    KeyWord keyword;

    public Token(TokenBuilder builder) {
        this.intVal = builder.intVal;
        this.type = builder.type;
        this.keyword = builder.keyword;
        this.symbol = builder.symbol;
        this.identifier = builder.identifier;
        this.stringVal = builder.stringVal;
        this.value = builder.value;
    }
    @Override
    public String toString() {
        return this.value + "|" + this.type;
    }
    public static class TokenBuilder {
        private final TokenType type;
        private KeyWord keyword;
        private String identifier;
        private char symbol;
        private int intVal;
        private String stringVal;
        private String value;


        public TokenBuilder(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public TokenBuilder keyword(String keyword) {
            this.keyword = KeyWord.valueOf(keyword.toUpperCase());
            return this;
        }

        public TokenBuilder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public TokenBuilder symbol(char symbol) {
            this.symbol = symbol;
            return this;
        }

        public TokenBuilder intVal(int val) {
            this.intVal = val;
            return this;
        }

        public TokenBuilder stringVal(String val) {
            this.stringVal = val;
            return this;
        }

        public Token build() {
            return new Token(this);
        }
    }
}
