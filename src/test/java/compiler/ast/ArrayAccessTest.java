package compiler.ast;

import compiler.ast.atom.ArrayAccess;
import compiler.ast.atom.Var;
import compiler.ast.expr.Expr;
import compiler.util.List;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static compiler.ast.atom.Literal.intLiteral;
import static compiler.ast.expr.BinaryExpr.mult;
import static compiler.ast.expr.BinaryExpr.plus;

public class ArrayAccessTest {
    public static Var<Number> var(String name) {
        return new Var<>(name, PCodeType.Int);
    }

    @Test
    public void testComputeOffset() {
        Var<Number> i = var("i");
        Var<Number> j = var("j");
        Var<Number> k = var("k");
        Var<Number> dim1 = var("dim1");
        Var<Number> dim2 = var("dim2");
        Var<Number> dim3 = var("dim3");
        List<Expr<Number>> indices = List.<Expr<Number>>list(i, j, k);
        Expr<Number> size = intLiteral(2);
        List<Expr<Number>> dimensions = List.<Expr<Number>>list(dim1, dim2, dim3);

        Expr<Number> result = ArrayAccess.computeOffset(indices, dimensions, size, Collections.<String, PCodeType>emptyMap());
        Expr<Number> expected = mult(plus(mult(plus(mult(plus(intLiteral(0), i), dim2), j), dim3), k), size);
        // ((0 + i) * dim2 + j) * dim3 + k
        Assert.assertEquals(expected, result);
    }
}
