package compiler.ast.atom;

import compiler.ast.PCodeType;

public class Var<A> extends Atom<A> {
    public final String name;
    public final PCodeType type;

    public Var(String name, PCodeType type) {
        this.name = name;
        this.type = type;
    }

    public PCodeType type() {
        return type;
    }

    public int precedence() {
        return 0;
    }

    public String toString() {
        return name;
    }
    public String declarationString(){
        return name + ": " + type.toString();
    }
}
