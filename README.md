# Custom Programming Language - Lexical Analyzer

## Language Overview

**Language Name:** SimpleLang  
**File Extension:** `.lang`

SimpleLang is a custom imperative programming language designed for educational purposes to demonstrate lexical analysis concepts.


---

## Language Specifications

### Keywords (Case-Sensitive)

| Keyword | Description |
|---------|-------------|
| `start` | Program entry point (like 'main' in C/Java) |
| `finish` | Program exit point |
| `loop` | Loop construct (while/for loop) |
| `condition` | Conditional statement (if statement) |
| `else` | Alternative branch for condition |
| `declare` | Variable declaration |
| `output` | Print/output statement |
| `input` | Read/input statement |
| `function` | Function definition |
| `return` | Return from function |
| `break` | Break from loop |
| `continue` | Continue to next iteration |

### Identifiers

**Rules:**
- Must start with an **uppercase letter** (A-Z)
- Followed by lowercase letters (a-z), digits (0-9), or underscores (_)
- Maximum length: 31 characters
- Case-sensitive

**Valid Examples:**
```
Count
Variable_name
X
Total_sum_2024
My_var
```

**Invalid Examples:**
```
count                    // Must start with uppercase
Variable                 // Second char must be lowercase/digit/underscore
2Count                   // Cannot start with digit
myVariable               // Must start with uppercase
This_is_way_too_long_identifier_name  // Exceeds 31 characters
```

### Literals

#### Integer Literals
- Format: Optional sign (+/-) followed by digits
- Examples: `42`, `+100`, `-567`, `0`

#### Floating-Point Literals
- Format: Optional sign, digits, decimal point, 1-6 decimal digits, optional exponent
- Examples: `3.14`, `+2.5`, `-0.123456`, `1.5e10`, `2.0E-3`
- Invalid: `3.` (no decimal), `.14` (no leading digit), `1.2345678` (>6 decimals)

#### String Literals
- Enclosed in double quotes: `"..."`
- Escape sequences: `\"`, `\\`, `\n`, `\t`, `\r`
- Examples: 
  - `"Hello, World!"`
  - `"Line1\nLine2"`
  - `"He said \"Hello\""`
  - `"Path: C:\\Users\\Documents"`

#### Character Literals
- Enclosed in single quotes: `'...'`
- Single character or escape sequence
- Examples: `'A'`, `'z'`, `'0'`, `'\n'`, `'\t'`, `'\''`, `'\\'`

#### Boolean Literals
- `true` or `false` (case-sensitive)

### Operators

#### Arithmetic Operators (Precedence: High to Low)
1. `**` - Exponentiation
2. `*` `/` `%` - Multiplication, Division, Modulus
3. `+` `-` - Addition, Subtraction

#### Relational Operators
- `==` - Equal to
- `!=` - Not equal to
- `<` - Less than
- `<=` - Less than or equal to
- `>` - Greater than
- `>=` - Greater than or equal to

#### Logical Operators
- `&&` - Logical AND
- `||` - Logical OR
- `!` - Logical NOT

#### Assignment Operators
- `=` - Simple assignment
- `+=` `-=` `*=` `/=` - Compound assignment

#### Increment/Decrement
- `++` - Increment
- `--` - Decrement

### Punctuators
- `(` `)` - Parentheses
- `{` `}` - Braces
- `[` `]` - Brackets
- `,` - Comma
- `;` - Semicolon
- `:` - Colon

### Comments

#### Single-Line Comments
- Start with `##`
- Continue until end of line
- Example: `## This is a comment`

#### Multi-Line Comments
- Start with `#*`
- End with `*#`
- Can span multiple lines
- Example:
```
#* This is a
   multi-line
   comment *#
```

---

## Sample Programs

### Sample 1: Hello World
```
start
    output "Hello, World!";
finish
```

### Sample 2: Sum Calculation
```
start
    declare Num1 = 10;
    declare Num2 = 20;
    declare Sum = Num1 + Num2;
    output "Sum is: ";
    output Sum;
finish
```

### Sample 3: Factorial Function
```
start
    function Factorial(N) {
        condition (N <= 1) {
            return 1;
        } else {
            return N * Factorial(N - 1);
        }
    }
    
    declare Result = Factorial(5);
    output "Factorial: ";
    output Result;
finish
```

### Sample 4: Loop Example
```
start
    declare I = 0;
    declare Sum = 0;
    
    loop (I < 10) {
        Sum += I;
        I++;
    }
    
    output "Sum of 0 to 9: ";
    output Sum;
finish
```

### Sample 5: Conditional Logic
```
start
    declare Age = 25;
    declare Has_license = true;
    
    condition (Age >= 18 && Has_license) {
        output "Can drive";
    } else {
        output "Cannot drive";
    }
finish
```

---

## Compilation and Execution

### Manual Scanner

#### Compilation
```bash
cd src
javac ManualScanner.java Token.java TokenType.java SymbolTable.java ErrorHandler.java
```

#### Execution
```bash
java ManualScanner ../tests/test1.lang
```

### JFlex Scanner

#### Generate Scanner
```bash
cd src
jflex Scanner.flex
```

#### Compilation
```bash
javac Yylex.java Token.java TokenType.java SymbolTable.java ErrorHandler.java
```

#### Execution
Create a main class to run the JFlex scanner:
```java
// Run JFlex scanner on a file
```

### Running Tests
```bash
# Test all files
cd src
java ManualScanner ../tests/test1.lang > ../tests/results_test1.txt
java ManualScanner ../tests/test2.lang > ../tests/results_test2.txt
java ManualScanner ../tests/test3.lang > ../tests/results_test3.txt
java ManualScanner ../tests/test4.lang > ../tests/results_test4.txt
java ManualScanner ../tests/test5.lang > ../tests/results_test5.txt
```

---

## Project Structure

```
Lexical-Analyzer-Implementation

│   ├── ManualScanner.java      # Manual DFA-based scanner
│   ├── Token.java              # Token representation
│   ├── TokenType.java          # Token type enumeration
│   ├── SymbolTable.java        # Identifier storage
│   ├── ErrorHandler.java       # Error detection and reporting
│   ├── Scanner.flex            # JFlex specification
│   └── Yylex.java             # Generated JFlex scanner
└── README.md                  # Project documentation
```

---

## Features Implemented

### Part 1: Manual Scanner
-  Regular expression-based token recognition
-  DFA-based matching with longest match principle
-  All token types from specification
-  Pre-processing and whitespace handling
-  Line and column tracking
-  Symbol table for identifiers
-  Statistics display

### Part 2: JFlex Scanner
-  Complete JFlex specification
-  Pattern matching with correct priority
-  Compatible Token class
-  Output comparison

### Part 3: Error Handling
-  Invalid character detection
-  Malformed literal detection
-  Invalid identifier detection
-  Unterminated string/char detection
- ✓ Unclosed comment detection
- ✓ Error recovery and continuation
- ✓ Comprehensive error reporting

---

## Testing

All test files are located in the `tests/` directory:

1. **test1.lang** - Comprehensive valid token test
2. **test2.lang** - Complex expressions and nested structures
3. **test3.lang** - String and character escape sequences
4. **test4.lang** - Various lexical errors
5. **test5.lang** - Comment handling

---

## Notes

- The scanner implements the **longest match principle**
- **Pattern matching priority** is strictly followed as per specification
- **Error recovery** allows scanning to continue after errors
- Both scanners produce identical output for valid programs
- Symbol table tracks identifier usage and frequency

---

## References

- Course Textbook: Compiler Design Principles
- JFlex Manual: https://jflex.de/manual.html
- Compiler Design: https://www.geeksforgeeks.org/introduction-of-compiler-design/
