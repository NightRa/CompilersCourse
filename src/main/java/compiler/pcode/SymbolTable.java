package compiler.pcode;

import compiler.ast.Type;
import compiler.ast.scopes.Declaration;
import compiler.errors.VariableNameUndefined;
import compiler.util.List;
import compiler.util.Option;

import java.util.HashMap;
import java.util.Map;

// TODO: Remove! Not relevant anymore.
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

    public static SymbolTable assignAddresses(final List<Declaration> variables, final int startingAddress, Map<String, Type> typeTable) {
        HashMap<String, AddressedVar> symbolTable = new HashMap<>();
        int currentAddress = startingAddress;
        for (Declaration var : variables) {
            symbolTable.put(var.name, assignAddress(var, currentAddress));
            currentAddress += var.type.size(typeTable);
        }
        return symbolTable(symbolTable);
    }

    private static AddressedVar assignAddress(Declaration var, int address) {
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
