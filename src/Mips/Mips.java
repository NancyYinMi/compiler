package Mips;

import MidCode.MidCode;
import Table.Symbol;
import Table.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class Mips {
    private ArrayList<MidCode> midCodes;
    private ArrayList<String> lines;
    private ArrayList<MipsCode> mipsCodes = new ArrayList<>();
    private HashMap<String, String> lineMap = new HashMap<>();
    private int offset = 0;
    private SymbolTable numTable = new SymbolTable();
    private SymbolTable funcTable = new SymbolTable();
    private boolean isInFunc = false;
    private boolean isInMain = false;
    private ArrayList<MidCode> stack = new ArrayList<>();
    private HashMap<String, Integer> funcLength = new HashMap<>();

    public Mips(ArrayList<MidCode> midCodes, ArrayList<String> lines) {
        this.lines = lines;
        this.midCodes = midCodes;
        treatFunc();
        mips();
        for (MipsCode mipsCode : mipsCodes) {
            mipsCode.turnToString();
        }
    }

    private void treatFunc() {
        String s = null;
        int i = 0;
        int flag = 0;
        while (i < midCodes.size()) {
            MidCode midCode = midCodes.get(i);
            if (midCode.getOperator().equals(MidCode.operator.FUNC) ||
                    midCode.getOperator().equals(MidCode.operator.MAIN)) {
                break;
            }
            i++;
        }
        while (i < midCodes.size()) {
            MidCode midCode = midCodes.get(i);
            if (midCode.getOperator().equals(MidCode.operator.FUNC) ||
                midCode.getOperator().equals(MidCode.operator.MAIN)) {
                if (s != null) {
                    funcLength.put(s, flag);
                }
                s = midCode.getAns();
                flag = 0;
            }
            if (midCode.getOperator().equals(MidCode.operator.ARRAY)) {
                if (midCode.getRight() != null) {
                    flag = flag + Integer.parseInt(midCode.getLeft()) * Integer.parseInt(midCode.getRight());
                } else {
                    flag = flag + Integer.parseInt(midCode.getLeft());
                }
            }
            flag = flag + 2;
            i++;
        }
        funcLength.put(s, flag);
    }

    public ArrayList<MipsCode> getMipsCodes() {
        return mipsCodes;
    }

    private boolean inGlobal(String name) {
        SymbolTable symbolTable = numTable;
        while (symbolTable != null) {
            if (symbolTable.contain(name)) {
                if (symbolTable.getPrev() == null) {
                    return true;
                } else {
                    return false;
                }
            }
            symbolTable = symbolTable.getPrev();
        }
        return false;
    }

    private int getOffset(String name) {
        int ans = 1;//-1?
        SymbolTable symbolTable = numTable;
        while (symbolTable != null) {
            if (symbolTable.contain(name)) {
                ans = symbolTable.getSymbol(name).getOffset();
                return symbolTable.getSymbol(name).getOffset();
            }
            symbolTable = symbolTable.getPrev();
        }
        return ans;
    }

    private void ldNormal(String name) {
        if (!numTable.contain(name)) {
            Symbol symbol = new Symbol(2, name, offset);
            numTable.add(symbol);
            offset++;
        }
    }
    
    private void ldNormal(String name, boolean isPointer) {
        if (!numTable.contain(name)) {
            //System.out.println("offset = " + offset);
            Symbol symbol = new Symbol(2, name, offset);
            symbol.setLevel(1);
            symbol.setPointer(isPointer);
            numTable.add(symbol);
            offset++;
        }
        /*for (Symbol symbol : numTable.getSymbols()) {
            System.out.println(symbol.getName() + " " + symbol.isPointer());
        }*/
    }

    private void ldNormal(String name, int length) {
        if (numTable.getPrev() == null) {
            Symbol symbol = new Symbol(2, name, offset);
            symbol.setLevel(1);
            offset = offset + length;
            numTable.add(symbol);
        } else {
            offset = offset + length - 1;
            Symbol symbol = new Symbol(2, name, offset);
            symbol.setLevel(1);
            numTable.add(symbol);
            offset++;
        }
    }

    private void ldValue(String name, String reg, boolean table) {
        if ((name.charAt(0) >='0' && name.charAt(0) <= '9') || (name.charAt(0) == '-' && name.length() >= 2 && name.charAt(1) >= '0' && name.charAt(1) <= '9')) {
            MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, reg, "", "", Integer.parseInt(name));
            mipsCodes.add(mipsCode);


        } else {
            if (table) {
                ldNormal(name);
            }
            int offset1 = getOffset(name);
            if (inGlobal(name) == true) {
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.lw, reg, "$gp", "", offset1 * 4);
                mipsCodes.add(mipsCode);
            } else {
                //System.out.println("offset = " + offset1);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.lw, reg, "$fp", "", offset1 * -4);
                mipsCodes.add(mipsCode);
            }
        }
    }

    private void strValue(String name, String reg, boolean table) {
        if (table == true) {
            ldNormal(name);
        }
        int offset1 = getOffset(name);
        if (inGlobal(name)) {
            MipsCode mipsCode = new MipsCode(MipsCode.Operator.sw, reg, "$gp", "", 4 * offset1);
            mipsCodes.add(mipsCode);
        } else {
            MipsCode mipsCode = new MipsCode(MipsCode.Operator.sw, reg, "$fp", "", -4 * offset1);
            mipsCodes.add(mipsCode);
        }
    }

    private boolean isTemp(String name) {
        if (name != null) {
            if (name.length() < 2) {
                return false;
            } else {
                if (name.charAt(1) == '&') {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void ldAddr(String name, String reg) {
        SymbolTable symbolTable = numTable;
        if ((name.charAt(0) >='0' && name.charAt(0) <= '9') || (name.charAt(0) == '-' && name.length() >= 2 && name.charAt(1) >= '0' && name.charAt(1) <= '9')) {
            MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, reg, "", "", Integer.parseInt(name));
            mipsCodes.add(mipsCode);
        } else {

            while (symbolTable != null) {
                if (symbolTable.contain(name)) {
                    Symbol symbol = symbolTable.getSymbol(name);
                    if (symbol.getLevel() > 0) {
                        //System.out.println(name);
                        //System.out.println(symbol.isPointer());
                        if (isPointer(name) == true) {
                            //System.out.println(true);
                            ldValue(name, reg, false);
                        } else {
                            if (inGlobal(name)) {
                                //System.out.println("inGlobal");
                                MipsCode mipsCode = new MipsCode(MipsCode.Operator.addi, reg, "$gp", "", 4 * symbol
                                        .getOffset());
                                mipsCodes.add(mipsCode);
                            } else {
                                //System.out.println("not in");
                                MipsCode mipsCode = new MipsCode(MipsCode.Operator.addi, reg, "$fp", "", -4 * symbol
                                        .getOffset());
                                mipsCodes.add(mipsCode);
                            }
                        }
                    } else {
                        ldValue(name, reg, false);
                    }
                    break;
                }
                symbolTable = symbolTable.getPrev();
            }
        }

    }

    private boolean isPointer(String name) {
        SymbolTable symbolTable = numTable;
        while (symbolTable != null) {
            if (symbolTable.contain(name)) {
                return symbolTable.getSymbol(name).isPointer();
            }
            symbolTable = symbolTable.getPrev();
        }
        return false;
    }

    public void mips() {
        MipsCode data = new MipsCode(MipsCode.Operator.dataSeg, "");
        mipsCodes.add(data);
        for (int i = 0; i < lines.size(); i++) {
            MipsCode string = new MipsCode(MipsCode.Operator.asciizSeg, "s_" + i, lines.get(i));
            mipsCodes.add(string);
            lineMap.put(lines.get(i), "s_" + i);
        }
        MipsCode text = new MipsCode(MipsCode.Operator.textSeg, "");
        mipsCodes.add(text);
        for (int i = 0; i < midCodes.size(); i++) {
            MidCode midCode = midCodes.get(i);
            String left = midCode.getLeft();
            String right = midCode.getRight();
            String ans = midCode.getAns();
            boolean t = isTemp(ans);
            /*SymbolTable symbolTable0 = numTable;
            while (symbolTable0 != null) {
                for (Symbol symbol : symbolTable0.getSymbols()) {
                    System.out.println(midCode.toString() + ": " + symbol.getName() + " " + symbol.isPointer());
                }
                symbolTable0 = symbolTable0.getPrev();
                //flag++;
            }*/
            if (midCode.getOperator().equals(MidCode.operator.ADD)) {
                ldValue(left, "$t0", false);
                ldValue(right, "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.add, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(ans, "$t2", t);
            } else if (midCode.getOperator().equals(MidCode.operator.MINU)) {
                ldValue(left, "$t0", false);
                ldValue(right, "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sub, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", t);
            } else if (midCode.getOperator().equals(MidCode.operator.MULT)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.mult, "$t0", "$t1", "");
                mipsCodes.add(mipsCode);
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.mflo, "$t2");
                mipsCodes.add(mipsCode1);
                strValue(midCode.getAns(), "$t2", t);
            } else if (midCode.getOperator().equals(MidCode.operator.DIV)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.divop, "$t0", "$t1", "");
                mipsCodes.add(mipsCode);
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.mflo, "$t2");
                mipsCodes.add(mipsCode1);
                strValue(midCode.getAns(), "$t2", t);
            } else if (midCode.getOperator().equals(MidCode.operator.MOD)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.divop, "$t0", "$t1", "");
                mipsCodes.add(mipsCode);
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.mfhi, "$t2");
                mipsCodes.add(mipsCode1);
                strValue(ans, "$t2", isTemp(ans));
            } else if (midCode.getOperator().equals(MidCode.operator.ASSIGN)) {
                ldValue(midCode.getLeft(), "$t0", false);
                strValue(midCode.getAns(), "$t0", t);
            } else if (midCode.getOperator().equals(MidCode.operator.PUSH)) {

                stack.add(midCode);
            } else if (midCode.getOperator().equals(MidCode.operator.RETURN)) {
                if (isInMain == true) {
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, "$v0", "", "", 10);
                    MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.syscall, "");
                    mipsCodes.add(mipsCode);
                    mipsCodes.add(mipsCode1);
                } else {
                    MipsCode mipsCode = new MipsCode();
                    if (midCode.getAns() != null) {
                        ldValue(midCode.getAns(), "$v0", false);
                        mipsCode = new MipsCode(MipsCode.Operator.jr, "$ra");

                    } else {
                        mipsCode = new MipsCode(MipsCode.Operator.jr, "$ra");
                    }
                    mipsCodes.add(mipsCode);

                }
            } else if (midCode.getOperator().equals(MidCode.operator.RETURNVALUE)) {
                //String ans = midCode.getAns();
                boolean temp = isTemp(ans);
                //System.out.println(temp);
                strValue(ans, "$v0", temp);
            } else if (midCode.getOperator().equals(MidCode.operator.CALL)) {
                /*SymbolTable symbolTable = numTable;
                while (symbolTable != null) {
                    for (Symbol symbol : symbolTable.getSymbols()) {
                        System.out.println(symbol.getName() + " " + symbol.isPointer());
                    }
                    symbolTable = symbolTable.getPrev();
                }*/
                for (int j = 0; j < stack.size(); j++) {
                    MidCode midCode1 = stack.get(j);
                    ldAddr(midCode1.getAns(), "$t0");
                    if (midCode1.getLeft() != null) {
                        ldValue(midCode1.getLeft(), "$t1", false);
                        MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, "$t2", "" , "", Integer.parseInt(midCode1.getRight()) * 4);
                        mipsCodes.add(mipsCode);
                        MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.mult, "$t2", "$t1", "");
                        mipsCodes.add(mipsCode1);
                        MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.mflo, "$t2");
                        mipsCodes.add(mipsCode2);
                        MipsCode mipsCode3 = new MipsCode(MipsCode.Operator.add, "$t0", "$t0", "$t2");
                        mipsCodes.add(mipsCode3);
                    }
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.sw, "$t0", "$sp", "", -4 * j);
                    mipsCodes.add(mipsCode);
                }
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.addi, "$sp", "$sp", "", -4 * funcLength.get(ans) - 8);
                mipsCodes.add(mipsCode);
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.sw, "$ra", "$sp", "", 4);
                mipsCodes.add(mipsCode1);
                MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.sw, "$fp", "$sp", "", 8);
                mipsCodes.add(mipsCode2);
                MipsCode  mipsCode3 = new MipsCode(MipsCode.Operator.addi, "$fp", "$sp", "", 4 * funcLength.get(ans) + 8);
                mipsCodes.add(mipsCode3);
                MipsCode mipsCode4 = new MipsCode(MipsCode.Operator.jal, midCode.getAns());
                mipsCodes.add(mipsCode4);
                MipsCode mipsCode5 = new MipsCode(MipsCode.Operator.lw, "$fp", "$sp", "", 8);
                mipsCodes.add(mipsCode5);
                MipsCode mipsCode6 = new MipsCode(MipsCode.Operator.lw, "$ra", "$sp", "", 4);
                mipsCodes.add(mipsCode6);
                MipsCode mipsCode7 = new MipsCode(MipsCode.Operator.addi, "$sp", "$sp", "", 4 * funcLength.get(ans) + 8);
                mipsCodes.add(mipsCode7);
                stack.clear();
            } else if (midCode.getOperator().equals(MidCode.operator.PRINT)) {
                if (midCode.getLeft().equals("string")) {
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.la, "$a0", lineMap.get(midCode.getAns()));
                    MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.li, "$v0", "", "", 4);
                    MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.syscall, "", "", "");
                    mipsCodes.add(mipsCode);
                    mipsCodes.add(mipsCode1);
                    mipsCodes.add(mipsCode2);
                } else {
                    ldValue(ans,  "$a0", false);
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, "$v0", "", "", 1);
                    MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.syscall, null);
                    mipsCodes.add(mipsCode);
                    mipsCodes.add(mipsCode1);
                }
            } else if (midCode.getOperator().equals(MidCode.operator.GETINT)) {
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, "$v0", "", "", 5);
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.syscall, null);
                mipsCodes.add(mipsCode);
                mipsCodes.add(mipsCode1);
                strValue(midCode.getAns(), "$v0", t);
            } else if (midCode.getOperator().equals(MidCode.operator.LABLE)) {
                if (midCode.getLeft().equals("start")) {
                    /*SymbolTable symbolTable0 = numTable;
                    while (symbolTable0 != null) {
                        for (Symbol symbol : symbolTable0.getSymbols()) {
                            System.out.println(midCode.toString() + ": " + symbol.getName() + " " + symbol.isPointer());
                        }
                        symbolTable0 = symbolTable0.getPrev();
                    }*/
                    SymbolTable symbolTable = new SymbolTable();
                    symbolTable.setPrev(numTable);
                    numTable = symbolTable;


                } else if (midCode.getLeft().equals("end")) {
                    offset = offset - numTable.getSymbols().size();
                    numTable = numTable.getPrev();
                }
            } else if (midCode.getOperator().equals(MidCode.operator.FUNC)) {

                if (isInFunc == false) {
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.j, "main");
                    mipsCodes.add(mipsCode);
                    isInFunc = true;
                }
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.label, midCode.getAns());
                mipsCodes.add(mipsCode);
                offset = 0;
            } else if (midCode.getOperator().equals(MidCode.operator.PARAM)) {
                //System.out.println("here");
                if (!midCode.getLeft().equals("0")){
                    ldNormal(midCode.getAns(), true);
                } else {
                    ldNormal(midCode.getAns());
                }
                /*for (Symbol symbol : numTable.getSymbols()) {
                    System.out.println(symbol.getName() + " " + symbol.isPointer());
                }*/
            } else if (midCode.getOperator().equals(MidCode.operator.GETARRAY)) {
                ldValue(midCode.getRight(), "$t0", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sll, "$t0", "$t0", "", 2);
                mipsCodes.add(mipsCode);
                if (isPointer(midCode.getLeft()) == true) {
                    ldValue(midCode.getLeft(), "$t1", false);
                    MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.add, "$t1", "$t1", "$t0");
                    MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.lw, "$t2", "$t1", "", 0);
                    mipsCodes.add(mipsCode1);
                    mipsCodes.add(mipsCode2);
                } else {
                    if (inGlobal(midCode.getLeft()) == true) {
                        MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.add, "$t1", "$t0", "$gp");
                        MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.lw, "$t2", "$t1", "", 4 * getOffset(midCode.getLeft()));
                        mipsCodes.add(mipsCode1);
                        mipsCodes.add(mipsCode2);
                    } else {
                        MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.add, "$t1", "$t0", "$fp");
                        MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.lw, "$t2", "$t1", "", -4 * getOffset(midCode.getLeft()));
                        mipsCodes.add(mipsCode1);
                        mipsCodes.add(mipsCode2);
                    }
                }
                strValue(midCode.getAns(), "$t2", t);
            } else if (midCode.getOperator().equals(MidCode.operator.PUTARRAY)) {
                ldValue(midCode.getRight(), "$t0", false);
                ldValue(midCode.getLeft(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sll, "$t1", "$t1", "", 2);
                mipsCodes.add(mipsCode);
                if (isPointer(midCode.getAns())) {
                    ldValue(midCode.getAns(), "$t2", false);
                    MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.add, "$t2", "$t2", "$t1");
                    MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.sw, "$t0", "$t2", "", 0);
                    mipsCodes.add(mipsCode1);
                    mipsCodes.add(mipsCode2);
                } else {
                    if (inGlobal(midCode.getAns())) {
                        MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.add, "$t1", "$t1", "$gp");
                        MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.sw, "$t0", "$t1", "", 4 * getOffset(midCode.getAns()));
                        mipsCodes.add(mipsCode1);
                        mipsCodes.add(mipsCode2);
                    } else {
                        MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.addu, "$t1", "$t1", "$fp");
                        MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.sw, "$t0", "$t1", "", -4 * getOffset(midCode.getAns()));
                        mipsCodes.add(mipsCode1);
                        mipsCodes.add(mipsCode2);
                    }
                }
            } else if (midCode.getOperator().equals(MidCode.operator.CONST)) {
                ldValue(midCode.getLeft(), "$t0", false);
                strValue(midCode.getAns(), "$t0", true);
            } else if (midCode.getOperator().equals(MidCode.operator.EXIT)) {
                continue;
            } else if (midCode.getOperator().equals(MidCode.operator.VAR)) {
                if (midCode.getLeft() == null) {
                    ldNormal(midCode.getAns());
                } else {
                    ldValue(midCode.getLeft(), "$t0", false);
                    strValue(midCode.getAns(), "$t0", true);
                }
            } else if (midCode.getOperator().equals(MidCode.operator.ARRAY)) {
                if (midCode.getRight() == null) {
                    ldNormal(midCode.getAns(), Integer.parseInt(midCode.getLeft()));
                } else {
                    ldNormal(midCode.getAns(), Integer.parseInt(midCode.getLeft()) * Integer.parseInt(midCode.getRight()));
                }
            } else if (midCode.getOperator().equals(MidCode.operator.MAIN)) {
                if (isInFunc == false) {
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.j, "main");
                    mipsCodes.add(mipsCode);
                    isInFunc = true;
                }
                isInMain = true;
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.label, midCode.getAns());
                mipsCodes.add(mipsCode);
                offset = 0;
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.moveop, "$fp", "$sp");
                mipsCodes.add(mipsCode1);
                MipsCode mipsCode2 = new MipsCode(MipsCode.Operator.addi, "$sp", "$sp", "", -4 * funcLength.get("main") - 8);
                mipsCodes.add(mipsCode2);
            } else if (midCode.getOperator().equals(MidCode.operator.LSS)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.slt, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", true);
            } else if (midCode.getOperator().equals(MidCode.operator.LEQ)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sle, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", true);
            } else if (midCode.getOperator().equals(MidCode.operator.GRE)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sgt, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", true);
            } else if (midCode.getOperator().equals(MidCode.operator.GEQ)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sge, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", true);
            } else if (midCode.getOperator().equals(MidCode.operator.EQ)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.seq, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", true);
            } else if (midCode.getOperator().equals(MidCode.operator.NEQ)) {
                ldValue(midCode.getLeft(), "$t0", false);
                ldValue(midCode.getRight(), "$t1", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.sne, "$t2", "$t0", "$t1");
                mipsCodes.add(mipsCode);
                strValue(midCode.getAns(), "$t2", true);
            } else if (midCode.getOperator().equals(MidCode.operator.BZ)) {
                ldValue(midCode.getLeft(), "$t0", false);
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.li, "$t1", "", "", 0);
                MipsCode mipsCode1 = new MipsCode(MipsCode.Operator.beq, midCode.getAns(), "$t0", "$t1");
                mipsCodes.add(mipsCode);
                mipsCodes.add(mipsCode1);
            } else if (midCode.getOperator().equals(MidCode.operator.GOTO)) {
                MipsCode mipsCode = new MipsCode(MipsCode.Operator.j, midCode.getAns(), "", "");
                mipsCodes.add(mipsCode);
            } else if (midCode.getOperator().equals(MidCode.operator.JUMP)) {
                if (midCode.getLeft() != null) {
                    String label = "loop" + ans + left;
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.label, label);
                    mipsCodes.add(mipsCode);
                } else {
                    String label = "jump" + ans;
                    MipsCode mipsCode = new MipsCode(MipsCode.Operator.label, label);
                    mipsCodes.add(mipsCode);
                }
            } else {
                error();
            }
        }
    }

    private void error() {

    }
}
