package AST;


import MidCode.MidCode;

public class Return extends Stmt {
    private Expr expr = null;

    public Return() {

    }

    public Return(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void midCode() {
        if (expr == null) {
            MidCode midCode = new MidCode(MidCode.operator.RETURN, null);
            addMidCode(midCode);
        } else {

            MidCode midCode = new MidCode(MidCode.operator.RETURN, treat(expr.reduce()));
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
