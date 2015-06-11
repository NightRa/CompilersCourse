package compiler.ast.atom;

import compiler.ast.Type;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

public abstract class LHS<A> extends Atom<A> {
    // Push to the head of the stack the address of the LHS.
    public abstract List<PCommand> loadAddress(SymbolTable symbolTable, Map<String, Type> typeTable);

    @Override
    public final List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, Type> typeTable) {
        /**
         * Load address.
         * IND
         **/
        List<PCommand> address = loadAddress(symbolTable, typeTable);
        PCommand.LoadIndirectCommand loadIndirect = new PCommand.LoadIndirectCommand();
        return address.append(List.<PCommand>single(loadIndirect));
    }
}
