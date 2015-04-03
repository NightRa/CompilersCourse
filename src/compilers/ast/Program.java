package compilers.ast;

import compilers.ast.atom.Var;
import compilers.ast.statement.Statement;
import compilers.util.List;

public class Program {
    public final String programName;
    public final List<Var> declarations;
    public final List<Statement> statements;

    public Program(String programName, List<Var> declarations, List<Statement> statements) {
        this.programName = programName;
        this.declarations = declarations;
        this.statements = statements;
    }
}
