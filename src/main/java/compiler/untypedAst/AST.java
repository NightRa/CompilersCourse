package compiler.untypedAst;

import java.util.Scanner;

final public class AST {
    public final String label;
    public final AST left;
    public final AST right;

    public AST(String label, AST left, AST right) {
        this.label = label;
        this.left = left;
        this.right = right;
    }

    public boolean is(String v) {
        return v != null && v.equals(label);
    }

    public String toString() {
        return label;
    }

    public static AST createAST(Scanner input) {
        if (!input.hasNext())
            return null;

        String value = input.nextLine();
        if (value.equals("~"))
            return null;

        return new AST(value, createAST(input), createAST(input));
    }
}
