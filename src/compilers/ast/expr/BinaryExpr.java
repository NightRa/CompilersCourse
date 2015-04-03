package compilers.ast.expr;

import compilers.ast.PCodeType;

/**
* A function (A,B) => C
**/
public abstract class BinaryExpr<A, B, C> extends Expr<C> {
    public final Expr<A> left;
    public final Expr<B> right;

    protected BinaryExpr(Expr<A> left, Expr<B> right) {
        this.left = left;
        this.right = right;
    }

    public static abstract class ClosedBinaryExpr<A> extends BinaryExpr<A, A, A> {
        protected ClosedBinaryExpr(Expr<A> left, Expr<A> right) {
            super(left, right);
        }

        /*TODO: If types of children are different, then the type() computation should be different*/
        public PCodeType type() {
            return left.type();
        }
    }
    public static abstract class ComparisonBinaryExpr<A> extends BinaryExpr<A, A, Boolean> {
        protected ComparisonBinaryExpr(Expr<A> left, Expr<A> right) {
            super(left, right);
        }

        public PCodeType type() {
            return PCodeType.BOOL;
        }
    }

    public static final class Plus<A> extends ClosedBinaryExpr<A> {
        public Plus(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class Minus<A> extends ClosedBinaryExpr<A> {
        public Minus(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class Mult<A> extends ClosedBinaryExpr<A> {
        public Mult(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class Div<A> extends ClosedBinaryExpr<A> {
        public Div(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }

    public static final class LT<A> extends ComparisonBinaryExpr<A> {
        public LT(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class GT<A> extends ComparisonBinaryExpr<A> {
        public GT(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class LE<A> extends ComparisonBinaryExpr<A> {
        public LE(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class GE<A> extends ComparisonBinaryExpr<A> {
        public GE(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }
    public static final class EQ<A> extends ComparisonBinaryExpr<A> {
        public EQ(Expr<A> left, Expr<A> right) {
            super(left, right);
        }
    }

    public static final class And extends ComparisonBinaryExpr<Boolean> {
        public And(Expr<Boolean> left, Expr<Boolean> right) {
            super(left, right);
        }
    }
    public static final class Or extends ComparisonBinaryExpr<Boolean> {
        public Or(Expr<Boolean> left, Expr<Boolean> right) {
            super(left, right);
        }
    }
}
