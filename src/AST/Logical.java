package AST;

import MidCode.MidCode;
import Token.Token;

public class Logical extends Expr {
    private Expr expr1;
    private Expr expr2;

    public Logical(Token token, Expr expr1, Expr expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        setToken(token);
    }

    @Override
    public void midCode() {

    }

    @Override
    public Expr reduce() {
        Temp temp = new Temp(getToken());
        String op = temp.getToken().getValue();
        MidCode.operator debug = MidCode.operator.DEGUB;
        if (op.equals("<")) {
            debug = MidCode.operator.LSS;
        } else if (op.equals("<=")) {
            debug = MidCode.operator.LEQ;
        } else if (op.equals(">")) {
            debug = MidCode.operator.GRE;
        } else if (op.equals(">=")) {
            debug = MidCode.operator.GEQ;
        } else if (op.equals("==")) {
            debug = MidCode.operator.EQ;
        } else if (op.equals("!=")) {
            debug = MidCode.operator.NEQ;
        }

        MidCode midCode = new MidCode(debug, temp.toString(), treat(expr1.reduce()), treat(expr2.reduce()));
        addMidCode(midCode);
        return temp;
    }

    @Override
    public void addLabelMidCode(int label) {

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
