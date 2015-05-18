package compiler.ast;

import compiler.ast.atom.Var;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Maps;
import compiler.util.Strings;

import java.util.HashMap;
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
            /*throw new IllegalStateException("Size should be called only after all the types have been resolved, and then size shouldn't ever be called on an identifier type." +
                    "\r\ntype name: " + toString() + "\r\n");*/
            return resolveIdentifier(typeTable, this).size(typeTable);
        }
    }
    public static final class PointerType extends BaseType {
        public final ReferenceType ofType;
        public PointerType(ReferenceType ofType) {
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
        public static final class Field {
            public final Var var;
            public final int offset;
            public Field(Var var, int offset) {
                this.var = var;
                this.offset = offset;
            }
        }
        public final List<Var> fields;

        public RecordType(List<Var> fields) {
            this.fields = fields;
        }

        // *** All types must already be resolved. ***
        public Map<String, Field> fieldsMap(Map<String, PCodeType> typeTable) {
            // TODO: Cache?
            Map<String, Field> fieldsMap = new HashMap<>();
            int currentOffset = 0;
            for (Var field : fields) {
                fieldsMap.put(field.name, new Field(field, currentOffset));
                currentOffset += field.type.size(typeTable);
            }
            return fieldsMap;
        }

        @Override
        public String toString() {
            return "record\r\n" + Program.declarationsString(fields) + "\r\nend;";
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
                    return bounds.size();
                }
            };
            public static Function<Bounds, Integer> start = new Function<Bounds, Integer>() {
                public Integer apply(Bounds bounds) {
                    return bounds.startIndex;
                }
            };
            public int size() {
                // Inclusive on both sides.
                return endIndex - startIndex + 1;
            }
        }

        public final List<Bounds> bounds;
        public final PCodeType ofType;

        public ArrayType(List<Bounds> bounds, PCodeType ofType) {
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

    public static PCodeType resolveIdentifier(Map<String, PCodeType> typeTable, PCodeType type) {
        PCodeType currentType = type;
        while (currentType instanceof PCodeType.IdentifierType) {
            String typeName = ((PCodeType.IdentifierType) currentType).typeName;
            currentType = Maps.getOrError(typeTable, typeName, "The type named '" + typeName + "' wasn't found in the type table: " + typeTable);
        }
        return currentType;
    }

    public abstract String toString();
    // TypeTable: Resolves to a non-identifier type.
    public abstract int size(Map<String, PCodeType> typeTable);
}
