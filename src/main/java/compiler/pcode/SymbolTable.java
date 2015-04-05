package compiler.pcode;

import compiler.ast.atom.Var;
import compiler.errors.VariableNameUndefined;
import compiler.util.List;
import compiler.util.Option;

import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, AddressedVar> symbolTable;

    private SymbolTable(HashMap<String, AddressedVar> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @SuppressWarnings("unchecked")
    public static SymbolTable symbolTable(HashMap<String, AddressedVar> symbolTable) {
        return new SymbolTable((HashMap<String, AddressedVar>) symbolTable.clone());
    }

    public Option<AddressedVar> get(String varName) {
        return Option.fromNull(symbolTable.getOrDefault(varName, null));
    }

    public static SymbolTable assignAddresses(final List<Var> variables, final int startingAddress) {
        HashMap<String, AddressedVar> symbolTable = new HashMap<>();
        int currentAddress = startingAddress;
        for (Var var : variables) {
            symbolTable.put(var.name, assignAddress(var, currentAddress));
            currentAddress += 1;
        }
        return symbolTable(symbolTable);
    }

    private static AddressedVar assignAddress(Var var, int address) {
        return new AddressedVar(var.name, var.type, address);
    }

    public int unsafeGetAddress(String varName){
        Option<AddressedVar> addressedVarOption = this.get(varName);
        if(addressedVarOption.isNone()) {
            throw new VariableNameUndefined(varName);
        } else{
            return addressedVarOption.get().address;
        }
    }
}
