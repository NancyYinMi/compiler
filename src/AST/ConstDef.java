package AST;

import MidCode.MidCode;
import Table.Symbol;

import java.util.ArrayList;

public class ConstDef extends Def {
    private ArrayList<Expr> initial;
    private ArrayList<Integer> value = new ArrayList<>();

    public ConstDef(Lval lval, ArrayList<Expr> initial) {
        this.initial = initial;
        super.setLval(lval);
    }

    public void error() {

    }

    @Override
    public void midCode() {
        for (int i = 0; i < initial.size(); i++) {
            value.add(initial.get(i).calculate());
        }
        String identifier = getLval().getToken().getValue();
        if (getLval() instanceof Id) {
            MidCode midCode = new MidCode(MidCode.operator.CONST, identifier, String.valueOf(value.get(0)), null);
            addMidCode(midCode);
            Symbol symbol = new Symbol();
            symbol.setType(1);//const
            symbol.setName(identifier);
            symbol.setLevel(0);
            symbol.setValue(value.get(0));
            numberTable.add(symbol);
        } else if (getLval() instanceof Array) {
            Array array = (Array) getLval();
            Expr expr1 = array.getExpr1();
            Expr expr2 = array.getExpr2();
            if (array.getLevel() == 1) {
                MidCode midCode = new MidCode(MidCode.operator.ARRAY, identifier, String.valueOf(expr1.calculate()), null);
                addMidCode(midCode);
                for (int i = 0; i < value.size(); i++) {
                    MidCode midCode1 = new MidCode(MidCode.operator.PUTARRAY, identifier, String.valueOf(i), String.valueOf(value.get(i)));
                    addMidCode(midCode1);
                }
                Symbol symbol = new Symbol();
                symbol.setType(1);//const
                symbol.setName(identifier);
                symbol.setLevel(1);
                symbol.setValues(value);
                symbol.setXLength(expr1.calculate());
                numberTable.add(symbol);
            } else {
                MidCode midCode = new MidCode(MidCode.operator.ARRAY, identifier, String.valueOf(expr1.calculate()), String.valueOf(expr2.calculate()));
                addMidCode(midCode);
                for (int i = 0; i < value.size(); i++) {
                    MidCode midCode1 = new MidCode(MidCode.operator.PUTARRAY, identifier, String.valueOf(i), String.valueOf(value.get(i)));
                    addMidCode(midCode1);
                }
                Symbol symbol = new Symbol();
                symbol.setType(1);
                symbol.setName(identifier);
                symbol.setLevel(2);
                symbol.setValues(value);
                symbol.setXLength(expr1.calculate());
                symbol.setYLength(expr2.calculate());
                numberTable.add(symbol);
            }
        } else {
            error();
        }
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
