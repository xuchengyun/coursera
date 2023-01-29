import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JackTokenizer {

    private final char[] chars;
    List<Token> tokens;
    Token currentToken;
    int pos;
    BufferedWriter writer;

    public JackTokenizer(File file, String filePath) throws IOException {
        writer = new BufferedWriter(new FileWriter(filePath));

        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(remoComments(scanner.nextLine()).trim());
            }
        }

        String precessedString = removeBlockComment(content.toString());
        int numChars = precessedString.length();
        this.chars = Arrays.copyOf(precessedString.toCharArray(), numChars + 1);
        tokens = new ArrayList<>();
        scan();
        if (tokens.size() > 0) {
            currentToken = tokens.get(0);
            pos = 0;
        } else {
            throw new IllegalArgumentException("No tokens generated from input");
        }
        printTokens();
        writer.close();
    }

    private String remoComments(String line) {
        int index = line.indexOf("//");
        if (index > -1) {
            return line.substring(0, index);
        }

        return line;
    }

    private void scan() {
        int pos = 0;
        boolean startStrConst = false;
        StringBuilder tokenBuilder = new StringBuilder();
        while (pos < chars.length) {
            char ch = chars[pos];
            if (!startStrConst && isSymbol(ch)) {
                if (!tokenBuilder.toString().isEmpty()) {
                    tokens.add(parseToken(tokenBuilder.toString()));
                }
                tokens.add(new Token.TokenBuilder(Token.TokenType.SYMBOL, Character.toString(ch)).symbol(ch).build());
                tokenBuilder = new StringBuilder();
            } else if (!startStrConst && isSpace(ch)) {
                if (!tokenBuilder.toString().isEmpty()) {
                    tokens.add(parseToken(tokenBuilder.toString()));
                }
                tokenBuilder = new StringBuilder();
            } else if (ch == '"') {
                if (startStrConst) {
                    tokens.add(new Token.TokenBuilder(Token.TokenType.STRING_CONST,
                            tokenBuilder.toString()).stringVal(tokenBuilder.toString()).build());
                    startStrConst = false;
                    tokenBuilder = new StringBuilder();
                } else {
                    startStrConst = true;
                }
            } else {
                tokenBuilder.append(ch);
            }
            pos++;
        }
        System.out.println(tokens);
    }

    private boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    private Token parseToken(String s) {
        if (Token.keywords.contains(s)) {
            return new Token.TokenBuilder(Token.TokenType.KEYWORD, s).keyword(s).build();
        }
        try {
            int val = Integer.parseInt(s);
            return new Token.TokenBuilder(Token.TokenType.INT_CONST, s).intVal(val).build();

        } catch (NumberFormatException e) {
            return  new Token.TokenBuilder(Token.TokenType.IDENTIFIER, s).identifier(s).build();
        }
    }

    private String removeBlockComment(String content) {
        return content.replaceAll("/\\*.*?\\*/", "");
    }

    public boolean hasMoreTokens() {
        return pos + 1 < tokens.size();
    }

    public Token.TokenType tokenType() {
        return currentToken.type;
    }

    public Token.KeyWord keyWord() {
        return currentToken.keyword;
    }

    public char symbol() {
        return currentToken.symbol;
    }

    public String identifier() {
        return currentToken.identifier;
    }

    public int intVal() {
        return currentToken.intVal;
    }

    public String stringVal() {
        return currentToken.stringVal;
    }
    public Token currentToken() {
        return this.currentToken;
    }

    public String tokenValue() {
        return this.currentToken.value;
    }

    private boolean isSymbol(char character) {
        return Token.symbols.contains(String.valueOf(character));
    }


    public void printTokens() throws IOException {
        writer.write("<tokens>");
        writer.newLine();
        for (Token t: tokens) {
            switch (t.type) {
                // Skip empty line and comments
                case KEYWORD:
                    writer.write("<keyword> " + t.keyword.toString().toLowerCase() +" </keyword>");
                    writer.newLine();
                    break;
                case SYMBOL:
                    writer.write("<symbol> " + (Token.symbolMap.containsKey(t.symbol) ? Token.symbolMap.get(t.symbol) : t.symbol) +" </symbol>");
                    writer.newLine();
                    break;
                case INT_CONST:
                    writer.write("<intConst> " + t.intVal +" </intConst>");
                    writer.newLine();
                    break;
                case STRING_CONST:
                    writer.write("<stringConstant> " + t.stringVal +" </stringConstant>");
                    writer.newLine();
                    break;
                case IDENTIFIER:
                    writer.write("<identifier> " + t.identifier +" </identifier>");
                    writer.newLine();
                    break;
            }
        }
        writer.write("</tokens>");
        writer.newLine();
    }

    public void advance() {
        if (hasMoreTokens()) {
            pos++;
            currentToken = tokens.get(pos);
        }
    }
}
