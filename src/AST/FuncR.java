package AST;

import MidCode.MidCode;
import Token.Token;

import java.util.ArrayList;

public class FuncR extends Expr {
    private ArrayList<Expr> exprs;//实参表达式表
    private ArrayList<Expr> exprReduces = new ArrayList<>();

    public FuncR(Token token, ArrayList<Expr> exprs) {
        this.exprs = exprs;
        super.setToken(token);
    }

    @Override
    public Expr reduce() {
        for (Expr expr : exprs) {
            exprReduces.add(expr.reduce());
        }
        for (Expr expr : exprReduces) {
            if (expr instanceof Array) {
                //System.out.println("flag");

                MidCode midCode = new MidCode(MidCode.operator.PUSH, treat(expr.reduce()), treat(((Array) expr).getHold()) , ((Array) expr).getNum2());
                addMidCode(midCode);
            } else {

                MidCode midCode = new MidCode(MidCode.operator.PUSH, treat(expr.reduce()));
                addMidCode(midCode);
            }
        }
        MidCode midCode = new MidCode(MidCode.operator.CALL, getToken().getValue());
        addMidCode(midCode);
        if (funcTable.getFunc(getToken().getValue()).getBack() == 2) {
            return this;
        } else {
            Temp temp = new Temp(getToken());
            MidCode midCode1 = new MidCode(MidCode.operator.RETURNVALUE, temp.toString());
            addMidCode(midCode1);
            return temp;
        }
    }

    @Override
    public void midCode() {
        for (Expr expr : exprs) {
            exprReduces.add(expr.reduce());
        }
        for (Expr expr : exprReduces) {
            if (expr instanceof Array) {
                MidCode midCode = new MidCode(MidCode.operator.PUSH, treat(expr.reduce()), treat(((Array) expr).getHold()), ((Array) expr).getNum2());
                addMidCode(midCode);
            } else {
                MidCode midCode = new MidCode(MidCode.operator.PUSH, treat(expr.reduce()));
                addMidCode(midCode);
            }
        }
        MidCode midCode = new MidCode(MidCode.operator.CALL, getToken().getValue());
        addMidCode(midCode);
        if (funcTable.getFunc(getToken().getValue()).getBack() == 2) {
        } else {
            Temp temp = new Temp(getToken());
            MidCode midCode1 = new MidCode(MidCode.operator.RETURNVALUE, temp.toString());
            addMidCode(midCode1);
        }
    }

    @Override
    public String toString() {
        //System.out.println("FuncR");
        return "RET";
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
