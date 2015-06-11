package compiler.ast.statement;

import compiler.ast.Type;
import compiler.ast.expr.Expr;
import compiler.pcode.Label;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.*;

import java.util.Map;

public final class Switch extends Statement {
    public static final class Case {
        public final int caseNum;
        public final List<Statement> body;
        public Case(int caseNum, List<Statement> body) {
            this.caseNum = caseNum;
            this.body = body;
        }
        @Override
        public String toString() {
            return Strings.blockToString("case " + caseNum, body);
        }
    }

    public final Expr<Type.Int> expr;
    // ATTENTION!
    // Cases must be in order, starting from 1, and going up 1 by 1, for example, 1,2,3,...,10.
    public final List<Case> cases;
    public Switch(Expr<Type.Int> expr, List<Case> cases) {
        this.expr = expr;
        this.cases = cases;
    }
    public static List<PCommand> genSwitch(List<PCommand> expr, List<Tuple2<Label, List<PCommand>>> cases, final Label switchEndLabel) {
        List<PCommand> negExpr = expr.append(List.<PCommand>single(new PCommand.NEGCommand()));
        List<PCommand> IXJSwitchEnd = List.<PCommand>single(new PCommand.IndexedJump(switchEndLabel));
        List<PCommand> casesCode = cases.flatMap(new Function<Tuple2<Label, List<PCommand>>, List<PCommand>>() {
            public List<PCommand> apply(Tuple2<Label, List<PCommand>> labelBlock) {
                return List.cons(new PCommand.LabelCommand(labelBlock.first), labelBlock.second)
                        .append(List.<PCommand>single(new PCommand.UnconditionalJumpCommand(switchEndLabel)));
            }
        });
        List<PCommand> reverseJumps = List.reverse(cases).map(new Function<Tuple2<Label, List<PCommand>>, PCommand>() {
            public PCommand apply(Tuple2<Label, List<PCommand>> block) {
                return new PCommand.UnconditionalJumpCommand(block.first);
            }
        });
        List<PCommand> switchEndCommand = List.<PCommand>single(new PCommand.LabelCommand(switchEndLabel));
        return negExpr
                .append(IXJSwitchEnd)
                .append(casesCode)
                .append(reverseJumps)
                .append(switchEndCommand);
    }
    @Override
    public List<PCommand> evaluateStatement(final SymbolTable symbolTable, final Map<String, Type> typeTable, final LabelGenerator labelGenerator) {
        List<PCommand> exprBody = expr.evaluateExpr(symbolTable, typeTable);
        Tuple2<Label, List<Label>> labels = labelGenerator.nextSwitchEndAndSwitchCases(cases.length);
        Label switchEndLabel = labels.first;
        List<Label> casesLabels = labels.second;
        List<Tuple2<Label, List<PCommand>>> casesBodies = List.zipWith(cases, casesLabels,
                new Function2<Case, Label, Tuple2<Label, List<PCommand>>>() {
                    @Override
                    public Tuple2<Label, List<PCommand>> apply(Case _case, Label caseLabel) {
                        List<Statement> caseBody = _case.body;
                        List<PCommand> casePCommandBody = caseBody.flatMap(new Function<Statement, List<PCommand>>() {
                            public List<PCommand> apply(Statement statement) {
                                return statement.evaluateStatement(symbolTable, typeTable, labelGenerator);
                            }
                        });
                        return Tuple2.pair(caseLabel, casePCommandBody);
                    }
                });
        return genSwitch(exprBody, casesBodies, switchEndLabel);
    }
    @Override
    public String toString() {
        return Strings.blockToString("switch(" + expr.toString() + ")", cases);
    }
}
