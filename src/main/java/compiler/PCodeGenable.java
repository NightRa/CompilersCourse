package compiler;

import compiler.ast.atom.Var;
import compiler.pcode.PCommand;
import compiler.util.List;

import java.util.HashMap;

public interface PCodeGenable {
    List<PCommand> genPCode(HashMap<String,Var> symbolTable);
}
