package compiler.ast.statement;

import compiler.ast.PCodeType;
import compiler.ast.atom.LHS;
import compiler.ast.expr.Expr;
import compiler.pcode.Label;
import compiler.pcode.LabelGenerator;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.*;

import java.util.Map;

public abstract class Statement {
    public static Function<Statement, List<PCommand>> genCode(final SymbolTable symbolTable, final Map<String, PCodeType> typeTable, final LabelGenerator labelGenerator) {
        return new Function<Statement, List<PCommand>>() {
            @Override
            public List<PCommand> apply(Statement statement) {
                return statement.evaluateStatement(symbolTable, typeTable, labelGenerator);
            }
        };
    }
    public static Function<Statement, Statement> transformExprFunc(final Function<Expr, Expr> f, final Function<LHS, LHS> g) {
        return new Function<Statement, Statement>() {
            @Override
            public Statement apply(Statement statement) {
                return statement.transformExpr(f, g);
            }
        };
    }


    // Invariant: Stack before = Stack After
    public abstract List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, PCodeType> typeTable, final LabelGenerator labelGenerator);
    public abstract Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g);


    public static final class Print extends Statement {
        public final Expr<?>/*existential*/ expr;

        public Print(Expr expr) {
            this.expr = expr;
        }

        public String toString() {
            return "print(" + expr.toString() + ")";
        }

        public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, PCodeType> typeTable, LabelGenerator labelGenerator) {
            /**
             * <Push expr.>
             * Print command
             **/
            List<PCommand> inner = expr.evaluateExpr(symbolTable, typeTable);
            List<PCommand> print = List.<PCommand>single(new PCommand.PrintCommand());
            return inner.append(print);
        }
        @Override
        public Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g) {
            return new Print(f.apply(expr));
        }
    }
    public static final class If extends Statement {
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
        public List<PCommand> evaluateStatement(final SymbolTable symbolTable, Map<String, PCodeType> typeTable, final LabelGenerator labelGenerator) {
            List<PCommand> conditionBody = condition.evaluateExpr(symbolTable, typeTable);
            List<PCommand> thenBody = this.thenBody.flatMap(genCode(symbolTable, typeTable, labelGenerator));
            Label afterIfLabel = labelGenerator.nextAfterIfLabel();
            return genIf(conditionBody, thenBody, afterIfLabel);
        }
        @SuppressWarnings("unchecked")
        @Override
        public Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g) {
            Expr<Boolean> transformedCondition = (Expr<Boolean>) f.apply(condition);
            List<Statement> newThenBody = thenBody.map(transformExprFunc(f, g));
            return new If(transformedCondition, newThenBody);
        }
    }
    public static final class IfElse extends Statement {
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

        public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, PCodeType> typeTable, LabelGenerator labelGenerator) {
            List<PCommand> conditionBody = condition.evaluateExpr(symbolTable, typeTable);
            List<PCommand> thenBlock = thenBody.flatMap(genCode(symbolTable, typeTable, labelGenerator));
            List<PCommand> elseBlock = elseBody.flatMap(genCode(symbolTable, typeTable, labelGenerator));
            Tuple2<Label, Label> labels = labelGenerator.nextIfElseLabels();
            Label afterIfLabel = labels.first;
            Label elseLabel = labels.second;
            return genIfElse(conditionBody, thenBlock, elseBlock, elseLabel, afterIfLabel);
        }
        @SuppressWarnings("unchecked")
        @Override
        public Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g) {
            Expr<Boolean> transformedCondition = (Expr<Boolean>) f.apply(condition);
            List<Statement> newThenBody = thenBody.map(transformExprFunc(f, g));
            List<Statement> newElseBody = elseBody.map(transformExprFunc(f, g));
            return new IfElse(transformedCondition, newThenBody, newElseBody);
        }
    }
    public static final class While extends Statement {
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
        public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, PCodeType> typeTable, LabelGenerator labelGenerator) {
            List<PCommand> conditionBody = condition.evaluateExpr(symbolTable, typeTable);
            List<PCommand> bodyBlock = body.flatMap(genCode(symbolTable, typeTable, labelGenerator));
            Tuple2<Label, Label> labels = labelGenerator.nextWhileAfterWhileLabels();
            Label whileLabel = labels.first;
            Label afterWhileLabel = labels.second;
            return genWhile(conditionBody, bodyBlock, whileLabel, afterWhileLabel);
        }
        @SuppressWarnings("unchecked")
        @Override
        public Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g) {
            Expr<Boolean> transformedCondition = (Expr<Boolean>) f.apply(condition);
            List<Statement> newBody = body.map(transformExprFunc(f, g));
            return new While(transformedCondition, newBody);
        }
    }
    public static final class Assignment<A> extends Statement {
        public final LHS<A> lhs;
        public final Expr<A> value;

        public Assignment(LHS<A> lhs, Expr<A> value) {
            this.lhs = lhs;
            this.value = value;
        }

        public String toString() {
            return lhs.toString() + " = " + value.toString();
        }

        public List<PCommand> evaluateStatement(SymbolTable symbolTable, Map<String, PCodeType> typeTable, LabelGenerator labelGenerator) {
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
        @SuppressWarnings("unchecked")
        @Override
        public Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g) {
            return new Assignment<>(g.apply(lhs), f.apply(value));
        }
    }
    public static final class Switch extends Statement {
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
            public static Function<Case, Case> transformExpr(final Function<Expr, Expr> f, final Function<LHS, LHS> g) {
                return new Function<Case, Case>() {
                    @Override
                    public Case apply(Case _case) {
                        return new Case(_case.caseNum, _case.body.map(transformExprFunc(f, g)));
                    }
                };
            }
        }

        public final Expr<PCodeType.Int> expr;
        // ATTENTION!
        // Cases must be in order, starting from 1, and going up 1 by 1, for example, 1,2,3,...,10.
        public final List<Case> cases;
        public Switch(Expr<PCodeType.Int> expr, List<Case> cases) {
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
        public List<PCommand> evaluateStatement(final SymbolTable symbolTable, final Map<String, PCodeType> typeTable, final LabelGenerator labelGenerator) {
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
        @SuppressWarnings("unchecked")
        @Override
        public Statement transformExpr(Function<Expr, Expr> f, Function<LHS, LHS> g) {
            Expr<PCodeType.Int> transformedExpr = f.apply(expr);
            List<Case> transformedCases = cases.map(Case.transformExpr(f, g));
            return new Switch(transformedExpr, transformedCases);
        }
    }

}
