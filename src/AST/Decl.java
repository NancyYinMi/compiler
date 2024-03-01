package AST;

import java.util.ArrayList;

public class Decl extends BlockItem{
    private ArrayList<Def> defs;
    private boolean isConst = true;

    public Decl() {

    }

    public Decl(ArrayList<Def> defs, boolean isConst) {
        this.defs = defs;
        this.isConst = isConst;
    }

    @Override
    public void midCode() {
        for (Def def : defs) {
            def.midCode();
        }
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
