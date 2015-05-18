package compiler.ast.atom;

import compiler.ast.PCodeType;
import compiler.errors.IllegalTypeException;
import compiler.pcode.PCommand;
import compiler.pcode.SymbolTable;
import compiler.util.List;
import compiler.util.Maps;

import java.util.Map;

import static compiler.ast.PCodeType.RecordType;
import static compiler.ast.PCodeType.RecordType.Field;

public class RecordAccess<A> extends LHS<A> {
    public final LHS<?> lhs;
    public final String fieldName;
    public RecordAccess(LHS lhs, String fieldName) {
        this.lhs = lhs;
        this.fieldName = fieldName;
    }

    public Field extractField(Map<String, PCodeType> typeTable) {
        PCodeType type = lhs.type(typeTable);
        if (!(type instanceof RecordType)) {
            throw new IllegalTypeException("Record access on non-record type: " + lhs.toString() + " of type " + type + ", fieldName: " + fieldName);
        } else {
            RecordType recordType = (RecordType) type;
            Map<String, Field> fieldsMap = recordType.fieldsMap(typeTable);
            return Maps.getOrError(fieldsMap, fieldName, "The record '" + recordType + "' doesn't include the fieldName '" + fieldName + "'");
        }
    }

    @Override
    public PCodeType rawType(Map<String, PCodeType> typeTable) {
        return extractField(typeTable).var.type;
    }

    @Override
    public List<PCommand> loadAddress(SymbolTable symbolTable, Map<String, PCodeType> typeTable) {
        /**
         * Load lhs address.
         * INC offset of the fieldName.
         **/
        Field field = extractField(typeTable);
        List<PCommand> loadLHSAddress = lhs.loadAddress(symbolTable, typeTable);
        List<PCommand> incrementCommand = List.<PCommand>single(new PCommand.IncrementCommand(field.offset));
        return loadLHSAddress
                .append(incrementCommand);
    }

    @Override
    public A eval() {
        throw new UnsupportedOperationException("Eval on Record Access isn't supported.");
    }

    @Override
    public String toString() {
        return lhs.toString() + "." + fieldName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordAccess)) return false;

        RecordAccess<?> that = (RecordAccess<?>) o;

        if (!lhs.equals(that.lhs)) return false;
        return fieldName.equals(that.fieldName);

    }
    @Override
    public int hashCode() {
        int result = lhs.hashCode();
        result = 31 * result + fieldName.hashCode();
        return result;
    }
}
