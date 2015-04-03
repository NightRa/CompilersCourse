package compiler.ast.expr;

import compiler.ast.PCodeType;

/**
 * A function A => A
 */
public abstract class UnaryExpr<A> extends Expr<A> {
    public final Expr<A> expr;

    protected UnaryExpr(Expr<A> expr) {
        this.expr = expr;
    }

    public static final class Neg<A> extends UnaryExpr<A> {
        public Neg(Expr<A> expr) {
            super(expr);
        }

        public PCodeType type() {
            return expr.type();
        }

        public String toString() {
            return "-"+precedenceParens(this.precedence(), expr);
        }
    }
    public static final class Not extends UnaryExpr<Boolean> {
        public Not(Expr<Boolean> expr) {
            super(expr);
        }

        public PCodeType type() {
            return PCodeType.Bool;
        }

        public String toString() {
            return "!"+precedenceParens(this.precedence(), expr);
        }
    }

    public int precedence() {
        return 1;
    }

    public static String precedenceParens(int precedence, Expr expr) {
        if (precedence < expr.precedence()) {
            return "(" + expr.toString() + ")";
        } else {
            return expr.toString();
        }
    }
}
