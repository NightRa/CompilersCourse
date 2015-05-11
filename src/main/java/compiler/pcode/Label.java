package compiler.pcode;

public final class Label implements ToPCodeString {
    public final String label;
    public Label(String label) {
        this.label = label;
    }
    public String toPCodeString() {
        return label;
    }
}
