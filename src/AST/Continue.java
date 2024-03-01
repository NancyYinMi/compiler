package AST;

import MidCode.MidCode;

public class Continue extends Stmt {
    @Override
    public void midCode() {
        if (forStmt.size() != 0) {
            if (forStmt.get(forStmt.size() - 1) != null) {
                forStmt.get(forStmt.size() - 1).midCode();
            }
        }
        MidCode midCode = new MidCode(MidCode.operator.GOTO, "loop" + loop.peek() + "begin");
        addMidCode(midCode);
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
