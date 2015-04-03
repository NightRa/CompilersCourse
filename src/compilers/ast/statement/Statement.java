package compilers.ast.statement;

import compilers.ast.atom.Var;
import compilers.ast.expr.Expr;
import compilers.util.List;

public abstract class Statement {
    public static final class Print extends Statement {
        public final Expr/*existential*/ expr;

        public Print(Expr expr) {
            this.expr = expr;
        }
    }
    public static final class If extends Statement {
        public final Expr<Boolean> condition;
        public final List<Statement> thenBody;

        public If(Expr<Boolean> condition, List<Statement> thenBody) {
            this.condition = condition;
            this.thenBody = thenBody;
        }
    }
    public static final class IfElse extends Statement{
        public final Expr<Boolean> condition;
        public final List<Statement> thenBody;
        public final List<Statement> elseBody;

        public IfElse(Expr<Boolean> condition, List<Statement> thenBody, List<Statement> elseBody) {
            this.condition = condition;
            this.thenBody = thenBody;
            this.elseBody = elseBody;
        }
    }
    public static final class While extends Statement{
        public final Expr<Boolean> condition;
        public final List<Statement> body;

        public While(Expr<Boolean> condition, List<Statement> body) {
            this.condition = condition;
            this.body = body;
        }
    }
    public static final class Assignment<A> extends Statement{
        public final Var<A> var;
        public final Expr<A> value;

        public Assignment(Var<A> var, Expr<A> value) {
            this.var = var;
            this.value = value;
        }
    }
}
