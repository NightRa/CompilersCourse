package compiler.ast.expr;

import compiler.ast.PCodeType;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

import static compiler.ast.expr.UnaryExpr.precedenceParens;

/**
 * A function (A,B) => C
 */
public abstract class BinaryExpr<A, B, C> extends Expr<C> {
    public final Expr<A> left;
    public final Expr<B> right;
    public final String symbol;

    protected abstract PCommand operation();
    public List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, PCodeType> typeTable) {
        /**
         * <Gen left  expr.>
         * <Gen right expr.>
         * Binary op.
         **/
        List<PCommand> leftCommands = left.evaluateExpr(symbolTable, typeTable);
        List<PCommand> rightCommands = right.evaluateExpr(symbolTable, typeTable);
        List<PCommand> operation = List.single(operation());
        // TODO: Change list to something with a faster append, O(n) here!
        return leftCommands.append(rightCommands).append(operation);
    }
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
        public PCodeType rawType(Map<String, PCodeType> typeTable) {
            return left.rawType(typeTable);
        }
    }
    public static abstract class ComparisonBinaryExpr<A> extends BinaryExpr<A, A, Boolean> {
        protected ComparisonBinaryExpr(Expr<A> left, Expr<A> right, String symbol) {
            super(left, right, symbol);
        }

        public PCodeType rawType(Map<String, PCodeType> typeTable) {
            return PCodeType.Bool;
        }
    }

    public static ClosedBinaryExpr<Number> plus(Expr<Number> left, Expr<Number> right) {
        return new Plus(left, right);
    }

    public static ClosedBinaryExpr<Number> minus(Expr<Number> left, Expr<Number> right) {
        return new Minus(left, right);
    }

    public static ClosedBinaryExpr<Number> mult(Expr<Number> left, Expr<Number> right) {
        return new Mult(left, right);
    }

    public static ClosedBinaryExpr<Number> div(Expr<Number> left, Expr<Number> right) {
        return new Div(left, right);
    }

    public static final class Plus extends ClosedBinaryExpr<Number> {
        public Plus(Expr<Number> left, Expr<Number> right) {
            super(left, right, "+");
        }
        public int precedence() {
            return 3;
        }
        protected PCommand operation() {
            return new PCommand.ADDCommand();
        }
        @Override
        public Number eval() {
            return left.eval().doubleValue() + right.eval().doubleValue();
        }
    }
    public static final class Minus extends ClosedBinaryExpr<Number> {
        public Minus(Expr<Number> left, Expr<Number> right) {
            super(left, right, "-");
        }
        public int precedence() {
            return 3;
        }
        protected PCommand operation() {
            return new PCommand.SUBCommand();
        }
        @Override
        public Number eval() {
            return left.eval().doubleValue() - right.eval().doubleValue();
        }
    }
    public static final class Mult extends ClosedBinaryExpr<Number> {
        public Mult(Expr<Number> left, Expr<Number> right) {
            super(left, right, "*");
        }
        public int precedence() {
            return 2;
        }
        @Override
        public Number eval() {
            return left.eval().doubleValue() * right.eval().doubleValue();
        }
        protected PCommand operation() {
            return new PCommand.MULCommand();
        }
    }
    public static final class Div extends ClosedBinaryExpr<Number> {
        public Div(Expr<Number> left, Expr<Number> right) {
            super(left, right, "/");
        }
        public int precedence() {
            return 2;
        }
        protected PCommand operation() {
            return new PCommand.DIVCommand();
        }
        @Override
        public Number eval() {
            return left.eval().doubleValue() / right.eval().doubleValue();
        }
    }

    public static final class LT extends ComparisonBinaryExpr<Number> {
        public LT(Expr<Number> left, Expr<Number> right) {
            super(left, right, "<");
        }
        public int precedence() {
            return 4;
        }
        protected PCommand operation() {
            return new PCommand.LTCommand();
        }
        @Override
        public Boolean eval() {
            return left.eval().doubleValue() < right.eval().doubleValue();
        }
    }
    public static final class GT extends ComparisonBinaryExpr<Number> {
        public GT(Expr<Number> left, Expr<Number> right) {
            super(left, right, ">");
        }
        public int precedence() {
            return 4;
        }
        protected PCommand operation() {
            return new PCommand.GTCommand();
        }
        @Override
        public Boolean eval() {
            return left.eval().doubleValue() > right.eval().doubleValue();
        }
    }
    public static final class LE extends ComparisonBinaryExpr<Number> {
        public LE(Expr<Number> left, Expr<Number> right) {
            super(left, right, "<=");
        }
        public int precedence() {
            return 4;
        }
        protected PCommand operation() {
            return new PCommand.LECommand();
        }
        @Override
        public Boolean eval() {
            return left.eval().doubleValue() <= right.eval().doubleValue();
        }
    }
    public static final class GE extends ComparisonBinaryExpr<Number> {
        public GE(Expr<Number> left, Expr<Number> right) {
            super(left, right, ">=");
        }
        public int precedence() {
            return 4;
        }
        protected PCommand operation() {
            return new PCommand.GECommand();
        }
        @Override
        public Boolean eval() {
            return left.eval().doubleValue() >= right.eval().doubleValue();
        }
    }
    public static final class EQ extends ComparisonBinaryExpr<Number> {
        public EQ(Expr<Number> left, Expr<Number> right) {
            super(left, right, "==");
        }
        public int precedence() {
            return 5;
        }
        protected PCommand operation() {
            return new PCommand.EQCommand();
        }
        @Override
        public Boolean eval() {
            return left.eval().doubleValue() == right.eval().doubleValue();
        }
    }
    public static final class NEQ extends ComparisonBinaryExpr<Number> {
        public NEQ(Expr<Number> left, Expr<Number> right) {
            super(left, right, "!=");
        }
        public int precedence() {
            return 5;
        }
        protected PCommand operation() {
            return new PCommand.NEQCommand();
        }
        @Override
        public Boolean eval() {
            return left.eval().doubleValue() != right.eval().doubleValue();
        }
    }

    public static final class And extends ComparisonBinaryExpr<Boolean> {
        public And(Expr<Boolean> left, Expr<Boolean> right) {
            super(left, right, "&&");
        }
        public int precedence() {
            return 6;
        }
        protected PCommand operation() {
            return new PCommand.ANDCommand();
        }
        @Override
        public Boolean eval() {
            return left.eval() && right.eval();
        }
    }
    public static final class Or extends ComparisonBinaryExpr<Boolean> {
        public Or(Expr<Boolean> left, Expr<Boolean> right) {
            super(left, right, "||");
        }
        public int precedence() {
            return 7;
        }
        protected PCommand operation() {
            return new PCommand.ORCommand();
        }
        @Override
        public Boolean eval() {
            return left.eval() || right.eval();
        }
    }

    public String toString() {
        return precedenceParens(this.precedence(), left) + " " + symbol + " " + precedenceParens(this.precedence(), right);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinaryExpr)) return false;

        BinaryExpr<?, ?, ?> that = (BinaryExpr<?, ?, ?>) o;

        if (!left.equals(that.left)) return false;
        if (!right.equals(that.right)) return false;
        return symbol.equals(that.symbol);

    }
    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        result = 31 * result + symbol.hashCode();
        return result;
    }
}
