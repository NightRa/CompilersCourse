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

public final class IfElse extends Statement {
    public final Expr<Boolean> condition;
    public final List<Statement> thenBody;
    public final List<Statement> elseBody;

    public IfElse(Expr<Boolean> condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }
    public String toString() {
        return Strings.blockToString("if(" + condition.toString() + ")", thenBody) +
                Strings.blockToString("else", elseBody);
    }

    public static List<PCommand> genIfElse(List<PCommand> condition, List<PCommand> thenBlock, List<PCommand> elseBlock, Label elseLabel, Label afterIfLabel) {
        /**
         * <Push condition>
         * FalseJump ElseLabel
         * <Then block>
         * UnconditionalJump AfterIfLabel
         * ElseLabel
         * <Else block>
         * AfterIfLabel
         **/
        List<PCommand> falseJumpToElse = List.<PCommand>single(new PCommand.FalseJumpCommand(elseLabel));
        List<PCommand> afterThenToAfterIf = List.<PCommand>single(new PCommand.UnconditionalJumpCommand(afterIfLabel));
        List<PCommand> elseLabelCommand = List.<PCommand>single(new PCommand.LabelCommand(elseLabel));
        List<PCommand> afterIfLabelCommand = List.<PCommand>single(new PCommand.LabelCommand(afterIfLabel));
        return condition
                .append(falseJumpToElse)
                .append(thenBlock)
                .append(afterThenToAfterIf)
                .append(elseLabelCommand)
                .append(elseBlock)
                .append(afterIfLabelCommand);
    }

    public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, Type> typeTable, LabelGenerator labelGenerator) {
        List<PCommand> conditionBody = condition.evaluateExpr(symbolTable, typeTable);
        List<PCommand> thenBlock = thenBody.flatMap(genCode(symbolTable, typeTable, labelGenerator));
        List<PCommand> elseBlock = elseBody.flatMap(genCode(symbolTable, typeTable, labelGenerator));
        Tuple2<Label, Label> labels = labelGenerator.nextIfElseLabels();
        Label afterIfLabel = labels.first;
        Label elseLabel = labels.second;
        return genIfElse(conditionBody, thenBlock, elseBlock, elseLabel, afterIfLabel);
    }
}
