package compiler.ast.expr;

import compiler.ast.Type;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

/**
 * A function A => A
 */
public abstract class UnaryExpr<A> extends Expr<A> {
    public final Expr<A> expr;

    // Cases
    public static final class Neg extends UnaryExpr<Number> {
        public Neg(Expr<Number> expr) {
            super(expr);
        }

        public Type rawType(Map<String, Type> typeTable) {
            return expr.rawType(typeTable);
        }
        public String toString() {
            return "-"+precedenceParens(this.precedence(), expr);
        }

        protected PCommand operation() {
            return new PCommand.NEGCommand();
        }

        @Override
        public Number eval() {
            return 5.0;
        }
        @Override
        public boolean equals(Object o) {
            return super.equals(o) && o instanceof Neg;
        }
    }
    public static final class Not extends UnaryExpr<Boolean> {
        public Not(Expr<Boolean> expr) {
            super(expr);
        }
        public Type rawType(Map<String, Type> typeTable) {
            return Type.Bool;
        }
        public String toString() {
            return "!"+precedenceParens(this.precedence(), expr);
        }

        protected PCommand operation() {
            return new PCommand.NOTCommand();
        }

        @Override
        public Boolean eval() {
            return !expr.eval();
        }
        @Override
        public boolean equals(Object o) {
            return super.equals(o) && o instanceof Not;
        }
    }
    // Cases end

    protected abstract PCommand operation();

    protected UnaryExpr(Expr<A> expr) {
        this.expr = expr;
    }
    public List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, Type> typeTable) {
        /**
         * <Push inner expr.>
         * Unary op.
         **/
        List<PCommand> inner = expr.evaluateExpr(symbolTable, typeTable);
        List<PCommand> op = List.single(operation());
        // Optimize TODO: Change list to something with a faster append, O(n) here!
        return inner.append(op);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnaryExpr)) return false;

        UnaryExpr<?> unaryExpr = (UnaryExpr<?>) o;

        return expr.equals(unaryExpr.expr);

    }
    @Override
    public int hashCode() {
        return expr.hashCode();
    }
}
