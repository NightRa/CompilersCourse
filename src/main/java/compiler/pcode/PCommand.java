package compiler.pcode;

import compiler.ast.atom.Literal;

public abstract class PCommand implements ToPCodeString {
    public static final class IncrementCommand extends PCommand {
        public final int incAmount;
        public IncrementCommand(int incAmount) {
            this.incAmount = incAmount;
        }
        @Override
        public String toPCodeString() {
            return "INC " + incAmount;
        }
    }
    public static final class DecrementCommand extends PCommand {
        public final int decrementAmount;
        public DecrementCommand(int decrementAmount) {
            this.decrementAmount = decrementAmount;
        }
        @Override
        public String toPCodeString() {
            return "DEC " + decrementAmount;
        }
    }
    public static final class ADDCommand extends PCommand {
        public String toPCodeString() {
            return "ADD";
        }
    }
    public static final class SUBCommand extends PCommand {
        public String toPCodeString() {
            return "SUB";
        }
    }
    public static final class MULCommand extends PCommand {
        public String toPCodeString() {
            return "MUL";
        }
    }
    public static final class DIVCommand extends PCommand {
        public String toPCodeString() {
            return "DIV";
        }
    }
    public static final class LTCommand extends PCommand {
        public String toPCodeString() {
            return "LES";
        }
    }
    public static final class LECommand extends PCommand {
        public String toPCodeString() {
            return "LEQ";
        }
    }
    public static final class GTCommand extends PCommand {
        public String toPCodeString() {
            return "GRT";
        }
    }
    public static final class GECommand extends PCommand {
        public String toPCodeString() {
            return "GEQ";
        }
    }
    public static final class EQCommand extends PCommand {
        public String toPCodeString() {
            return "EQU";
        }
    }
    public static final class NEQCommand extends PCommand {
        public String toPCodeString() {
            return "NEQ";
        }
    }
    public static final class ANDCommand extends PCommand {
        public String toPCodeString() {
            return "AND";
        }
    }
    public static final class ORCommand extends PCommand {
        public String toPCodeString() {
            return "OR";
        }
    }
    public static final class NEGCommand extends PCommand {
        public String toPCodeString() {
            return "NEG";
        }
    }
    public static final class NOTCommand extends PCommand {
        public String toPCodeString() {
            return "NOT";
        }
    }
    public static final class StoreCommand extends PCommand {
        public String toPCodeString() {
            return "STO";
        }
    }
    public static final class LoadIndirectCommand extends PCommand {
        public String toPCodeString() {
            return "IND";
        }
    }
    public static final class LoadConstCommand extends PCommand {
        public final Literal value;
        public LoadConstCommand(Literal value) {
            this.value = value;
        }

        public String toPCodeString() {
            return "LDC " + value.toPCodeString();
        }
    }
    public static final class LoadNestedValueCommand extends PCommand {
        public final int parentDepth;
        public final int offset;
        public LoadNestedValueCommand(int parentDepth, int offset) {
            this.parentDepth = parentDepth;
            this.offset = offset;
        }
        @Override
        public String toPCodeString() {
            return "LOD " + parentDepth + " " + offset;
        }
    }
    public static final class LoadNestedAddressCommand extends PCommand {
        public static int parentDepth;
        public static int offset;
        @Override
        public String toPCodeString() {
            return "LDA " + parentDepth + " " + offset;
        }
    }
    public static final class FalseJumpCommand extends PCommand {
        public final Label jumpAddress;
        public FalseJumpCommand(Label jumpAddress) {
            this.jumpAddress = jumpAddress;
        }

        public String toPCodeString() {
            return "FJP " + jumpAddress.toPCodeString();
        }
    }
    public static final class UnconditionalJumpCommand extends PCommand {
        public final Label jumpAddress;
        public UnconditionalJumpCommand(Label jumpAddress) {
            this.jumpAddress = jumpAddress;
        }
        public String toPCodeString() {
            return "UJP " + jumpAddress.toPCodeString();
        }
    }
    public static final class IndexedJump extends PCommand {
        public final Label label;
        public IndexedJump(Label label) {
            this.label = label;
        }
        @Override
        public String toPCodeString() {
            return "IXJ " + label.label;
        }
    }
    public static final class PrintCommand extends PCommand {
        public String toPCodeString() {
            return "PRINT";
        }
    }
    public static final class LabelCommand extends PCommand {
        public final Label label;
        public LabelCommand(Label label) {
            this.label = label;
        }
        public String toPCodeString() {
            return label.label + ": ";
        }
    }
    public static final class PrepareFunctionCallCommand extends PCommand {
        public final int callerDistanceFromLCA;
        // The LCA is the parent of the callee.
        public PrepareFunctionCallCommand(int callerDistanceFromLCA) {
            this.callerDistanceFromLCA = callerDistanceFromLCA;
        }
        @Override
        public String toPCodeString() {
            return "MST " + callerDistanceFromLCA;
        }
        // Note: logic.
        public static PrepareFunctionCallCommand prepareFunctionCall(int callDepth, int calleeDepth) {
            return new PrepareFunctionCallCommand(callDepth - calleeDepth);
        }
    }
    public static final class ProcedureReturnCommand extends PCommand {
        @Override
        public String toPCodeString() {
            return "RETP";
        }
    }
    public static final class FunctionReturnCommand extends PCommand {
        @Override
        public String toPCodeString() {
            return "RETF";
        }
    }
    public static final class AllocateStackCommand extends PCommand {
        public final int frameSize;
        public AllocateStackCommand(int frameSize) {
            this.frameSize = frameSize;
        }

        @Override
        public String toPCodeString() {
            return "SSP " + frameSize;
        }
        // Note: logic.
        public static AllocateStackCommand allocateFunctionFrame(int paramSize, int localVarsSize) {
            // 5 is the function frame bookkeeping size.
            return new AllocateStackCommand(5 + paramSize + localVarsSize);
        }
    }
    public static final class CopyMemoryCommand extends PCommand {
        // The address from which we copy is on top of the stack.
        public final int amount;
        public CopyMemoryCommand(int amount) {
            this.amount = amount;
        }
        @Override
        public String toPCodeString() {
            return "MOVS " + amount;
        }
    }
    public static final class CallCommand extends PCommand {
        public final int paramsSize;
        public final Label label;
        public CallCommand(int paramsSize, Label label) {
            this.paramsSize = paramsSize;
            this.label = label;
        }
        @Override
        public String toPCodeString() {
            return "CUP " + paramsSize + " " + label;
        }
    }
    public static final class STOPCommand extends PCommand {
        @Override
        public String toPCodeString() {
            return "STP";
        }
    }
}
