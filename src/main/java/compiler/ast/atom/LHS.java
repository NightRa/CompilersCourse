package compiler.ast.atom;

import compiler.ast.PCodeType;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

public abstract class LHS<A> extends Atom<A> {
    public abstract List<PCommand> loadAddress(SymbolTable symbolTable, Map<String, PCodeType> typeTable);

    public final List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, PCodeType> typeTable) {
        /**
         * Load address.
         * IND
         **/
        List<PCommand> address = loadAddress(symbolTable, typeTable);
        PCommand.LoadIndirectCommand loadIndirect = new PCommand.LoadIndirectCommand();
        return address.append(List.<PCommand>single(loadIndirect));
    }
}
