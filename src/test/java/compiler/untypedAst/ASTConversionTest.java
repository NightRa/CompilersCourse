package compiler.untypedAst;

import compiler.ast.PCodeType;
import compiler.ast.atom.Var;
import compiler.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static compiler.untypedAst.AST.*;
import static compiler.util.List.*;

import java.util.HashMap;

public class ASTConversionTest {
    @Test
    public void testCreateSymbolTable(){
        Var a = new Var("a", PCodeType.Int);
        Var b = new Var("b", PCodeType.Real);
        Var c = new Var("c", PCodeType.Bool);
        List<Var> vars = list(a, b, c);
        HashMap<String, Var> symbolTable = FromUntypedAST.createSymbolTable(vars);

        assertEquals(symbolTable.size(), 3);
        assertTrue(symbolTable.containsKey("a"));
        assertTrue(symbolTable.containsKey("b"));
        assertTrue(symbolTable.containsKey("c"));
        assertEquals(symbolTable.get("a"), a);
        assertEquals(symbolTable.get("b"), b);
        assertEquals(symbolTable.get("c"), c);
    }

    @Test
    public void testParseASTList(){
        AST child1 = ast("root1", leaf("singleChild"), null);
        AST child2 = leaf("root2");
        AST child3 = ast("root3", null, null);
        List<AST> expected = list(child1, child2, child3);
        AST original = ast("List", ast("List", ast("List", null, child1), child2), child3);
        List<AST> result = FromUntypedAST.parseASTList(original, "List");
        assertEquals(expected, result);
    }
}
