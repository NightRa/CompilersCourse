package compiler.pcode;

import compiler.util.Tuple2;
import static compiler.util.Tuple2.*;

public class CounterLabelGenerator implements LabelGenerator {
    private int ifCounter;
    private int whileCounter;

    public CounterLabelGenerator(int ifCounter, int whileCounter) {
        this.ifCounter = ifCounter;
        this.whileCounter = whileCounter;
    }
    public CounterLabelGenerator() {
        this(0, 0);
    }

    public Address.Label nextAfterIfLabel() {
        Address.Label afterIf = new Address.Label(afterIfLabel(ifCounter));
        ifCounter += 1;
        return afterIf;
    }
    public Tuple2<Address.Label, Address.Label> nextIfElseLabels() {
        Address.Label afterIf = new Address.Label(afterIfLabel(ifCounter));
        Address.Label elseLabel = new Address.Label(elseLabel(ifCounter));
        ifCounter += 1;
        return pair(afterIf, elseLabel);
    }
    public Tuple2<Address.Label, Address.Label> nextWhileAfterWhileLabels() {
        Address.Label whileLabel = new Address.Label(whileLabel(whileCounter));
        Address.Label afterWhileLabel = new Address.Label(afterWhileLabel(whileCounter));
        whileCounter += 1;
        return pair(whileLabel, afterWhileLabel);
    }


    public static String afterIfLabel(int count) {
        return "AfterIf" + count;
    }
    public static String elseLabel(int count) {
        return "Else" + count;
    }
    public static String whileLabel(int count) {
        return "While" + count;
    }
    public static String afterWhileLabel(int count) {
        return "AfterWhile" + count;
    }
}
