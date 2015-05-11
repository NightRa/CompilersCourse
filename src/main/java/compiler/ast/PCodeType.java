package compiler.ast;

import compiler.ast.atom.Var;
import compiler.util.List;
import compiler.util.Strings;

public abstract class PCodeType {
    public static abstract class BaseType extends PCodeType {
    }
    public static abstract class ReferenceType extends BaseType {
    }
    public static abstract class PrimitiveType extends ReferenceType {
    }

    public static final PrimitiveType Int = new Int();
    public static final PrimitiveType Real = new Real();
    public static final PrimitiveType Bool = new Bool();

    public static final class Int extends PrimitiveType {
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Int;
        }
        @Override
        public String toString() {
            return "Int";
        }
    }
    public static final class Real extends PrimitiveType {
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Real;
        }
        @Override
        public String toString() {
            return "Real";
        }
    }
    public static final class Bool extends PrimitiveType {
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Bool;
        }
        @Override
        public String toString() {
            return "Bool";
        }
    }

    public static final class IdentifierType extends ReferenceType {
        public final String typeName;
        public IdentifierType(String typeName) {
            this.typeName = typeName;
        }
        @Override
        public String toString() {
            return typeName;
        }
    }
    public static final class Pointer extends BaseType {
        public final ReferenceType ofType;
        public Pointer(ReferenceType ofType) {
            this.ofType = ofType;
        }
        @Override
        public String toString() {
            return "^" + ofType.toString();
        }
    }

    public static final class RecordType extends PCodeType {
        public final List<Var> fields;
        public RecordType(List<Var> fields) {
            this.fields = fields;
        }

        @Override
        public String toString() {
            return Strings.indentBlock("record", fields) + "end;";
        }
    }
    public static final class ArrayType extends PCodeType {
        public static final class Bounds {
            public final int startIndex;
            public final int endIndex;
            public Bounds(int startIndex, int endIndex) {
                this.startIndex = startIndex;
                this.endIndex = endIndex;
            }
            @Override
            public String toString() {
                return startIndex + ".." + endIndex;
            }
        }
        public final List<Bounds> bounds;
        public final ReferenceType ofType;
        public ArrayType(List<Bounds> bounds, ReferenceType ofType) {
            this.bounds = bounds;
            this.ofType = ofType;
        }
        @Override
        public String toString() {
            return Strings.mkString("array[", ",", "]", bounds) + " of " + ofType.toString();
        }
    }

    public abstract String toString();
}
