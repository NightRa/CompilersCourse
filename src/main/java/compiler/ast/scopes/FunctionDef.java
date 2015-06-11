package compiler.ast.scopes;

import compiler.ast.Type;
import compiler.ast.statement.Statement;
import compiler.util.List;
import compiler.util.Tuple2;

public class FunctionDef implements DefinableScope {
    public final String name;
    public final List<Declaration> declarations;
    public final List<DefinableScope> funcs;
    public final List<Tuple2<String, Type>> inputs;
    public final Type.PrimitiveType output;
    public final List<Statement> body;

    public FunctionDef(String name, List<Declaration> declarations, List<DefinableScope> funcs, List<Tuple2<String, Type>> inputs, Type.PrimitiveType output, List<Statement> body) {
        this.name = name;
        this.declarations = declarations;
        this.funcs = funcs;
        this.inputs = inputs;
        this.output = output;
        this.body = body;
    }
}
