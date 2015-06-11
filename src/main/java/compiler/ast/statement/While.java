package compiler.ast.statement;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.pcode.Label;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;
import compiler.util.Strings;
import compiler.util.Tuple2;

import java.util.Map;

public final class While extends Statement {
    public final Expr<Boolean> condition;
    public final List<Statement> body;

    public While(Expr<Boolean> condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    public String toString() {
        return Strings.blockToString("while(" + condition.toString() + ")", body);
    }
    public static List<PCommand> genWhile(List<PCommand> condition, List<PCommand> body, Label whileLabel, Label afterWhileLabel) {
        /**
         * WhileLabel
         * <Condition>
         * FalseJump AfterWhileLabel
         * <Body>
         * UnconditionalJump WhileLabel
         * AfterWhileLabel
         **/
        List<PCommand> whileLabelCommand = List.<PCommand>single(new PCommand.LabelCommand(whileLabel));
        List<PCommand> falseConditionToAfterWhile = List.<PCommand>single(new PCommand.FalseJumpCommand(afterWhileLabel));
        List<PCommand> recheckWhileCondition = List.<PCommand>single(new PCommand.UnconditionalJumpCommand(whileLabel));
        List<PCommand> afterWhileLabelCommand = List.<PCommand>single(new PCommand.LabelCommand(afterWhileLabel));
        return whileLabelCommand
                .append(condition)
                .append(falseConditionToAfterWhile)
                .append(body)
                .append(recheckWhileCondition)
                .append(afterWhileLabelCommand);
    }
    public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, Type> typeTable, LabelGenerator labelGenerator) {
        List<PCommand> conditionBody = condition.evaluateExpr(symbolTable, typeTable);
        List<PCommand> bodyBlock = body.flatMap(genCode(symbolTable, typeTable, labelGenerator));
        Tuple2<Label, Label> labels = labelGenerator.nextWhileAfterWhileLabels();
        Label whileLabel = labels.first;
        Label afterWhileLabel = labels.second;
        return genWhile(conditionBody, bodyBlock, whileLabel, afterWhileLabel);
    }
}
