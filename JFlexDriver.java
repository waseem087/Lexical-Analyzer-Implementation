import java.io.*;
import java.util.*;

/**
 * JFlex Scanner Driver
 * Main program to run the JFlex-generated scanner
 */
public class JFlexDriver {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java JFlexDriver <input_file>");
            return;
        }
        
        try {
            String filename = args[0];
            FileReader reader = new FileReader(filename);
            Yylex scanner = new Yylex(reader);
            
            List<Token> tokens = new ArrayList<>();
            Token token;
            
            // Scan all tokens
            while ((token = scanner.yylex()) != null) {
                tokens.add(token);
            }
            
            // Display tokens
            System.out.println("\n========== TOKENS ==========");
            for (Token t : tokens) {
                System.out.println(t);
            }
            System.out.println("============================");
            
            // Display statistics
            System.out.println("\n========== SCANNING STATISTICS ==========");
            System.out.println("Total Tokens: " + tokens.size());
            System.out.println("Lines Processed: " + (scanner.yyline + 1));
            System.out.println("Comments Removed: " + scanner.getTotalComments());
            
            Map<TokenType, Integer> counts = scanner.getTokenCounts();
            System.out.println("\nToken Type Distribution:");
            System.out.println("-".repeat(45));
            for (Map.Entry<TokenType, Integer> entry : counts.entrySet()) {
                System.out.printf("%-25s: %d%n", entry.getKey(), entry.getValue());
            }
            System.out.println("=".repeat(45));
            
            // Display symbol table
            scanner.getSymbolTable().display();
            
            // Display errors
            scanner.getErrorHandler().displayErrors();
            
            reader.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + args[0]);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
