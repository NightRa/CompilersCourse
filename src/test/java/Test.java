import compiler.ast.PCodeType;
import compiler.ast.atom.Atom;
import compiler.ast.atom.Var;
import compiler.untypedAst.AST;
import compiler.untypedAst.FromUntypedAST;
import compiler.util.Option;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        HashMap<String,Var> symbolTable = new HashMap<>();
        symbolTable.put("a", new Var("a", PCodeType.Int));
        AST ast = new AST("ConstInt", new AST("5", null, null), null);
        Option<Atom> atom = FromUntypedAST.parseAtom(ast, symbolTable);
        System.out.println(atom);
    }
}
