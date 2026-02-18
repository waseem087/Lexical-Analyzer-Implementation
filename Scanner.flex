/* Scanner.flex - JFlex Specification for Custom Language */

import java.util.*;

%%

%class Yylex
%public
%unicode
%line
%column
%type Token

%{
    private SymbolTable symbolTable = new SymbolTable();
    private ErrorHandler errorHandler = new ErrorHandler();
    private Map<TokenType, Integer> tokenCounts = new HashMap<>();
    private int totalComments = 0;
    
    private Token createToken(TokenType type, String lexeme) {
        Token token = new Token(type, lexeme, yyline + 1, yycolumn + 1);
        tokenCounts.put(type, tokenCounts.getOrDefault(type, 0) + 1);
        
        if (type == TokenType.IDENTIFIER) {
            symbolTable.addIdentifier(lexeme, yyline + 1, yycolumn + 1);
        }
        
        return token;
    }
    
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
    
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
    
    public Map<TokenType, Integer> getTokenCounts() {
        return tokenCounts;
    }
    
    public int getTotalComments() {
        return totalComments;
    }
%}

/* Macro Definitions */
DIGIT = [0-9]
LETTER_UPPER = [A-Z]
LETTER_LOWER = [a-z]
UNDERSCORE = _

/* Whitespace */
WHITESPACE = [ \t\r\n]+

/* Comments */
SINGLE_LINE_COMMENT = ##[^\n]*
MULTI_LINE_COMMENT = #\*([^*]|\*+[^*#])*\*+#

/* Keywords */
KEYWORD = start|finish|loop|condition|declare|output|input|function|return|break|continue|else

/* Identifiers */
IDENTIFIER = {LETTER_UPPER}({LETTER_LOWER}|{DIGIT}|{UNDERSCORE}){0,30}

/* Literals */
INTEGER_LITERAL = [+-]?{DIGIT}+
FLOAT_LITERAL = [+-]?{DIGIT}+\.{DIGIT}{1,6}([eE][+-]?{DIGIT}+)?
BOOLEAN_LITERAL = true|false

/* String and Character Literals */
STRING_LITERAL = \"([^\\\"\n]|\\[\"\\ntr])*\"
CHAR_LITERAL = '([^'\\\n]|\\['\\ntr])'

/* Operators - Multi-character (must be defined before single-character) */
EXPONENT_OP = \*\*
RELATIONAL_OP = ==|!=|<=|>=|<|>
LOGICAL_AND = &&
LOGICAL_OR = \|\|
LOGICAL_NOT = !
INC_OP = \+\+
DEC_OP = --
ADD_ASSIGN = \+=
SUB_ASSIGN = -=
MUL_ASSIGN = \*=
DIV_ASSIGN = /=

/* Operators - Single-character */
ARITHMETIC_OP = [+\-*/%]
ASSIGNMENT_OP = =

/* Punctuators */
PUNCTUATOR = [(){}\[\],;:]

%%

/* Lexical Rules - Order matters! */

/* 1. Comments (highest priority) */
{MULTI_LINE_COMMENT}     { totalComments++; /* skip */ }
{SINGLE_LINE_COMMENT}    { totalComments++; /* skip */ }

/* 2. Whitespace */
{WHITESPACE}             { /* skip */ }

/* 3. Multi-character operators (before single-character) */
{EXPONENT_OP}            { return createToken(TokenType.ARITHMETIC_OP, yytext()); }
{RELATIONAL_OP}          { return createToken(TokenType.RELATIONAL_OP, yytext()); }
{LOGICAL_AND}            { return createToken(TokenType.LOGICAL_OP, yytext()); }
{LOGICAL_OR}             { return createToken(TokenType.LOGICAL_OP, yytext()); }
{LOGICAL_NOT}            { return createToken(TokenType.LOGICAL_OP, yytext()); }
{INC_OP}                 { return createToken(TokenType.INC_DEC_OP, yytext()); }
{DEC_OP}                 { return createToken(TokenType.INC_DEC_OP, yytext()); }
{ADD_ASSIGN}             { return createToken(TokenType.ASSIGNMENT_OP, yytext()); }
{SUB_ASSIGN}             { return createToken(TokenType.ASSIGNMENT_OP, yytext()); }
{MUL_ASSIGN}             { return createToken(TokenType.ASSIGNMENT_OP, yytext()); }
{DIV_ASSIGN}             { return createToken(TokenType.ASSIGNMENT_OP, yytext()); }

/* 4. Keywords (before identifiers) */
{KEYWORD}                { return createToken(TokenType.KEYWORD, yytext()); }

/* 5. Boolean literals */
{BOOLEAN_LITERAL}        { return createToken(TokenType.BOOLEAN_LITERAL, yytext()); }

/* 6. Identifiers */
{IDENTIFIER}             { 
    if (yytext().length() > 31) {
        errorHandler.reportInvalidIdentifier(yyline + 1, yycolumn + 1, yytext(),
            "Identifier exceeds maximum length of 31 characters");
    }
    return createToken(TokenType.IDENTIFIER, yytext()); 
}

/* 7. Literals */
{FLOAT_LITERAL}          { 
    String[] parts = yytext().split("[eE]")[0].split("\\.");
    if (parts.length > 1 && parts[1].length() > 6) {
        errorHandler.reportMalformedLiteral(yyline + 1, yycolumn + 1, yytext(),
            "Float literal cannot have more than 6 decimal places");
    }
    return createToken(TokenType.FLOAT_LITERAL, yytext()); 
}
{INTEGER_LITERAL}        { return createToken(TokenType.INTEGER_LITERAL, yytext()); }
{STRING_LITERAL}         { return createToken(TokenType.STRING_LITERAL, yytext()); }
{CHAR_LITERAL}           { return createToken(TokenType.CHAR_LITERAL, yytext()); }

/* 8. Single-character operators */
{ARITHMETIC_OP}          { return createToken(TokenType.ARITHMETIC_OP, yytext()); }
{ASSIGNMENT_OP}          { return createToken(TokenType.ASSIGNMENT_OP, yytext()); }

/* 9. Punctuators */
{PUNCTUATOR}             { return createToken(TokenType.PUNCTUATOR, yytext()); }

/* 10. Error handling - anything else */
.                        { 
    errorHandler.reportInvalidCharacter(yyline + 1, yycolumn + 1, yytext().charAt(0));
    /* Continue scanning */
}

/* Unclosed comment check would need to be handled in the main program */
