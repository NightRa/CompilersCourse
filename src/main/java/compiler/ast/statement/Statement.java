package compiler.ast.statement;

import compiler.ast.atom.Var;
import compiler.ast.expr.Expr;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Strings;

public abstract class Statement {
    public static final class Print extends Statement {
        public final Expr/*existential*/ expr;

        public Print(Expr expr) {
            this.expr = expr;
        }

        public String toString() {
            return "print(" + expr.toString() + ")";
        }
    }
    public static final class If extends Statement {
        public final Expr<Boolean> condition;
        public final List<Statement> thenBody;

        public If(Expr<Boolean> condition, List<Statement> thenBody) {
            this.condition = condition;
            this.thenBody = thenBody;
        }

        public String toString() {
            return blockToString("if(" + condition + ")", thenBody);
        }
    }
    public static final class IfElse extends Statement {
        public final Expr<Boolean> condition;
        public final List<Statement> thenBody;
        public final List<Statement> elseBody;

        public IfElse(Expr<Boolean> condition, List<Statement> thenBody, List<Statement> elseBody) {
            this.condition = condition;
            this.thenBody = thenBody;
            this.elseBody = elseBody;
        }

        public String toString() {
            return blockToString("if(" + condition.toString() + ")", thenBody) +
                    blockToString("else", elseBody);
        }
    }
    public static final class While extends Statement {
        public final Expr<Boolean> condition;
        public final List<Statement> body;

        public While(Expr<Boolean> condition, List<Statement> body) {
            this.condition = condition;
            this.body = body;
        }

        public String toString() {
            return blockToString("while(" + condition.toString() + ")", body);
        }
    }
    public static final class Assignment<A> extends Statement {
        public final Var<A> var;
        public final Expr<A> value;

        public Assignment(Var<A> var, Expr<A> value) {
            this.var = var;
            this.value = value;
        }

        public String toString() {
            return var.name + " = " + value.toString();
        }
    }

    private static <A> String blockToString(String header, List<A> body) {
        return header + " {\r\n" +
                Strings.indent(2,
                        Strings.mkString("", "\r\n", "",
                                body.map(new Function<A, String>() {
                                    public String apply(A line) {
                                        return line.toString();
                                    }
                                })))
                + "\r\n} ";
    }
}
