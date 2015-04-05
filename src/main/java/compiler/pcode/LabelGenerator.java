package compiler.pcode;

import compiler.util.Tuple2;

public interface LabelGenerator {
    /**
     * AfterIfLabel
     * ElseLabel
     * WhileLabel
     * AfterWhileLabel
    **/
    Address.Label nextAfterIfLabel();
    Tuple2<Address.Label, Address.Label> nextIfElseLabels();
    Tuple2<Address.Label, Address.Label> nextWhileAfterWhileLabels();
}
