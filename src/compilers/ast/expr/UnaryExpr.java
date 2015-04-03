package compilers.ast.expr;

import compilers.ast.PCodeType;

/**
 * A function A => A
 */
public abstract class UnaryExpr<A> extends Expr<A> {
    public final Expr<A> expr;

    protected UnaryExpr(Expr<A> expr) {
        this.expr = expr;
    }

    public static final class Neg<A> extends UnaryExpr<A> {
        protected Neg(Expr<A> expr) {
            super(expr);
        }

        public PCodeType type() {
            return expr.type();
        }
    }
    public static final class Not extends UnaryExpr<Boolean> {
        protected Not(Expr<Boolean> expr) {
            super(expr);
        }

        public PCodeType type() {
            return PCodeType.BOOL;
        }
    }
}
