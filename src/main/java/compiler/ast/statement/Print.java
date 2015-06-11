package compiler.ast.statement;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

public class Print extends Statement {
    public final Expr<?>/*existential*/ expr;

    public Print(Expr expr) {
        this.expr = expr;
    }

    public String toString() {
        return "print(" + expr.toString() + ")";
    }

    public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, Type> typeTable, LabelGenerator labelGenerator) {
        /**
         * <Push expr.>
         * Print command
         **/
        List<PCommand> inner = expr.evaluateExpr(symbolTable, typeTable);
        List<PCommand> print = List.<PCommand>single(new PCommand.PrintCommand());
        return inner.append(print);
    }
}
