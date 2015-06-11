package compiler.ast.expr;

import compiler.ast.Type;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;

import java.util.Map;

// TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Function call. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class FunctionCallExpr<A> extends Expr<A> {
    public final String name;
    public final List<Expr<?>> arguments;
    public FunctionCallExpr(String name, List<Expr<?>> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
    @Override
    public Type rawType(Map<String, Type> typeTable) {
        throw new UnsupportedOperationException();
    }
    @Override
    public int precedence() {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<PCommand> evaluateExpr(SymbolTable symbolTable, Map<String, Type> typeTable) {
        throw new UnsupportedOperationException();
    }
    @Override
    public A eval() {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
