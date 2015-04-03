package compiler.errors;

public class StatementUnsupportedException extends RuntimeException {
    public StatementUnsupportedException(String label) {
        super("The statement \"" + label + "\" is currently unsupported.");
    }
}
