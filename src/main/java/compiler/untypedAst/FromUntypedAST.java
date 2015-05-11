package compiler.untypedAst;

import compiler.ast.PCodeType;
import compiler.ast.Program;
import compiler.ast.atom.Atom;
import compiler.ast.atom.Literal;
import compiler.ast.atom.Var;
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
            List<AST> declarationASTs = parseASTList(scope.left, "DeclarationsList");

            declarations = declarationASTs.map(new Function<AST, Var>() {
                public Var apply(AST value) {
                    return parseDeclaration(value);
                }
            });
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
        PCodeType type = parseType(varAST.right.label);
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
                        parseVarReference(statementAST.left, symbolTable).get(),
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

    public static Expr parseExpression(AST ast, HashMap<String, Var> symbolTable) {
        //noinspection unchecked
        return ((Option<Expr>) (Object) parseAtom(ast, symbolTable))
                .orElse((Option<Expr>) (Object) parseUnary(ast, symbolTable))
                .orElse((Option<Expr>) (Object) parseBinaryExpr(ast, symbolTable)).get();
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

    public static Option<Atom> parseAtom(AST ast, HashMap<String, Var> symbolTable) {
        // Dafaq. Double casting = Just believe me stupid java compiler.
        // Inheritance -> Variance -> Fucked up type inference
        // noinspection unchecked
        return ((Option<Atom>) (Object) parseConst(ast)).orElse((Option<Atom>) (Object) parseVarReference(ast, symbolTable));
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

    public static PCodeType parseType(String type) {
        switch (type) {
            case "Bool":
                return PCodeType.Bool;
            case "Int":
                return PCodeType.Int;
            case "Real":
                return PCodeType.Real;
            default:
                throw new IllegalArgumentException("parseType(" + type + "), illegal type name.");
        }
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
