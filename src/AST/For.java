package AST;

import MidCode.MidCode;

public class For extends Stmt {
    private Or cond;
    private Assign forStmt1 = null;
    private Assign forStmt2 = null;
    private Stmt stmt;
    private int jump;

    public For(Or cond, Assign forStmt1, Assign forStmt2, Stmt stmt) {
        this.cond = cond;
        this.forStmt1 = forStmt1;
        this.forStmt2 = forStmt2;
        this.stmt = stmt;


    }

    @Override
    public void midCode() {
        if (forStmt2 != null) {
            forStmt.add(forStmt2);
        } else {
            forStmt.add(new Stmt());
        }
        jump = jumpNum + 1;
        jumpNum++;
        if (forStmt1 != null) {
            forStmt1.midCode();
        }
        MidCode midCode0 = new MidCode(MidCode.operator.JUMP, String.valueOf(jump), "begin");
        addMidCode(midCode0);
        if (cond != null) {
            cond.midCode(jumpNum, true);
        }


        loop.push(jump);
        if (stmt != null) {
            stmt.midCode();
        }

        if (forStmt2 != null) {
            forStmt2.midCode();
        }
        MidCode midCode1 = new MidCode(MidCode.operator.GOTO, "loop" + jump + "begin");
        addMidCode(midCode1);
        MidCode midCode = new MidCode(MidCode.operator.JUMP, String.valueOf(jump), "end");
        addMidCode(midCode);
        if (forStmt.size() > 0) {
            forStmt.remove(forStmt.size() - 1);
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
