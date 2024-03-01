package AST;

import MidCode.MidCode;

public class Assign extends Stmt {
    private Lval lval;
    private Expr expr;

    public Assign() {

    }

    public Assign(Lval lval, Expr expr) {
        this.lval = lval;
        this.expr = expr;
    }

    @Override
    public void midCode() {
        if (lval instanceof Id) {
            MidCode midCode = new MidCode(MidCode.operator.ASSIGN, lval.reduce().getToken().getValue(), treat(expr.reduce()));
            addMidCode(midCode);
        } else {
            MidCode midCode = new MidCode(MidCode.operator.PUTARRAY, lval.getToken().getValue(), lval.toString(), treat(expr.reduce()));
            addMidCode(midCode);
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
