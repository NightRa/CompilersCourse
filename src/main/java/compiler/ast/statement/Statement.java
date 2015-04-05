package compiler.ast.statement;

import compiler.ast.atom.Literal;
import compiler.pcode.*;
import compiler.ast.atom.Var;
import compiler.ast.expr.Expr;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Strings;
import compiler.util.Tuple2;

public abstract class Statement implements PCodeGenable {
    public static Function<Statement, List<PCommand>> genCode(final SymbolTable symbolTable, final LabelGenerator labelGenerator) {
        return new Function<Statement, List<PCommand>>() {
            @Override
            public List<PCommand> apply(Statement statement) {
                return statement.genPCode(symbolTable, labelGenerator);
            }
        };
    }

    public static final class Print extends Statement {
        public final Expr/*existential*/ expr;

        public Print(Expr expr) {
            this.expr = expr;
        }

        public String toString() {
            return "print(" + expr.toString() + ")";
        }

        public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
            /**
             * <Push expr.>
             * Print command
             **/
            List<PCommand> inner = expr.genPCode(symbolTable, labelGenerator);
            List<PCommand> print = List.<PCommand>single(new PCommand.PrintCommand());
            return inner.append(print);
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
            return blockToString("if(" + condition + ")", thenBody);
        }
        public static List<PCommand> genIf(List<PCommand> condition, List<PCommand> then, Address.Label afterIfLabel) {
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

        public List<PCommand> genPCode(final SymbolTable symbolTable, final LabelGenerator labelGenerator) {
            List<PCommand> conditionBody = condition.genPCode(symbolTable, labelGenerator);
            List<PCommand> thenBody = this.thenBody.flatMap(genCode(symbolTable, labelGenerator));
            Address.Label afterIfLabel = labelGenerator.nextAfterIfLabel();
            return genIf(conditionBody, thenBody, afterIfLabel);
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
            return blockToString("if(" + condition.toString() + ")", thenBody) +
                    blockToString("else", elseBody);
        }

        public static List<PCommand> genIfElse(List<PCommand> condition, List<PCommand> thenBlock, List<PCommand> elseBlock, Address.Label elseLabel, Address.Label afterIfLabel) {
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

        public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
            List<PCommand> conditionBody = condition.genPCode(symbolTable, labelGenerator);
            List<PCommand> thenBlock = thenBody.flatMap(genCode(symbolTable, labelGenerator));
            List<PCommand> elseBlock = elseBody.flatMap(genCode(symbolTable, labelGenerator));
            Tuple2<Address.Label, Address.Label> labels = labelGenerator.nextIfElseLabels();
            Address.Label afterIfLabel = labels.first;
            Address.Label elseLabel = labels.second;
            return genIfElse(conditionBody, thenBlock, elseBlock, elseLabel, afterIfLabel);
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
            return blockToString("while(" + condition.toString() + ")", body);
        }
        public static List<PCommand> genWhile(List<PCommand> condition, List<PCommand> body, Address.Label whileLabel, Address.Label afterWhileLabel) {
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
        public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
            List<PCommand> conditionBody = condition.genPCode(symbolTable, labelGenerator);
            List<PCommand> bodyBlock = body.flatMap(genCode(symbolTable, labelGenerator));
            Tuple2<Address.Label, Address.Label> labels = labelGenerator.nextWhileAfterWhileLabels();
            Address.Label whileLabel = labels.first;
            Address.Label afterWhileLabel = labels.second;
            return genWhile(conditionBody, bodyBlock, whileLabel, afterWhileLabel);
        }
    }
    public static final class Assignment<A> extends Statement {
        public final Var<A> var;
        public final Expr<A> value;

        public Assignment(Var<A> var, Expr<A> value) {
            this.var = var;
            this.value = value;
        }

        public String toString() {
            return var.name + " = " + value.toString();
        }

        public List<PCommand> genPCode(SymbolTable symbolTable, LabelGenerator labelGenerator) {
            /**
             * Push var's address
             * Push expr.'s value
             * Store
             **/
            int address = symbolTable.unsafeGetAddress(var.name);
            PCommand loadAddress = new PCommand.LoadConstCommand(Literal.intLiteral(address));
            List<PCommand> exprValue = value.genPCode(symbolTable, labelGenerator);
            PCommand store = new PCommand.StoreCommand();
            return List.cons(loadAddress, exprValue.append(List.single(store)));
        }
    }

    private static <A> String blockToString(String header, List<A> body) {
        return header + " {\r\n" +
                Strings.indent(2,
                        Strings.mkString("", "\r\n", "",
                                body.map(new Function<A, String>() {
                                    public String apply(A line) {
                                        return line.toString();
                                    }
                                })))
                + "\r\n} ";
    }
}
