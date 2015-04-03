package compiler.ast;

import compiler.ast.atom.Var;
import compiler.ast.statement.Statement;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Strings;

public class Program {
    public final String programName;
    public final List<Var> declarations;
    public final List<Statement> statements;

    public Program(String programName, List<Var> declarations, List<Statement> statements) {
        this.programName = programName;
        this.declarations = declarations;
        this.statements = statements;
    }

    @Override
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
}
