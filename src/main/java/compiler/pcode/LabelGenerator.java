package compiler.pcode;

import compiler.util.List;
import compiler.util.Tuple2;

public interface LabelGenerator {
    /**
     * AfterIfLabel
     * ElseLabel
     * WhileLabel
     * AfterWhileLabel
    **/
    Label nextAfterIfLabel();
    Tuple2<Label, Label> nextIfElseLabels();
    Tuple2<Label, Label> nextWhileAfterWhileLabels();
    Tuple2<Label, List<Label>> nextSwitchEndAndSwitchCases(int amountOfCases);
}
