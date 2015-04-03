package compiler.ast.atom;

import compiler.ast.PCodeType;
import compiler.ast.expr.Expr;

public abstract class Atom<A> extends Expr<A> {
    public abstract PCodeType type();
}
