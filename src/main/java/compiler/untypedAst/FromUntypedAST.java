package compiler.untypedAst;

import compiler.ast.Type;
import compiler.ast.atom.*;
import compiler.ast.expr.BinaryExpr;
import compiler.ast.expr.Expr;
import compiler.ast.expr.UnaryExpr;
import compiler.ast.scopes.*;
import compiler.ast.statement.*;
import compiler.errors.StatementUnsupportedException;
import compiler.util.Function;
import compiler.util.List;
import compiler.util.Option;
import compiler.util.Tuple2;

import static compiler.ast.Type.*;
import static compiler.util.Option.*;

/* Safety TODO: Unsafe fromUntypedAST - throws exceptions all over the place :)*/
/* TODO: Parse function/procedure calls. */
public class FromUntypedAST {
    // Program => PCode
    // main = parse: AST => Option[Program] o codeGen: Program => PCode
    // main: AST => Option[PCode]
    public static Program fromUntyped(AST ast) {
        ParseScopeResult programData = parseScope(ast, "Program");
        return new Program(programData.name, programData.declarations, programData.functionsAndProcedures, programData.statementList);
    }

    public static final class ParseScopeResult {
        public final String name;
        public final AST inOutParameters;
        public final List<Statement> statementList;
        public final List<Declaration> declarations;
        public final List<FunctionsAndProcedures> functionsAndProcedures;
        public ParseScopeResult(String name, AST inOutParameters, List<Statement> statementList, List<Declaration> declarations, List<FunctionsAndProcedures> functionsAndProcedures) {
            this.name = name;
            this.inOutParameters = inOutParameters;
            this.statementList = statementList;
            this.declarations = declarations;
            this.functionsAndProcedures = functionsAndProcedures;
        }
    }

    public static ParseScopeResult parseScope(AST ast, String header) {
        assert ast.label.equals(header);
        AST identAndParams = ast.left;
        assert identAndParams.label.equals("IdentifierAndParameters");
        // ---------------------------------------------
        assert identAndParams.right.label.equals("InOutParameters");
        AST inOutParameters = identAndParams.right;
        // ---------------------------------------------
        AST identifier = identAndParams.left;
        // ---------------------------------------------
        assert identifier.label.equals("Identifier");
        String name = parseIdentifier(identifier).get().name;
        // ---------------------------------------------
        AST content = ast.right;
        assert content.label.equals("Content");
        // ---------------------------------------------
        List<Declaration> declarations;
        List<FunctionsAndProcedures> functions;
        List<Statement> statements;
        // ---------------------------------------------
        AST scope = content.left;
        if (scope == null) {
            declarations = List.nil();
            functions = List.nil();
        } else {
            assert scope.label.equals("Scope");
            // ---------------------------------------------
            declarations = parseDeclarationList(scope.left);
            // Note: Recursion here!
            functions = parseFunctions(scope.right);
        }
        // ---------------------------------------------
        if (content.right == null) {
            statements = List.nil();
        } else {
            statements = parseStatements(content.right);
        }
        // ---------------------------------------------
        return new ParseScopeResult(name, inOutParameters, statements, declarations, functions);
    }

    public static List<FunctionsAndProcedures> parseFunctions(AST funcsAndProcedures) {
        List<AST> functionsList = parseASTList(funcsAndProcedures, "FunctionsList");
        return functionsList.map(new Function<AST, FunctionsAndProcedures>() {
            @Override
            public FunctionsAndProcedures apply(AST ast) {
                return parseDefinableScope(ast);
            }
        });
    }
    public static FunctionsAndProcedures parseDefinableScope(AST ast) {
        if (ast.label.equals("Procedure")) {
            return parseProcedure(ast);
        } else if (ast.label.equals("Function")) {
            return parseFunction(ast);
        } else {
            throw new IllegalArgumentException("Function/Procedure with label " + ast.label);
        }
    }

    public static List<FunctionParameter> parseInputParameters(AST ast) {
        List<AST> parametersList = parseASTList(ast, "ParametersList");
        return parametersList.map(new Function<AST, FunctionParameter>() {
            @Override
            public FunctionParameter apply(AST parameter) {
                return parseParameter(parameter);
            }
        });
    }
    public static FunctionParameter parseParameter(AST parameter) {
        if (parameter.label.equals("ByValue")) {
            String name = parseIdentifier(parameter.left).getOrError("Parameter name unavailable.").name;
            PType pType = parsePType(parameter.right);
            return new FunctionParameter(name, pType);
        } else if (parameter.label.equals("ByReference")) {
            String name = parseIdentifier(parameter.left).getOrError("Parameter name unavailable.").name;
            PType pType = parsePType(parameter.right);
            Type referenceType = new ReferenceType(pType);
            return new FunctionParameter(name, referenceType);
        } else {
            throw new IllegalArgumentException("Invalid parameter kind: " + parameter.label);
        }
    }

    public static FunctionDef parseFunction(AST ast) {
        ParseScopeResult functionData = parseScope(ast, "Function");
        List<FunctionParameter> inputs = parseInputParameters(functionData.inOutParameters.left);
        PrimitiveType output = parsePrimitiveType(functionData.inOutParameters.right).getOrError("No output for the function with name " + functionData.name);
        return new FunctionDef(functionData.name, functionData.declarations, functionData.functionsAndProcedures, inputs, output, functionData.statementList);
    }

    public static ProcedureDef parseProcedure(AST ast) {
        ParseScopeResult procedureData = parseScope(ast, "Procedure");
        List<FunctionParameter> inputs = parseInputParameters(procedureData.inOutParameters.left);
        return new ProcedureDef(procedureData.name, procedureData.declarations, procedureData.functionsAndProcedures, inputs, procedureData.statementList);
    }

    // NOTE: null is allowed in the AST parameter here.
    public static List<Declaration> parseDeclarationList(AST declarationList) {
        List<AST> declarationASTs = parseASTList(declarationList, "DeclarationsList");
        return declarationASTs.map(new Function<AST, Declaration>() {
            public Declaration apply(AST value) {
                return parseDeclaration(value);
            }
        });
    }

    public static List<Statement> parseStatements(AST statementsAST) {
        List<AST> statementsASTs = parseASTList(statementsAST, "StatementsList");
        return statementsASTs.map(new Function<AST, Statement>() {
            public Statement apply(AST value) {
                return parseStatement(value);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static Declaration parseDeclaration(AST varAST) {
        assert varAST.label.equals("Var");
        AST identAST = varAST.left;
        assert identAST.label.equals("Identifier");
        String identifier = parseIdentifier(identAST).get().name;
        PType type = parsePType(varAST.right);
        return new Declaration(identifier, type);
    }

    @SuppressWarnings("unchecked")
    public static Statement parseStatement(AST statementAST) {
        switch (statementAST.label) {
            case "Print":
                return new Print(parseExpression(statementAST.left));
            case "If":
                if (statementAST.right.label.equals("Else")) {
                    return new IfElse(
                            // TypeChecking TODO: Typecheck that this is boolean:
                            parseExpression(statementAST.left),
                            parseStatements(statementAST.right.left),
                            parseStatements(statementAST.right.right));
                } else {
                    return new If(
                            parseExpression(statementAST.left),
                            parseStatements(statementAST.right)
                    );
                }
            case "While":
                return new While(
                        parseExpression(statementAST.left),
                        parseStatements(statementAST.right)
                );
            case "Assignment":
                return new Assignment(
                        parseLHS(statementAST.left).getOrError("Assignment to non-LHS: " + statementAST.left),
                        parseExpression(statementAST.right)
                );
            case "Switch":
                return new Switch(
                        parseExpression(statementAST.left),
                        parseSwitchCases(statementAST.right)
                );
            default:
                throw new StatementUnsupportedException(statementAST.label);
        }

    }

    @SuppressWarnings("unchecked")
    public static Option<LHS<Object>> parseLHS(AST ast) {
        Option<Var> varOption = parseVarReference(ast);
        Option<PointerDeref> pointerDerefOption = parsePointerDeref(ast);
        Option<ArrayAccess> arrayAccessOption = parseArrayAccess(ast);
        Option<RecordAccess> recordAccessOption = parseRecordAccess(ast);

        return ((Option<LHS<Object>>) (Object) varOption)
                .orElse((Option<LHS<Object>>) (Object) pointerDerefOption)
                .orElse((Option<LHS<Object>>) (Object) arrayAccessOption)
                .orElse((Option<LHS<Object>>) (Object) recordAccessOption);
    }

    private static Option<RecordAccess> parseRecordAccess(AST ast) {
        if (ast.label.equals("Record")) {
            LHS recordVar = parseLHS(ast.left).getOrError("Record access on something which isn't an LHS.");
            String fieldName = parseIdentifier(ast.right).getOrError("Record access .not fieldName name").name;
            return some(new RecordAccess(recordVar, fieldName));
        } else {
            return none();
        }
    }

    @SuppressWarnings("unchecked")
    private static Option<ArrayAccess> parseArrayAccess(AST ast) {
        if (ast.label.equals("Array")) {
            LHS<Object> arrayVar = parseLHS(ast.left).getOrError("Array access of something which isn't an LHS.");
            List<Expr<Number>> indexList = parseASTList(ast.right, "IndexList").map(new Function<AST, Expr<Number>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Expr<Number> apply(AST exprAST) {
                    return (Expr<Number>) parseExpression(exprAST);
                }
            });
            return some(new ArrayAccess(arrayVar, indexList));
        } else {
            return none();
        }
    }


    @SuppressWarnings("unchecked")
    private static Option<PointerDeref> parsePointerDeref(AST ast) {
        if (ast.label.equals("Pointer")) {
            LHS<Object> pointerVar = parseLHS(ast.left).getOrError("Pointer of something that isn't an LHS.");
            return some(new PointerDeref(pointerVar));
        } else {
            return none();
        }

    }

    public static Tuple2<Integer, List<Statement>> parseCase(AST caseAST) {
        assert caseAST.label.equals("Case");
        int caseNumber = parseConstInt(caseAST.left);
        List<Statement> statements = parseStatements(caseAST.right);
        return Tuple2.pair(caseNumber, statements);
    }

    public static List<Switch.Case> parseSwitchCases(AST casesList) {
        if (casesList == null) {
            return List.nil();
        } else {
            assert casesList.label.equals("CaseList");
            Tuple2<Integer, List<Statement>> caseStructure = parseCase(casesList.right);
            Switch.Case _case = new Switch.Case(caseStructure.first, caseStructure.second);
            return parseSwitchCases(casesList.left).append(List.single(_case));
        }
    }

    @SuppressWarnings("unchecked")
    public static Expr parseExpression(AST ast) {
        return ((Option<Expr>) (Object) parseAtom(ast))
                .orElse((Option<Expr>) (Object) parseUnary(ast))
                .orElse((Option<Expr>) (Object) parseBinaryExpr(ast))
                .getOrError("Unsupported Expression: " + ast);
    }

    @SuppressWarnings("unchecked")
    public static Option<UnaryExpr> parseUnary(AST exprAST) {
        switch (exprAST.label) {
            case "Negative":
                return new Some<UnaryExpr>(new UnaryExpr.Neg(parseExpression(exprAST.left)));
            case "Not":
                return new Some<UnaryExpr>(new UnaryExpr.Not(parseExpression(exprAST.left)));
            default:
                return none();
        }
    }

    @SuppressWarnings("unchecked")
    public static Option<BinaryExpr> parseBinaryExpr(AST exprAST) {
        // TypeChecking TODO: Untyped: need type checking to recover type parameter.
        // The code duplication hurts.
        switch (exprAST.label) {
            case "Plus":
                return new Some<BinaryExpr>(new BinaryExpr.Plus(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "Minus":
                return new Some<BinaryExpr>(new BinaryExpr.Minus(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "Multiply":
                return new Some<BinaryExpr>(new BinaryExpr.Mult(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "Divide":
                return new Some<BinaryExpr>(new BinaryExpr.Div(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "LessThan":
                return new Some<BinaryExpr>(new BinaryExpr.LT(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "GreaterThan":
                return new Some<BinaryExpr>(new BinaryExpr.GT(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "LessOrEquals":
                return new Some<BinaryExpr>(new BinaryExpr.LE(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "GreaterOrEquals":
                return new Some<BinaryExpr>(new BinaryExpr.GE(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "Equals":
                return new Some<BinaryExpr>(new BinaryExpr.EQ(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "NotEquals":
                return new Some<BinaryExpr>(new BinaryExpr.NEQ(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "And":
                return new Some<BinaryExpr>(new BinaryExpr.And(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            case "Or":
                return new Some<BinaryExpr>(new BinaryExpr.Or(parseExpression(exprAST.left), parseExpression(exprAST.right)));
            default:
                return none();
        }
    }

    @SuppressWarnings("unchecked")
    public static Option<Atom> parseAtom(AST ast) {
        // Dafaq. Double casting = Just believe me stupid java compiler.
        // Inheritance -> Variance -> Fucked up type inference
        return ((Option<Atom>) (Object) parseConst(ast))
                .orElse((Option<Atom>) (Object) parseLHS(ast));
    }

    public static Option<Var> parseVarReference(AST ast) {
        return parseIdentifier(ast);
    }

    public static int parseConstInt(AST ast) {
        assert ast.label.equals("ConstInt");
        return Integer.parseInt(ast.left.label);
    }

    /* Safety TODO: Throws an exception if parsing label fails*/
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

    public static Option<Var> parseIdentifier(AST ast) {
        if (ast.left == null) {
            return none();
        } else {
            return iff(ast.label.equals("Identifier"), ast.left.label).map(Var.var);
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
    public static Option<SimpleType> parseSimpleType(AST type) {
        Option<Var> ident = parseIdentifier(type);
        Option<SimpleType> identType = ident.map(new Function<Var, SimpleType>() {
            public SimpleType apply(Var identName) {
                return new IdentifierType(identName.name);
            }
        });
        return identType
                .orElse(
                        (Option<SimpleType>) (Object)
                                parsePrimitiveType(type));
    }

    public static Option<PointerType> parsePointerType(AST type) {
        if (type.label.equals("Pointer")) {
            return parseSimpleType(type.left).map(new Function<SimpleType, PointerType>() {
                public PointerType apply(SimpleType refType) {
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
                .orElse((Option<BaseType>) (Object) parseSimpleType(type));
    }

    public static Option<RecordType> parseRecord(AST type) {
        if (type.label.equals("Record")) {
            List<Declaration> fields = parseDeclarationList(type.left);
            return some(new RecordType(fields));
        } else {
            return none();
        }
    }

    public static Option<ArrayType> parseArrayType(AST type) {
        if (type.label.equals("Array")) {
            SimpleType ofType = parseSimpleType(type.right).getOrError("No 'of' type in Array type.");
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
    public static PType parsePType(AST type) {
        return ((Option<PType>) (Object) parseBaseType(type))
                .orElse((Option<PType>) (Object) parseRecord(type))
                .orElse((Option<PType>) (Object) parseArrayType(type))
                .getOrError("Unsupported type: " + type.label + ", tree: " + type.toString());
    }


    // NOTE: This function is allowed to get null in the AST parameter.
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
