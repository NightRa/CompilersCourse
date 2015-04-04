package compiler.pcode;

public abstract class Address {
    public static final class Label extends Address{
        public final String label;
        public Label(String label) {
            this.label = label;
        }
    }
    public static final class AbsoluteAddress extends Address{
        public final int absoluteAddress;
        public AbsoluteAddress(int absoluteAddress) {
            this.absoluteAddress = absoluteAddress;
        }
    }
}
