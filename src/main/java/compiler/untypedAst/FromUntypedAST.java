package compiler.untypedAst;

import compiler.ast.PCodeType;
import compiler.ast.Program;
import compiler.ast.atom.*;
import compiler.ast.expr.BinaryExpr;
import compiler.ast.expr.Expr;
import compiler.ast.expr.UnaryExpr;
import compiler.ast.statement.Statement;
import compiler.errors.StatementUnsupportedException;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Option;
import compiler.util.Tuple2;

import java.util.HashMap;

import static compiler.ast.PCodeType.*;
import static compiler.util.Option.*;

/*TODO: Unsafe fromUntypedAST - throws exceptions all over the place :)*/
public class FromUntypedAST {
    // Program => PCode
    // main = parse: AST => Option[Program] o codeGen: Program => PCode
    // main: AST => Option[PCode]
    public static Program fromUntyped(AST ast) {
        assert ast.label.equals("Program");
        AST identAndParams = ast.left;
        assert identAndParams.right.label.equals("InOutParameters");
        assert identAndParams.label.equals("IdentifierAndParameters");
        AST identifier = identAndParams.left;
        // ---------------------------------------------
        assert identifier.label.equals("Identifier");
        String programName = parseIdentifier(identifier).get();
        // ---------------------------------------------
        AST content = ast.right;
        assert content.label.equals("Content");
        // ---------------------------------------------
        List<Var> declarations;
        List<Statement> statements;
        final HashMap<String, Var> symbolTable;
        // ---------------------------------------------
        AST scope = content.left;
        if (scope == null) {
            declarations = List.nil();
            symbolTable = new HashMap<>();
        } else {
            assert scope.label.equals("Scope");
            // ---------------------------------------------
            declarations = parseDeclarationList(scope.left);
            symbolTable = createSymbolTable(declarations);
        }
        // ---------------------------------------------
        if (content.right == null) {
            statements = List.nil();
        } else {
            statements = parseStatements(content.right, symbolTable);
        }
        // ---------------------------------------------
        return new Program(programName, declarations, statements);
    }

    public static HashMap<String, Var> createSymbolTable(List<Var> declarations) {
        HashMap<String, Var> symbolTable = new HashMap<>();
        for (Var var : declarations) {
            symbolTable.put(var.name, var);
        }
        return symbolTable;
    }

    public static List<Var> parseDeclarationList(AST declarationList) {
        List<AST> declarationASTs = parseASTList(declarationList, "DeclarationsList");
        return declarationASTs.map(new Function<AST, Var>() {
            public Var apply(AST value) {
                return parseDeclaration(value);
            }
        });
    }

    public static List<Statement> parseStatements(AST statementsAST, final HashMap<String, Var> symbolTable) {
        List<AST> statementsASTs = parseASTList(statementsAST, "StatementsList");
        return statementsASTs.map(new Function<AST, Statement>() {
            public Statement apply(AST value) {
                return parseStatement(value, symbolTable);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static Var parseDeclaration(AST varAST) {
        assert varAST.label.equals("Var");
        AST identAST = varAST.left;
        assert identAST.label.equals("Identifier");
        String identifier = parseIdentifier(identAST).get();
        PCodeType type = parseType(varAST.right);
        return new Var(identifier, type);
    }

    @SuppressWarnings("unchecked")
    public static Statement parseStatement(AST statementAST, HashMap<String, Var> symbolTable) {
        switch (statementAST.label) {
            case "Print":
                return new Statement.Print(parseExpression(statementAST.left, symbolTable));
            case "If":
                if (statementAST.right.label.equals("Else")) {
                    return new Statement.IfElse(
                            // TODO: Typecheck that this is boolean:
                            parseExpression(statementAST.left, symbolTable),
                            parseStatements(statementAST.right.left, symbolTable),
                            parseStatements(statementAST.right.right, symbolTable));
                } else {
                    return new Statement.If(
                            parseExpression(statementAST.left, symbolTable),
                            parseStatements(statementAST.right, symbolTable)
                    );
                }
            case "While":
                return new Statement.While(
                        parseExpression(statementAST.left, symbolTable),
                        parseStatements(statementAST.right, symbolTable)
                );
            case "Assignment":
                return new Statement.Assignment(
                        parseLHS(statementAST.left, symbolTable).getOrError("Assignment to non-LHS: " + statementAST.left),
                        parseExpression(statementAST.right, symbolTable)
                );
            case "Switch":
                return new Statement.Switch(
                        parseExpression(statementAST.left, symbolTable),
                        parseSwitchCases(statementAST.right, symbolTable)
                );
            default:
                throw new StatementUnsupportedException(statementAST.label);
        }

    }

    @SuppressWarnings("unchecked")
    public static Option<LHS<Object>> parseLHS(AST ast, HashMap<String, Var> symbolTable) {
        Option<Var> varOption = parseVarReference(ast, symbolTable);
        Option<PointerDeref> pointerDerefOption = parsePointerDeref(ast, symbolTable);
        Option<ArrayAccess> arrayAccessOption = parseArrayAccess(ast, symbolTable);
        Option<RecordAccess> recordAccessOption = parseRecordAccess(ast, symbolTable);

        return ((Option<LHS<Object>>) (Object) varOption)
                .orElse((Option<LHS<Object>>) (Object) pointerDerefOption)
                .orElse((Option<LHS<Object>>) (Object) arrayAccessOption)
                .orElse((Option<LHS<Object>>) (Object) recordAccessOption);
    }

    private static Option<RecordAccess> parseRecordAccess(AST ast, HashMap<String, Var> symbolTable) {
        if (ast.label.equals("Record")) {
            LHS recordVar = parseLHS(ast.left, symbolTable).getOrError("Record access on something which isn't an LHS.");
            String fieldName = parseIdentifier(ast.right).getOrError("Record access .not fieldName name");
            return some(new RecordAccess(recordVar, fieldName));
        } else {
            return none();
        }
    }

    @SuppressWarnings("unchecked")
    private static Option<ArrayAccess> parseArrayAccess(AST ast, final HashMap<String, Var> symbolTable) {
        if (ast.label.equals("Array")) {
            LHS<Object> arrayVar = parseLHS(ast.left, symbolTable).getOrError("Array access of something which isn't an LHS.");
            List<Expr<Number>> indexList = parseASTList(ast.right, "IndexList").map(new Function<AST, Expr<Number>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Expr<Number> apply(AST exprAST) {
                    return (Expr<Number>) parseExpression(exprAST, symbolTable);
                }
            });
            return some(new ArrayAccess(arrayVar, indexList));
        } else {
            return none();
        }
    }


    @SuppressWarnings("unchecked")
    private static Option<PointerDeref> parsePointerDeref(AST ast, HashMap<String, Var> symbolTable) {
        if (ast.label.equals("Pointer")) {
            LHS<Object> pointerVar = parseLHS(ast.left, symbolTable).getOrError("Pointer of something that isn't an LHS.");
            return some(new PointerDeref(pointerVar));
        } else {
            return none();
        }

    }

    public static Tuple2<Integer, List<Statement>> parseCase(AST caseAST, HashMap<String, Var> symbolTable) {
        assert caseAST.label.equals("Case");
        int caseNumber = parseConstInt(caseAST.left);
        List<Statement> statements = parseStatements(caseAST.right, symbolTable);
        return Tuple2.pair(caseNumber, statements);
    }

    public static List<Statement.Switch.Case> parseSwitchCases(AST casesList, HashMap<String, Var> symbolTable) {
        if (casesList == null) {
            return List.nil();
        } else {
            assert casesList.label.equals("CaseList");
            Tuple2<Integer, List<Statement>> caseStructure = parseCase(casesList.right, symbolTable);
            Statement.Switch.Case _case = new Statement.Switch.Case(caseStructure.first, caseStructure.second);
            return parseSwitchCases(casesList.left, symbolTable).append(List.single(_case));
        }
    }

    @SuppressWarnings("unchecked")
    public static Expr parseExpression(AST ast, HashMap<String, Var> symbolTable) {
        return ((Option<Expr>) (Object) parseAtom(ast, symbolTable))
                .orElse((Option<Expr>) (Object) parseUnary(ast, symbolTable))
                .orElse((Option<Expr>) (Object) parseBinaryExpr(ast, symbolTable))
                .getOrError("Unsupported Expression: " + ast);
    }

    @SuppressWarnings("unchecked")
    public static Option<UnaryExpr> parseUnary(AST exprAST, HashMap<String, Var> symbolTable) {
        switch (exprAST.label) {
            case "Negative":
                return new Some<UnaryExpr>(new UnaryExpr.Neg(parseExpression(exprAST.left, symbolTable)));
            case "Not":
                return new Some<UnaryExpr>(new UnaryExpr.Not(parseExpression(exprAST.left, symbolTable)));
            default:
                return none();
        }
    }

    @SuppressWarnings("unchecked")
    public static Option<BinaryExpr> parseBinaryExpr(AST exprAST, HashMap<String, Var> symbolTable) {
        // TODO: Untyped: need type checking to recover type parameter.
        // The code duplication hurts.
        switch (exprAST.label) {
            case "Plus":
                return new Some<BinaryExpr>(new BinaryExpr.Plus(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "Minus":
                return new Some<BinaryExpr>(new BinaryExpr.Minus(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "Multiply":
                return new Some<BinaryExpr>(new BinaryExpr.Mult(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "Divide":
                return new Some<BinaryExpr>(new BinaryExpr.Div(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "LessThan":
                return new Some<BinaryExpr>(new BinaryExpr.LT(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "GreaterThan":
                return new Some<BinaryExpr>(new BinaryExpr.GT(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "LessOrEquals":
                return new Some<BinaryExpr>(new BinaryExpr.LE(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "GreaterOrEquals":
                return new Some<BinaryExpr>(new BinaryExpr.GE(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "Equals":
                return new Some<BinaryExpr>(new BinaryExpr.EQ(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "NotEquals":
                return new Some<BinaryExpr>(new BinaryExpr.NEQ(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "And":
                return new Some<BinaryExpr>(new BinaryExpr.And(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            case "Or":
                return new Some<BinaryExpr>(new BinaryExpr.Or(parseExpression(exprAST.left, symbolTable), parseExpression(exprAST.right, symbolTable)));
            default:
                return none();
        }
    }

    @SuppressWarnings("unchecked")
    public static Option<Atom> parseAtom(AST ast, HashMap<String, Var> symbolTable) {
        // Dafaq. Double casting = Just believe me stupid java compiler.
        // Inheritance -> Variance -> Fucked up type inference
        return ((Option<Atom>) (Object) parseConst(ast))
                .orElse((Option<Atom>) (Object) parseLHS(ast, symbolTable));
    }

    public static Option<Var> parseVarReference(AST ast, final HashMap<String, Var> symbolTable) {
        return parseIdentifier(ast).flatMap(new Function<String, Option<Var>>() {
            public Option<Var> apply(String name) {
                if (symbolTable.containsKey(name)) {
                    return some(symbolTable.get(name));
                } else {
                    return none();
                }
            }
        });
    }

    public static int parseConstInt(AST ast) {
        assert ast.label.equals("ConstInt");
        return Integer.parseInt(ast.left.label);
    }

    /*TODO: Throws an exception if parsing label fails*/
    public static Option<Literal> parseConst(AST ast) {
        switch (ast.label) {
            case "ConstInt":
                return new Some<Literal>(Literal.intLiteral(ast.left.label));
            case "ConstReal":
                return new Some<Literal>(Literal.realLiteral(ast.left.label));
            case "False":
            case "True":
                return new Some<Literal>(Literal.booleanLiteral(ast.label));
            default:
                return none();
        }
    }

    public static Option<String> parseIdentifier(AST ast) {
        if (ast.left == null) {
            return none();
        } else {
            return iff(ast.label.equals("Identifier"), ast.left.label);
        }
    }

    public static Option<PrimitiveType> parsePrimitiveType(AST type) {
        switch (type.label) {
            case "Bool":
                return some(Bool);
            case "Int":
                return some(Int);
            case "Real":
                return some(Real);
            default:
                return none();
        }
    }

    @SuppressWarnings("unchecked")
    public static Option<ReferenceType> parseRefType(AST type) {
        Option<String> ident = parseIdentifier(type);
        Option<ReferenceType> identType = ident.map(new Function<String, ReferenceType>() {
            public ReferenceType apply(String identName) {
                return new IdentifierType(identName);
            }
        });
        return identType
                .orElse(
                        (Option<ReferenceType>) (Object)
                                parsePrimitiveType(type));
    }

    public static Option<PointerType> parsePointerType(AST type) {
        if (type.label.equals("Pointer")) {
            return parseRefType(type.left).map(new Function<ReferenceType, PointerType>() {
                public PointerType apply(ReferenceType refType) {
                    return new PointerType(refType);
                }
            });
        } else {
            return none();
        }
    }

    @SuppressWarnings("unchecked")
    public static Option<BaseType> parseBaseType(AST type) {
        return ((Option<BaseType>) (Object) parsePointerType(type))
                .orElse((Option<BaseType>) (Object) parseRefType(type));
    }

    public static Option<RecordType> parseRecord(AST type) {
        if (type.label.equals("Record")) {
            List<Var> fields = parseDeclarationList(type.left);
            return some(new RecordType(fields));
        } else {
            return none();
        }
    }

    public static Option<ArrayType> parseArrayType(AST type) {
        if (type.label.equals("Array")) {
            ReferenceType ofType = parseRefType(type.right).getOrError("No 'of' type in Array type.");
            List<ArrayType.Bounds> bounds = parseASTList(type.left, "RangeList").map(new Function<AST, ArrayType.Bounds>() {
                @Override
                public ArrayType.Bounds apply(AST bound) {
                    return parseBounds(bound);
                }
            });
            return some(new ArrayType(bounds, ofType));
        } else {
            return none();
        }
    }

    public static ArrayType.Bounds parseBounds(AST ast) {
        assert ast.label.equals("Range");
        return new ArrayType.Bounds(parseConstInt(ast.left), parseConstInt(ast.right));
    }

    @SuppressWarnings("unchecked")
    public static PCodeType parseType(AST type) {
        return ((Option<PCodeType>) (Object) parseBaseType(type))
                .orElse((Option<PCodeType>) (Object) parseRecord(type))
                .orElse((Option<PCodeType>) (Object) parseArrayType(type))
                .getOrError("Unsupported type: " + type.label + ", tree: " + type.toString());
    }

    public static List<AST> parseASTList(AST astList, String nodeName) {
        List<AST> acc = List.nil();
        while (astList != null) {
            assert astList.label.equals(nodeName);

            acc = List.cons(astList.right, acc);
            astList = astList.left;
        }
        return acc;
    }
}
