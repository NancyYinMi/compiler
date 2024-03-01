package AST;
import MidCode.MidCode;

import java.util.ArrayList;

public class Program extends Node {
    private ArrayList<Decl> decls;
    private ArrayList<Func> funcs;
    private ArrayList<MidCode> midCodes = new ArrayList<>();

    public Program(ArrayList<Decl> decls, ArrayList<Func> funcs) {
        this.funcs = funcs;
        this.decls = decls;
    }

    public void midCode() {
        for (Decl decl : decls) {
            decl.midCode();
        }
        for (Func func : funcs) {
            func.midCode();
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
