package compiler.pcode;

import compiler.ast.Type;

// TODO: Not relevant anymore
public class AddressedVar {
    public final String name;
    public final Type type;
    public final int address;
    public AddressedVar(String name, Type type, int address) {
        this.name = name;
        this.type = type;
        this.address = address;
    }
}
