package compiler;

import compiler.ast.scopes.Program;
import compiler.pcode.PCommand;
import compiler.untypedAst.AST;
import compiler.untypedAst.FromUntypedAST;
import compiler.util.List;

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
        // Program resolvedProgram = TypeResolution.resolveProgramTypes(program);
        // System.err.println("Resolved program: \r\n" + resolvedProgram.toString());
        List<PCommand> commands = program.genPCode();
        String outPCode = Program.generateProgramString(commands);
        System.out.println(outPCode);
        // Debugging only!
        // Pretty printing:
        // System.out.println("\r\nPretty Printing:");
        // System.out.println(program.toString());
    }
}
