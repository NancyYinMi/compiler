package AST;

import Token.Token;

public class Expr extends Stmt{
    private Token token;

    public Expr() {

    }

    public Expr(Token token) {
        this.token = token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int calculate() {
        return 0;
    }

    public Expr reduce() {
        return this;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void midCode() {

    }

    /*@Override
    public String toString() {
        //System.out.println(token == null);
        //System.out.println("Expr");
        return token.getValue();
    }*/

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
