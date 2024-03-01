package AST;

import MidCode.MidCode;
import Token.Token;

public class Unary extends Expr {
    private Expr expr;

    public Unary(Token op, Expr expr) {
        super.setToken(op);
        this.expr = expr;
    }

    @Override
    public void midCode() {
    }

    @Override
    public Expr reduce() {
        Token token = getToken();
        Expr ans = null;
        if (token.getValue().equals("+")) {
            ans = expr.reduce();
            //return expr.reduce();
        } else if (token.getValue().equals("-")) {
            Temp temp = new Temp(token);

            MidCode midCode = new MidCode(MidCode.operator.MINU, temp.toString(), "0", treat(expr.reduce()));
            addMidCode(midCode);
            ans = temp;
            //return temp;
        } else if (token.getValue().equals("!")) {
            Temp temp = new Temp(token);

            MidCode midCode = new MidCode(MidCode.operator.EQ, temp.toString(), "0", treat(expr.reduce()));
            addMidCode(midCode);
            ans = temp;
            //return temp;
        }
        return ans;
    }

    @Override
    public int calculate() {
        Token token = getToken();
        if (token.getValue().equals("-")) {
            return -expr.calculate();
        } else {
            return expr.calculate();
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
