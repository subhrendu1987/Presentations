// -------------------------------- Checker --------------------------------
//
// Methods to walk an Abstract Syntax Tree, performing type checking.
// There should only be one instance of this class.  The primary method is:
//      checker.checkAST (ast)
// This method will walk the tree (calling other methods as necessary).
// If semantic errors occur, a message will be printed and the walk will
// continue, looking for additional errors.
//
// <Your name here> -- 00/00/00
//

import java.io.*;

class Checker {

    //
    //  Constructor
    //
    Checker () { }



    // semanticError (t, msg)
    //
    // This method is passed a pointer to a node.  It prints out the
    // line number of that node and a description of that node.  It then
    // prints the given message.  It increments the "errorCount".
    // It does not halt the compiler.  Instead, it returns.
    //
    void semanticError (Ast.Node t, String msg)
        throws FatalError
    {
        Main.errorCount ++;
        System.err.print ("Error on line " + t.lineNumber);
        printNear (t);
        System.err.println (": " + msg);
    }



    //
    // printNear (t)
    //
    // This method is passed a pointer to a tree.  It prints
    // " near 'xxx'" where xxx is a description of the node.
    //
    void printNear (Ast.Node t)
        throws FatalError
    {
        if (t instanceof Ast.Body) {
            System.err.print (" near 'begin'");
        } else if (t instanceof Ast.VarDecl) {
            System.err.print (" near '" + ((Ast.VarDecl) t).id + "'");
        } else if (t instanceof Ast.TypeDecl) {
            System.err.print (" near '" + ((Ast.TypeDecl) t).id + "'");
        } else if (t instanceof Ast.ProcDecl) {
            System.err.print (" near '" + ((Ast.ProcDecl) t).id + "'");
        } else if (t instanceof Ast.Formal) {
            System.err.print (" near '" + ((Ast.Formal) t).id + "'");
        } else if (t instanceof Ast.TypeName) {
            System.err.print (" near '" + ((Ast.TypeName) t).id + "'");
        } else if (t instanceof Ast.ArrayType) {
            System.err.print (" near 'array'");
        } else if (t instanceof Ast.RecordType) {
            System.err.print (" near 'record'");
        } else if (t instanceof Ast.FieldDecl) {
            System.err.print (" near '" + ((Ast.FieldDecl) t).id + "'");
        } else if (t instanceof Ast.AssignStmt) {
            System.err.print (" near ':='");
        } else if (t instanceof Ast.CallStmt) {
            System.err.print (" near '" + ((Ast.CallStmt) t).id + "'");
        } else if (t instanceof Ast.ReadStmt) {
            System.err.print (" near 'read'");
        } else if (t instanceof Ast.ReadArg) {
            printNear (((Ast.ReadArg) t).lValue);
        } else if (t instanceof Ast.WriteStmt) {
            System.err.print (" near 'write'");
        } else if (t instanceof Ast.IfStmt) {
            System.err.print (" near 'if'");
        } else if (t instanceof Ast.WhileStmt) {
            System.err.print (" near 'while'");
        } else if (t instanceof Ast.LoopStmt) {
            System.err.print (" near 'loop'");
        } else if (t instanceof Ast.ForStmt) {
            System.err.print (" near 'for " + ((Ast.Variable) ((Ast.ForStmt) t).lValue).id + "'");
        } else if (t instanceof Ast.ExitStmt) {
            System.err.print (" near 'exit'");
        } else if (t instanceof Ast.ReturnStmt) {
            System.err.print (" near 'return'");
        } else if (t instanceof Ast.IntToReal) {
            printNear (((Ast.IntToReal) t).expr);
        } else if (t instanceof Ast.UnaryOp) {
            Ast.UnaryOp unOp = (Ast.UnaryOp) t;
            if (unOp.op == Token.PLUS) {
                System.err.print (" near '+'");
            } else if (unOp.op == Token.MINUS) {
                System.err.print (" near '-'");
            } else if (unOp.op == Token.NOT) {
                System.err.print (" near 'not'");
            } else {
               throw new LogicError ("Unknown UnaryOp.op in method printNear");
            }
        } else if (t instanceof Ast.BinaryOp) {
            Ast.BinaryOp binOp = (Ast.BinaryOp) t;
            if (binOp.op == Token.PLUS) {
                System.err.print (" near '+'");
            } else if (binOp.op == Token.MINUS) {
                System.err.print (" near '-'");
            } else if (binOp.op == Token.STAR) {
                System.err.print (" near '*'");
            } else if (binOp.op == Token.SLASH) {
                System.err.print (" near '/'");
            } else if (binOp.op == Token.EQUAL) {
                System.err.print (" near '='");
            } else if (binOp.op == Token.LESS) {
                System.err.print (" near '<'");
            } else if (binOp.op == Token.GREATER) {
                System.err.print (" near '>'");
            } else if (binOp.op == Token.MOD) {
                System.err.print (" near 'mod'");
            } else if (binOp.op == Token.DIV) {
                System.err.print (" near 'div'");
            } else if (binOp.op == Token.OR) {
                System.err.print (" near 'or'");
            } else if (binOp.op == Token.AND) {
                System.err.print (" near 'and'");
            } else if (binOp.op == Token.NEQ) {
                System.err.print (" near '<>'");
            } else if (binOp.op == Token.LEQ) {
                System.err.print (" near '<='");
            } else if (binOp.op == Token.GEQ) {
                System.err.print (" near '>='");
            } else {
                throw new LogicError ("Unknown BinaryOp.op in method printNear");
            }
        } else if (t instanceof Ast.FunctionCall) {
            System.err.print (" near '" + ((Ast.FunctionCall) t).id + "'");
        } else if (t instanceof Ast.Argument) {
            printNear (((Ast.Argument) t).expr);
        } else if (t instanceof Ast.ArrayConstructor) {
            System.err.print (" near '" + ((Ast.ArrayConstructor) t).id + "'");
        } else if (t instanceof Ast.ArrayValue) {
            printNear (((Ast.ArrayValue) t).valueExpr);
        } else if (t instanceof Ast.RecordConstructor) {
            System.err.print (" near '" + ((Ast.RecordConstructor) t).id + "'");
        } else if (t instanceof Ast.FieldInit) {
            System.err.print (" near '" + ((Ast.FieldInit) t).id + "'");
        } else if (t instanceof Ast.IntegerConst) {
            System.err.print (" near '" + ((Ast.IntegerConst) t).iValue + "'");
        } else if (t instanceof Ast.RealConst) {
            System.err.print (" near '" + ((Ast.RealConst) t).rValue + "'");
        } else if (t instanceof Ast.StringConst) {
            System.err.print (" near '" + ((Ast.StringConst) t).sValue + "'");
        } else if (t instanceof Ast.BooleanConst) {
            if (((Ast.BooleanConst) t).iValue == 0) {
                System.err.print (" near 'false'");
            } else {
                System.err.print (" near 'true'");
            }
        } else if (t instanceof Ast.NilConst) {
            System.err.print (" near 'nil'");
        } else if (t instanceof Ast.ValueOf) {
            printNear (((Ast.ValueOf) t).lValue);
        } else if (t instanceof Ast.Variable) {
            System.err.print (" near '" + ((Ast.Variable) t).id + "'");
        } else if (t instanceof Ast.ArrayDeref) {
            printNear (((Ast.ArrayDeref) t).lValue);
        } else if (t instanceof Ast.RecordDeref) {
            printNear (((Ast.RecordDeref) t).lValue);
        } else {
            throw new LogicError ("Unknown class in method printNear");
        }
    }



    //
    // checkAst (body)
    //
    // This method checks the Abstract Syntax Tree.
    //
    void checkAst (Ast.Body body)
        throws FatalError
    {
        Main.parser.lexer.lineNumber = 0;    // For newly created nodes in proj 6

        // Initialize nilString, trueString,...

        checkBody (body);
    }



    // checkBody (body)
    //
    // Check the given Body for semantic errors.
    //
    void checkBody (Ast.Body body)
        throws FatalError
    {
        enterTypeDecls (body.typeDecls);
        checkTypeDecls (body.typeDecls);
        enterProcDecls (body.procDecls);
        enterAndCheckVarDecls (body.varDecls);
        checkProcDecls (body.procDecls);
        // SymbolTable.printTable ();          // Debugging: print out symbol table
        checkStmts (body.stmts);
    }


...


    //
    // checkIfStmt (p)
    //
    // Check the IF_STMT pointed to by "p" for semantic errors.
    //
    void checkIfStmt (Ast.IfStmt p)
        throws FatalError
    {
        checkExpr (p.expr);
        checkStmts (p.thenStmts);
        checkStmts (p.elseStmts);
    }



...



    //
    // checkExpr (p)
    //
    // Check the expression pointed to by "p" for semantic errors.
    //
    void checkExpr (Ast.Node p)
        throws FatalError
    {
        if (p instanceof Ast.BinaryOp) {
            checkBinaryOp ((Ast.BinaryOp) p);
        } else if (p instanceof Ast.UnaryOp) {
            checkUnaryOp ((Ast.UnaryOp) p);
...
        } else {
            throw new LogicError ("Unknown class within checkExpr");
        }
    }
