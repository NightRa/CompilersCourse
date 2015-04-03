package compiler.ast.expr;

import compiler.ast.PCodeType;

import static compiler.ast.expr.UnaryExpr.precedenceParens;

/**
 * A function (A,B) => C
 */
public abstract class BinaryExpr<A, B, C> extends Expr<C> {
    public final Expr<A> left;
    public final Expr<B> right;
    public final String symbol;
    protected BinaryExpr(Expr<A> left, Expr<B> right, String symbol) {
        this.left = left;
        this.right = right;
        this.symbol = symbol;
    }

    public static abstract class ClosedBinaryExpr<A> extends BinaryExpr<A, A, A> {
        protected ClosedBinaryExpr(Expr<A> left, Expr<A> right, String symbol) {
            super(left, right, symbol);
        }

        /*TODO: If types of children are different, then the type() computation should be different*/
        public PCodeType type() {
            return left.type();
        }
    }
    public static abstract class ComparisonBinaryExpr<A> extends BinaryExpr<A, A, Boolean> {
        protected ComparisonBinaryExpr(Expr<A> left, Expr<A> right, String symbol) {
            super(left, right, symbol);
        }

        public PCodeType type() {
            return PCodeType.Bool;
        }
    }

    public static final class Plus<A> extends ClosedBinaryExpr<A> {
        public Plus(Expr<A> left, Expr<A> right) {
            super(left, right, "+");
        }
        public int precedence() {
            return 3;
        }
    }
    public static final class Minus<A> extends ClosedBinaryExpr<A> {
        public Minus(Expr<A> left, Expr<A> right) {
            super(left, right, "-");
        }
        public int precedence() {
            return 3;
        }
    }
    public static final class Mult<A> extends ClosedBinaryExpr<A> {
        public Mult(Expr<A> left, Expr<A> right) {
            super(left, right, "*");
        }
        public int precedence() {
            return 2;
        }
    }
    public static final class Div<A> extends ClosedBinaryExpr<A> {
        public Div(Expr<A> left, Expr<A> right) {
            super(left, right, "/");
        }
        public int precedence() {
            return 2;
        }
    }

    public static final class LT<A> extends ComparisonBinaryExpr<A> {
        public LT(Expr<A> left, Expr<A> right) {
            super(left, right, "<");
        }
        public int precedence() {
            return 4;
        }
    }
    public static final class GT<A> extends ComparisonBinaryExpr<A> {
        public GT(Expr<A> left, Expr<A> right) {
            super(left, right, ">");
        }
        public int precedence() {
            return 4;
        }
    }
    public static final class LE<A> extends ComparisonBinaryExpr<A> {
        public LE(Expr<A> left, Expr<A> right) {
            super(left, right, "<=");
        }
        public int precedence() {
            return 4;
        }
    }
    public static final class GE<A> extends ComparisonBinaryExpr<A> {
        public GE(Expr<A> left, Expr<A> right) {
            super(left, right, ">=");
        }
        public int precedence() {
            return 4;
        }
    }
    public static final class EQ<A> extends ComparisonBinaryExpr<A> {
        public EQ(Expr<A> left, Expr<A> right) {
            super(left, right, "==");
        }
        public int precedence() {
            return 5;
        }
    }
    public static final class NEQ<A> extends ComparisonBinaryExpr<A> {
        public NEQ(Expr<A> left, Expr<A> right) {
            super(left, right, "!=");
        }
        public int precedence() {
            return 5;
        }
    }

    public static final class And extends ComparisonBinaryExpr<Boolean> {
        public And(Expr<Boolean> left, Expr<Boolean> right) {
            super(left, right, "&&");
        }
        public int precedence() {
            return 6;
        }
    }
    public static final class Or extends ComparisonBinaryExpr<Boolean> {
        public Or(Expr<Boolean> left, Expr<Boolean> right) {
            super(left, right, "||");
        }
        public int precedence() {
            return 7;
        }
    }

    public String toString() {
        return precedenceParens(this.precedence(), left) + " " + symbol + " " + precedenceParens(this.precedence(), right);
    }
}
