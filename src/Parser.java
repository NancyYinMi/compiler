import Table.Symbol;
import Table.SymbolTable;
import Token.Token;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private ArrayList<Sentence> sentences = new ArrayList<>();
    private SymbolTable numTable = new SymbolTable();
    private SymbolTable funcTable = new SymbolTable();
    private ArrayList<String> outputs;
    private String nowFuncType = "";//记录当前函数的返回值类型，然后在判断函数返回值是否匹配的时候用到
    private int index = 0;
    private boolean isInFunc = false;//记录当前是不是在函数定义的{里面
    private boolean isOutFunc = false;//记录当前有没有离开函数定义的}
    private boolean hasReturn = false;//记录当前是不是有return值
    private boolean isCond = false;//记录当前是不是在cond里
    private int forLevel = 0;
    private ArrayList<ArrayList<Integer>> funcR = new ArrayList<>();
    private int level = 0;
    private int forLevel0 = 0;
    private int changeLevel = 0;
    private boolean hasError = false;

    public Parser(ArrayList<Token> tokens, ArrayList<String> outputs) {
        this.tokens = tokens;
        this.outputs = outputs;
        //这个地方要记得处理error
    }

    public void analyse() {//语法分析
        compileUnit();//编译单元
    }

    private void compileUnit() {//编译单元
        while (!getToken(index + 2).getType().equals("LPARENT")) {
            decl();//声明(读不到到左括号就是声明)
        }
        while (!getToken(index + 1).getType().equals("MAINTK")) {
            funcDef();//函数定义（读不到main就是函数定义）
        }//这个地方不能用if
        mainFuncDef();//主函数定义
        //outputs.add("<CompUnit>");
    }

    private void decl() {
        if (getToken().getValue().equals("const")) {
            constDecl();//常量声明
        } else {
            varDecl();//变量声明
        }
    }

    private void constDecl() {//常量声明
        if (getToken().getValue().equals("const")) {
            //outputs.add(getToken().toString());
            nextToken();
            bType();
            constDef();
            while (getToken().getValue().equals(",")) {
                //outputs.add(getToken().toString());
                nextToken();
                constDef();
            }
            if (getToken().getValue().equals(";")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " i");//i类错误没加分号
            }
        } else {
            error();
        }
        outputs.add("<ConstDecl>");
    }

    private void bType() {
        if (getToken().getValue().equals("int")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
    }

    private void constDef() {
        Symbol symbol = new Symbol();
        symbol.setType(1);
        int level = 0;
        if (getToken().getType().equals("IDENFR")) {
            symbol.setName(getToken().getValue());
            symbol.setPosition(getToken().getPosition());
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        while (getToken().getValue().equals("[")) {
            level++;
            outputs.add(getToken().toString());
            nextToken();
            constExp();
            if (getToken().getValue().equals("]")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " k");//k类错误，缺少]
            }
        }
        symbol.setLevel(level);
        if (getToken().getValue().equals("=")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        constInitVal();//没考虑到常量值不会变的问题，没有记录常量初值
        if (numTable.contain(symbol.getName(), true)) {
            hasError = true;
            System.out.println(symbol.getPosition() + " b"); //b类错误，重定义
        } else {
            numTable.add(symbol);//把这个常量加到符号表中
        }
        outputs.add("<ConstDef>");
    }

    private void constInitVal() {
        if (getToken().getValue().equals("{")) {
            outputs.add(getToken().toString());
            nextToken();
            if (getToken().getValue().equals("}")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                constInitVal();
                while (getToken().getValue().equals(",")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    constInitVal();
                }
                if (getToken().getValue().equals("}")) {
                    outputs.add(getToken().toString());
                    nextToken();
                } else {
                    error();
                }
            }
        } else {
            constExp();
        }
        outputs.add("<ConstInitVal>");
    }

    private void constExp() {//常量表达式
        addExp();
        outputs.add("<ConstExp>");
    }

    private void varDecl() {//变量声明
        bType();//int
        varDef();
        while (getToken().getValue().equals(",")) {
            outputs.add(getToken().toString());
            nextToken();
            varDef();
        }
        if (getToken().getValue().equals(";")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            hasError = true;
            System.out.println(getToken(index - 1).getPosition() + " i");//i类错误，没加分号
        }
        outputs.add("<VarDecl>");
    }

    private void varDef() {//变量定义
        Symbol symbol = new Symbol();
        symbol.setType(2);
        int level = 0;
        if (getToken().getType().equals("IDENFR")) {
            symbol.setName(getToken().getValue());
            symbol.setPosition(getToken().getPosition());
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        while (getToken().getValue().equals("[")) {
            level++;
            outputs.add(getToken().toString());
            nextToken();
            constExp();
            if (getToken().getValue().equals("]")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " k");//k型错误，没加]
            }
        }
        symbol.setLevel(level);
        if (getToken().getValue().equals("=")) {
            outputs.add(getToken().toString());
            nextToken();
            initVal();//变量初值
        }
        if (numTable.contain(symbol.getName(), true)) {
            hasError = true;
            System.out.println(symbol.getPosition() + " b");//b类错误，名字重定义
        } else {
            numTable.add(symbol);
        }
        outputs.add("<VarDef>");
    }

    private void initVal() {//变量初值
        if (getToken().getValue().equals("{")) {
            outputs.add(getToken().toString());
            nextToken();
            if (getToken().getValue().equals("}")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                initVal();
                while (getToken().getValue().equals(",")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    initVal();
                }
                if (getToken().getValue().equals("}")) {
                    outputs.add(getToken().toString());
                    nextToken();
                } else {
                    error();
                }
            }
        } else {
            exp();
        }
        outputs.add("<InitVal>");
    }

    private void exp() {//表达式
        addExp();
        outputs.add("<Exp>");
    }

    private void funcDef() {//函数定义
        SymbolTable numberTable = new SymbolTable();
        SymbolTable preTable = new SymbolTable();
        preTable = numTable;
        numberTable.setPrev(numTable);//新开一个符号表
        numTable = numberTable;//更新掉原来的符号表
        Symbol func = new Symbol();
        func.setType(3);//符号类别设置为函数类
        //String returnType = "";//函数返回值类型
        ArrayList<Symbol> parameters = new ArrayList<>();//函数的形参列表
        nowFuncType = funcType();//获取函数返回值类型
        func.setBack(nowFuncType);//设置函数返回类型
        //Table.SymbolTable symbolTable1 = new Table.SymbolTable();
        //symbolTable1.setPrev(symbolTable);
        //symbolTables.add(symbolTable1);//新建一个符号表，承继原始传进来的符号表
        if (getToken().getType().equals("IDENFR")) {
            func.setName(getToken().getValue());//设置函数名
            func.setPosition(getToken().getPosition());
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        if (getToken().getValue().equals("(")) {
            isInFunc = true;
            outputs.add(getToken().toString());
            nextToken();
            if (!getToken().getValue().equals(")")) {
                funcFParams(parameters);
                if (getToken().getValue().equals(")")) {
                    outputs.add(getToken().toString());
                    nextToken();
                } else {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " j");//j类错误，少)
                }
            } else {
                outputs.add(getToken().toString());
                nextToken();
            }
        }
        if (funcTable.contain(func.getName(), true)) {
            hasError = true;
            System.out.println(func.getPosition() + " b");//函数名字重定义，b类错误
        } else if (preTable.contain(func.getName(), true)) {
            hasError = true;
            System.out.println(func.getPosition() + " b");//函数名字重定义，b类错误
        } else {
            func.setParameters(parameters);
            funcTable.add(func);
        }
        //Table.SymbolTable symbolTable2 = new Table.SymbolTable();
        //symbolTable.setNext(symbolTable1);
        //symbolTable2.setPrev(symbolTable1);
        //symbolTables.add(symbolTable2);
        isInFunc = true;
        isOutFunc = true;
        block();//这个symbolTable2继承自symbolTable1
        if (nowFuncType.equals("int") && !hasReturn) {
            hasError = true;
            System.out.println(getToken(index - 1).getPosition() + " g");// 缺少return语句，g类错误
        }
        //numTable = numTable.getPrev();
        outputs.add("<FuncDef>");
    }

    private void funcFParams(ArrayList<Symbol> parameters) {//函数形参列表
        funcFParam(parameters);
        while (getToken().getValue().equals(",")) {
            outputs.add(getToken().toString());
            nextToken();
            funcFParam(parameters);
        }
        outputs.add("<FuncFParams>");
    }

    private void funcFParam(ArrayList<Symbol> parameters) {
        bType();
        if (getToken().getType().equals("IDENFR")) {
            outputs.add(getToken().toString());
            Symbol symbol = new Symbol();
            symbol.setName(getToken().getValue());
            symbol.setPosition(getToken().getPosition());
            int level = 0;
            nextToken();
            if (getToken().getValue().equals("[")) {
                level++;
                outputs.add(getToken().toString());
                nextToken();
                if (getToken().getValue().equals("]")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    if (getToken().getValue().equals("[")) {
                        level++;
                        outputs.add(getToken().toString());
                        nextToken();
                        constExp();
                        if (getToken().getValue().equals("]")) {
                            outputs.add(getToken().toString());
                            nextToken();
                        } else {
                            hasError = true;
                            System.out.println(getToken(index - 1).getPosition() + " k");//k型错误，少]
                        }
                    }
                } else {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " k");//k型错误，少]
                }
            }
            symbol.setLevel(level);
            parameters.add(symbol);
            if (numTable.contain(symbol.getName(), true)) {
                hasError = true;
                System.out.println(symbol.getPosition() + " b");//b类错误，重定义
            } else {
                numTable.add(symbol);
            }
            outputs.add("<FuncFParam>");
        } else {
            error();
        }
    }

    private void block() {
        int flag = 0;
        if (getToken().getValue().equals("{")) {
            if (forLevel0 != 0) {
                forLevel++;
            }
            if (!isInFunc) {
                SymbolTable newNemTable = new SymbolTable();
                newNemTable.setPrev(numTable);
                numTable = newNemTable;
            }
            if (isInFunc) {
                isInFunc = false;
            }
            outputs.add(getToken().toString());
            nextToken();
            if (getToken().getValue().equals("}")) {
                if (forLevel0 != 0) {
                    forLevel--;
                    if (forLevel == 0) {
                        forLevel0 = 0;
                    }
                }
                hasReturn = false;//直接{}，没有return语句
                outputs.add(getToken().toString());
                nextToken();
                outputs.add("<Block>");
                numTable = numTable.getPrev();
                return;
            }
            //if (isInFunc) {
            //    isInFunc = false;
            //} else {
            //    Table.SymbolTable nowNumTable = new Table.SymbolTable();
            //    nowNumTable.setPrev(numTable);
            //    numTable = nowNumTable;
            //}
            hasReturn = false;
            blockItem();
            while (!getToken().getValue().equals("}")) {
                hasReturn = false;
                flag = 1;
                blockItem();
                if (getToken().getValue().equals("}")) {
                    if (forLevel0 != 0) {
                        forLevel--;
                        if (forLevel == 0) {
                            forLevel0 = 0;
                        }
                    }
                    numTable = numTable.getPrev();
                    outputs.add(getToken().toString());
                    nextToken();
                    break;
                }
            }
            if (flag == 0 && getToken().getValue().equals("}")) {
                if (forLevel != 0) {
                    forLevel--;
                    if (forLevel == 0) {
                        forLevel0 = 0;
                    }
                }
                outputs.add(getToken().toString());
                nextToken();
                numTable = numTable.getPrev();//跳回外层
            } else if (flag == 0) {
                error();
            }
        } else {
            error();
        }
        //symbolTables.add(symbolTable);
        outputs.add("<Block>");
    }

    private void blockItem() {
        if (getToken().getValue().equals("int") || getToken().getValue().equals("const")) {
            decl();
        } else {
            stmt();
        }
        //outputs.add("<BlockItem>");
    }

    private void stmt() {//语句
        if (getToken().getValue().equals("break")) {//break;
            outputs.add(getToken().toString());
            nextToken();
            if (forLevel == 0 && forLevel0 == 0) {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " m");//没有for直接return，m型错误
            }
            if (getToken().getValue().equals(";")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " i");//没加;，i类错误
            }
        } else if (getToken().getValue().equals("continue")) {//continue;
            outputs.add(getToken().toString());
            nextToken();
            if (forLevel == 0 && forLevel0 == 0) {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " m");//没有for直接continue，m型错误
            }
            if (getToken().getValue().equals(";")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " i");//没加;，i类错误
            }
        } else if (getToken().getValue().equals("return")) {//return[exp];
            int position = getToken().getPosition();
            if (!isCond) {
                hasReturn = true;
            }
            outputs.add(getToken().toString());
            nextToken();
            if (!getToken().getValue().equals(";")) {
                if (!getToken().getValue().equals("}")) {
                    exp();
                    if (nowFuncType.equals("void")) {
                        hasError = true;
                        System.out.println(position + " f");//void类型函数有输出，f型错误
                    }
                }
                if (getToken().getValue().equals(";")) {
                    outputs.add(getToken().toString());
                    nextToken();
                } else {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " i");//没加；，i类错误
                }
            } else {
                if (nowFuncType.equals("int")) {
                    hasError = true;
                    System.out.println(position + " g");//int类型无输出，g型错误
                }
                outputs.add(getToken().toString());
                nextToken();
            }
        } else if (getToken().getValue().equals("{")) {//block
            isCond = true;
            //Table.SymbolTable symbolTable1 = new Table.SymbolTable();
            //symbolTable1.setPrev(symbolTable);
            //symbolTable.setNext(symbolTable1);
            //symbolTables.add(symbolTable1);
            block();
            isCond = false;
        } else if (getToken().getValue().equals("printf")) {//printf
            int formatNum = 0;
            int expNum = 0;
            outputs.add(getToken().toString());
            nextToken();
            if (getToken().getValue().equals("(")) {
                outputs.add(getToken().toString());
                nextToken();
                formatNum = formatString();
                while (getToken().getValue().equals(",")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    exp();
                    expNum++;
                }
                if (getToken().getValue().equals(")")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    if (getToken().getValue().equals(";")) {
                        outputs.add(getToken().toString());
                        nextToken();
                    } else {
                        hasError = true;
                        System.out.println(getToken(index - 1).getPosition() + " i");//没；，i类错误
                    }
                } else {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " j");//没），j类错误
                }
            } else {
                error();
            }
            if (formatNum != expNum) {
                hasError = true;
                System.out.println(getToken(index - 1).getPosition() + " l");//l类错误，打印元素个数和表达式个数不匹配
            }
        } else if (getToken().getValue().equals("for")) {//for
            forLevel0 = 1;
            outputs.add(getToken().toString());
            nextToken();
            if (getToken().getValue().equals("(")) {//for(
                outputs.add(getToken().toString());
                nextToken();
                if (!getToken().getValue().equals(";")) {//for(<forStmt>
                    forStmt();
                    if (getToken().getValue().equals(";")) {//for(<forStmt>;
                        outputs.add(getToken().toString());
                        nextToken();
                        if (!getToken().getValue().equals(";")) {//for(<forStmt>;<cond>
                            cond();
                            if (getToken().getValue().equals(";")) {//for(<forStmt>;<cond>;
                                outputs.add(getToken().toString());
                                nextToken();
                                if (!getToken().getValue().equals(")")) {//for(<forStmt>;<cond>;<forStmt>
                                    forStmt();
                                    if (getToken().getValue().equals(")")) {//for(<forStmt>;<cond>;<forStmt>)
                                        outputs.add(getToken().toString());
                                        nextToken();
                                    } else {
                                        error();
                                    }
                                } else {//for(<forStmt>;<cond>;)
                                    outputs.add(getToken().toString());
                                    nextToken();
                                }
                            } else {
                                error();
                            }
                        } else {//for(<forStmt>;;
                            outputs.add(getToken().toString());
                            nextToken();
                            if (!getToken().getValue().equals(")")) {//for(<forStmt>;;<forStmt>
                                forStmt();
                                if (getToken().getValue().equals(")")) {//for(<forStmt>;;<forStmt>)
                                    outputs.add(getToken().toString());
                                    nextToken();
                                } else {
                                    error();
                                }
                            } else {//for(<forStmt;;)
                                outputs.add(getToken().toString());
                                nextToken();
                            }
                        }
                    } else {
                        error();
                    }
                } else {//for(;
                    outputs.add(getToken().toString());
                    nextToken();
                    if (!getToken().getValue().equals(";")) {//for(;<cond>
                        cond();
                        if (getToken().getValue().equals(";")) {//for(;<cond>;
                            outputs.add(getToken().toString());
                            nextToken();
                            if (!getToken().getValue().equals(")")) {//for(;<cond>;<forStmt>
                                forStmt();
                                if (getToken().getValue().equals(")")) {//for(;<cond>;<forStmt>)
                                    outputs.add(getToken().toString());
                                    nextToken();
                                } else {
                                    error();
                                }
                            } else {//for(;<cond>;)
                                outputs.add(getToken().toString());
                                nextToken();
                            }
                        } else {
                            error();
                        }
                    } else {
                        outputs.add(getToken().toString());//for(;;
                        nextToken();
                        if (!getToken().getValue().equals(")")) {//for(;;<forStmt>
                            forStmt();
                            if (getToken().getValue().equals(")")) {//for(;;<forStmt>)
                                outputs.add(getToken().toString());
                                nextToken();
                            } else {
                                error();
                            }
                        } else {//for(;;)
                            outputs.add(getToken().toString());
                            nextToken();
                        }
                    }
                }
            } else {
                error();
            }
            stmt();
        } else if (getToken().getValue().equals("if")) {//if
            isCond = true;
            outputs.add(getToken().toString());
            nextToken();
            if (getToken().getValue().equals("(")) {
                outputs.add(getToken().toString());
                nextToken();
                cond();
                if (getToken().getValue().equals(")")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    stmt();
                    if (getToken().getValue().equals("else")) {
                        outputs.add(getToken().toString());
                        nextToken();
                        stmt();
                    }
                } else {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " j");//缺），j类错误
                }
            } else {
                error();
            }
            isCond = false;//离开if
        } else if (getToken().getValue().equals(";")) {//无exp,直接加;的情况
            outputs.add(getToken().toString());
            nextToken();
        } else if (isLVal()) {//这里还有一个不能改变常量的值，h性错误可能出现
            int lValType;
            int position = getToken().getPosition();
            lValType = lVal();
            if (lValType == 1) {
                hasError = true;
                System.out.println(position + " h");//常量赋值，h型错误
            }
            if (getToken().getValue().equals("=")) {
                outputs.add(getToken().toString());
                nextToken();
                if (getToken().getValue().equals("getint")) {
                    outputs.add(getToken().toString());
                    nextToken();
                    if (getToken().getValue().equals("(")) {
                        outputs.add(getToken().toString());
                        nextToken();
                        if (getToken().getValue().equals(")")) {
                            outputs.add(getToken().toString());
                            nextToken();
                            if (getToken().getValue().equals(";")) {
                                outputs.add(getToken().toString());
                                nextToken();
                            } else {
                                hasError = true;
                                System.out.println(getToken(index - 1).getPosition() + " i");//没加;,i型错误
                            }
                        } else {
                            hasError = true;
                            System.out.println(getToken(index - 1).getPosition() + " j");//没加), j型错误
                        }
                    } else {
                        error();
                    }
                } else {
                    exp();
                    if (getToken().getValue().equals(";")) {
                        outputs.add(getToken().toString());
                        nextToken();
                    } else {
                        hasError = true;
                        System.out.println(getToken(index - 1).getPosition() + " i");//缺分号，i类错误
                    }
                }
            } else {
                error();
            }
        } else {
            exp();
            if (getToken().getValue().equals(";")) {
                outputs.add(getToken().toString());
                nextToken();
            }
        }
        outputs.add("<Stmt>");
    }

    private int lVal() {
        int type = 2;
        Symbol num = null;
        if (getToken().getType().equals("IDENFR")) {
            if (!numTable.contain(getToken().getValue(), false) ) {
                hasError = true;
                System.out.println(getToken().getPosition() + " c");//c类错误，未定义
                nextToken();
            } else {
                num = numTable.getSymbol(getToken().getValue());
                outputs.add(getToken().toString());
                if (changeLevel == 0) {
                    this.level = num.getLevel();
                }
                nextToken();
            }
            while (getToken().getValue().equals("[")) {
                if (changeLevel == 0) {
                    this.level--;
                }
                outputs.add(getToken().toString());
                nextToken();
                changeLevel = 1;
                exp();
                changeLevel = 0;
                if (getToken().getValue().equals("]")) {
                    outputs.add(getToken().toString());
                    nextToken();
                } else {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " k");//没加]. k类错误
                }
            }
        } else {
            error();
        }
        outputs.add("<LVal>");
        if (num != null) {
            type = num.getType();
        }
        return type;
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
                    //System.out.println(getToken(index));
                    //System.out.println(getToken(index).toString());
                    if (getToken(index).getValue().equals("]")) {
                        level--;
                        if (getToken(index + 1).getValue().equals("[")) {
                            level++;
                            //index++;
                        }
                    } else if (getToken(index).getValue().equals("[")) {
                        level++;
                    } else if (getToken(index).getValue().equals("=")) {
                        flag = 1;
                        break;
                    } else if (getToken(index).getValue().equals(";")) {
                        break;
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

    private void primaryExp() {
        if (getToken().getValue().equals("(")) {
            outputs.add(getToken().toString());
            nextToken();
            exp();
            if (getToken().getValue().equals(")")) {
                outputs.add(getToken().toString());
                nextToken();
            } else {
                error();
            }
        } else if (getToken().getType().equals("INTCON")) {
            outputs.add(getToken().toString());
            outputs.add("<Number>");
            nextToken();
        } else {
            lVal();
        }
        outputs.add("<PrimaryExp>");
    }

    private void unaryExp() {//这个要好好理一下
        String funName = "";
        Symbol func = null;
        int position = 0;
        if (getToken().getValue().equals("+") || getToken().getValue().equals("-")
                || getToken().getValue().equals("!")) {
            unaryOp();
            unaryExp();
        } else if (getToken().getType().equals("IDENFR") && getToken(index + 1).getValue().equals("(")) {
            position = getToken().getPosition();
            if (!funcTable.contain(getToken().getValue(), false)) {
                hasError = true;
                System.out.println(getToken().getPosition() + " c");//c类错误，未定义
                nextToken();//就算没有定义，也要把整个的函数解析过去，函数也有可能出现不加右括号的错误
            } else {
                func = funcTable.getFunc(getToken().getValue());
                funName = getToken().getValue();
                outputs.add(getToken().toString());
                nextToken();
            }
            outputs.add(getToken().toString());
            nextToken();
            int num = 0;
            if (getToken().getValue().equals(")")) {
                outputs.add(getToken().toString());
                nextToken();
                num = 0;
            } else {
                if (getToken().getValue().equals(";")) {
                    hasError = true;
                    System.out.println(getToken(index - 1).getPosition() + " j");//没有),直接;, j类错误
                } else {
                    num = funcRParams();
                    if (getToken().getValue().equals(")")) {
                        outputs.add(getToken().toString());
                        nextToken();
                    } else {
                        hasError = true;
                        System.out.println(getToken(index - 1).getPosition() + " j");//没加), j类错误
                    }
                }
            }
            if (func != null) {
                if (funcTable.getFunc(funName).getParameters().size() != num) {
                    hasError = true;
                    System.out.println(position + " d");//d类错误，形参实参个数不匹配
                } else {
                    if (funcR.size() != 0) {
                        for (int i = 0; i < funcR.get(funcR.size() - 1).size() && i < func.getParameters().size(); i++) {
                            if (funcR.get(funcR.size() - 1).get(i) != func.getParameters().get(i).getLevel()) {
                                System.out.println(position + " e");//e型错误，形参实参类型不匹配
                                hasError = true;
                            }
                        }
                        funcR.remove(funcR.size() - 1);
                    }
                }
            }
            if (func != null && func.getBack() == 1) {
                this.level = 0;
            } else if (func != null && func.getBack() == 2) {
                this.level = -1;
            }
        } else {
            primaryExp();
        }
        outputs.add("<UnaryExp>");
    }

    private int funcRParams() {
        funcR.add(new ArrayList<>());
        int num = 1;
        this.level = 0;
        exp();
        int x = level;
        funcR.get(funcR.size() - 1).add(x);
        while (getToken().getValue().equals(",")) {
            outputs.add(getToken().toString());
            nextToken();
            this.level = 0;
            exp();
            int y = level;
            if (funcR.size() == 0) {
                funcR.add(new ArrayList<>());
                funcR.get(funcR.size() - 1).add(y);
                num++;
            } else {
                funcR.get(funcR.size() - 1).add(y);
                num++;
            }

        }
        outputs.add("<FuncRParams>");
        return num;
    }

    private void unaryOp() {
        if (getToken().getValue().equals("+") || getToken().getValue().equals("-")
                || getToken().getValue().equals("!")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        outputs.add("<UnaryOp>");
    }

    private void mulExp() {
        unaryExp();
        while (getToken().getValue().equals("*") ||
                getToken().getValue().equals("/") ||
                getToken().getValue().equals("%")) {
            outputs.add("<MulExp>");
            outputs.add(getToken().toString());
            nextToken();
            unaryExp();
        }
        outputs.add("<MulExp>");
    }

    private void addExp() {
        mulExp();
        while (getToken().getValue().equals("+") || getToken().getValue().equals("-")) {
            outputs.add("<AddExp>");
            outputs.add(getToken().toString());
            nextToken();
            mulExp();
        }
        outputs.add("<AddExp>");
    }

    private void relExp() {
        addExp();
        while (getToken().getValue().equals("<") ||
                getToken().getValue().equals(">") ||
                getToken().getValue().equals(">=") ||
                getToken().getValue().equals("<=")) {
            outputs.add("<RelExp>");
            outputs.add(getToken().toString());
            nextToken();
            addExp();
        }
        outputs.add("<RelExp>");
    }

    private void eqExp() {
        relExp();
        while (getToken().getValue().equals("==") || getToken().getValue().equals("!=")) {
            outputs.add("<EqExp>");
            outputs.add(getToken().toString());
            nextToken();
            relExp();
        }
        outputs.add("<EqExp>");
    }

    private void lAndExp() {
        eqExp();
        while (getToken().getValue().equals("&&")) {
            outputs.add("<LAndExp>");
            outputs.add(getToken().toString());
            nextToken();
            eqExp();
        }
        outputs.add("<LAndExp>");
    }

    private void lOrExp() {
        lAndExp();
        while (getToken().getValue().equals("||")) {
            outputs.add("<LOrExp>");
            outputs.add(getToken().toString());
            nextToken();
            lAndExp();
        }
        outputs.add("<LOrExp>");
    }

    private void cond() {
        lOrExp();
        outputs.add("<Cond>");
    }

    private void forStmt() {
        if (getToken().getType().equals("IDENFR")) {
            Symbol num = numTable.getSymbol(getToken().getValue());
            if (num.getType() == 1) {
                hasError = true;
                System.out.println(getToken().getPosition() + " h");//常量赋值，h型错误
            }
            lVal();
            if (getToken().getValue().equals("=")) {
                outputs.add(getToken().toString());
                nextToken();
                exp();
            } else {
                error();
            }
        } else {
            error();
        }
        outputs.add("<ForStmt>");
    }

    private int formatString() {
        int num = 0;
        if (getToken().getType().equals("STRCON")) {
            if (isStrcon(getToken().getValue())) {
                String line = getToken().getValue();
                for (int i = 0; i < line.length() - 1; i++) {
                    if (line.charAt(i) == '%' && line.charAt(i + 1) == 'd') {
                        num++;
                    }
                }
                outputs.add(getToken().toString());
                nextToken();
            } else {
                hasError = true;
                System.out.println(getToken().getPosition() + " a");//a类错误情况2：词语为引用类，但引号中的内容格式不符合要求
                String line = getToken().getValue();
                for (int i = 0; i < line.length() - 1; i++) {
                    if (line.charAt(i) == '%' && line.charAt(i + 1) == 'd') {
                        num++;
                    }
                }
                nextToken();
            }
        } else {
            hasError = true;
            System.out.println(getToken().getPosition() + " a");//a类错误情况1：词语非引用类
            nextToken();
            //error();
        }
        return num;
    }

    private String funcType() {
        if (getToken().getValue().equals("int")) {
            outputs.add(getToken().toString());
            nextToken();
            outputs.add("<FuncType>");
            return "int";
        } else if (getToken().getValue().equals("void")) {
            outputs.add(getToken().toString());
            nextToken();
            outputs.add("<FuncType>");
            return "void";
        } else {
            error();
            return "";
        }
    }

    private void mainFuncDef(){
        int position = 0;
        if (getToken().getValue().equals("int")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        if (getToken().getValue().equals("main")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        }
        if (getToken().getValue().equals("(")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            error();
        } if (getToken().getValue().equals(")")) {
            outputs.add(getToken().toString());
            nextToken();
        } else {
            hasError = true;
            System.out.println(getToken(index - 1).getPosition() + " j"); //缺），j类错误
        }
        nowFuncType = "int";//设置当前返回值类型为int
        block();
        if (!hasReturn) {
            hasError = true;
            System.out.println(getToken(index - 1).getPosition() + " g");//缺return语句，g类错误
        }
        outputs.add("<MainFuncDef>");
    }

    public ArrayList<Sentence> getSentences() {
        return sentences;
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

    private void nextToken() {
        if (index + 1 < tokens.size()) {
            index++;
        } else {
            index = tokens.size();//溢出
        }
    }

    private void error() {

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

    public boolean isHasError() {
        return hasError;
    }
}