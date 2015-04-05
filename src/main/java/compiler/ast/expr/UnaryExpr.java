package compiler.ast.expr;

import compiler.ast.PCodeType;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

/**
 * A function A => A
 */
public abstract class UnaryExpr<A> extends Expr<A> {
    public final Expr<A> expr;
    protected abstract PCommand operation();

    protected UnaryExpr(Expr<A> expr) {
        this.expr = expr;
    }
    public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
        /**
         * <Push inner expr.>
         * Unary op.
         **/
        List<PCommand> inner = expr.genPCode(symbolTable, labelGenerator);
        List<PCommand> op = List.single(operation());
        // TODO: Change list to something with a faster append, O(n) here!
        return inner.append(op);
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

        protected PCommand operation() {
            return new PCommand.NEGCommand();
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

        protected PCommand operation() {
            return new PCommand.NOTCommand();
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
