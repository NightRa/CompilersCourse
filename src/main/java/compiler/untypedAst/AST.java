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

    public static AST createAST(Scanner input) {
        if (!input.hasNext())
            return null;

        String value = input.nextLine();
        if (value.equals("~"))
            return null;

        return new AST(value, createAST(input), createAST(input));
    }

    public static AST leaf(String label) {
        return new AST(label, null, null);
    }

    public static AST ast(String label, AST left, AST right){
        return new AST(label, left, right);
    }

    public String toString() {
        return label;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AST ast = (AST) o;

        if (!label.equals(ast.label)) return false;
        if (left != null ? !left.equals(ast.left) : ast.left != null) return false;
        return !(right != null ? !right.equals(ast.right) : ast.right != null);

    }
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
