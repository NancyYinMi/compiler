import Token.Token;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<String> lines = new ArrayList<>();
    private Pattern pattern0 = Pattern.compile("(^\"[\\W\\w]*?\")([\\W\\w]*)");
    private Pattern pattern1 = Pattern.compile("(^main)(\\W[\\W\\w]*)");
    private Pattern pattern2 = Pattern.compile("(^const)(\\W[\\W\\w]*)");
    private Pattern pattern3 = Pattern.compile("(^int)(\\W[\\W\\w]*)");
    private Pattern pattern4 = Pattern.compile("(^break)(\\W[\\W\\w]*)");
    private Pattern pattern5 = Pattern.compile("(^continue)(\\W[\\W\\w]*)");
    private Pattern pattern6 = Pattern.compile("(^if)(\\W[\\W\\w]*)");
    private Pattern pattern7 = Pattern.compile("(^else)(\\W[\\W\\w]*)");
    private Pattern pattern8 = Pattern.compile("(^!)([\\W\\w]*)");
    private Pattern pattern9 = Pattern.compile("(^&&)([\\W\\w]*)");
    private Pattern pattern10 = Pattern.compile("(^\\|\\|)([\\W\\w]*)");
    private Pattern pattern11 = Pattern.compile("(^for)(\\W[\\W\\w]*)");
    private Pattern pattern12 = Pattern.compile("(^getint)(\\W[\\W\\w]*)");
    private Pattern pattern13 = Pattern.compile("(^printf)(\\W[\\W\\w]*)");
    private Pattern pattern14 = Pattern.compile("(^return)(\\W[\\W\\w]*)");
    private Pattern pattern15 = Pattern.compile("(^\\+)([\\W\\w]*)");
    private Pattern pattern16 = Pattern.compile("(^-)([\\W\\w]*)");
    private Pattern pattern17 = Pattern.compile("(^void)(\\W[\\W\\w]*)");
    private Pattern pattern18 = Pattern.compile("(^\\*)([\\W\\w]*)");
    private Pattern pattern19 = Pattern.compile("(^/)([\\W\\w]*)");
    private Pattern pattern20 = Pattern.compile("(^%)([\\W\\w]*)");
    private Pattern pattern21 = Pattern.compile("(^<)([\\W\\w]*)");
    private Pattern pattern22 = Pattern.compile("(^<=)([\\W\\w]*)");
    private Pattern pattern23 = Pattern.compile("(^>)([\\W\\w]*)");
    private Pattern pattern24 = Pattern.compile("(^>=)([\\W\\w]*)");
    private Pattern pattern25 = Pattern.compile("(^==)([\\W\\w]*)");
    private Pattern pattern26 = Pattern.compile("(^\\!=)([\\W\\w]*)");
    private Pattern pattern27 = Pattern.compile("(^=)([\\W\\w]*)");
    private Pattern pattern28 = Pattern.compile("(^;)([\\W\\w]*)");
    private Pattern pattern29 = Pattern.compile("(^,)([\\W\\w]*)");
    private Pattern pattern30 = Pattern.compile("(^\\()([\\W\\w]*)");
    private Pattern pattern31 = Pattern.compile("(^\\))([\\W\\w]*)");
    private Pattern pattern32 = Pattern.compile("(^\\[)([\\W\\w]*)");
    private Pattern pattern33 = Pattern.compile("(^\\])([\\W\\w]*)");
    private Pattern pattern34 = Pattern.compile("(^\\{)([\\W\\w]*)");
    private Pattern pattern35 = Pattern.compile("(^\\})([\\W\\w]*)");
    private Pattern pattern36 = Pattern.compile("(^-?\\d+)([^0-9]*)");
    private Pattern pattern37 = Pattern.compile("(^[a-zA-Z_]\\w*)(\\W?[\\W\\w]*)");

    public Lexer(String file) {
        String hold = file;
        while (hold.contains("\n")) {
            String line = hold.substring(0, hold.indexOf('\n'));
            hold = hold.substring(hold.indexOf('\n') + 1);
            this.lines.add(line);
        }
        this.lines.add(hold);
    }

    public void analyzer() {
        int position = 0;
        for (String line : lines) {
            line = line.trim() + " ";
            if (line.contains("//") && checkColon(line.substring(0, line.indexOf("//"))) % 2 == 0) {
                line = line.substring(0, line.indexOf("//"));
            }
            position++;
            while (!line.isEmpty()) {
                Matcher strcon = pattern0.matcher(line);
                Matcher maintk = pattern1.matcher(line);
                Matcher consttk = pattern2.matcher(line);
                Matcher inttk = pattern3.matcher(line);
                Matcher breaktk = pattern4.matcher(line);
                Matcher continuetk = pattern5.matcher(line);
                Matcher iftk = pattern6.matcher(line);
                Matcher elsetk = pattern7.matcher(line);
                Matcher not = pattern8.matcher(line);
                Matcher and = pattern9.matcher(line);
                Matcher or = pattern10.matcher(line);
                Matcher fortk = pattern11.matcher(line);
                Matcher getinttk = pattern12.matcher(line);
                Matcher printftk = pattern13.matcher(line);
                Matcher returntk = pattern14.matcher(line);
                Matcher plus = pattern15.matcher(line);
                Matcher minu = pattern16.matcher(line);
                Matcher voidtk = pattern17.matcher(line);
                Matcher mult = pattern18.matcher(line);
                Matcher div = pattern19.matcher(line);
                Matcher mod = pattern20.matcher(line);
                Matcher lss = pattern21.matcher(line);
                Matcher leq = pattern22.matcher(line);
                Matcher gre = pattern23.matcher(line);
                Matcher geq = pattern24.matcher(line);
                Matcher eql = pattern25.matcher(line);
                Matcher neq = pattern26.matcher(line);
                Matcher assign = pattern27.matcher(line);
                Matcher semicn = pattern28.matcher(line);
                Matcher comma = pattern29.matcher(line);
                Matcher lparent = pattern30.matcher(line);
                Matcher rparent = pattern31.matcher(line);
                Matcher lbrack = pattern32.matcher(line);
                Matcher rbrack = pattern33.matcher(line);
                Matcher lbrace = pattern34.matcher(line);
                Matcher rbrace = pattern35.matcher(line);
                Matcher intcon = pattern36.matcher(line);
                Matcher idenfr = pattern37.matcher(line);
                if (strcon.find()) {
                    tokens.add(new Token("STRCON", strcon.group(1), position));
                    line = line.substring(strcon.group(1).length()).trim();
                } else if (minu.find()) {
                    tokens.add(new Token("MINU", minu.group(1), position));
                    line = line.substring(minu.group(1).length()).trim();
                } else if (consttk.find()) {
                    tokens.add(new Token("CONSTTK", consttk.group(1), position));
                    line = line.substring(consttk.group(1).length()).trim();
                } else if (inttk.find()) {
                    tokens.add(new Token("INTTK", inttk.group(1), position));
                    line = line.substring(inttk.group(1).length()).trim();
                } else if (breaktk.find()) {
                    tokens.add(new Token("BREAKTK", breaktk.group(1), position));
                    line = line.substring(breaktk.group(1).length()).trim();
                } else if (continuetk.find()) {
                    tokens.add(new Token("CONTINUETK", continuetk.group(1), position));
                    line = line.substring(continuetk.group(1).length()).trim();
                } else if (iftk.find()) {
                    tokens.add(new Token("IFTK", iftk.group(1), position));
                    line = line.substring(iftk.group(1).length()).trim();
                } else if (elsetk.find()) {
                    tokens.add(new Token("ELSETK", elsetk.group(1), position));
                    line = line.substring(elsetk.group(1).length()).trim();
                } else if (neq.find()) {
                    tokens.add(new Token("NEQ", neq.group(1), position));
                    line = line.substring(neq.group(1).length()).trim();
                } else if (and.find()) {
                    tokens.add(new Token("AND", and.group(1), position));
                    line = line.substring(and.group(1).length()).trim();
                } else if (or.find()) {
                    tokens.add(new Token("OR", or.group(1), position));
                    line = line.substring(or.group(1).length()).trim();
                } else if (fortk.find()) {
                    tokens.add(new Token("FORTK", fortk.group(1), position));
                    line = line.substring(fortk.group(1).length()).trim();
                } else if (getinttk.find()) {
                    tokens.add(new Token("GETINTTK", getinttk.group(1), position));
                    line = line.substring(getinttk.group(1).length()).trim();
                } else if (printftk.find()) {
                    tokens.add(new Token("PRINTFTK", printftk.group(1), position));
                    line = line.substring(printftk.group(1).length()).trim();
                } else if (returntk.find()) {
                    tokens.add(new Token("RETURNTK", returntk.group(1), position));
                    line = line.substring(returntk.group(1).length()).trim();
                } else if (plus.find()) {
                    tokens.add(new Token("PLUS", plus.group(1), position));
                    line = line.substring(plus.group(1).length()).trim();
                } else if (intcon.find()) {
                    tokens.add(new Token("INTCON", intcon.group(1), position));
                    line = line.substring(intcon.group(1).length()).trim();
                } else if (voidtk.find()) {
                    tokens.add(new Token("VOIDTK", voidtk.group(1), position));
                    line = line.substring(voidtk.group(1).length()).trim();
                } else if (mult.find()) {
                    tokens.add(new Token("MULT", mult.group(1), position));
                    line = line.substring(mult.group(1).length()).trim();
                } else if (div.find()) {
                    tokens.add(new Token("DIV", div.group(1), position));
                    line = line.substring(div.group(1).length()).trim();
                } else if (mod.find()) {
                    tokens.add(new Token("MOD", mod.group(1), position));
                    line = line.substring(mod.group(1).length()).trim();
                } else if (leq.find()) {
                    tokens.add(new Token("LEQ", leq.group(1), position));
                    line = line.substring(leq.group(1).length()).trim();
                } else if (lss.find()) {
                    tokens.add(new Token("LSS", lss.group(1), position));
                    line = line.substring(lss.group(1).length()).trim();
                } else if (geq.find()) {
                    tokens.add(new Token("GEQ", geq.group(1), position));
                    line = line.substring(geq.group(1).length()).trim();
                } else if (gre.find()) {
                    tokens.add(new Token("GRE", gre.group(1), position));
                    line = line.substring(gre.group(1).length()).trim();
                } else if (eql.find()) {
                    tokens.add(new Token("EQL", eql.group(1), position));
                    line = line.substring(eql.group(1).length()).trim();
                } else if (not.find()) {
                    tokens.add(new Token("NOT", not.group(1), position));
                    line = line.substring(not.group(1).length()).trim();
                } else if (assign.find()) {
                    tokens.add(new Token("ASSIGN", assign.group(1), position));
                    line = line.substring(assign.group(1).length()).trim();
                } else if (semicn.find()) {
                    tokens.add(new Token("SEMICN", semicn.group(1), position));
                    line = line.substring(semicn.group(1).length()).trim();
                } else if (comma.find()) {
                    tokens.add(new Token("COMMA", comma.group(1), position));
                    line = line.substring(comma.group(1).length()).trim();
                } else if (lparent.find()) {
                    tokens.add(new Token("LPARENT", lparent.group(1), position));
                    line = line.substring(lparent.group(1).length()).trim();
                } else if (rparent.find()) {
                    tokens.add(new Token("RPARENT", rparent.group(1), position));
                    line = line.substring(rparent.group(1).length()).trim();
                } else if (lbrack.find()) {
                    tokens.add(new Token("LBRACK", lbrack.group(1), position));
                    line = line.substring(lbrack.group(1).length()).trim();
                } else if (rbrack.find()) {
                    tokens.add(new Token("RBRACK", rbrack.group(1), position));
                    line = line.substring(rbrack.group(1).length()).trim();
                } else if (lbrace.find()) {
                    tokens.add(new Token("LBRACE", lbrace.group(1), position));
                    line = line.substring(lbrace.group(1).length()).trim();
                } else if (rbrace.find()) {
                    tokens.add(new Token("RBRACE", rbrace.group(1), position));
                    line = line.substring(rbrace.group(1).length()).trim();
                } else if (maintk.find()) {
                    tokens.add(new Token("MAINTK", maintk.group(1), position));
                    line = line.substring(maintk.group(1).length()).trim();
                } else if (idenfr.find()) {
                    tokens.add(new Token("IDENFR", idenfr.group(1), position));
                    line = line.substring(idenfr.group(1).length()).trim();
                } else {
                    line = line.substring(1).trim();
                }
            }
        }
        cutNote();
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<String> getFile() {
        return lines;
    }

    private int checkColon(String line) {
        int ans = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\"') {
                ans++;
            }
        }
        return ans;
    }

    private void cutNote () {
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (tokens.get(i).getValue().equals("/") && tokens.get(i + 1).getValue().equals("*")) {
                int j = i + 2;
                while (j + 1 < tokens.size() && !(tokens.get(j).getValue().equals("*") && tokens.get(j + 1).getValue().equals("/"))) {
                    tokens.remove(j);
                }
                tokens.remove(i);
                tokens.remove(i);
                tokens.remove(i);
                tokens.remove(i);
                i--;
            }
        }
    }
}
