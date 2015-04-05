package compiler.pcode;

import compiler.util.List;

public interface PCodeGenable {
    List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator);
}
