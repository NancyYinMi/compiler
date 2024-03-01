package AST;

import MidCode.MidCode;
import Table.SymbolTable;

import java.util.ArrayList;

public class Block extends Stmt{
    private ArrayList<BlockItem> blockItems = new ArrayList<>();
    private static int level = 0;
    private int flag;

    public Block() {

    }

    public Block(ArrayList<BlockItem> blockItems) {
        this.blockItems = blockItems;
    }

    @Override
    public void midCode() {
        SymbolTable numTable = new SymbolTable();
        numTable.setPrev(numberTable);
        numberTable = numTable;
        flag = level + 1;
        level++;
        MidCode midCode = new MidCode(MidCode.operator.LABLE, String.valueOf(flag), "start");
        addMidCode(midCode);
        for (BlockItem blockItem : blockItems) {
            blockItem.midCode();
        }
        MidCode midCode1 = new MidCode(MidCode.operator.LABLE, String.valueOf(flag), "end");
        addMidCode(midCode1);
        numberTable = numberTable.getPrev();
    }

    public void midCode(int level) {
        for (BlockItem blockItem : blockItems) {
            blockItem.midCode();
        }
        int hold = level;
        MidCode midCode = new MidCode(MidCode.operator.RETURN, null);
        MidCode midCode1 = new MidCode(MidCode.operator.LABLE, String.valueOf(hold), "end");
        addMidCode(midCode);
        addMidCode(midCode1);
        if (numberTable.getPrev() != null) {
            numberTable = numberTable.getPrev();
        }
        //numberTable = numberTable.getPrev();
    }

    public static int getLevel() {
        level++;
        return level;
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
