package compiler.ast.atom;

import compiler.ast.Type;
import compiler.ast.expr.Expr;

import java.util.Map;

public abstract class Atom<A> extends Expr<A> {
    public abstract Type rawType(Map<String, Type> typeTable);
    public int precedence() {
        return 0;
    }
}
