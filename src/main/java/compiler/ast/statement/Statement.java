package compiler.ast.statement;

import compiler.ast.Type;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.Function;
import compiler.util.List;

import java.util.Map;

public abstract class Statement {
    public static Function<Statement, List<PCommand>> genCode(final SymbolTable symbolTable, final Map<String, Type> typeTable, final LabelGenerator labelGenerator) {
        return new Function<Statement, List<PCommand>>() {
            @Override
            public List<PCommand> apply(Statement statement) {
                return statement.evaluateStatement(symbolTable, typeTable, labelGenerator);
            }
        };
    }

    // Invariant: Stack before = Stack After
    public abstract List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, Type> typeTable, final LabelGenerator labelGenerator);

}
