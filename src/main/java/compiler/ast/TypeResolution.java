package compiler.ast;

import compiler.ast.atom.Var;
import compiler.util.List;
import compiler.util.Maps;

import java.util.Map;

public class TypeResolution {
    // All the identifier types other than those in PointerType are resolved.

    public static Map<String, PCodeType> makeTypeTable(List<Var> declarations) {
        Map<String, PCodeType> typeTable = topLevelTypeTable(declarations);
        for (PCodeType type : typeTable.values()) {
            if (type instanceof PCodeType.RecordType) {
                // Recurse for nested records defining types. See examples2/sample9
                Map<String, PCodeType> fieldTypes = makeTypeTable(((PCodeType.RecordType) type).fields);
                typeTable = Maps.union(typeTable, fieldTypes);
            }
        }
        return typeTable;
    }

    public static Map<String, PCodeType> topLevelTypeTable(List<Var> declarations) {
        return Maps.assocTable(declarations, Var.varName, Var.varType);
    }

}
