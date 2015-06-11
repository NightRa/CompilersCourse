package compiler.ast.scopes;

import compiler.ast.Type;
import compiler.ast.Type.PType;
import compiler.util.Function;

public class Declaration {
    public final String name;
    public final PType type;
    public Declaration(String name, PType type) {
        this.name = name;
        this.type = type;
    }

    public String declarationString() {
        return name + ": " + type.toString();
    }

    public static final Function<Declaration, Type> declarationType = new Function<Declaration, Type>() {
        @Override
        public Type apply(Declaration declaration) {
            return declaration.type;
        }
    };

    public static final Function<Declaration, String> declarationName = new Function<Declaration, String>() {
        public String apply(Declaration declaration) {
            return declaration.name;
        }
    };
}
