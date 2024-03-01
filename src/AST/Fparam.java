package AST;

import MidCode.MidCode;
import Table.Symbol;

public class Fparam extends Node {
    private Id id;
    private int level;
    private Expr expr;

    public Fparam() {

    }

    public Fparam(Id id, int level, Expr expr) {
        this.id = id;
        this.level = level;
        this.expr = expr;
    }

    @Override
    public void midCode() {
        MidCode midCode;
        if (level == 0) {
            midCode = new MidCode(MidCode.operator.PARAM, id.getName(), "0");
        } else if (level == 1) {
            midCode = new MidCode(MidCode.operator.PARAM, id.getName(), "1");
            Symbol symbol = new Symbol();
            symbol.setType(2);
            symbol.setName(id.getName());
            symbol.setLevel(1);//var array
            numberTable.add(symbol);
        } else {
            int value = expr.calculate();
            midCode = new MidCode(MidCode.operator.PARAM, id.getName(), "2", String.valueOf(value));
            Symbol symbol = new Symbol();
            symbol.setName(id.getName());
            symbol.setLevel(2);
            symbol.setYLength(value);
            symbol.setType(2);
            numberTable.add(symbol);
        }
        addMidCode(midCode);
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
