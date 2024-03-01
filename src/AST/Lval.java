package AST;

import Token.Token;

public class Lval extends Expr{
    //private Token token;

    public Lval() {

    }

    public Lval(Token token) {
        super(token);
    }

    public Token getToken() {
        return super.getToken();
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
