package AST;

import MidCode.MidCode;

public class GetInt extends Stmt {
    private Lval lval;

    public GetInt() {

    }

    public GetInt(Lval lval) {
        this.lval = lval;
    }

    @Override
    public void midCode() {
        if (lval instanceof Id) {
            MidCode midCode = new MidCode(MidCode.operator.GETINT, lval.reduce().getToken().getValue());
            addMidCode(midCode);
        } else {
            Temp temp = new Temp(null);
            MidCode midCode = new MidCode(MidCode.operator.GETINT, temp.toString());
            MidCode midCode1 = new MidCode(MidCode.operator.PUTARRAY, lval.getToken().getValue(), lval.toString(), temp.toString());
            addMidCode(midCode);
            addMidCode(midCode1);
        }
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
