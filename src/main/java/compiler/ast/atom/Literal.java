package compiler.ast.atom;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.pcode.ToPCodeString;
import compiler.util.Function;
import compiler.util.List;

import java.util.Map;

/* Safety TODO: Wrap in option, make a safe casts from String to Literal values*/
public abstract class Literal<A> extends Atom<A> implements ToPCodeString {
    public final A value;
    public abstract Type rawType(Map<String, Type> typeTable);

    public List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, Type> typeTable) {
        return List.<PCommand>single(new PCommand.LoadConstCommand(this));
    }

    protected Literal(A value) {
        this.value = value;
    }
    public static final class IntLiteral extends Literal<Number> {
        protected IntLiteral(Integer value) {
            super(value);
        }

        public Type rawType(Map<String, Type> typeTable) {
            return Type.Int;
        }
        @Override
        public Number eval() {
            return value;
        }
    }
    public static IntLiteral intLiteral(String intValue) {
        return new IntLiteral(Integer.valueOf(intValue));
    }
    public static IntLiteral intLiteral(int intValue) {
        return new IntLiteral(intValue);
    }
    public static Function<Integer, Expr<Number>> intLiteral = new Function<Integer, Expr<Number>>() {
        @Override
        public Expr<Number> apply(Integer value) {
            return intLiteral(value);
        }
    };

    public static final class RealLiteral extends Literal<Double> {
        protected RealLiteral(Double value) {
            super(value);
        }

        public Type rawType(Map<String, Type> typeTable) {
            return Type.Real;
        }
        @Override
        public Double eval() {
            return value;
        }
    }
    public static RealLiteral realLiteral(String realValue) {
        return new RealLiteral(Double.valueOf(realValue));
    }
    public static final class BooleanLiteral extends Literal<Boolean> {
        protected BooleanLiteral(Boolean value) {
            super(value);
        }

        public Type rawType(Map<String, Type> typeTable) {
            return Type.Bool;
        }
        @Override
        public Boolean eval() {
            return value;
        }
    }
    public static BooleanLiteral booleanLiteral(String boolValue) {
        if (boolValue.equals("True")) {
            return new BooleanLiteral(true);
        } else if (boolValue.equals("False")) {
            return new BooleanLiteral(false);
        } else {
            throw new IllegalArgumentException("booleanLiteral(" + boolValue + ") isn't of the required format.");
        }
    }

    public String toString() {
        return value.toString().toLowerCase();
    }

    public String toPCodeString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;

        Literal<?> literal = (Literal<?>) o;

        return value.equals(literal.value);

    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
