package compiler.pcode;

public abstract class Address implements ToPCodeString {
    public static final class Label extends Address{
        public final String label;
        public Label(String label) {
            this.label = label;
        }
        public String toPCodeString() {
            return label;
        }
    }
    public static final class AbsoluteAddress extends Address{
        public final int absoluteAddress;
        public AbsoluteAddress(int absoluteAddress) {
            this.absoluteAddress = absoluteAddress;
        }
        public String toPCodeString() {
            return String.valueOf(absoluteAddress);
        }
    }
}
