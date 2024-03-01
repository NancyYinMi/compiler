import MidCode.MidCode;
import Mips.Mips;
import Token.Token;

import java.io.*;
import java.util.ArrayList;

public class Compiler {
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    public static void main(String[] args) {
        ArrayList<Token> tokens;
        Lexer lexer;
        ArrayList<String> outputs = new ArrayList<>();
        String inputPath = "testfile.txt";
        String outputPath = "error.txt";
        StringBuffer sb = new StringBuffer();
        try {
            Compiler.readToBuffer(sb, inputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String source = sb.toString();

        lexer = new Lexer(source);
        lexer.analyzer();
        tokens = lexer.getTokens();
        Parser parser = new Parser(tokens, outputs);
        PrintStream out = null;
        try {
            out = new PrintStream(outputPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);
        //for (Token.Token token : tokens) {
        //    out.println(token.getType() + " " + token.getValue());
        //}
        parser.analyse();
        if (!parser.isHasError()) {
            outputPath = "mips.txt";
            try {
                out = new PrintStream(outputPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.setOut(out);
            ParserMid parserMid = new ParserMid(tokens);
            parserMid.analyse();

            Mips mips = new Mips(parserMid.getMidCodes(), parserMid.getLines());

            //parser.analyse();
        }


        //for (String output : outputs) {
        //    out.println(output);
        //}
        //ArrayList<Table.SymbolTable> symbolTables = parser.getSymbolTables();
        /*for (int i = 0; i < symbolTables.size(); i++) {
            for (Table.Symbol symbol : symbolTables.get(i).getSymbols()) {
                if (i == 0) {
                    System.out.println("table" + i + ": " + symbol.getName());
                } else {
                    System.out.println("table" + i + ": " + symbol.getName() + " preTable is table" + symbolTables.get(i).getPrev().getIndex());
                }
            }
        }*/
    }
}
