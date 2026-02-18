/**
 * TokenType Enum
 * Defines all possible token types in the language
 */
public enum TokenType {
    // Keywords
    KEYWORD,
    
    // Identifiers
    IDENTIFIER,
    
    // Literals
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    CHAR_LITERAL,
    BOOLEAN_LITERAL,
    
    // Operators
    ARITHMETIC_OP,
    RELATIONAL_OP,
    LOGICAL_OP,
    ASSIGNMENT_OP,
    INC_DEC_OP,
    
    // Punctuators
    PUNCTUATOR,
    
    // Comments (for tracking purposes)
    SINGLE_LINE_COMMENT,
    MULTI_LINE_COMMENT,
    
    // Special
    WHITESPACE,
    ERROR,
    EOF
}
