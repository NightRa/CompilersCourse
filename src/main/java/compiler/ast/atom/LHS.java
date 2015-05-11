package compiler.ast.atom;

import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

public interface LHS<A> {
    List<PCommand> loadAddress(SymbolTable symbolTable, LabelGenerator labelGenerator);
}
