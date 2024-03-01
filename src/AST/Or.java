package AST;

import MidCode.MidCode;

import java.util.ArrayList;

public class Or extends Node {
    private ArrayList<And> ands;
    private int jump;
    public Or(ArrayList<And> ands) {
        this.ands = ands;
    }

    @Override
    public void midCode() {
    }

    public void midCode(int k) {
        jumpNum++;
        jump = jumpNum;
        for (And and : ands) {
            and.midCode(jump);
        }
        MidCode midCode = new MidCode(MidCode.operator.GOTO, "jump" + k);
        MidCode midCode1 = new MidCode(MidCode.operator.JUMP, String.valueOf(jump));
        addMidCode(midCode);
        addMidCode(midCode1);
    }

    public void midCode(int k, boolean forTrue) {
        jumpNum++;
        jump = jumpNum;
        for (And and : ands) {
            and.midCode(jump);
        }
        MidCode midCode = new MidCode(MidCode.operator.GOTO, "loop" + k + "end");
        MidCode midCode1 = new MidCode(MidCode.operator.JUMP, String.valueOf(jump));
        addMidCode(midCode);
        addMidCode(midCode1);
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
