package AST;

import MidCode.MidCode;
import Token.Token;

import java.util.ArrayList;

public class Constant extends Expr {
    public Constant(Token token) {
        super(token);
    }

    /*
    Constant(ArrayList<midCode> midCodes, int num)
     */

    @Override
    public int calculate() {
        int ans = Integer.parseInt(getToken().getValue());
        return ans;
    }

    @Override
    public void midCode() {

    }

    @Override
    public String toString() {
        return getToken().getValue();
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
