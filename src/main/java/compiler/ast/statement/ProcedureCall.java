package compiler.ast.statement;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

public final class ProcedureCall extends Statement {
    public final String procedureName;
    public final List<Expr<?>> parameters;
    public ProcedureCall(String procedureName, List<Expr<?>> parameters) {
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    @Override
    public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, Type> typeTable, LabelGenerator labelGenerator) {
        // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Procedure call. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Prepare call (current depth, function depth)
        return null;
    }
}
