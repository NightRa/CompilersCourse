package compiler.ast.atom;

import compiler.ast.PCodeType;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.Function;
import compiler.util.List;

public class Var<A> extends Atom<A> {
    public final String name;
    public final PCodeType type;

    public Var(String name, PCodeType type) {
        this.name = name;
        this.type = type;
    }

    public PCodeType type() {
        return type;
    }

    public int precedence() {
        return 0;
    }

    public String toString() {
        return name;
    }
    public String declarationString() {
        return name + ": " + type.toString();
    }

    /**
     * Var is an expression, generate the code which puts the value of the var into the stack.
     */
    public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
        /**
         * LDC Address
         * IND
         **/
        // TODO: Proper error handling, don't throw an exception.
        int address = symbolTable.unsafeGetAddress(name);
        PCommand.LoadConstCommand loadAddress = new PCommand.LoadConstCommand(Literal.intLiteral(address));
        PCommand.LoadIndirectCommand loadIndirect = new PCommand.LoadIndirectCommand();
        return List.list(loadAddress, loadIndirect);

    }

    public static final Function<Var, String> varName = new Function<Var, String>() {
        public String apply(Var var) {
            return var.name;
        }
    };

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var<?> var = (Var<?>) o;
        return name.equals(var.name);
    }
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

}
