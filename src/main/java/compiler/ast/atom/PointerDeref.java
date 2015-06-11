package compiler.ast.atom;

import compiler.ast.Type;
import compiler.ast.Type.PType;
import compiler.ast.Type.PointerType;
import compiler.errors.IllegalTypeException;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

public class PointerDeref<A> extends LHS<A> {
    public final LHS<A> pointerVar;
    public PointerDeref(LHS<A> pointerVar) {
        this.pointerVar = pointerVar;
    }

    public PointerType varType(Map<String, Type> typeTable) {
        PType type = pointerVar.type(typeTable);
        if (!(type instanceof Type.PointerType)) {
            throw new IllegalTypeException("Dereference of a non-pointer type: " + pointerVar.toString());
        } else {
            return (Type.PointerType) type;
        }
    }

    @Override
    public Type rawType(Map<String, Type> typeTable) {
        // This is the only reason for the typeTable parameter.
        return varType(typeTable).ofType;
    }

    public List<PCommand> loadAddress(SymbolTable symbolTable, Map<String, Type> typeTable) {
        /**
         * load addr(var^) = eval(var)  = *(&var)
         * eval     (var^) = *eval(var) = *(*(&var)) = *(load addr(var^))
         **/
        return pointerVar.evaluateExpr(symbolTable, typeTable);
    }

    @Override
    public A eval() {
        throw new UnsupportedOperationException("Eval of pointer isn't supported.");
    }

    @Override
    public String toString() {
        return pointerVar.toString() + "^";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointerDeref)) return false;

        PointerDeref<?> that = (PointerDeref<?>) o;

        return pointerVar.equals(that.pointerVar);

    }
    @Override
    public int hashCode() {
        return pointerVar.hashCode();
    }
}
