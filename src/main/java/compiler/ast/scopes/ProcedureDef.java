package compiler.ast.scopes;

import compiler.ast.statement.Statement;
import compiler.util.List;

public class ProcedureDef implements FunctionsAndProcedures {
    public final String name;
    public final List<Declaration> declarations;
    public final List<FunctionsAndProcedures> funcs;
    public final List<FunctionParameter> inputs;
    // No output
    public final List<Statement> body;

    public ProcedureDef(String name, List<Declaration> declarations, List<FunctionsAndProcedures> funcs, List<FunctionParameter> inputs, List<Statement> body) {
        this.name = name;
        this.declarations = declarations;
        this.funcs = funcs;
        this.inputs = inputs;
        this.body = body;
    }
}
