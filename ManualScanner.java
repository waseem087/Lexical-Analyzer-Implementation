import java.util.*;
import java.util.regex.*;

/**
 * Manual Scanner Implementation
 * Lexical Analyzer using DFA-based token recognition
 */
public class ManualScanner {
    
    private String input;
    private int position;
    private int line;
    private int column;
    private int lineStartPos;
    
    private List<Token> tokens;
    private SymbolTable symbolTable;
    private ErrorHandler errorHandler;
    
    // Statistics
    private Map<TokenType, Integer> tokenCounts;
    private int totalComments;
    
    // Keywords set for fast lookup
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "start", "finish", "loop", "condition", "declare", "output", 
        "input", "function", "return", "break", "continue", "else"
    ));
    
    // Pattern definitions
    private static final Pattern MULTI_LINE_COMMENT = Pattern.compile("^#\\*(.*?)\\*#", Pattern.DOTALL);
    private static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("^##[^\\n]*");
    private static final Pattern FLOAT_LITERAL = Pattern.compile("^[+-]?[0-9]+\\.[0-9]{1,6}([eE][+-]?[0-9]+)?");
    private static final Pattern INTEGER_LITERAL = Pattern.compile("^[+-]?[0-9]+");
    private static final Pattern IDENTIFIER = Pattern.compile("^[A-Z][a-z0-9_]{0,30}");
    private static final Pattern STRING_LITERAL = Pattern.compile("^\"([^\\\\\"]|\\\\[\"\\\\ntr])*\"");
    private static final Pattern CHAR_LITERAL = Pattern.compile("^'([^'\\\\\\n]|\\\\['\\\\ntr])'");
    private static final Pattern WHITESPACE = Pattern.compile("^[ \\t\\r\\n]+");
    
    public ManualScanner(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.lineStartPos = 0;
        
        this.tokens = new ArrayList<>();
        this.symbolTable = new SymbolTable();
        this.errorHandler = new ErrorHandler();
        this.tokenCounts = new HashMap<>();
        this.totalComments = 0;
    }
    
    /**
     * Main scanning method
     */
    public List<Token> scan() {
        while (position < input.length()) {
            boolean matched = false;
            
            // Save current position
            int startLine = line;
            int startColumn = column;
            
            // Try to match patterns in priority order
            
            // 1. Multi-line comments
            if (tryMatchMultiLineComment()) {
                matched = true;
                continue;
            }
            
            // 2. Single-line comments
            if (tryMatchSingleLineComment()) {
                matched = true;
                continue;
            }
            
            // 3. Whitespace (skip but track position)
            if (tryMatchWhitespace()) {
                matched = true;
                continue;
            }
            
            // 4. Multi-character operators (must be before single-character)
            if (tryMatchMultiCharOperator(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // 5. String literals
            if (tryMatchStringLiteral(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // 6. Character literals
            if (tryMatchCharLiteral(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // 7. Float literals (must be before integer)
            if (tryMatchFloatLiteral(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // 8. Integer literals
            if (tryMatchIntegerLiteral(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // 9. Keywords and identifiers (keywords checked first within this)
            if (tryMatchKeywordOrIdentifier(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // 10. Single-character operators and punctuators
            if (tryMatchSingleChar(startLine, startColumn)) {
                matched = true;
                continue;
            }
            
            // If nothing matched, it's an error
            if (!matched) {
                char ch = input.charAt(position);
                errorHandler.reportInvalidCharacter(line, column, ch);
                advance();
            }
        }
        
        return tokens;
    }
    
    /**
     * Try to match multi-line comment
     */
    private boolean tryMatchMultiLineComment() {
        Matcher m = MULTI_LINE_COMMENT.matcher(input.substring(position));
        if (m.find()) {
            String comment = m.group();
            totalComments++;
            advanceBy(comment);
            return true;
        }
        
        // Check for unclosed comment
        if (input.substring(position).startsWith("#*")) {
            errorHandler.reportUnclosedComment(line, column);
            // Skip to end
            while (position < input.length()) {
                advance();
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Try to match single-line comment
     */
    private boolean tryMatchSingleLineComment() {
        Matcher m = SINGLE_LINE_COMMENT.matcher(input.substring(position));
        if (m.find()) {
            String comment = m.group();
            totalComments++;
            advanceBy(comment);
            return true;
        }
        return false;
    }
    
    /**
     * Try to match whitespace
     */
    private boolean tryMatchWhitespace() {
        Matcher m = WHITESPACE.matcher(input.substring(position));
        if (m.find()) {
            String ws = m.group();
            advanceBy(ws);
            return true;
        }
        return false;
    }
    
    /**
     * Try to match multi-character operators
     */
    private boolean tryMatchMultiCharOperator(int startLine, int startColumn) {
        String remaining = input.substring(position);
        
        // Check two-character operators first
        if (remaining.length() >= 2) {
            String twoChar = remaining.substring(0, 2);
            TokenType type = null;
            
            switch (twoChar) {
                case "**": type = TokenType.ARITHMETIC_OP; break;
                case "==": case "!=": case "<=": case ">=": 
                    type = TokenType.RELATIONAL_OP; break;
                case "&&": case "||": 
                    type = TokenType.LOGICAL_OP; break;
                case "++": case "--": 
                    type = TokenType.INC_DEC_OP; break;
                case "+=": case "-=": case "*=": case "/=": 
                    type = TokenType.ASSIGNMENT_OP; break;
            }
            
            if (type != null) {
                addToken(type, twoChar, startLine, startColumn);
                advanceBy(twoChar);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Try to match string literal
     */
    private boolean tryMatchStringLiteral(int startLine, int startColumn) {
        if (input.charAt(position) != '"') {
            return false;
        }
        
        Matcher m = STRING_LITERAL.matcher(input.substring(position));
        if (m.find()) {
            String str = m.group();
            addToken(TokenType.STRING_LITERAL, str, startLine, startColumn);
            advanceBy(str);
            return true;
        }
        
        // Unterminated string
        int endPos = position + 1;
        while (endPos < input.length() && input.charAt(endPos) != '\n') {
            endPos++;
        }
        String badString = input.substring(position, endPos);
        errorHandler.reportUnterminatedString(startLine, startColumn, badString);
        position = endPos;
        return true;
    }
    
    /**
     * Try to match character literal
     */
    private boolean tryMatchCharLiteral(int startLine, int startColumn) {
        if (input.charAt(position) != '\'') {
            return false;
        }
        
        Matcher m = CHAR_LITERAL.matcher(input.substring(position));
        if (m.find()) {
            String ch = m.group();
            addToken(TokenType.CHAR_LITERAL, ch, startLine, startColumn);
            advanceBy(ch);
            return true;
        }
        
        // Unterminated or invalid character
        int endPos = position + 1;
        while (endPos < input.length() && endPos < position + 5 && 
               input.charAt(endPos) != '\n') {
            endPos++;
        }
        String badChar = input.substring(position, Math.min(endPos, input.length()));
        errorHandler.reportUnterminatedChar(startLine, startColumn, badChar);
        position = endPos;
        return true;
    }
    
    /**
     * Try to match float literal
     */
    private boolean tryMatchFloatLiteral(int startLine, int startColumn) {
        Matcher m = FLOAT_LITERAL.matcher(input.substring(position));
        if (m.find()) {
            String num = m.group();
            
            // Validate decimal places
            String[] parts = num.split("[eE]")[0].split("\\.");
            if (parts.length > 1 && parts[1].length() > 6) {
                errorHandler.reportMalformedLiteral(startLine, startColumn, num, 
                    "Float literal cannot have more than 6 decimal places");
            } else {
                addToken(TokenType.FLOAT_LITERAL, num, startLine, startColumn);
            }
            
            advanceBy(num);
            return true;
        }
        return false;
    }
    
    /**
     * Try to match integer literal
     */
    private boolean tryMatchIntegerLiteral(int startLine, int startColumn) {
        Matcher m = INTEGER_LITERAL.matcher(input.substring(position));
        if (m.find()) {
            String num = m.group();
            addToken(TokenType.INTEGER_LITERAL, num, startLine, startColumn);
            advanceBy(num);
            return true;
        }
        return false;
    }
    
    /**
     * Try to match keyword or identifier
     */
    private boolean tryMatchKeywordOrIdentifier(int startLine, int startColumn) {
        Matcher m = IDENTIFIER.matcher(input.substring(position));
        if (m.find()) {
            String word = m.group();
            
            // Check if it's a keyword or boolean literal
            if (KEYWORDS.contains(word)) {
                addToken(TokenType.KEYWORD, word, startLine, startColumn);
            } else if (word.equals("true") || word.equals("false")) {
                addToken(TokenType.BOOLEAN_LITERAL, word, startLine, startColumn);
            } else {
                // Validate identifier length
                if (word.length() > 31) {
                    errorHandler.reportInvalidIdentifier(startLine, startColumn, word,
                        "Identifier exceeds maximum length of 31 characters");
                } else {
                    addToken(TokenType.IDENTIFIER, word, startLine, startColumn);
                    symbolTable.addIdentifier(word, startLine, startColumn);
                }
            }
            
            advanceBy(word);
            return true;
        }
        return false;
    }
    
    /**
     * Try to match single character operators and punctuators
     */
    private boolean tryMatchSingleChar(int startLine, int startColumn) {
        char ch = input.charAt(position);
        TokenType type = null;
        
        switch (ch) {
            case '+': case '-': case '*': case '/': case '%':
                type = TokenType.ARITHMETIC_OP; break;
            case '<': case '>':
                type = TokenType.RELATIONAL_OP; break;
            case '!':
                type = TokenType.LOGICAL_OP; break;
            case '=':
                type = TokenType.ASSIGNMENT_OP; break;
            case '(': case ')': case '{': case '}': case '[': case ']':
            case ',': case ';': case ':':
                type = TokenType.PUNCTUATOR; break;
        }
        
        if (type != null) {
            addToken(type, String.valueOf(ch), startLine, startColumn);
            advance();
            return true;
        }
        
        return false;
    }
    
    /**
     * Add token to list
     */
    private void addToken(TokenType type, String lexeme, int line, int column) {
        tokens.add(new Token(type, lexeme, line, column));
        tokenCounts.put(type, tokenCounts.getOrDefault(type, 0) + 1);
    }
    
    /**
     * Advance position by one character
     */
    private void advance() {
        if (position < input.length()) {
            if (input.charAt(position) == '\n') {
                line++;
                column = 1;
                lineStartPos = position + 1;
            } else {
                column++;
            }
            position++;
        }
    }
    
    /**
     * Advance position by string length
     */
    private void advanceBy(String str) {
        for (char ch : str.toCharArray()) {
            if (ch == '\n') {
                line++;
                column = 1;
                lineStartPos = position + 1;
            } else {
                column++;
            }
            position++;
        }
    }
    
    /**
     * Display statistics
     */
    public void displayStatistics() {
        System.out.println("\n========== SCANNING STATISTICS ==========");
        System.out.println("Total Tokens: " + tokens.size());
        System.out.println("Lines Processed: " + line);
        System.out.println("Comments Removed: " + totalComments);
        System.out.println("\nToken Type Distribution:");
        System.out.println("-".repeat(45));
        
        for (Map.Entry<TokenType, Integer> entry : tokenCounts.entrySet()) {
            System.out.printf("%-25s: %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println("=".repeat(45));
    }
    
    /**
     * Display all tokens
     */
    public void displayTokens() {
        System.out.println("\n========== TOKENS ==========");
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("============================");
    }
    
    /**
     * Get methods
     */
    public List<Token> getTokens() { return tokens; }
    public SymbolTable getSymbolTable() { return symbolTable; }
    public ErrorHandler getErrorHandler() { return errorHandler; }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ManualScanner <input_file>");
            return;
        }
        
        try {
            String filename = args[0];
            String input = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(filename)));
            
            ManualScanner scanner = new ManualScanner(input);
            scanner.scan();
            
            scanner.displayTokens();
            scanner.displayStatistics();
            scanner.getSymbolTable().display();
            scanner.getErrorHandler().displayErrors();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
