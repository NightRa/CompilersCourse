package compiler;

import compiler.ast.Program;
import compiler.pcode.PCommand;
import compiler.untypedAst.AST;
import compiler.untypedAst.FromUntypedAST;
import compiler.util.List;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        /*Scanner in = new Scanner(System.in);
        int treeNumber = in.nextInt();
        String filePath = "example/tree" + treeNumber;
        File file = new File(filePath);
        Scanner fileIn = new Scanner(file);*/
        Scanner fileIn = new Scanner(System.in);
        AST ast = AST.createAST(fileIn);
        Program program = FromUntypedAST.fromUntyped(ast);
        List<PCommand> commands = program.genPCode();
        String outPCode = Program.generateProgramString(commands);
        System.out.println(outPCode);
    }
}
