package MidCode;

import AST.Or;
import Mips.MipsCode;

public class MidCode {
    public enum operator{
        ADD, //+
        MINU, //-
        MULT, //*
        DIV, // /
        LSS, //<
        LEQ, //<=
        GRE, //>
        GEQ, //>=
        EQ, //==
        NEQ, //!=
        ASSIGN, // =
        GOTO, //无条件跳转
        JUMP, //跳转标记
        BZ, //不满足跳转条件
        BNZ, //满足跳转条件
        CALL, //函数调用
        PUSH, //传参
        RETURN, //函数返回语句
        RETURNVALUE, //返回值
        GETINT, //读语句
        PRINT, //写语句
        LABLE, //标签
        CONST, //常量
        ARRAY, //数组
        VAR, //变量
        FUNC, //函数定义
        PARAM, //函数参数
        GETARRAY, //t = a[]
        PUTARRAY, //a[] = t
        EXIT,
        DEGUB,
        MAIN,
        MOD, //%
    }
    public operator operation;
    private String left = null;
    private String right = null;
    private String ans = null;

    public MidCode(operator operator, String ans, String left, String right) {
        this.operation = operator;
        this.ans = ans;
        this.left = left;
        this.right = right;
    }

    public MidCode(operator operator, String ans) {
        this.ans = ans;
        this.operation = operator;
    }

    public MidCode(operator operator, String ans, String left) {
        this.operation = operator;
        this.left = left;
        this.ans = ans;
    }

    public String toString() {
        String s = "";
        if (operation == operator.ADD) {
            s = ans + " = " + left + " + " + right;
            return s;
        } else if (operation == MidCode.operator.MINU) {
            s = ans + " = " + left + " - " + right;
            return s;
        } else if (operation == MidCode.operator.MULT) {
            s = ans + " = " + left + " * " + right;
            return s;
        } else if (operation == MidCode.operator.DIV) {
            s = ans + " = " + left + " / " + right;
            return s;
        } else if (operation == MidCode.operator.MOD) {
            s = ans + " = " + left + " % " + right;
            return s;
        } else if (operation == MidCode.operator.LSS) {
            s = ans + " = " + left + " < " + right;
            return s;
        } else if (operation == MidCode.operator.LEQ) {
            s = ans + " = " + left + " <= " + right;
            return s;
        } else if (operation == MidCode.operator.GRE) {
            s = ans + " = " + left + " > " + right;
            return s;
        } else if (operation == MidCode.operator.GEQ) {
            s = ans + " = " + left + " >= " + right;
            return s;
        } else if (operation == MidCode.operator.EQ) {
            s = ans + " = " + left + " == " + right;
            return s;
        } else if (operation == MidCode.operator.NEQ) {
            s = ans + " = " + left + " != " + right;
            return s;
        } else if (operation == MidCode.operator.ASSIGN) {
            s = ans + " = " + left;
            return s;
        } else if (operation == MidCode.operator.GOTO) {
            s = "goto " + ans;
            return s;
        } else if (operation == MidCode.operator.BZ) {
            s = "if" + left + " == 0 then goto " + ans;
            return s;
        } else if (operation == MidCode.operator.BNZ) {
            return null;
        } else if (operation == MidCode.operator.JUMP) {
            if (left == null) {
                s = "jump " + ans;
            } else {
                s = "jump " + ans + " - " + left;
            }
            return s;
        } else if (operation == MidCode.operator.PUSH) {
            if (left == null) {
                s = "push " + ans;
            } else {
                s = "push " + ans + "[" + left + "]" + "[" + right + "]";
            }
            return s;
        } else if (operation == MidCode.operator.CALL) {
            s = "call " + ans;
            return s;
        } else if (operation == MidCode.operator.RETURN) {
            if (ans == null) {
                s = "return null";
            } else {
                s = "return " + ans;
            }
            return s;
        } else if (operation == MidCode.operator.RETURNVALUE) {
            s = "returnValue " + ans;
            return s;
        } else if (operation == MidCode.operator.GETINT) {
            s = "getint " + ans;
            return s;
        } else if (operation == MidCode.operator.PRINT) {
            if (left.equals("string")) {
                s = "print \"" + ans + "\"";
            } else {
                s = "print " + ans;
            }
            return s;
        } else if (operation == MidCode.operator.LABLE) {
            s = "label " + ans + " " + left;
            return s;
        } else if (operation == MidCode.operator.CONST) {
            s = "const int " + ans + " = " + left;
            return s;
        } else if (operation == MidCode.operator.ARRAY) {
            if (right == null) {
                s = "array int " + ans + "[" + left + "]";
            } else {
                s = "array int " + ans + "[" + left + "]" + "[" + right + "]";
            }
            return s;
        } else if (operation == MidCode.operator.VAR) {
            if (left == null) {
                s = "var int " + ans;
            } else {
                s = "var int " + ans + " = " + left;
            }
            return s;
        } else if (operation == MidCode.operator.FUNC) {
            s = left + " " + ans + "()";
            return s;
        } else if (operation == MidCode.operator.PARAM) {
            if (left.equals("0")) {
                s = "para int " + ans;
            } else if (left.equals("1")) {
                s = "para int " + ans + "[]";
            } else {
                s = "para int " + ans + "[][" + right + "]";
            }
            return s;
        } else if (operation == MidCode.operator.MAIN) {
            s = "main";
            return s;
        } else if (operation == MidCode.operator.GETARRAY) {
            s = ans + " = " + left + "[" + right + "]";
            return s;
        } else if (operation == MidCode.operator.PUTARRAY) {
            s = ans + "[" + left + "] = " + right;
            return s;
        } else if (operation == MidCode.operator.EXIT) {
            s = "exit";
            return s;
        } else {
            return "";
        }
    }

    public operator getOperator() {
        return operation;
    }

    public String getAns() {
        return ans;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }
}
