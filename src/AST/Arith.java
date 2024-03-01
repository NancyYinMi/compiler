package AST;

import MidCode.MidCode;
import Token.Token;

public class Arith extends Expr {
    private Expr expr1;
    public Expr expr2;

    public Arith(Token token, Expr expr1, Expr expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        super.setToken(token);
    }

    @Override
    public Expr reduce() {
        //System.out.println(super.getToken() == null);
        //System.out.println(super.getToken().getValue());
        Temp temp = new Temp(super.getToken());
        String op = super.getToken().getValue();
        MidCode.operator operator = MidCode.operator.DEGUB;
        if (op.equals("+")) {
            operator = MidCode.operator.ADD;
        } else if (op.equals("-")) {
            operator = MidCode.operator.MINU;
        } else if (op.equals("*")) {
            operator = MidCode.operator.MULT;
        } else if (op.equals("/")) {
            operator = MidCode.operator.DIV;
        } else if (op.equals("%")) {
            operator = MidCode.operator.MOD;
        }
        //原本下一行是：
        //Expr expr11 = expr1.reduce();
        //Expr expr22 = expr2.reduce();
        //MidCode midCode = new MidCode(operator, temp.toString(), expr1.reduce().toString(), expr2.reduce().toString());

        MidCode midCode = new MidCode(operator, temp.toString(), treat(expr1.reduce()), treat(expr2.reduce()));
        addMidCode(midCode);
        return temp;
    }

    @Override
    public int calculate() {
        int left = expr1.calculate();
        int right = expr2.calculate();
        if (getToken().getValue().equals("+")) {
            return left + right;
        } else if (getToken().getValue().equals("-")) {
            return left - right;
        } else if (getToken().getValue().equals("*")) {
            return left * right;
        } else if (getToken().getValue().equals("/")) {
            return left / right;
        } else if (getToken().getValue().equals("%")) {
            return left % right;
        }
        return 0;
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
