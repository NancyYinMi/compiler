package AST;

import MidCode.MidCode;
import Table.Symbol;
import Token.Token;

import java.util.ArrayList;

public class VerDef extends Def{
    private ArrayList<ArrayList<Expr>> initial;

    public VerDef(Lval lval, ArrayList<ArrayList<Expr>> exprs) {
        this.initial = exprs;
        super.setLval(lval);
    }

    @Override
    public void midCode() {
        Token token = getLval().getToken();
        if (getLval() instanceof Id) {
            if (initial.size() == 0) {
                MidCode midCode = new MidCode(MidCode.operator.VAR, token.getValue(), null, null);
                addMidCode(midCode);
            } else {
                Expr expr = initial.get(0).get(0);
                //MidCode midCode = new MidCode(MidCode.operator.VAR, token.getValue(), expr.reduce().toString(), null);
                //System.out.println(expr == null);
                //System.out.println(token.getValue());
                //System.out.println(expr.getToken() == null);

                MidCode midCode = new MidCode(MidCode.operator.VAR, token.getValue(), treat(expr.reduce()), null);
                addMidCode(midCode);
            }
        } else if (getLval() instanceof Array) {
            Array array = (Array) getLval();
            Expr expr1 = array.getExpr1();
            Expr expr2 = array.getExpr2();
            int l1 = expr1.calculate();
            int l2 = 0;
            if (array.getLevel() == 1) {
                MidCode midCode = new MidCode(MidCode.operator.ARRAY, token.getValue(), String.valueOf(l1), null);
                addMidCode(midCode);
                Symbol symbol = new Symbol();
                symbol.setName(token.getValue());
                symbol.setXLength(l1);
                symbol.setLevel(1);
                symbol.setType(2);
                numberTable.add(symbol);
            } else {
                l2 = expr2.calculate();
                MidCode midCode = new MidCode(MidCode.operator.ARRAY, token.getValue(), String.valueOf(l1), String.valueOf(l2));
                addMidCode(midCode);
                Symbol symbol = new Symbol();
                symbol.setName(token.getValue());
                symbol.setXLength(l1);
                symbol.setYLength(l2);
                symbol.setType(2);
                symbol.setLevel(2);
                numberTable.add(symbol);
            }
            for (int i = 0; i < initial.size(); i++) {
                for (int j = 0; j < initial.get(i).size(); j++) {

                    MidCode midCode1 = new MidCode(MidCode.operator.PUTARRAY, token.getValue(), String.valueOf(i * l2 + j), treat(initial.get(i).get(j).reduce()));
                    addMidCode(midCode1);
                }
            }
        } else {
            error();
        }
    }

    private void error() {

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
