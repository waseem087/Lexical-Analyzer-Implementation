import java.util.*;

/**
 * Symbol Table
 * Stores information about identifiers
 */
public class SymbolTable {
    
    private static class SymbolEntry {
        String name;
        String type;
        int firstLine;
        int firstColumn;
        int frequency;
        
        SymbolEntry(String name, String type, int line, int column) {
            this.name = name;
            this.type = type;
            this.firstLine = line;
            this.firstColumn = column;
            this.frequency = 1;
        }
    }
    
    private Map<String, SymbolEntry> table;
    
    public SymbolTable() {
        table = new LinkedHashMap<>();
    }
    
    /**
     * Add or update an identifier in the symbol table
     */
    public void addIdentifier(String name, int line, int column) {
        if (table.containsKey(name)) {
            table.get(name).frequency++;
        } else {
            table.put(name, new SymbolEntry(name, "IDENTIFIER", line, column));
        }
    }
    
    /**
     * Check if identifier exists
     */
    public boolean contains(String name) {
        return table.containsKey(name);
    }
    
    /**
     * Get frequency of identifier
     */
    public int getFrequency(String name) {
        return table.containsKey(name) ? table.get(name).frequency : 0;
    }
    
    /**
     * Display symbol table
     */
    public void display() {
        System.out.println("\n========== SYMBOL TABLE ==========");
        System.out.printf("%-30s %-15s %-15s %-10s%n", 
                         "Identifier", "Type", "First Occurrence", "Frequency");
        System.out.println("=".repeat(75));
        
        for (SymbolEntry entry : table.values()) {
            System.out.printf("%-30s %-15s (L:%d, C:%d)%7s %d%n",
                            entry.name, 
                            entry.type,
                            entry.firstLine,
                            entry.firstColumn,
                            "",
                            entry.frequency);
        }
        System.out.println("=".repeat(75));
        System.out.println("Total Unique Identifiers: " + table.size());
    }
    
    /**
     * Get total number of unique identifiers
     */
    public int size() {
        return table.size();
    }
    
    /**
     * Clear the symbol table
     */
    public void clear() {
        table.clear();
    }
}
