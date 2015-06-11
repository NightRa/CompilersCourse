package compiler.ast.scopes;

import compiler.ast.Type;

public class FunctionParameter {
    public final String name;
    public final Type type;
    public FunctionParameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
