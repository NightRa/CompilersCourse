package compiler.pcode;

import compiler.ast.atom.Literal;

public abstract class PCommand {
    public static final class ADDCommand extends PCommand{}
    public static final class SUBCommand extends PCommand{}
    public static final class MULCommand extends PCommand{}
    public static final class DIVCommand extends PCommand{}
    public static final class LTCommand extends PCommand{}
    public static final class LECommand extends PCommand{}
    public static final class GTCommand extends PCommand{}
    public static final class GECommand extends PCommand{}
    public static final class EQCommand extends PCommand{}
    public static final class NEQCommand extends PCommand{}
    public static final class ANDCommand extends PCommand{}
    public static final class ORCommand extends PCommand{}
    public static final class NEGCommand extends PCommand{}
    public static final class NOTCommand extends PCommand{}
    public static final class StoreCommand extends PCommand{}
    public static final class LoadIndirectCommand extends PCommand{}
    public static final class LoadConstCommand extends PCommand{
        public final Literal value;
        public LoadConstCommand(Literal value) {
            this.value = value;
        }
    }
    public static final class FalseJumpCommand extends PCommand{
        public final Address jumpAddress;
        public FalseJumpCommand(Address jumpAddress) {
            this.jumpAddress = jumpAddress;
        }
    }
    public static final class UnconditionalJumpCommand extends PCommand{
        public final Address jumpAddress;
        public UnconditionalJumpCommand(Address jumpAddress) {
            this.jumpAddress = jumpAddress;
        }
    }
    public static final class PrintCommand extends PCommand{}
}
