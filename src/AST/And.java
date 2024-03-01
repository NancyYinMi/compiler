package AST;

import MidCode.MidCode;

import java.util.ArrayList;

public class And extends Node {
    private ArrayList<Expr> exprs = new ArrayList<>();
    private int jump;

    public And(ArrayList<Expr> exprs) {
        this.exprs = exprs;
    }

    public void midCode(int k) {
        jump = jumpNum + 1;
        jumpNum++;
        for (Expr expr : exprs) {
            if (expr instanceof FuncR) {
                //System.out.println("here");
                MidCode midCode = new MidCode(MidCode.operator.BZ, "jump" + jump, treat(expr.reduce()));
                addMidCode(midCode);
            } else {
                MidCode midCode = new MidCode(MidCode.operator.BZ, "jump" + jump, treat(expr.reduce()));
                addMidCode(midCode);
            }

        }
        MidCode midCode = new MidCode(MidCode.operator.GOTO, "jump" + k);
        addMidCode(midCode);
        MidCode midCode1 = new MidCode(MidCode.operator.JUMP, String.valueOf(jump));
        addMidCode(midCode1);
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
