package compiler.ast;

import compiler.ast.scopes.Declaration;
import compiler.util.List;
import compiler.util.Maps;

import java.util.Map;

public class TypeResolution {
    // All the identifier types other than those in PointerType are resolved.

    public static Map<String, Type> makeTypeTable(List<Declaration> declarations) {
        Map<String, Type> typeTable = topLevelTypeTable(declarations);
        for (Type type : typeTable.values()) {
            if (type instanceof Type.RecordType) {
                // Recurse for nested records defining types. See examples2/sample9
                Map<String, Type> fieldTypes = makeTypeTable(((Type.RecordType) type).fields);
                typeTable = Maps.union(typeTable, fieldTypes);
            }
        }
        return typeTable;
    }

    public static Map<String, Type> topLevelTypeTable(List<Declaration> declarations) {
        return Maps.assocTable(declarations, Declaration.declarationName, Declaration.declarationType);
    }

}
