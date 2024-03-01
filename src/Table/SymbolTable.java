package Table;

import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<Symbol> symbols = new ArrayList<>();
    private SymbolTable prev = null;//前序符号表
    private SymbolTable next = null;//后序符号表
    private int index = 0;

    public void add(Symbol symbol) {
            symbols.add(symbol);
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    public void setPrev(SymbolTable prev) {
        this.prev = prev;
    }

    public void setIndex(int number) {
        this.index = number;
    }

    public SymbolTable getPrev() {
        return prev;
    }

    public int getIndex() {
        return index;
    }

    public boolean checkName(String name) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public boolean contain(String name) {
        for (Symbol symbol : symbols) {
            if (symbol.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public boolean contain(String name, boolean isInFunc) {
        if (isInFunc) {
            for (Symbol symbol : symbols) {
                if (symbol.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
        ArrayList<SymbolTable> symbolTables = new ArrayList<>();
        symbolTables.add(this);
        SymbolTable symbolTable = this;
        while (symbolTable.getPrev() != null) {
            SymbolTable p = symbolTable.getPrev();
            symbolTables.add(p);
            symbolTable = symbolTable.getPrev();
        }
        for (SymbolTable symbolTable1 : symbolTables) {
            for (Symbol symbol : symbolTable1.getSymbols()) {
                if (symbol.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Symbol getFunc(String funName) {
        ArrayList<SymbolTable> symbolTables = new ArrayList<>();
        symbolTables.add(this);
        SymbolTable symbolTable = this;
        while (symbolTable.getPrev() != null) {
            SymbolTable p = symbolTable.getPrev();
            symbolTables.add(p);
            symbolTable = symbolTable.getPrev();
        }
        for (SymbolTable symbolTable1 : symbolTables) {
            for (Symbol symbol : symbolTable1.getSymbols()) {
                if (symbol.getName().equals(funName) && symbol.getType() == 3) {
                    return symbol;
                }
            }
        }
        return null;
    }

    public Symbol getSymbol(String name) {
        ArrayList<SymbolTable> symbolTables = new ArrayList<>();
        symbolTables.add(this);
        SymbolTable symbolTable = this;
        while (symbolTable.getPrev() != null) {
            SymbolTable p = symbolTable.getPrev();
            symbolTables.add(p);
            symbolTable = symbolTable.getPrev();
        }
        for (SymbolTable symbolTable1 : symbolTables) {
            for (Symbol symbol : symbolTable1.getSymbols()) {
                if (symbol.getName().equals(name)) {
                    return symbol;
                }
            }
        }
        return null;
    }
}
