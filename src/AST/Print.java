package AST;

import MidCode.MidCode;
import Token.Token;

import java.util.ArrayList;

public class Print extends Stmt {
    private Token format;
    private ArrayList<Expr> exprs;
    private ArrayList<String> lines = new ArrayList<>();

    public Print() {

    }

    public Print(Token format, ArrayList<Expr> exprs) {
        this.format = format;
        this.exprs = exprs;
    }

    @Override
    public void midCode() {
        String line = format.getValue();
        int length = line.length();
        int head = 1;
        int tail = 1;
        while (tail < length && tail + 1 < length) {
            if (line.charAt(tail) == '%' && line.charAt(tail + 1) == 'd') {
                if (tail != head) {
                    lines.add(line.substring(head, tail));
                }
                lines.add("%d");
                tail = tail + 2;
                head = tail;
                continue;
            }
            tail++;
        }
        if (tail != head) {
            lines.add(line.substring(head, tail));
        }
        int flag = 0;
        for (String string : lines) {
            if (string.equals("%d")) {
                //System.out.println(exprs.get(flag) == null);

                MidCode midCode = new MidCode(MidCode.operator.PRINT, treat(exprs.get(flag).reduce()), "digit");
                addMidCode(midCode);
                flag++;
            } else {
                MidCode midCode = new MidCode(MidCode.operator.PRINT, string, "string");
                addMidCode(midCode);
                addLine(string);
            }
        }
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
