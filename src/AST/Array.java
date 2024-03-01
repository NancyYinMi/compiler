package AST;

import MidCode.MidCode;
import Table.Symbol;
import Table.SymbolTable;
import Token.Token;

public class Array extends Lval{
    //private Token token;
    private Expr expr1 = null;
    private Expr expr2 = null;
    private int level = 0;
    private boolean headAddr = false;
    private Expr hold;
    private String num2;

    public Array() {

    }

    public Array(Token token, Expr expr1) {
        super(token);
        this.expr1 = expr1;
        this.level = 1;
    }

    public Array(Token token, Expr expr1, Expr expr2) {
        super(token);
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.level = 2;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int calculate() {
        SymbolTable numTable = numberTable;
        Symbol array = null;
        while (numTable != null) {
            if (numTable.contain(getToken().getValue())) {
                array = numTable.getSymbol(getToken().getValue());
                break;
            }
            numTable = numTable.getPrev();
        }
        int i = 0;
        int j = 0;
        if (level == 1) {
            j = expr1.calculate();
        } else if (level == 2) {
            i = expr2.calculate();
            j = expr1.calculate();
        }
        return array.getArrayValue(i * array.getYLength() + j);
    }

    @Override
    public Expr reduce() {
        if (level == 1) {
            SymbolTable numTable = numberTable;
            Symbol array = null;
            while (numTable != null) {
                if (numTable.contain(getToken().getValue())) {
                    array = numTable.getSymbol(getToken().getValue());
                    break;
                }
                numTable = numTable.getPrev();
            }
            if (array.getYLength() != 0) {
                headAddr = true;
                hold = expr1.reduce();
                num2 = String.valueOf(array.getYLength());
                return this;
            } else {
                Temp temp = new Temp(getToken());
                MidCode midCode = new MidCode(MidCode.operator.GETARRAY, temp.toString(), getToken().getValue(), treat(expr1.reduce()));
                addMidCode(midCode);
                return temp;
            }
        } else {
            Temp temp1 = new Temp(getToken());
            Temp temp2 = new Temp(getToken());
            Temp temp3 = new Temp(getToken());
            SymbolTable numTable = numberTable;
            Symbol array = null;
            while (numTable != null) {
                if (numTable.contain(getToken().getValue())) {
                    array = numTable.getSymbol(getToken().getValue());
                    break;
                }
                numTable = numTable.getPrev();
            }
            int y = array.getYLength();

            MidCode midCode = new MidCode(MidCode.operator.MULT, temp1.toString(), treat(expr1.reduce()), String.valueOf(y));
            MidCode midCode1 = new MidCode(MidCode.operator.ADD, temp2.toString(), temp1.toString(), treat(expr2.reduce()));
            MidCode midCode2 = new MidCode(MidCode.operator.GETARRAY, temp3.toString(), getToken().getValue(), temp2.reduce().toString());
            addMidCode(midCode);
            addMidCode(midCode1);
            addMidCode(midCode2);
            return temp3;
        }
    }

    @Override
    public String toString() {
        //System.out.println("Array");
        if (level == 1) {
            //System.out.println(expr1.getToken().getValue());
            if (headAddr) {
                return getToken().getValue();
            }
            return treat(expr1.reduce());
        } else {
            Temp temp1 = new Temp(getToken());
            Temp temp2 = new Temp(getToken());
            SymbolTable numTable = numberTable;
            Symbol array = null;
            while (numTable != null) {
                if (numTable.contain(getToken().getValue())) {
                    array = numTable.getSymbol(getToken().getValue());
                    break;
                }
                numTable = numTable.getPrev();
            }
            int y = array.getYLength();
            MidCode midCode = new MidCode(MidCode.operator.MULT, temp1.toString(), treat(expr1.reduce()), String.valueOf(y));
            MidCode midCode1 = new MidCode(MidCode.operator.ADD, temp2.toString(), temp1.toString(), treat(expr2.reduce()));
            addMidCode(midCode);
            addMidCode(midCode1);
            return temp2.toString();
        }
    }

    public Expr getExpr1() {
        return this.expr1;
    }

    public Expr getExpr2() {
        return this.expr2;
    }

    public int getLevel() {
        if (expr2 == null) {
            return 1;
        } else {
            return 2;
        }
    }

    public Expr getHold() {
        return this.hold;
    }

    public String getNum2() {
        return this.num2;
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
