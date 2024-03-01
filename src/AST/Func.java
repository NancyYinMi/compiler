package AST;

import MidCode.MidCode;
import Table.Symbol;
import Table.SymbolTable;

import java.util.ArrayList;

public class Func extends Node {
    private String type = "";
    private Id id;
    private ArrayList<Fparam> parameters;
    private Block block;
    private boolean isMain = false;

    public Func() {

    }

    public Func(String type, Id id, ArrayList<Fparam> parameters, Block block, boolean isMain) {
        this.type = type;
        this.id = id;
        this.parameters = parameters;
        this.block = block;
        this.isMain = isMain;
    }

    @Override
    public void midCode() {
        if (isMain == false) {
            Symbol symbol = new Symbol();
            symbol.setType(3);//func
            symbol.setName(id.getName());
            symbol.setBack(type);
            funcTable.add(symbol);
        }
        SymbolTable newTable = new SymbolTable();
        newTable.setPrev(numberTable);
        numberTable = newTable;
        int level = block.getLevel();
        //MidCode midCode = new MidCode(MidCode.operator.LABLE, String.valueOf(level), "start");

        addMidCode(new MidCode(MidCode.operator.LABLE, String.valueOf(level), "start"));
        if (isMain == true) {
            //MidCode midCode1 = new MidCode(MidCode.operator.MAIN, "main");
            addMidCode(new MidCode(MidCode.operator.MAIN, "main"));
        } else {
            MidCode midCode1 = new MidCode(MidCode.operator.FUNC, id.getName(), type);
            addMidCode(midCode1);
            for (Fparam fparam : parameters) {
                fparam.midCode();
            }
        }
        block.midCode(level);
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
