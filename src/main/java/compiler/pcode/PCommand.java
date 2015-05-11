package compiler.pcode;

import compiler.ast.atom.Literal;

public abstract class PCommand implements ToPCodeString {
    public static final class ADDCommand extends PCommand{
        public String toPCodeString() {
            return "ADD";
        }
    }
    public static final class SUBCommand extends PCommand{
        public String toPCodeString() {
            return "SUB";
        }
    }
    public static final class MULCommand extends PCommand{
        public String toPCodeString() {
            return "MUL";
        }
    }
    public static final class DIVCommand extends PCommand{
        public String toPCodeString() {
            return "DIV";
        }
    }
    public static final class LTCommand extends PCommand{
        public String toPCodeString() {
            return "LES";
        }
    }
    public static final class LECommand extends PCommand{
        public String toPCodeString() {
            return "LEQ";
        }
    }
    public static final class GTCommand extends PCommand{
        public String toPCodeString() {
            return "GRT";
        }
    }
    public static final class GECommand extends PCommand{
        public String toPCodeString() {
            return "GEQ";
        }
    }
    public static final class EQCommand extends PCommand{
        public String toPCodeString() {
            return "EQU";
        }
    }
    public static final class NEQCommand extends PCommand{
        public String toPCodeString() {
            return "NEQ";
        }
    }
    public static final class ANDCommand extends PCommand{
        public String toPCodeString() {
            return "AND";
        }
    }
    public static final class ORCommand extends PCommand{
        public String toPCodeString() {
            return "OR";
        }
    }
    public static final class NEGCommand extends PCommand{
        public String toPCodeString() {
            return "NEG";
        }
    }
    public static final class NOTCommand extends PCommand{
        public String toPCodeString() {
            return "NOT";
        }
    }
    public static final class StoreCommand extends PCommand{
        public String toPCodeString() {
            return "STO";
        }
    }
    public static final class LoadIndirectCommand extends PCommand{
        public String toPCodeString() {
            return "IND";
        }
    }
    public static final class LoadConstCommand extends PCommand{
        public final Literal value;
        public LoadConstCommand(Literal value) {
            this.value = value;
        }

        public String toPCodeString() {
            return "LDC " + value.toPCodeString();
        }
    }
    public static final class FalseJumpCommand extends PCommand{
        public final Address jumpAddress;
        public FalseJumpCommand(Address jumpAddress) {
            this.jumpAddress = jumpAddress;
        }

        public String toPCodeString() {
            return "FJP " + jumpAddress.toPCodeString();
        }
    }
    public static final class UnconditionalJumpCommand extends PCommand{
        public final Address jumpAddress;
        public UnconditionalJumpCommand(Address jumpAddress) {
            this.jumpAddress = jumpAddress;
        }
        public String toPCodeString() {
            return "UJP " + jumpAddress.toPCodeString();
        }
    }
    public static final class IndexedJump extends PCommand{
        public final Address.Label label;
        public IndexedJump(Address.Label label) {
            this.label = label;
        }
        @Override
        public String toPCodeString() {
            return "IXJ " + label.label;
        }
    }
    public static final class PrintCommand extends PCommand{
        public String toPCodeString() {
            return "PRINT";
        }
    }
    public static final class LabelCommand extends PCommand{
        public final Address.Label label;
        public LabelCommand(Address.Label label) {
            this.label = label;
        }
        public String toPCodeString() {
            return label.label + ": ";
        }
    }
}
