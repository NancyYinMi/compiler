package Mips;

public class MipsCode {
    public enum Operator{
        add,
        addu,
        addi,
        sub,
        mult,
        divop,
        mflo,
        mfhi,
        sll,
        sle,
        sne,
        sgt,
        sge,
        slt,
        seq,
        beq,
        bne,
        bgt,
        bge,
        blt,
        ble,
        j,
        jal,
        jr,
        lw,
        sw,
        syscall,
        li,
        la,
        moveop,
        dataSeg,
        textSeg,
        asciizSeg,
        globlSeq,
        label
    }

    private Operator operator;
    private String ans = null;
    private String left = null;
    private String right = null;
    private int number;

    public MipsCode(Operator operator, String ans, String left, String right) {
        this.ans = ans;
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public MipsCode(Operator operator, String ans) {
        this.operator = operator;
        this.ans = ans;
    }

    public MipsCode(Operator operator, String ans, String left) {
        this.operator = operator;
        this.ans = ans;
        this.left = left;
    }

    public MipsCode(Operator operator, String ans, String left, String right, int number) {
        this.operator = operator;
        this.left = left;
        this.ans = ans;
        this.right = right;
        this.number = number;
    }

    public MipsCode() {

    }

    public void turnToString() {
        switch (operator) {
            case add:
                System.out.println("add " + ans + ", " + left + ", " + right);
                break;
            case addu:
                System.out.println("addu " + ans + ", " + left + ", " + right);
                break;
            case beq:
                System.out.println("beq " + left + ", " + right + ", " + ans);
                break;
            case j:
                System.out.println("j " + ans);
                break;
            case jr:
                System.out.println("jr " + ans);
                break;
            case la:
                System.out.println("la " + ans + ", " + left);
                break;
            case li:
                System.out.println("li " + ans + ", " + number);
                break;
            case lw:
                System.out.println("lw " + ans + ", " + number + "(" + left + ")");
                break;
            case sw:
                System.out.println("sw " + ans + ", " + number + "(" + left + ")");
                break;
            case sle:
                System.out.println("sle " + ans + ", " + left + ", " + right);
                break;
            case sgt:
                System.out.println("sgt " + ans + ", " + left + ", " + right);
                break;
            case sge:
                System.out.println("sge " + ans + ", " + left + ", " + right);
                break;
            case slt:
                System.out.println("slt " + ans + ", " + left + ", " + right);
                break;
            case sne:
                System.out.println("sne " + ans + ", " + left + ", " + right);
                break;
            case seq:
                System.out.println("seq " + ans + ", " + left + ", " + right);
                break;
            case sub:
                System.out.println("sub " + ans + ", " + left + ", " + right);
                break;
            case mult:
                System.out.println("mult " + ans + ", " + left);
                break;
            case divop:
                System.out.println("div " + ans + ", " + left);
                break;
            case addi:
                System.out.println("addi " + ans + ", " + left + ", " + number);
                break;
            case mflo:
                System.out.println("mflo " + ans);
                break;
            case mfhi:
                System.out.println("mfhi " + ans);
                break;
            case jal:
                System.out.println("jal " + ans);
                break;
            case syscall:
                System.out.println("syscall");
                break;
            case moveop:
                System.out.println("move " + ans + ", " + left);
                break;
            case dataSeg:
                System.out.println(".data");
                break;
            case textSeg:
                System.out.println(".text");
                break;
            case asciizSeg:
                System.out.println(ans + ": .asciiz \"" + left + "\"");
                break;
            case label:
                System.out.println("\n" + ans + ":");
                break;
            case sll:
                System.out.println("sll " + ans + ", " + left + ", " + number);
                break;
            default:
                error();
                break;
        }
    }

    public Operator getOperator() {
        return this.operator;
    }

    private void error() {

    }
}
