package Table;

import java.util.ArrayList;

public class Symbol {
    private int type = 0;//const = 1, var = 2, func = 3
    private String name = "";//标识符的名字
    private ArrayList<Symbol> parameters = new ArrayList<>();//func的参数
    private int level = 0;//维度
    private int back = 0;//func == 3 : int = i, void = 2;
    private int value = 0;//变量的具体数值
    private int xLength = 0;//一维数组的维度。
    private int yLength = 0;//二维数组第二个维度
    private int position = 0;//标识符行数
    private ArrayList<Integer> values;
    private int offset = 0;
    private boolean isPointer = false;

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setParameters(ArrayList<Symbol> symbols) {
        this.parameters = symbols;
    }

    public void setYLength(int yLength) {
        this.yLength = yLength;
    }

    public void setXLength(int xLength) {
        this.xLength = xLength;
    }

    public void setBack(String back) {
        if (back.equals("int")) {
            this.back = 1;
        } else if (back.equals("void")) {
            this.back = 2;
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }

    public ArrayList<Symbol> getParameters() {
        return parameters;
    }

    public int getLevel() {
        return level;
    }

    public int getBack() {
        return back;
    }

    public ArrayList<Integer> getValues() {
        return this.values;
    }

    public int getXLength() {
        return xLength;
    }

    public int getYLength() {
        return yLength;
    }

    public int getArrayValue(int index) {
        if (index < values.size()) {
            return values.get(index);
        } else {
            return 0;
        }
    }

    public Symbol() {

    }

    public Symbol(int type, String name, int offset) {
        this.type = type;//const = 1, var = 2, func = 3
        this.name = name;
        this.offset = offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isPointer() {
        return isPointer;
    }

    public void setPointer(boolean pointer) {
        isPointer = pointer;
    }
}
