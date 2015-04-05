package compiler.pcode;

import compiler.ast.PCodeType;

public class AddressedVar {
    public final String name;
    public final PCodeType type;
    public final int address;
    public AddressedVar(String name, PCodeType type, int address) {
        this.name = name;
        this.type = type;
        this.address = address;
    }
}
