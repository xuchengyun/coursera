import java.util.Arrays;
import java.util.List;

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

    String identifier;
    char symbol;
    int intVal;
    String stringVal;

    public enum TokenType {
        KEYWORD,
        SYMBOL,
        INT_CONST,
        STRING_CONST,
        IDENTIFIER
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
    }

    public static class TokenBuilder {
        private final TokenType type;
        private KeyWord keyword;
        private String identifier;
        private char symbol;
        private int intVal;
        private String stringVal;


        public TokenBuilder(TokenType type) {
            this.type = type;
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
