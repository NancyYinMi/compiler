package AST;

import Token.Token;

public class Temp extends Expr {
    private static int level = 0;
    private int num = 0;

    public Temp(Token token) {
        super(token);
        this.num = level + 1;
        level++;
    }

    public String toString() {
        //System.out.println("Temp");
        return "t&" + num;
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
