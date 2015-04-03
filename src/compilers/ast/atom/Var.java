package compilers.ast.atom;

import compilers.ast.PCodeType;

public class Var<A> extends Atom<A> {
    public final String identifier;
    public final PCodeType type;

    public Var(String identifier, PCodeType type) {
        this.identifier = identifier;
        this.type = type;
    }

    public PCodeType type() {
        return type;
    }
}
