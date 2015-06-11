package compiler.ast.scopes;

import compiler.ast.Type;
import compiler.ast.statement.Statement;
import compiler.util.List;
import compiler.util.Tuple2;

public class ProcedureDef implements DefinableScope {
    public final String name;
    public final List<Declaration> declarations;
    public final List<DefinableScope> funcs;
    public final List<Tuple2<String, Type>> inputs;
    // No output
    public final List<Statement> body;

    public ProcedureDef(String name, List<Declaration> declarations, List<DefinableScope> funcs, List<Tuple2<String, Type>> inputs, List<Statement> body) {
        this.name = name;
        this.declarations = declarations;
        this.funcs = funcs;
        this.inputs = inputs;
        this.body = body;
    }
}
