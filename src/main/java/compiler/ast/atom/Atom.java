package compiler.ast.atom;

import compiler.ast.PCodeType;
import compiler.ast.expr.Expr;

import java.util.Map;

public abstract class Atom<A> extends Expr<A> {
    public abstract PCodeType rawType(Map<String, PCodeType> typeTable);
    public int precedence() {
        return 0;
    }
}
