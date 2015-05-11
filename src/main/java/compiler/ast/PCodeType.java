package compiler.ast;

public abstract class PCodeType {
    public static final PCodeType Int = new Int();
    public static final PCodeType Real = new Real();
    public static final PCodeType Bool = new Bool();

    public static final class Int extends PCodeType {
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Int;
        }
        @Override
        public String toString() {
            return "Int";
        }
    }
    public static final class Real extends PCodeType {
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Real;
        }
        @Override
        public String toString() {
            return "Real";
        }
    }
    public static final class Bool extends PCodeType {
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Bool;
        }
        @Override
        public String toString() {
            return "Bool";
        }
    }

    public abstract boolean equals(Object obj);
    public abstract String toString();
}
