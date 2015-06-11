package compiler.ast.scopes;

import compiler.ast.Type;
import compiler.ast.statement.Statement;
import compiler.util.List;

public class FunctionDef implements FunctionsAndProcedures {
    public final String name;
    public final List<Declaration> declarations;
    public final List<FunctionsAndProcedures> funcs;
    public final List<FunctionParameter> inputs;
    public final Type.PrimitiveType output;
    public final List<Statement> body;

    public FunctionDef(String name, List<Declaration> declarations, List<FunctionsAndProcedures> funcs, List<FunctionParameter> inputs, Type.PrimitiveType output, List<Statement> body) {
        this.name = name;
        this.declarations = declarations;
        this.funcs = funcs;
        this.inputs = inputs;
        this.output = output;
        this.body = body;
    }
}
