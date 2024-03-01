package AST;

import MidCode.MidCode;

public class Break extends Stmt {
    @Override
    public void midCode() {
        int peek = loop.peek();
        MidCode midCode = new MidCode(MidCode.operator.GOTO, "loop" + peek +"end");
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
