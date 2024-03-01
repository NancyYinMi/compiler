package AST;

import MidCode.MidCode;

public class Def extends Node{
    private Lval lval;

    public Def() {

    }

    public Def(Lval lval) {
        this.lval = lval;
    }

    public void setLval(Lval lval) {
        this.lval = lval;
    }

    public Lval getLval() {
        return this.lval;
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
