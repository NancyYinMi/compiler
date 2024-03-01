package AST;

import Table.Symbol;
import Table.SymbolTable;
import Token.Token;

public class Id extends Lval{

    public Id() {

    }

    public Id(Token token) {
        super(token);
    }

    public String getName() {
        return getToken().getValue();
    }

    @Override
    public int calculate() {
        SymbolTable numTable = numberTable;
        Symbol symbol = new Symbol();
        symbol.setType(2);
        while (numTable != null) {
            if (numTable.contain(getToken().getValue())) {
                symbol = numberTable.getSymbol(getToken().getValue());
                break;
            }
            numTable = numTable.getPrev();
        }
        return symbol.getValue();
    }

    @Override
    public void midCode() {

    }

    private String treat(Expr expr) {
        if (expr instanceof Array) {
            Array array = (Array) expr;
            return array.toString();
        } else if (expr instanceof Temp) {
            Temp temp = (Temp) expr;
            return temp.toString();
        } else if (expr instanceof FuncR) {
            FuncR funcR = (FuncR) expr;
            return funcR.toString();
        } else {
            return expr.getToken().getValue();
        }
    }
}
