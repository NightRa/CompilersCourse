package compiler.ast.atom;

import compiler.ast.PCodeType;

/*TODO: Wrap in option, make a safe casts from String to Literal values*/
public abstract class Literal<A> extends Atom<A> {
    public final String original;
    public final A value;
    public abstract PCodeType type();

    protected Literal(String original, A value) {
        this.original = original;
        this.value = value;
    }
    public static final class IntLiteral extends Literal<Integer> {
        protected IntLiteral(String original, Integer value) {
            super(original, value);
        }

        public PCodeType type() {
            return PCodeType.Int;
        }
    }
    public static IntLiteral intLiteral(String intValue) {
        return new IntLiteral(intValue, Integer.valueOf(intValue));
    }
    public static final class RealLiteral extends Literal<Double> {
        protected RealLiteral(String original, Double value) {
            super(original, value);
        }

        public PCodeType type() {
            return PCodeType.Real;
        }
    }
    public static RealLiteral realLiteral(String realValue) {
        return new RealLiteral(realValue, Double.valueOf(realValue));
    }
    public static final class BooleanLiteral extends Literal<Boolean> {
        protected BooleanLiteral(String original, Boolean value) {
            super(original, value);
        }

        public PCodeType type() {
            return PCodeType.Bool;
        }
    }
    public static BooleanLiteral booleanLiteral(String boolValue) {
        if (boolValue.equals("True")) {
            return new BooleanLiteral(boolValue, true);
        } else if (boolValue.equals("False")) {
            return new BooleanLiteral(boolValue, false);
        } else {
            throw new IllegalArgumentException("booleanLiteral(" + boolValue + ") isn't of the required format.");
        }
    }

    public int precedence() {
        return 0;
    }
    public String toString() {
        return value.toString().toLowerCase();
    }
}
