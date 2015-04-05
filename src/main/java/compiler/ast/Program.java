package compiler.ast;

import compiler.ast.atom.Var;
import compiler.ast.statement.Statement;
import compiler.pcode.*;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Strings;

import java.util.HashMap;

public class Program implements PCodeGenable {
    public final String programName;
    public final List<Var> declarations;
    public final List<Statement> statements;
    public static final int startingAddress = 5;

    public Program(String programName, List<Var> declarations, List<Statement> statements) {
        this.programName = programName;
        this.declarations = declarations;
        this.statements = statements;
    }

    public String toString() {
        String header = "Program " + programName + ": \r\n";
        String declarations =
                "Variables: \r\n" +
                        (this.declarations.isEmpty() ? "  No variables." :
                                (Strings.indent(2, Strings.mkString(
                                        "", "\r\n", "",
                                        this.declarations,
                                        new Function<Var, String>() {
                                            public String apply(Var var) {
                                                return var.declarationString();
                                            }
                                        }))))
                        + "\r\n";
        String program =
                "Program: \r\n" +
                        (this.statements.isEmpty() ? "  Empty program" :
                                Strings.indent(2,
                                        Strings.mkString(
                                                "", "\r\n", "",
                                                statements
                                        )))
                        + "\r\n";
        return header + declarations + program;
    }

    public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
        return statements.flatMap(Statement.genCode(symbolTable, labelGenerator));
    }
    public List<PCommand> genPCode(int startingAddress){
        SymbolTable symbolTable = SymbolTable.assignAddresses(declarations, startingAddress);
        LabelGenerator labelGenerator = new CounterLabelGenerator();
        return genPCode(symbolTable, labelGenerator);
    }
    public List<PCommand> genPCode(){
        return genPCode(startingAddress);
    }

    public static String generateProgramString(List<PCommand> commands){
        return Strings.mkString("", "\r\n", "", commands, new Function<PCommand, String>() {
            @Override
            public String apply(PCommand command) {
                return command.toPCodeString();
            }
        });
    }
}
