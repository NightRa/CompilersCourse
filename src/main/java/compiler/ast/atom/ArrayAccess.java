package compiler.ast.atom;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.errors.IllegalTypeException;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;
import compiler.util.Strings;

import java.util.Map;

import static compiler.ast.Type.ArrayType;
import static compiler.ast.Type.ArrayType.Bounds;
import static compiler.ast.atom.Literal.intLiteral;
import static compiler.ast.expr.BinaryExpr.mult;
import static compiler.ast.expr.BinaryExpr.plus;
import static compiler.util.List.list;

public class ArrayAccess<A> extends LHS<A> {
    public final LHS<A> arrayVar;
    public final List<Expr<Number>> indices;
    public ArrayAccess(LHS<A> arrayVar, List<Expr<Number>> indices) {
        this.arrayVar = arrayVar;
        this.indices = indices;
    }

    public ArrayType arrayType(Map<String, Type> typeTable) {
        Type type = arrayVar.type(typeTable);
        if (!(type instanceof ArrayType)) {
            throw new IllegalTypeException("ArrayAccess of a non-array type: " + arrayVar.toString());
        } else {
            return (ArrayType) type;
        }
    }

    @Override
    public Type rawType(Map<String, Type> typeTable) {
        return arrayType(typeTable).ofType;
    }

    @Override
    public List<PCommand> loadAddress(SymbolTable symbolTable, Map<String, Type> typeTable) {
        List<Expr<Number>> indicesExprs = indices;
        List<Expr<Number>> dimensions = arrayType(typeTable).bounds.map(Bounds.size).map(intLiteral);
        Expr<Number> sizeExpr = intLiteral(arrayType(typeTable).ofType.size(typeTable));
        Expr<Number> indexOffset = computeOffset(indicesExprs, dimensions, sizeExpr, typeTable);
        int subpartOffset = computeStartOffset(arrayType(typeTable).bounds, arrayType(typeTable).bounds.map(Bounds.size), sizeExpr, typeTable);
        return generateIndexAddressCode(arrayVar, indexOffset, subpartOffset, symbolTable, typeTable);
    }

    public static List<PCommand> generateIndexAddressCode(LHS<?> arrayVar, Expr<Number> indexOffset, int subpartOffset, SymbolTable symbolTable, Map<String, Type> typeTable) {
        // a + indexOffset - subpartOffset
        // load a.
        // compute indexOffset
        // add
        // load subpartOffset
        // subtract
        List<PCommand> loadA = arrayVar.loadAddress(symbolTable, typeTable);
        List<PCommand> evalIndexOffset = indexOffset.evaluateExpr(symbolTable, typeTable);
        PCommand add = new PCommand.ADDCommand();
        PCommand loadSubpart = new PCommand.LoadConstCommand(intLiteral(subpartOffset));
        PCommand sub = new PCommand.SUBCommand();
        return loadA
                .append(evalIndexOffset)
                .append(list(
                        add,
                        loadSubpart,
                        sub));
    }

    public static int computeStartOffset(List<Bounds> bounds, List<Integer> dimensions, Expr<Number> sizeExpr, Map<String, Type> typeTable) {
        List<Expr<Number>> startIndices = bounds.map(Bounds.start).map(intLiteral);
        List<Expr<Number>> dimensionsExpr = dimensions.map(intLiteral);
        return computeOffset(startIndices, dimensionsExpr, sizeExpr, typeTable).eval().intValue();
    }

    // function: [Expr] (indices) -> Expr that computes the offset.
    @SuppressWarnings("unchecked")
    public static Expr<Number> computeOffset(List<Expr<Number>> indices, List<Expr<Number>> dimensions, Expr<Number> size, Map<String, Type> typeTable) {
        if (indices.length != dimensions.length) {
            throw new IllegalArgumentException("indices isn't of the same length as loweBounds!" +
                    "\r\nindices:\r\n" + indices +
                    "\r\ndimensions:\r\n" + dimensions);
        } else if (indices.length == 0) { // implies indices.length == 0
            throw new IllegalArgumentException("Empty indices list is invalid!");
        } else {
            dimensions = dimensions.tail();
            Expr computation = intLiteral(0);
            while (indices.length > 1) {
                Expr index = indices.head();
                Expr lowerBound = dimensions.head();
                // TypeChecking TODO: Unification instead of equality.
                if (!(index.type(typeTable) instanceof Type.Int)) {
                    throw new IllegalTypeException("Non int indices!");
                }
                if (!(lowerBound.type(typeTable) instanceof Type.Int)) {
                    throw new IllegalTypeException("Non int lower bound!");
                }
                computation = mult(plus(computation, index), dimensions.head());
                indices = indices.tail();
                dimensions = dimensions.tail();
            }
            computation = plus(computation, indices.head());
            computation = mult(computation, size);
            return computation;
        }
    }


    @Override
    public A eval() {
        throw new UnsupportedOperationException("Eval on array access isn't supported.");
    }

    @Override
    public String toString() {
        return Strings.mkString(arrayVar.toString() + "[", ",", "]", indices);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayAccess)) return false;

        ArrayAccess<?> that = (ArrayAccess<?>) o;

        if (!arrayVar.equals(that.arrayVar)) return false;
        return indices.equals(that.indices);

    }
    @Override
    public int hashCode() {
        int result = arrayVar.hashCode();
        result = 31 * result + indices.hashCode();
        return result;
    }
}
