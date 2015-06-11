package compiler.ast.statement;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.pcode.Label;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;
import compiler.util.Strings;

import java.util.Map;

public final class If extends Statement {
    public final Expr<Boolean> condition;
    public final List<Statement> thenBody;

    public If(Expr<Boolean> condition, List<Statement> thenBody) {
        this.condition = condition;
        this.thenBody = thenBody;
    }

    public String toString() {
        return Strings.blockToString("if(" + condition + ")", thenBody);
    }
    public static List<PCommand> genIf(List<PCommand> condition, List<PCommand> then, Label afterIfLabel) {
        /**
         * <Push condition>
         * FalseJump AfterIfLabel
         * <Then block>
         * AfterIfLabel
         **/
        List<PCommand> falseJump = List.<PCommand>single(new PCommand.FalseJumpCommand(afterIfLabel));
        List<PCommand> labelCommand = List.<PCommand>single(new PCommand.LabelCommand(afterIfLabel));
        return condition
                .append(falseJump)
                .append(then)
                .append(labelCommand);
    }

    @Override
    public List<PCommand> evaluateStatement(final SymbolTable symbolTable, Map<String, Type> typeTable, final LabelGenerator labelGenerator) {
        List<PCommand> conditionBody = condition.evaluateExpr(symbolTable, typeTable);
        List<PCommand> thenBody = this.thenBody.flatMap(genCode(symbolTable, typeTable, labelGenerator));
        Label afterIfLabel = labelGenerator.nextAfterIfLabel();
        return genIf(conditionBody, thenBody, afterIfLabel);
    }
}
