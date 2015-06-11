package compiler.ast.atom;

import compiler.ast.Type;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.Function;
import compiler.util.List;

import java.util.Map;

public class Var<A> extends LHS<A> {
    public final String name;

    public Var(String name) {
        this.name = name;
    }

    public Type rawType(Map<String, Type> typeTable) {
        return typeTable.get(name);
    }

    public String toString() {
        return name;
    }

    public final static Function<String, Var> var = new Function<String, Var>() {
        @Override
        public Var apply(String name) {
            return new Var(name);
        }
    };

    @Override
    public List<PCommand> loadAddress(SymbolTable symbolTable, Map<String, Type> typeTable) {
        // Safety TODO: Proper error handling, don't throw an exception.
        int address = symbolTable.unsafeGetAddress(this.name);
        PCommand loadAddress = new PCommand.LoadConstCommand(Literal.intLiteral(address));
        return List.single(loadAddress);
    }

    @Override
    public A eval() {
        throw new UnsupportedOperationException("Eval of Var isn't supported.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Var)) return false;

        Var<?> var = (Var<?>) o;

        return name.equals(var.name);

    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
