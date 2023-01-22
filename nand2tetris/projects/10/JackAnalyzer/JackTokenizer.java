import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class JackTokenizer {

    private final char[] chars;
    List<Token> tokens;
    Token token;
    Scanner scanner;



    public JackTokenizer(File file) throws IOException {

        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine().trim());
                System.out.println(file.getName() + ":" + content);
            }
        }

        String precessedString = removeBlockComment(content.toString());
        int numChars = precessedString.length();
        this.chars = Arrays.copyOf(precessedString.toCharArray(), numChars + 1);
        tokens = new ArrayList<>();
        scan();
    }

    private void scan() {
        int pos = 0;
        StringBuilder tokenBuilder = new StringBuilder();
        while (pos < chars.length) {
            char ch = chars[pos];
            if (isSymbol(ch)) {
                tokens.add(new Token.TokenBuilder(Token.TokenType.SYMBOL).symbol(ch).build());
                pos++;
//                break;
            }
//            switch (ch) {
//
//                // Symbols
//                case '{': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '}': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '(': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case ')': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '[': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case ']': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '.': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case ';': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '+': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '-': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '*': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '/': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '&': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '|': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '<': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '>': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '=': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//                case '~': {tokens.add(new Token(TokenType.SYMBOL)); pos++;} break;
//
//                case
//
//            }
        }

    }

    private String removeBlockComment(String content) {
        return content.replaceAll("/\\*.*?\\*/", "");
    }

    public boolean hasMoreTokens() {
        return true;
    }

    public Token tokenType() {
        return null;
    }

    public KeyWord keyWord() {
        return null;
    }

    public char symbol() {
        return 'c';
    }

    public String identifier() {
        return null;
    }

    public int intVal() {
        return 0;
    }

    public String stringVal() {
        return "";
    }

    private boolean isSymbol(char character) {
        return Token.symbols.contains(String.valueOf(character));
    }

}
