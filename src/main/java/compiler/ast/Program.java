package compiler.ast;

import compiler.ast.atom.Var;
import compiler.ast.statement.Statement;
import compiler.pcode.CounterLabelGenerator;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Strings;

import java.util.Map;

public class Program {
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
        String declarationsString =
                "Variables: \r\n" +
                        (this.declarations.isEmpty() ? "  No variables." :
                                declarationsString(declarations))
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
        return header + declarationsString + program;
    }

    public static String declarationsString(List<Var> declarations) {
        return Strings.indent(2, Strings.mkString(
                "", "\r\n", "",
                declarations,
                new Function<Var, String>() {
                    public String apply(Var var) {
                        return var.declarationString();
                    }
                }));
    }

    public List<PCommand> genPCode(SymbolTable symbolTable, Map<String, PCodeType> typeTable, LabelGenerator labelGenerator) {
        return statements.flatMap(Statement.genCode(symbolTable, typeTable, labelGenerator));
    }
    public List<PCommand> genPCode(int startingAddress){
        Map<String, PCodeType> typeTable = TypeResolution.makeTypeTable(declarations);
        SymbolTable symbolTable = SymbolTable.assignAddresses(declarations, startingAddress, typeTable);
        LabelGenerator labelGenerator = new CounterLabelGenerator();
        return genPCode(symbolTable, typeTable, labelGenerator);
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
