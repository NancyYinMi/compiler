import AST.*;
import MidCode.MidCode;
import Token.Token;

import java.util.ArrayList;

public class ParserMid {
    private ArrayList<MidCode> midCodes = new ArrayList<>();
    private ArrayList<String> lines = new ArrayList<>();
    private ArrayList<Token> tokens;
    private Program program;
    private int index = 0;
    private ArrayList<Decl> decls = new ArrayList<>();
    private ArrayList<Func> funcs = new ArrayList<>();
    private ArrayList<String> outputs = new ArrayList<>();

    public ParserMid(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public void analyse() {
        compileUnit();
        program.midCode();
        midCodes = program.getMidCodes();
        lines = program.getLines();
    }


    private void compileUnit() {
        while (!getToken(index + 2).getType().equals("LPARENT")) {
            Decl decl = decl();
            decls.add(decl);
        }
        while (!getToken(index + 1).getType().equals("MAINTK")) {
            Func func = funcDef();
            funcs.add(func);
        }//这个地方不能用if
        Func main = mainFuncDef();//主函数定义
        funcs.add(main);
        program = new Program(decls, funcs);
    }

    private Decl decl() {
        Decl decl;
        if (getToken().getValue().equals("const")) {
            decl = constDecl();//常量声明
        } else {
            decl = varDecl();//变量声明
        }
        return decl;
    }

    private Decl constDecl() {
        ArrayList<Def> defs = new ArrayList<>();
        if (getToken().getValue().equals("const")) {
            nextToken();
            bType();
            Def def = constDef();
            defs.add(def);
            while (getToken().getValue().equals(",")) {
                nextToken();
                Def def1 = constDef();
                defs.add(def1);
            }
            if (getToken().getValue().equals(";")) {
                nextToken();
            } else {
                outputs.add(getToken(index - 1).getPosition() + " i");
                //System.out.println(getToken(index - 1).getPosition() + " i");//i类错误没加分号
            }
        } else {
            error();
        }
        Decl decl = new Decl(defs, true);
        return decl;
    }

    private void bType() {
        if (getToken().getValue().equals("int")) {
            nextToken();
        } else {
            error();
        }
    }

    private Def constDef() {
        Def def;
        Lval lval = new Lval();
        int level = 0;
        Expr expr1 = new Expr();
        Expr expr2 = new Expr();
        int indexToken = 0;
        if (getToken().getType().equals("IDENFR")) {
            indexToken = index;
            nextToken();
        } else {
            error();
        }
        while (getToken().getValue().equals("[")) {
            level++;
            nextToken();
            if (level == 1) {
                expr1 = constExp();
            }
            if (level == 2) {
                expr2 = constExp();
            }
            if (getToken().getValue().equals("]")) {
                nextToken();
            } else {
                System.out.println(getToken(index - 1).getPosition() + " k");//k类错误，缺少]
            }
        }
        if (level == 0) {
            Id id = new Id(getToken(indexToken));
            lval = id;
        } else if (level == 1) {
            Array array = new Array(getToken(indexToken), expr1);
            lval = array;
        } else if (level == 2) {
            Array array = new Array(getToken(indexToken), expr1, expr2);
            lval = array;
        }
        if (getToken().getValue().equals("=")) {
            nextToken();
        } else {
            error();
        }
        ArrayList<Expr> exprs = new ArrayList<>();
        constInitVal(exprs);//没考虑到常量值不会变的问题，没有记录常量初值
        ConstDef constDef = new ConstDef(lval, exprs);
        def = constDef;

        return def;
    }

    private void constInitVal(ArrayList<Expr> exprs) {
        if (getToken().getValue().equals("{")) {
            nextToken();
            if (getToken().getValue().equals("}")) {
                nextToken();
            } else {
                constInitVal(exprs);
                while (getToken().getValue().equals(",")) {
                    nextToken();
                    constInitVal(exprs);
                }
                if (getToken().getValue().equals("}")) {
                    nextToken();
                } else {
                    error();
                }
            }
        } else {
            Expr expr = constExp();
            exprs.add(expr);
        }
    }

    private Decl varDecl() {
        Decl decl;
        bType();//int
        ArrayList<Def> defs = new ArrayList<>();
        Def def = varDef();
        defs.add(def);
        while (getToken().getValue().equals(",")) {
            nextToken();
            Def def1 = varDef();
            defs.add(def1);
        }
        if (getToken().getValue().equals(";")) {
            nextToken();
        } else {
            outputs.add(getToken(index - 1).getPosition() + " i");
            //System.out.println(getToken(index - 1).getPosition() + " i");//i类错误，没加分号
        }
        decl = new Decl(defs, false);
        return decl;
    }

    private Def varDef() {
        Def def;
        Lval lval = new Lval();
        int level = 0;
        Expr expr1 = new Expr();
        Expr expr2 = new Expr();
        int indexToken = 0;
        ArrayList<ArrayList<Expr>> exprs = new ArrayList<>();
        if (getToken().getType().equals("IDENFR")) {
            indexToken = index;
            nextToken();
        } else {
            error();
        }
        while (getToken().getValue().equals("[")) {
            level++;
            nextToken();
            if (level == 1) {
                expr1 = constExp();
            }
            if (level == 2) {
                expr2 = constExp();
            }
            if (getToken().getValue().equals("]")) {
                nextToken();
            } else {
                System.out.println(getToken(index - 1).getPosition() + " k");//k类错误，缺少]
            }
        }
        if (level == 0) {
            Id id = new Id(getToken(indexToken));
            lval = id;
        } else if (level == 1) {
            Array array = new Array(getToken(indexToken), expr1);
            lval = array;
        } else if (level == 2) {
            Array array = new Array(getToken(indexToken), expr1, expr2);
            lval = array;
        }
        if (getToken().getValue().equals("=")) {
            nextToken();

            initVal(exprs, 0);//没考虑到常量值不会变的问题，没有记录常量初值
        } else {
            error();
        }

        VerDef verDef = new VerDef(lval, exprs);
        def = verDef;
        return def;
    }

    private void initVal(ArrayList<ArrayList<Expr>> exprs, int level) {
        if (getToken().getValue().equals("{")) {
            nextToken();
            if (level == 1) {
                exprs.add(new ArrayList<>());
            }
            if (getToken().getValue().equals("}")) {
                nextToken();
            } else {
                initVal(exprs, level + 1);
                while (getToken().getValue().equals(",")) {
                    nextToken();
                    initVal(exprs, level + 1);
                }
                if (getToken().getValue().equals("}")) {
                    nextToken();
                } else {
                    error();
                }
            }
        } else {
            if (exprs.size() == 0) {
                exprs.add(new ArrayList<>());
            }
            exprs.get(exprs.size() - 1).add(exp());
        }
    }

    private Func funcDef() {
        Func func = new Func();
        Block block;
        Id id = null;
        String nowFuncType = funcType();//获取函数返回值类型
        //System.out.println(nowFuncType);
        ArrayList<Fparam> parameters = new ArrayList<>();
        //Table.SymbolTable symbolTable1 = new Table.SymbolTable();
        //symbolTable1.setPrev(symbolTable);
        //symbolTables.add(symbolTable1);//新建一个符号表，承继原始传进来的符号表
        if (getToken().getType().equals("IDENFR")) {
            //System.out.println("1: " + getToken().getValue());
            id = new Id(getToken());
            nextToken();
        } else {
            error();
        }
        if (getToken().getValue().equals("(")) {
            nextToken();
            if (!getToken().getValue().equals(")")) {
                parameters = funcFParams();
                if (getToken().getValue().equals(")")) {
                    nextToken();
                } else {
                    //System.out.println(322);
                    System.out.println(getToken(index - 1).getPosition() + " j");//j类错误，少)
                }
            } else {
                nextToken();
            }
        } else {
            error();
        }
        //Table.SymbolTable symbolTable2 = new Table.SymbolTable();
        //symbolTable.setNext(symbolTable1);
        //symbolTable2.setPrev(symbolTable1);
        //symbolTables.add(symbolTable2);
        block = block();//这个symbolTable2继承自symbolTable1
        func = new Func(nowFuncType, id, parameters, block, false);
        return func;
    }

    private Func mainFuncDef() {
        int position = 0;
        Token token = null;
        if (getToken().getValue().equals("int")) {
            nextToken();
        } else {
            error();
        }
        if (getToken().getValue().equals("main")) {
            token = new Token("MAINTK", "main", getToken().getPosition());
            nextToken();
        } else {
            error();
        }
        if (getToken().getValue().equals("(")) {
            nextToken();
        } else {
            error();
        } if (getToken().getValue().equals(")")) {
            nextToken();
        } else {
            //System.out.println(361);
            System.out.println(getToken(index - 1).getPosition() + " j"); //缺），j类错误
        }
        String nowFuncType = "int";//设置当前返回值类型为int
        Block block = block();
        Id id = new Id(token);
        Func func = new Func(nowFuncType, id, new ArrayList<>(), block, true);
        return func;
    }

    private String funcType() {
        if (getToken().getValue().equals("int")) {
            nextToken();
            return "int";
        } else if (getToken().getValue().equals("void")) {
            nextToken();
            return "void";
        } else {
            error();
            return "";
        }
    }

    private ArrayList<Fparam> funcFParams() {
        //System.out.println("2 : I'm in funcFParams");
        ArrayList<Fparam> fparams = new ArrayList<>();
        Fparam fparam = funcFParam();
        fparams.add(fparam);
        while (getToken().getValue().equals(",")) {
            nextToken();
            Fparam fparam1 = funcFParam();
            fparams.add(fparam1);
        }
        return fparams;
    }

    private Fparam funcFParam() {
        Fparam fparam;
        bType();
        Id id = null;
        Expr expr = null;
        int level = 0;
        if (getToken().getType().equals("IDENFR")) {
            //System.out.println("3: " + getToken().getValue());
            id = new Id(getToken());
            nextToken();
            if (getToken().getValue().equals("[")) {
                //System.out.println("4: I'm in []");
                level++;
                nextToken();
                if (getToken().getValue().equals("]")) {
                    nextToken();
                    if (getToken().getValue().equals("[")) {
                        level++;
                        nextToken();
                        expr = constExp();
                        if (getToken().getValue().equals("]")) {
                            nextToken();
                        } else {
                            System.out.println(getToken(index - 1).getPosition() + " k");//k型错误，少]
                        }
                    }
                } else {
                    System.out.println(getToken(index - 1).getPosition() + " k");//k型错误，少]
                }
            }
        } else {
            error();
        }
        fparam = new Fparam(id, level, expr);
        return fparam;
    }

    private Block block() {
        //System.out.println("4: in block()");
        Block block = null;
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        if (getToken().getValue().equals("{")) {
            nextToken();
            if (getToken().getValue().equals("}")) {
                nextToken();
            } else {
                BlockItem blockItem = blockItem();
                if (blockItem != null) {
                    blockItems.add(blockItem);
                }
                while (!getToken().getValue().equals("}")) {
                    BlockItem blockItem1 = blockItem();
                    if (blockItem1 != null) {
                        blockItems.add(blockItem1);
                    }
                }
                nextToken();
            }
            //if (isInFunc) {
            //    isInFunc = false;
            //} else {
            //    Table.SymbolTable nowNumTable = new Table.SymbolTable();
            //    nowNumTable.setPrev(numTable);
            //    numTable = nowNumTable;
            //}
        } else {
            error();
        }
        //symbolTables.add(symbolTable);
        block = new Block(blockItems);
        return block;
    }

    private BlockItem  blockItem() {
        //System.out.println("5 : in blockItem()");
        BlockItem blockItem;
        if (getToken().getValue().equals("int") || getToken().getValue().equals("const")) {
            blockItem =  decl();
        } else {
            blockItem = stmt();
        }
        return blockItem;
    }

    private Stmt stmt() {
        Stmt stmt = null;
        if (getToken().getValue().equals("break")) {//break;
            nextToken();
            if (getToken().getValue().equals(";")) {
                nextToken();
            } else {
                outputs.add(getToken(index - 1).getPosition() + " i");
                //System.out.println(getToken(index - 1).getPosition() + " i");//没加;，i类错误
            }
            return new Break();
        } else if (getToken().getValue().equals("continue")) {//continue;
            nextToken();
            if (getToken().getValue().equals(";")) {
                nextToken();
            } else {
                outputs.add(getToken(index - 1).getPosition() + " i");
                //System.out.println(getToken(index - 1).getPosition() + " i");//没加;，i类错误
            }
            return new Continue();
        } else if (getToken().getValue().equals("return")) {//return[exp];
            //System.out.println("6: in return");
            //System.out.println("in return");
            nextToken();
            Expr expr = null;
            if (!getToken().getValue().equals(";")) {
                if (!getToken().getValue().equals("}")) {
                    expr = exp();
                }
                if (getToken().getValue().equals(";")) {
                    nextToken();
                } else {
                    outputs.add(getToken(index - 1).getPosition() + " i");
                    //System.out.println(getToken(index - 1).getPosition() + " i");//没加；，i类错误
                }
            } else {
                nextToken();
            }
            //System.out.println("7: " + expr.toString());
            Return r = new Return(expr);
            return r;
        } else if (getToken().getValue().equals("{")) {//block
            //Table.SymbolTable symbolTable1 = new Table.SymbolTable();
            //symbolTable1.setPrev(symbolTable);
            //symbolTable.setNext(symbolTable1);
            //symbolTables.add(symbolTable1);
            return block();
        } else if (getToken().getValue().equals("printf")) {//printf
            Print print = null;
            Token token = null;
            ArrayList<Expr> exprs = new ArrayList<>();
            int expNum = 0;
            nextToken();
            if (getToken().getValue().equals("(")) {
                nextToken();
                token = getToken();
                expNum = formatString(token);
                nextToken();
                while (getToken().getValue().equals(",")) {
                    nextToken();
                    Expr expr = exp();
                    //System.out.println(expr instanceof Array);
                    exprs.add(expr);
                    expNum++;
                }
                if (getToken().getValue().equals(")")) {
                    nextToken();
                    if (getToken().getValue().equals(";")) {
                        nextToken();
                    } else {
                        outputs.add(getToken(index - 1).getPosition() + " i");
                        //System.out.println(getToken(index - 1).getPosition() + " i");//没；，i类错误
                    }
                } else {
                    //System.out.println(542);
                    System.out.println(getToken(index - 1).getPosition() + " j");//没），j类错误
                }
            } else {
                error();
            }
            print = new Print(token, exprs);
            return print;
        } else if (getToken().getValue().equals("for")) {//for
            nextToken();
            Or cond = null;
            Stmt stmt1;
            Assign expr1 = null;
            Assign expr2 = null;
            if (getToken().getValue().equals("(")) {//for(
                nextToken();
                if (!getToken().getValue().equals(";")) {//for(<forStmt>
                    expr1 = forStmt();
                    if (getToken().getValue().equals(";")) {//for(<forStmt>;
                        nextToken();
                        if (!getToken().getValue().equals(";")) {//for(<forStmt>;<cond>
                            cond = cond();
                            if (getToken().getValue().equals(";")) {//for(<forStmt>;<cond>;
                                nextToken();
                                if (!getToken().getValue().equals(")")) {//for(<forStmt>;<cond>;<forStmt>
                                    expr2 = forStmt();
                                    if (getToken().getValue().equals(")")) {//for(<forStmt>;<cond>;<forStmt>)
                                        nextToken();
                                    } else {
                                        error();
                                    }
                                } else {//for(<forStmt>;<cond>;)
                                    nextToken();
                                }
                            } else {
                                error();
                            }
                        } else {//for(<forStmt>;;
                            nextToken();
                            if (!getToken().getValue().equals(")")) {//for(<forStmt>;;<forStmt>
                                expr2 = forStmt();
                                if (getToken().getValue().equals(")")) {//for(<forStmt>;;<forStmt>)
                                    nextToken();
                                } else {
                                    error();
                                }
                            } else {//for(<forStmt;;)
                                nextToken();
                            }
                        }
                    } else {
                        error();
                    }
                } else {//for(;
                    nextToken();
                    if (!getToken().getValue().equals(";")) {//for(;<cond>
                        cond = cond();
                        if (getToken().getValue().equals(";")) {//for(;<cond>;
                            nextToken();
                            if (!getToken().getValue().equals(")")) {//for(;<cond>;<forStmt>
                                expr2 = forStmt();
                                if (getToken().getValue().equals(")")) {//for(;<cond>;<forStmt>)
                                    nextToken();
                                } else {
                                    error();
                                }
                            } else {//for(;<cond>;)
                                nextToken();
                            }
                        } else {
                            error();
                        }
                    } else {
                        nextToken();
                        if (!getToken().getValue().equals(")")) {//for(;;<forStmt>
                            expr2 = forStmt();
                            if (getToken().getValue().equals(")")) {//for(;;<forStmt>)
                                nextToken();
                            } else {
                                error();
                            }
                        } else {//for(;;)
                            nextToken();
                        }
                    }
                }
            } else {
                error();
            }
            stmt1 = stmt();
            For forStmt = new For(cond, expr1, expr2, stmt1);
            return forStmt;
        } else if (getToken().getValue().equals("if")) {//if
            Or cond = null;
            Stmt stmt1 = null;
            Stmt stmt2 = null;
            nextToken();
            if (getToken().getValue().equals("(")) {
                nextToken();
                cond = cond();
                if (getToken().getValue().equals(")")) {
                    nextToken();
                    stmt1 = stmt();
                    if (getToken().getValue().equals("else")) {
                        nextToken();
                        stmt2 = stmt();
                    }
                } else {
                    //System.out.println(650);
                    System.out.println(getToken(index - 1).getPosition() + " j");//缺），j类错误
                }
            } else {
                error();
            }
            If ifStmt;
            if (stmt2 == null) {
                ifStmt = new If(cond, stmt1);
            } else {
                ifStmt = new If(cond, stmt1, stmt2);
            }
            return ifStmt;
        } else if (getToken().getValue().equals(";")) {//无exp,直接加;的情况
            nextToken();
        } else if (isLVal()) {//这里还有一个不能改变常量的值，h性错误可能出现
            int lValType;
            int position = getToken().getPosition();
            Lval lval;
            lval = (Lval) lVal();
            if (getToken().getValue().equals("=")) {
                nextToken();
                if (getToken().getValue().equals("getint")) {
                    nextToken();
                    if (getToken().getValue().equals("(")) {
                        nextToken();
                        if (getToken().getValue().equals(")")) {
                            nextToken();
                            if (getToken().getValue().equals(";")) {
                                nextToken();
                            } else {
                                outputs.add(getToken(index - 1).getPosition() + " i");
                                //System.out.println(getToken(index - 1).getPosition() + " i");//没加;,i型错误
                            }
                        } else {
                            //System.out.println(684);
                            System.out.println(getToken(index - 1).getPosition() + " j");//没加), j型错误
                        }
                    } else {
                        error();
                    }
                    GetInt getInt = new GetInt(lval);
                    return getInt;
                } else {
                    Expr expr = exp();
                    if (getToken().getValue().equals(";")) {
                        nextToken();
                    } else {
                        outputs.add(getToken(index - 1).getPosition() + " i");
                        //System.out.println(getToken(index - 1).getPosition() + " i");//缺分号，i类错误
                    }
                    Assign assign = new Assign(lval, expr);
                    return assign;
                }
            } else {
                error();
            }
        } else {
            Expr expr = exp();
            if (getToken().getValue().equals(";")) {
                nextToken();
            } else {
                error();
            }
            return expr;
        }
        return null;
    }

    private Expr exp() {
        Expr expr = addExp();
        return expr;
    }

    private Or cond() {
        Or or = lOrExp();
        return or;
    }

    private Expr lVal() {
        int level = 0;
        Expr expr1 = null;
        Expr expr2 = null;
        Token token = null;
        Lval lval = null;
        if (getToken().getType().equals("IDENFR")) {
            token = getToken();
            nextToken();
            while (getToken().getValue().equals("[")) {
                level++;
                nextToken();
                if (level == 1) {
                    expr1 = exp();
                } else if (level == 2) {
                    expr2 = exp();
                }
                if (getToken().getValue().equals("]")) {
                    nextToken();
                } else {
                    System.out.println(getToken(index - 1).getPosition() + " k");//没加]. k类错误
                }
            }
        } else {
            error();
        }
        if (level == 0) {
            lval = new Id(token);
        } else if (level == 1) {
            lval = new Array(token, expr1);
        } else {
            lval = new Array(token, expr1, expr2);
        }
        return lval;
    }

    private Expr primaryExp() {
        if (getToken().getValue().equals("(")) {
            nextToken();
            Expr expr = exp();
            if (getToken().getValue().equals(")")) {
                nextToken();
            } else {
                error();
            }
            return expr;
        } else if (getToken().getType().equals("INTCON")) {
            Constant constant = new Constant(getToken());
            nextToken();
            return constant;
        } else {
            Expr expr = lVal();
            return expr;
        }
    }

    private Expr unaryExp() {
        String funName = "";
        //Symbol func = null;
        if (getToken().getValue().equals("+") || getToken().getValue().equals("-")
                || getToken().getValue().equals("!")) {
            Token token = unaryOp();
            Expr expr = unaryExp();
            Unary unary = new Unary(token, expr);
            return unary;
        } else if (getToken().getType().equals("IDENFR") && getToken(index + 1).getValue().equals("(")) {
            Token token = getToken();
            ArrayList<Expr> exprs = new ArrayList<>();
            funName = getToken().getValue();
            nextToken();
            nextToken();
            if (getToken().getValue().equals(")")) {
                nextToken();
            } else {
                if (getToken().getValue().equals(";")) {
                    //System.out.println(802);
                    System.out.println(getToken(index - 1).getPosition() + " j");//没有),直接;, j类错误
                } else {
                    exprs = funcRParams();
                    if (getToken().getValue().equals(")")) {
                        nextToken();
                    } else {
                        //System.out.println(809);
                        System.out.println(getToken(index - 1).getPosition() + " j");//没加), j类错误
                    }
                }
            }
            //System.out.println(token.getValue());
            FuncR funcR = new FuncR(token ,exprs);
            return funcR;
        } else {
            Expr expr = primaryExp();
            return expr;
        }
    }

    private Token unaryOp() {
        if (getToken().getValue().equals("+") || getToken().getValue().equals("-")
                || getToken().getValue().equals("!")) {
            Token token = getToken();
            nextToken();
            return token;
        } else {
            error();
            return null;
        }
    }

    private ArrayList<Expr> funcRParams() {
        ArrayList<Expr> exprs = new ArrayList<>();
        Expr expr = exp();
        exprs.add(expr);
        while (getToken().getValue().equals(",")) {
            nextToken();
            Expr expr1 = exp();
            exprs.add(expr1);
        }
        return exprs;
    }

    private Expr mulExp() {
        Expr expr = unaryExp();
        while (getToken().getValue().equals("*") ||
                getToken().getValue().equals("/") ||
                getToken().getValue().equals("%")) {
            Token token = getToken();
            nextToken();
            Expr expr1 = unaryExp();
            Arith arith = new Arith(token, expr, expr1);
            expr = arith;
        }
        return expr;
    }

    private Expr addExp() {
        Expr expr = mulExp();
        while (getToken().getValue().equals("+") || getToken().getValue().equals("-")) {
            //System.out.println("ha");
            Token token = getToken();
            nextToken();
            Expr expr1 = mulExp();
            Arith arith = new Arith(token, expr, expr1);
            expr = arith;
        }
        return expr;
    }

    private Expr relExp() {
        Expr expr = addExp();
        while (getToken().getValue().equals("<") ||
                getToken().getValue().equals(">") ||
                getToken().getValue().equals(">=") ||
                getToken().getValue().equals("<=")) {
            Token token = getToken();
            nextToken();
            Expr expr1 = addExp();
            Logical logical = new Logical(token, expr, expr1);
            expr = logical;
        }
        return expr;
    }

    private Expr eqExp() {
        Expr expr = relExp();
        while (getToken().getValue().equals("==") || getToken().getValue().equals("!=")) {
            Token token = getToken();
            nextToken();
            Expr expr1 = relExp();
            Logical logical = new Logical(token, expr, expr1);
            expr = logical;
        }
        return expr;
    }

    private And lAndExp() {
        ArrayList<Expr> exprs = new ArrayList<>();
        Expr expr = eqExp();
        exprs.add(expr);
        while (getToken().getValue().equals("&&")) {
            nextToken();
            Expr expr1 = eqExp();
            exprs.add(expr1);
        }
        And and = new And(exprs);
        return and;
    }

    private Or lOrExp() {
        ArrayList<And> ands = new ArrayList<>();
        And and = lAndExp();
        ands.add(and);
        while (getToken().getValue().equals("||")) {
            nextToken();
            And and1 = lAndExp();
            ands.add(and1);
        }
        Or or = new Or(ands);
        return or;
    }

    private Expr constExp() {
        Expr expr = addExp();
        return expr;
    }

    private Assign forStmt() {
        if (getToken().getType().equals("IDENFR")) {
            Lval lval = (Lval) lVal();
            if (getToken().getValue().equals("=")) {
                nextToken();
                Expr expr = exp();
                Assign assign = new Assign(lval, expr);
                return assign;
            } else {
                error();
            }
        } else {
            error();
        }
        return null;
    }

    public ArrayList<MidCode> getMidCodes() {
        return this.midCodes;
    }

    private Token getToken() {
        if (index < tokens.size()) {
            return tokens.get(index);
        } else {
            return null;
        }
    }

    private Token getToken(int index) {
        if (index < tokens.size()) {
            return tokens.get(index);
        } else {
            return null;
        }
    }

    private void error() {

    }

    private void nextToken() {
        if (index + 1 < tokens.size()) {
            index++;
        } else {
            index = tokens.size();//溢出
        }
    }

    private boolean isLVal() {
        int flag = 0;
        int level = 0;//计算有多少层数组
        int index = this.index;
        if (getToken().getType().equals("IDENFR")) {
            if (getToken(index + 1).getValue().equals("=")) {
                flag = 1;//是LVal
            } else if (getToken(index + 1).getValue().equals("[")) {
                level++;
                index = index + 2;
                while (level > 0) {
                    //System.out.println(getToken(index).toString());
                    if (getToken(index).getValue().equals("]")) {
                        level--;
                        if (getToken(index + 1).getValue().equals("[")) {
                            level++;
                            index++;
                        }
                    } else if (getToken(index).getValue().equals("[")) {
                        level++;
                    }
                    index++;
                }
                if (getToken(index).getValue().equals("=")) {
                    flag = 1;
                }
            }
        }
        if (flag == 1) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getOutputs() {
        return outputs;
    }

    private int formatString(Token token) {
        int num = 0;
        if (token.getType().equals("STRCON")) {
            if (isStrcon(token.getValue())) {
                String line = token.getValue();
                for (int i = 0; i < line.length() - 1; i++) {
                    if (line.charAt(i) == '%' && line.charAt(i + 1) == 'd') {
                        num++;
                    }
                }
                //outputs.add(token.toString());
                //nextToken();
            } else {
                outputs.add(token.getPosition() + " a");
                //System.out.println(token.getPosition() + " a");//a类错误情况2：词语为引用类，但引号中的内容格式不符合要求
                String line = token.getValue();
                for (int i = 0; i < line.length() - 1; i++) {
                    if (line.charAt(i) == '%' && line.charAt(i + 1) == 'd') {
                        num++;
                    }
                }
                //nextToken();
            }
        } else {
            outputs.add(token.getPosition() + " a");
            //System.out.println(token.getPosition() + " a");//a类错误情况1：词语非引用类
            //nextToken();
            //error();
        }
        return num;
    }

    private boolean isStrcon(String str) {
        String strcon = str.substring(1, str.length() - 1);
        for (int i = 0; i < strcon.length(); i++) {
            if (!((i < strcon.length() - 1 && strcon.charAt(i) == '%' && strcon.charAt(i + 1) == 'd') ||
                    (i < strcon.length() - 1 && strcon.charAt(i) == 92 && strcon.charAt(i + 1) == 'n') ||
                    (strcon.charAt(i) >= 40 && strcon.charAt(i) <= 126 && strcon.charAt(i) != 92) ||
                    (strcon.charAt(i) == 32) || (strcon.charAt(i) == 33))) {
                return false;
            }
        }
        return true;
    }
}
