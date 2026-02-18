import java.util.*;

/**
 * Error Handler
 * Detects and reports lexical errors
 */
public class ErrorHandler {
    
    public static class LexicalError {
        String errorType;
        int line;
        int column;
        String lexeme;
        String reason;
        
        public LexicalError(String errorType, int line, int column, 
                          String lexeme, String reason) {
            this.errorType = errorType;
            this.line = line;
            this.column = column;
            this.lexeme = lexeme;
            this.reason = reason;
        }
        
        @Override
        public String toString() {
            return String.format("ERROR [%s] at Line %d, Col %d: '%s' - %s",
                               errorType, line, column, lexeme, reason);
        }
    }
    
    private List<LexicalError> errors;
    
    public ErrorHandler() {
        errors = new ArrayList<>();
    }
    
    /**
     * Report an invalid character error
     */
    public void reportInvalidCharacter(int line, int column, char ch) {
        errors.add(new LexicalError(
            "INVALID_CHARACTER",
            line,
            column,
            String.valueOf(ch),
            "Character '" + ch + "' is not allowed in the language"
        ));
    }
    
    /**
     * Report a malformed literal error
     */
    public void reportMalformedLiteral(int line, int column, String lexeme, String reason) {
        errors.add(new LexicalError(
            "MALFORMED_LITERAL",
            line,
            column,
            lexeme,
            reason
        ));
    }
    
    /**
     * Report an invalid identifier error
     */
    public void reportInvalidIdentifier(int line, int column, String lexeme, String reason) {
        errors.add(new LexicalError(
            "INVALID_IDENTIFIER",
            line,
            column,
            lexeme,
            reason
        ));
    }
    
    /**
     * Report an unterminated string error
     */
    public void reportUnterminatedString(int line, int column, String lexeme) {
        errors.add(new LexicalError(
            "UNTERMINATED_STRING",
            line,
            column,
            lexeme,
            "String literal is not properly closed"
        ));
    }
    
    /**
     * Report an unterminated character literal error
     */
    public void reportUnterminatedChar(int line, int column, String lexeme) {
        errors.add(new LexicalError(
            "UNTERMINATED_CHAR",
            line,
            column,
            lexeme,
            "Character literal is not properly closed"
        ));
    }
    
    /**
     * Report an unclosed multi-line comment error
     */
    public void reportUnclosedComment(int line, int column) {
        errors.add(new LexicalError(
            "UNCLOSED_COMMENT",
            line,
            column,
            "#*",
            "Multi-line comment is not properly closed with *#"
        ));
    }
    
    /**
     * Report a generic lexical error
     */
    public void reportError(String errorType, int line, int column, 
                          String lexeme, String reason) {
        errors.add(new LexicalError(errorType, line, column, lexeme, reason));
    }
    
    /**
     * Check if any errors occurred
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Get error count
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Display all errors
     */
    public void displayErrors() {
        if (errors.isEmpty()) {
            System.out.println("\n✓ No lexical errors found!");
            return;
        }
        
        System.out.println("\n========== LEXICAL ERRORS ==========");
        for (LexicalError error : errors) {
            System.out.println(error);
        }
        System.out.println("====================================");
        System.out.println("Total Errors: " + errors.size());
    }
    
    /**
     * Clear all errors
     */
    public void clear() {
        errors.clear();
    }
    
    /**
     * Get all errors
     */
    public List<LexicalError> getErrors() {
        return new ArrayList<>(errors);
    }
}
