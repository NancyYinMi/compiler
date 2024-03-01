package AST;

import MidCode.MidCode;

public class If extends Stmt {
    private Or cond;
    private Stmt stmtIf;
    private Stmt stmtElse;
    private int jump1;
    private int jump2;

    public If() {

    }

    public If(Or cond, Stmt stmtIf) {
        this.cond = cond;
        this.stmtIf = stmtIf;
        this.stmtElse = null;
    }

    public If(Or cond, Stmt stmtIf, Stmt stmtElse) {
        this.cond = cond;
        this.stmtIf = stmtIf;
        this.stmtElse = stmtElse;
    }

    @Override
    public void midCode() {
       if (stmtElse == null) {
           jump1 = jumpNum + 1;
           jumpNum++;
           cond.midCode(jump1);
           if (stmtIf != null) {
               stmtIf.midCode();
           }
           MidCode midCode = new MidCode(MidCode.operator.JUMP, String.valueOf(jump1));
           addMidCode(midCode);
       } else {
           jump1 = jumpNum + 1;
           jumpNum++;
           jump2 = jumpNum + 1;
           jumpNum++;
           cond.midCode(jump1);
           if (stmtIf != null) {
               stmtIf.midCode();
           }
           MidCode midCode = new MidCode(MidCode.operator.GOTO, "jump" + jump2);
           MidCode midCode1 = new MidCode(MidCode.operator.JUMP, String.valueOf(jump1));
           addMidCode(midCode);
           addMidCode(midCode1);
           stmtElse.midCode();
           MidCode midCode2 = new MidCode(MidCode.operator.JUMP, String.valueOf(jump2));
           addMidCode(midCode2);
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
