package compiler.pcode;

public abstract class Address implements ToPCodeString {
    public final int address;
    public Address(int address) {
        this.address = address;
    }
    public String toPCodeString() {
        return String.valueOf(address);
    }
}
