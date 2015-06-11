package compiler.ast.statement;

import compiler.ast.Type;
import compiler.ast.atom.LHS;
import compiler.ast.expr.Expr;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

public final class Assignment<A> extends Statement {
    public final LHS<A> lhs;
    public final Expr<A> value;

    public Assignment(LHS<A> lhs, Expr<A> value) {
        this.lhs = lhs;
        this.value = value;
    }

    public String toString() {
        return lhs.toString() + " = " + value.toString();
    }

    public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, Type> typeTable, LabelGenerator labelGenerator) {
        /**
         * Push pointerVar's address
         * Push expr.'s value
         * Store
         **/
        List<PCommand> loadLHSAddress = lhs.loadAddress(symbolTable, typeTable);
        List<PCommand> exprValue = value.evaluateExpr(symbolTable, typeTable);
        PCommand store = new PCommand.StoreCommand();
        return loadLHSAddress
                .append(exprValue)
                .append(List.single(store));
    }
}
