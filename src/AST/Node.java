package AST;

import MidCode.MidCode;
import Table.SymbolTable;

import java.util.ArrayList;
import java.util.Stack;

public class Node {
    public static ArrayList<MidCode> midCodes = new ArrayList<>();
    public static SymbolTable numberTable = new SymbolTable();
    public static SymbolTable funcTable = new SymbolTable();
    public static ArrayList<String> lines = new ArrayList<>();
    public static int label = 0;//标签数量
    public static int jumpNum = 0;//跳转数量
    public static Stack<Integer> loop = new Stack<>();
    public static ArrayList<Stmt> forStmt = new ArrayList<>();

    public int newLabel() {
        label++;
        return label;
    }

    public void addMidCode(MidCode midCode) {
        midCodes.add(midCode);
    }

    public void addLabelMidCode(int label) {
        MidCode midCode = new MidCode(MidCode.operator.LABLE, String.valueOf(label));
    }


    public void midCode() {

    }

    public static ArrayList<String> getLines() {
        return lines;
    }

    public static ArrayList<MidCode> getMidCodes() {
        return midCodes;
    }

    public void addLine(String line) {
        lines.add(line);
    }


}
