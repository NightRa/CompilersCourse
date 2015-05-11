package compiler.pcode;

import compiler.util.Function;
import compiler.util.List;
import compiler.util.Tuple2;

import static compiler.pcode.Address.Label;
import static compiler.util.Tuple2.pair;

public class CounterLabelGenerator implements LabelGenerator {
    private int ifCounter;
    private int whileCounter;
    private int switchCounter;

    public CounterLabelGenerator(int ifCounter, int whileCounter, int switchCounter) {
        this.ifCounter = ifCounter;
        this.whileCounter = whileCounter;
        this.switchCounter = switchCounter;
    }
    public CounterLabelGenerator() {
        this(0, 0, 0);
    }

    public Label nextAfterIfLabel() {
        Label afterIf = new Label(afterIfLabel(ifCounter));
        ifCounter += 1;
        return afterIf;
    }
    public Tuple2<Label, Label> nextIfElseLabels() {
        Label afterIf = new Label(afterIfLabel(ifCounter));
        Label elseLabel = new Label(elseLabel(ifCounter));
        ifCounter += 1;
        return pair(afterIf, elseLabel);
    }
    public Tuple2<Label, Label> nextWhileAfterWhileLabels() {
        Label whileLabel = new Label(whileLabel(whileCounter));
        Label afterWhileLabel = new Label(afterWhileLabel(whileCounter));
        whileCounter += 1;
        return pair(whileLabel, afterWhileLabel);
    }

    @Override
    public Tuple2<Label, List<Label>> nextSwitchEndAndSwitchCases(int amountOfCases) {
        Label switchEnd = new Label(switchEndLabel(switchCounter));
        List<Label> casesLabels = List.range(1, amountOfCases).map(new Function<Integer, Label>() {
            public Label apply(Integer caseNumber) {
                return new Label(caseLabel(switchCounter, caseNumber));
            }
        });
        switchCounter += 1;
        return pair(switchEnd, casesLabels);
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
    public static String switchEndLabel(int id) {
        return "SwitchEnd" + id;
    }
    public static String caseLabel(int switchID, int caseNumber) {
        return "Case" + switchID + "-" + caseNumber;
    }
}
