package compiler.ast;

import compiler.ast.atom.Var;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Maps;
import compiler.util.Strings;

import java.util.Map;

public abstract class PCodeType {
    public static abstract class BaseType extends PCodeType {
    }
    public static abstract class ReferenceType extends BaseType {
    }
    public static abstract class PrimitiveType extends ReferenceType {
        @Override
        public int size(Map<String, PCodeType> typeTable) {
            return 1;
        }
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
        @Override
        public int size(Map<String, PCodeType> typeTable) {
            if (!typeTable.containsKey(typeName)) {
                throw new IllegalStateException("typeTable does not contain the type identifier '" + typeName + "'");
            } else {
                return typeTable.get(typeName).size(typeTable);
            }
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
        @Override
        public int size(Map<String, PCodeType> typeTable) {
            return 1;
        }
    }

    public static final class RecordType extends PCodeType {
        public final List<Var> fields;

        public RecordType(List<Var> fields) {
            this.fields = fields;
        }

        public Map<String, Var> fieldsMap() {
            // TODO: Cache?
            return Maps.assocTable(fields, Var.varName);
        }

        @Override
        public String toString() {
            return Strings.indentBlock("record", fields) + "end;";
        }
        @Override
        public int size(final Map<String, PCodeType> typeTable) {
            return List.sum(fields.map(new Function<Var, Integer>() {
                public Integer apply(Var var) {
                    return var.type.size(typeTable);
                }
            }));
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
            public static Function<Bounds, Integer> size = new Function<Bounds, Integer>() {
                public Integer apply(Bounds bounds) {
                    // Inclusive on both sides.
                    return bounds.endIndex - bounds.startIndex + 1;
                }
            };
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
        @Override
        public int size(Map<String, PCodeType> typeTable) {
            return List.mult(bounds.map(Bounds.size)) * ofType.size(typeTable);
        }
    }

    public abstract String toString();
    // TypeTable: Resolves to a non-identifier type.
    public abstract int size(Map<String, PCodeType> typeTable);
}
