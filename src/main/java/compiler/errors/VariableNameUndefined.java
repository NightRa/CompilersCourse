package compiler.errors;

public class VariableNameUndefined extends RuntimeException {
    public VariableNameUndefined(String varName) {
        super("Variable name \"" + varName + "\" hasn't been defined.");
    }
}
