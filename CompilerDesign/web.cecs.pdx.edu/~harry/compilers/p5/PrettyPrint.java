// PrettyPrint.java
//
// Harry Porter -- 10/29/05
//
// This file contains a static method called "prettyPrintAst()" which can be
// called to print an Abstract Syntax Tree in a form closely resembling
// the PCAT program from which the AST originated.  This is useful for
// making sure that the ASTs we are building match the syntax of the source
// program.
//
// The output from "printAst()" tends to be long and difficult to read, since it
// was designed to be a complete and reliable dump of the AST.  However, you may
// wish to see only a small, selected part of the AST, to check see what your
// program is producing.  The code in this file can be modified easily to print
// just the portions of the AST you are interested in.
//
// Of course, the comments and the indentation of the original source file
// are not captured in the AST, so the output from this program will have no
// comments and will have rather mechanical indentation and spacing.
// Nevertheless, the indentation should resemble that a human PCAT programmer
// might use.  Also, this program will fully parenthesize expressions, leaving
// no doubt as to how expressions have been parsed.
//
// For example, here is a simple PCAT program:
//
//       program is
//         var x: integer := 1;
//                   y: integer := 2;   (* funky indentation *)
//             begin
//              x := 5*y;  (* Parens are not used here. *)
//             end;
//
// Here is how prettyPrint() prints the Abstract Syntax Tree we build
// after parsing the above program:
//
// 1.    PROGRAM IS
// 2.        VAR
// 3.            x: integer := 1;
// 4.            y: integer := 2;
// 5.        BEGIN
// 6.            x := (5 * y);
// 7.        END;
//
// (Line numbers have been added for ease of reference in this discussion.)
//
// In addition to printing the tokens, these methods can be easily modified
// to print the "lexLevel", "currentLevel", and "myDef" fields wherever
// desired.  The print statements to print that info are included in
// this program but these lines are currently commented out.  These lines can be
// uncommented to get the output.  Code to print the ValueOf nodes is
// also included, but is also commented out.
//
// For example, the following output shows how these methods would print the
// Abstract Syntax Tree for a simple program if the appropriate statements
// were uncommented.
//
// 1.    PROGRAM IS
// 2.        VAR
// 3.             [#1:] x [lexLevel=0] : integer [myDef=#2]  := 1;
// 4.             [#3:] y [lexLevel=0] : integer [myDef=#2]  := 2;
// 5.        BEGIN
// 6.            x [myDef=#1]  [currLev=0]  := (5 * ValueOf (y [myDef=#3]  [currLev=0] ));
// 7.        END;
//
// Note that the "myDef" fields point back to VarDecl nodes.  To indicate
// which pointers point where, certain structures are labelled as they
// are printed.  For example, the VarDecl structure for "x" is labelled
// "#1".  Later, on line 6, a Variable node for "x" is printed.  The value
// of its "myDef" field is shown using this label.
//
// These methods do not alway check the AST and may crash if the tree is
// messed up.  If, for example, some field is null which should not be null,
// these methods may throw a NullPointerException.
//

import java.io.*;
import java.util.*;

class PrettyPrint {

    //
    // Static Variables
    //
    // "ptrMap" is a mapping from ptrs to integers, used to give each object a
    // unique number.  See the "printPtr" method for more details.
    //
    static private Map ptrMap = new HashMap ();
    static int nextLabel = 1;                     // Next number to use

    static final int TAB = 4;                     // Number of spaced for indenting


    //
    //  Constructor.
    //
    private PrettyPrint () { }
    
    
    // prettyPrintAst (body)
    //
    // This method prints the Abstract Syntax Tree with formatting.
    // It is passed a pointer to a Body node.
    //
    // This method will create a "PrettyPrint" object, which is only used
    // during the pretty-printing.  It has no fields.  All of the remaining
    // methods are instance methods, but they could just as easily have been
    // static methods, since the receiver is never really used.
    //
    static void prettyPrintAst (Ast.Body body) {
        PrettyPrint pp = new PrettyPrint ();
        System.out.println ("PROGRAM IS");
        pp.ppBody (TAB, body);
    }
    
    
    
    //
    // ppBody (indent, body)
    //
    // Print the Body, indented by "indent" spaces.
    //
    void ppBody (int indent, Ast.Body body) {
        if (body.typeDecls != null) {
            printLine (indent, "TYPE");
            ppTypeDecls (indent + TAB, body.typeDecls);
        }
        if (body.varDecls != null) {
            printLine (indent, "VAR");
            ppVarDecls (indent + TAB, body.varDecls);
        }
        ppProcDecls (indent, body.procDecls);
        printLine (indent, "BEGIN");
        ppStmts (indent + TAB, body.stmts);
        printLine (indent, "END;");
    }
    
    
    
    //
    // ppTypeDecls (indent, typeDeclList)
    //
    // Print all the TypeDecls pointed to by "typeDeclList",
    // indented by "indent" spaces.
    //
    void ppTypeDecls (int indent, Ast.TypeDecl p) {
        while (p != null) {
            printIndent (indent);
            // For testing; feel free to uncomment the following...
            // printLabel (p);
            System.out.print (p.id);
            System.out.print (" IS ");
            ppCompoundType (p.compoundType);
            System.out.println (";");
            p = p.next;
        }
    }
    
    
    
    //
    // ppVarDecls (indent, varDeclList)
    //
    // Print the list of VarDecls pointed to by "varDeclList",
    // indented by "indent" spaces.
    //
    void ppVarDecls (int indent, Ast.VarDecl p) {
        while (p != null) {
            printIndent (indent);
            // For testing; feel free to uncomment the following...
            // printLabel (p);
            System.out.print (p.id);
            // For testing; feel free to uncomment the following...
            // System.out.print (" [lexLevel=" + p.lexLevel + "] ");
            if (p.typeName != null) {
                System.out.print (": ");
                ppTypeName (p.typeName);
            }
            if (p.expr != null) {
                System.out.print (" := ");
                ppExpr (p.expr);
            }
            System.out.println (";");
            p = p.next;
        }
    }
    
    
    
    //
    // ppProcDecls (indent, procDeclList)
    //
    // Print the list of ProcDecls pointed to by "procDeclList",
    // indented by "indent" spaces.
    //
    void ppProcDecls (int indent, Ast.ProcDecl p) {
        while (p != null) {
            ppProcDecl (indent, p);
            p = p.next;
        }
    }
    
    
    
    //
    // ppProcDecl (indent, procDecl)
    //
    // Print the ProcDecl, indented by "indent" spaces.
    //
    void ppProcDecl (int indent, Ast.ProcDecl p) {
        printIndent (indent);
        System.out.print ("PROCEDURE ");
        // For testing; feel free to uncomment the following...
        // printLabel (p);
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // System.out.print (" [lexLevel=" + p.lexLevel + "] ");
        ppFormals (p.formals);
        if (p.retType != null) {
            System.out.print (" : ");
            ppTypeName (p.retType);
        }
        System.out.println (" IS");
        ppBody (indent+TAB, p.body);
    }
    
    
    
    //
    // ppFormals (formalList)
    //
    // Print the list of Formals pointed to by "formalList".
    //
    void ppFormals (Ast.Formal p) {
        System.out.print (" (");
        while (p != null) {
            // For testing; feel free to uncomment the following...
            // printLabel (p);
            System.out.print (p.id);
            // For testing; feel free to uncomment the following...
            // System.out.print (" [lexLevel=" + p.lexLevel + "] ");
            System.out.print (": ");
            ppTypeName (p.typeName);
            p = p.next;
            if (p != null) {
              System.out.print ("; ");
            }
        }
        System.out.print (")");
    }
    
    
    
    //
    // ppTypeName (typeName)
    //
    // Print the TypeName.
    //
    void ppTypeName (Ast.TypeName p) {
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // printMyDef (p.myDef);

    }



    //
    // ppCompoundType (compoundType)
    //
    // Print the CompoundType.
    //
    void ppCompoundType (Ast.CompoundType p) {
        if (p == null) {
            System.out.print ("NULL");
        } else if (p instanceof Ast.ArrayType) {
            ppArrayType ((Ast.ArrayType) p);
        } else if (p instanceof Ast.RecordType) {
            ppRecordType ((Ast.RecordType) p);
        } else {
            System.out.print ("*****  Unknown class in ppCompoundType!  *****");
        }
    }
    
    
    
    //
    // ppArrayType (arrayType)
    //
    // Print the ArrayType.
    //
    void ppArrayType (Ast.ArrayType p) {
        System.out.print ("ARRAY OF ");
        ppTypeName (p.elementType);
    }
    
    
    
    //
    // ppRecordType (recordType)
    //
    // Print the RecordType.
    //
    void ppRecordType (Ast.RecordType p) {
        System.out.print ("RECORD ");
        ppFieldDecls (p.fieldDecls);
        System.out.print ("END");
    }
    
    
    
    //
    // ppFieldDecls (fieldDecl)
    //
    // Print the FieldDecl.
    //
    void ppFieldDecls (Ast. FieldDecl p) {
        while (p != null) {
            System.out.print (p.id);
            System.out.print (": ");
            ppTypeName (p.typeName);
            System.out.print ("; ");
            p = p.next;
        }
    }
    
    
    
    //
    // ppStmts (indent, stmt)
    //
    // Print the list of statements pointed to by "stmt", indented by
    // "indent" spaces.  The argument "stmt" may be NULL; in that case
    // nothing will be printed.
    //
    void ppStmts (int indent, Ast.Stmt stmt) {
        while (stmt != null) {
            if (stmt instanceof Ast.AssignStmt) {
                ppAssignStmt (indent, (Ast.AssignStmt) stmt);
            } else if (stmt instanceof Ast.CallStmt) {
                ppCallStmt (indent, (Ast.CallStmt) stmt);
            } else if (stmt instanceof Ast.ReadStmt) {
                ppReadStmt (indent, (Ast. ReadStmt) stmt);
            } else if (stmt instanceof Ast.WriteStmt) {
                ppWriteStmt (indent, (Ast.WriteStmt) stmt);
            } else if (stmt instanceof Ast.WhileStmt) {
                ppWhileStmt (indent, (Ast.WhileStmt) stmt);
            } else if (stmt instanceof Ast.LoopStmt) {
                ppLoopStmt (indent, (Ast.LoopStmt) stmt);
            } else if (stmt instanceof Ast.ForStmt) {
                ppForStmt (indent, (Ast.ForStmt) stmt);
            } else if (stmt instanceof Ast.IfStmt) {
                ppIfStmt (indent, (Ast.IfStmt) stmt);
            } else if (stmt instanceof Ast.ExitStmt) {
                ppExitStmt (indent, (Ast.ExitStmt) stmt);
            } else if (stmt instanceof Ast.ReturnStmt) {
                ppReturnStmt (indent, (Ast.ReturnStmt) stmt);
            } else {
                System.out.print ("*****  Unknown class within ppStmts!  *****");
            }
            stmt = stmt.next;
        }
    }
    
    
    
    //
    // ppAssignStmt (indent, assignStmt)
    //
    // Print the AssignStmt, indented by "indent" spaces.
    //
    void ppAssignStmt (int indent, Ast.AssignStmt p) {
        printIndent (indent);
        ppLValue (p.lValue);
        System.out.print (" :=");
        System.out.print (" ");
        ppExpr (p.expr);
        System.out.println (";");
    }
    
    
    
    //
    // ppCallStmt (indent, callStmt)
    //
    // Print the CallStmt, indented by "indent" spaces.
    //
    void ppCallStmt (int indent, Ast.CallStmt p) {
        printIndent (indent);
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // printMyDef (p.myDef);
        ppArguments (p.args);
        System.out.println (";");
    }
    
    
    
    //
    // ppReadStmt (indent, readStmt)
    //
    // Print the ReadStmt, indented by "indent" spaces.
    //
    void ppReadStmt (int indent, Ast.ReadStmt p) {
        printIndent (indent);
        System.out.print ("READ (");
        ppReadArgs (p.readArgs);
        System.out.println (");");
    }
    
    
    
    //
    // ppReadArgs (readArg)
    //
    // Print the list of ReadArgs.
    //
    void ppReadArgs (Ast.ReadArg p) {
        while (p != null) {
            ppLValue (p.lValue);
            // printMode (p.mode);
            p = p.next;
            if (p != null) {
              System.out.print (", ");
            }
        }
    }
    
    
    
    //
    // ppWriteStmt (indent, writeStmt)
    //
    // Print the WriteStmt, indented by "indent" spaces.
    //
    void ppWriteStmt (int indent, Ast.WriteStmt p) {
        printIndent (indent);
        System.out.print ("WRITE");
        ppArguments (p.args);
        System.out.println (";");
    }
    
    
    
    //
    // ppIfStmt (indent, ifStmt)
    //
    // Print the IfStmt, indented by "indent" spaces.
    //
    void ppIfStmt (int indent, Ast.IfStmt p) {
        printIndent (indent);
        System.out.print ("IF ");
        ppExpr (p.expr);
        System.out.println (" THEN");
        ppStmts (indent + TAB, p.thenStmts);
        if (p.elseStmts != null) {
            printLine (indent, "ELSE");
            ppStmts (indent + TAB, p.elseStmts);
        }
        printLine (indent, "END;");
    }
    
    
    
    //
    // ppWhileStmt (indent, whileStmt)
    //
    // Print the WhileStmt, indented by "indent" spaces.
    //
    void ppWhileStmt (int indent, Ast.WhileStmt p) {
        printIndent (indent);
        // For testing; feel free to uncomment the following...
        // printLabel (p);
        System.out.print ("WHILE ");
        ppExpr (p.expr);
        System.out.println (" DO");
        ppStmts (indent + TAB, p.stmts);
        printLine (indent, "END;");
    }
    
    
    
    //
    // ppLoopStmt (indent, loopStmt)
    //
    // Print the LoopStmt, indented by "indent" spaces.
    //
    void ppLoopStmt (int indent, Ast.LoopStmt p) {
        printIndent (indent);
        // For testing; feel free to uncomment the following...
        // printLabel (p);
        System.out.println ("LOOP");
        ppStmts (indent + TAB, p.stmts);
        printLine (indent, "END;");
    }
    
    
    
    //
    // ppForStmt (indent, forStmt)
    //
    // Print the ForStmt, indented by "indent" spaces.
    //
    void ppForStmt (int indent, Ast.ForStmt p) {
        printIndent (indent);
        // For testing; feel free to uncomment the following...
        // printLabel (p);
        System.out.print ("FOR ");
        ppLValue (p.lValue);
        System.out.print (" := ");
        ppExpr (p.expr1);
        System.out.print (" TO ");
        ppExpr (p.expr2);
        if (p.expr3 != null) {
            System.out.print (" BY ");
            ppExpr (p.expr3);
        }
        System.out.println (" DO");
        ppStmts (indent + TAB, p.stmts);
        printLine (indent, "END;");
    }
    
    
    
    //
    // ppExitStmt (indent, exitStmt)
    //
    // Print the ExitStmt, indented by "indent" spaces.
    //
    void ppExitStmt (int indent, Ast.ExitStmt p) {
        printIndent (indent);
        System.out.print ("EXIT");
        // For testing; feel free to uncomment the following...
        // System.out.print (" [myLoop=");
        // printPtr (p.myLoop);
        // System.out.print ("] ");
        System.out.println (";");
    }
    
    
    
    //
    // ppReturnStmt (indent, returnStmt)
    //
    // Print the ReturnStmt, indented by "indent" spaces.
    //
    void ppReturnStmt (int indent, Ast.ReturnStmt p) {
        printIndent (indent);
        System.out.print ("RETURN");
        if (p.expr != null) {
            System.out.print (" ");
            ppExpr (p.expr);
        }
        // For testing; feel free to uncomment the following...
        // System.out.print (" [myProc=");
        // printPtr (p.myProc);
        // System.out.print ("] ");
        System.out.println (";");
    }
    
    
    
    //
    // ppExpr (p)
    //
    // Print the expression pointed to by "p".
    //
    void ppExpr (Ast.Expr p) {
        if (p instanceof Ast.BinaryOp) {
            ppBinaryOp ((Ast.BinaryOp) p);
        } else if (p instanceof Ast.UnaryOp) {
            ppUnaryOp ((Ast.UnaryOp) p);
        } else if (p instanceof Ast.IntToReal) {
            ppIntToReal ((Ast.IntToReal) p);
        } else if (p instanceof Ast.FunctionCall) {
            ppFunctionCall ((Ast.FunctionCall) p);
        } else if (p instanceof Ast.ArrayConstructor) {
            ppArrayConstructor ((Ast.ArrayConstructor) p);
        } else if (p instanceof Ast.RecordConstructor) {
            ppRecordConstructor ((Ast.RecordConstructor) p);
        } else if (p instanceof Ast.IntegerConst) {
            System.out.print (((Ast.IntegerConst) p).iValue);
        } else if (p instanceof Ast.RealConst) {
            System.out.print (((Ast.RealConst) p).rValue);
        } else if (p instanceof Ast.StringConst) {
            System.out.print ("\"" + ((Ast.StringConst) p).sValue + "\"");
        } else if (p instanceof Ast.BooleanConst) {
            if (((Ast.BooleanConst) p).iValue == 0) {
                System.out.print ("FALSE");
            } else {
                System.out.print ("TRUE");
            }
        } else if (p instanceof Ast.NilConst) {
            System.out.print ("NIL");
        } else if (p instanceof Ast.ValueOf) {
            ppValueOf ((Ast.ValueOf) p);
        } else {
            System.out.print ("*****  Unknown class within ppExpr!  *****");
        }
    }
    
    
    
    //
    // ppBinaryOp (binaryOp)
    //
    // Print the BinaryOp.
    //
    void ppBinaryOp (Ast.BinaryOp p) {
        System.out.print ("(");
        ppExpr (p.expr1);
        switch (p.op) {
            case Token.DIV:
              System.out.print (" DIV ");
              break;
            case Token.MOD:
              System.out.print (" MOD ");
              break;
            case Token.OR:
              System.out.print (" OR ");
              break;
            case Token.AND:
              System.out.print (" AND ");
              break;
            case Token.LEQ:
              System.out.print (" <= ");
              break;
            case Token.GEQ:
              System.out.print (" >= ");
              break;
            case Token.NEQ:
              System.out.print (" <> ");
              break;
            case Token.LESS:
              System.out.print (" < ");
              break;
            case Token.GREATER:
              System.out.print (" > ");
              break;
            case Token.EQUAL:
              System.out.print (" = ");
              break;
            case Token.PLUS:
              System.out.print (" + ");
              break;
            case Token.MINUS:
              System.out.print (" - ");
              break;
            case Token.STAR:
              System.out.print (" * ");
              break;
            case Token.SLASH:
              System.out.print (" / ");
              break;
            default:
              System.out.print (" *****  Bad op in ppBinaryOp!  ***** ");
              break;
        }
        // printMode (p.mode);
        ppExpr (p.expr2);
        System.out.print (")");
    }
    
    
    
    //
    // ppUnaryOp (unaryOp)
    //
    // Print the UnaryOp.
    //
    void ppUnaryOp (Ast.UnaryOp p) {
        switch (p.op) {
            case Token.NOT:
              System.out.print ("NOT ");
              break;
            case Token.PLUS:
              System.out.print ("+ ");
              break;
            case Token.MINUS:
              System.out.print ("- ");
              break;
            default:
              System.out.print ("*****  Bad op in ppUnaryOp!  *****");
              break;
        }
        // printMode (p.mode);
        ppExpr (p.expr);
    }
    
    
    
    //
    // ppIntToReal (intToReal)
    //
    // Print the IntToReal.
    //
    void ppIntToReal (Ast.IntToReal p) {
        System.out.print ("intToReal (");
        ppExpr (p.expr);
        System.out.print (")");
    }
    
    
    
    //
    // ppFunctionCall (functionCall)
    //
    // Print the FunctionCall.
    //
    void ppFunctionCall (Ast.FunctionCall p) {
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // printMyDef (p.myDef);
        ppArguments (p.args);
    }
    
    
    
    //
    // ppArguments (p)
    //
    // Print the lists of Arguments pointed to by "p".
    //
    void ppArguments (Ast.Argument p) {
        System.out.print (" (");
        while (p != null) {
            ppExpr (p.expr);
            // printMode (p.mode);
            p = p.next;
            if (p != null) {
                System.out.print (", ");
            }
        }
        System.out.print (")");
    }
    
    
    
    //
    // ppArrayConstructor (arrayConstructor)
    //
    // Print the ArrayConstructor.
    //
    void ppArrayConstructor (Ast.ArrayConstructor p) {
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // printMyDef (p.myDef);
        System.out.print (" {{ ");
        ppArrayValues (p.values);
        System.out.print (" }}");
    }
    
    
    
    //
    // ppArrayValues (arrayValueList)
    //
    // Print the list of ArrayValues pointed to by "arrayValueList".
    //
    void ppArrayValues (Ast.ArrayValue p) {
        while (p != null) {
            if (p.countExpr != null) {
              ppExpr (p.countExpr);
              System.out.print (" OF ");
            }
            ppExpr (p.valueExpr);
            p = p.next;
            if (p != null) {
              System.out.print (", ");
            }
        }
    }
    
    
    
    //
    // ppRecordConstructor (recordConstructor)
    //
    // Print the RecordConstructor.
    //
    void ppRecordConstructor (Ast.RecordConstructor p) {
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // printMyDef (p.myDef);
        System.out.print (" { ");
        ppFieldInits (p.fieldInits);
        System.out.print (" }");
    }
    
    
    
    //
    // ppFieldInits (fieldInitList)
    //
    // Print the list of FieldInits pointed to by "fieldInitList".
    //
    void ppFieldInits (Ast.FieldInit p) {
        while (p != null) {
            System.out.print (p.id + " := ");
            ppExpr (p.expr);
            p = p.next;
            if (p != null) {
              System.out.print ("; ");
            }
        }
    }
    
    
    
    //
    // ppValueOf (valueOf)
    //
    // Print the ValueOf.
    //
    void ppValueOf (Ast.ValueOf p) {
        // For testing; feel free to uncomment the following...
        // System.out.print ("ValueOf (");
        ppLValue (p.lValue);
        // For testing; feel free to uncomment the following...
        // System.out.print (")");
    }
    
    
    
    //
    // ppLValue (LValue)
    //
    // Print the LValue.
    //
    void ppLValue (Ast.LValue p) {
        if (p instanceof Ast.Variable) {
            ppVariable ((Ast.Variable) p);
        } else if (p instanceof Ast.ArrayDeref) {
            ppArrayDeref ((Ast.ArrayDeref) p);
        } else if (p instanceof Ast.RecordDeref) {
            ppRecordDeref ((Ast.RecordDeref) p);
        } else {
            System.out.print ("*****  Unknown class in ppLValue!  *****");
        }
    }
    
    
    
    //
    // ppVariable (variable)
    //
    // Print the Variable.
    //
    void ppVariable (Ast.Variable p) {
        System.out.print (p.id);
        // For testing; feel free to uncomment the following...
        // printMyDef (p.myDef);
        // For testing; feel free to uncomment the following...
        // System.out.print (" [currLev=" + p.currentLevel + "] ");
    }
    
    
    
    //
    // ppArrayDeref (arrayDeref)
    //
    // Print the ArrayDeref.
    //
    void ppArrayDeref (Ast.ArrayDeref p) {
        ppLValue (p.lValue);
        System.out.print ("[");
        ppExpr (p.expr);
        System.out.print ("]");
    }
    
    
    
    //
    // ppRecordDeref (recordDeref)
    //
    // Print the RecordDeref.
    //
    void ppRecordDeref (Ast.RecordDeref p) {
        ppLValue (p.lValue);
        System.out.print ("." + p.id);
    }
    
    
    
    //
    // printIndent (indent)
    //
    // This method prints "indent" space characters.
    //
    void printIndent (int indent) {
        for (int i=indent; i>0; i--) {
            System.out.print (" ");
        }
    }
    
    
    
    //
    // printLine (indent, str)
    //
    // This method indents, then prints the given string, then prints newline.
    //
    void printLine (int indent, String str) {
        printIndent (indent);
        System.out.println (str);
    }



    //
    // printPtr (p)
    //
    // This method is passed a pointer, possibly null.  It prints the pointer.
    // The actual address is not printed, since these may vary from run to run.
    // Instead, this method assigns "labels" to each address and prints the
    // same label each time.  It saves the mapping between label and address
    // in a static hashMap called "ptrMap".
    //
    void printPtr (Ast.Node p) {
        int i;

        // If the pointer is null, then print "NULL"...
        if (p == null) {
            System.out.print ("NULL");
            return;
        }

        // Figure out what integer goes with this ptr...
        Integer val = (Integer) ptrMap.get (p);
        if (val != null) {
          i = val.intValue ();
        } else {
          i = nextLabel++;
          ptrMap.put (p, new Integer (i));
        }

        // Print the number...
        System.out.print ("#");
        System.out.print (i);
    }
    
    
    
    //
    // printLabel (p)
    //
    // This method prints something like " [#123:] "
    //
    void printLabel (Ast.Node p) {
        System.out.print (" [");
        printPtr (p);
        System.out.print (":] ");
    }
    
    
    
    //
    // printMyDef (p)
    //
    // This method prints something like " [myDef=#123] "
    //
    void printMyDef (Ast.Node p) {
        System.out.print (" [myDef=");
        printPtr (p);
        System.out.print ("] ");
    }



    // printMode (m)
    //
    // Print something like "[mode=INTEGER] " to indicate the value of argument m.
    //
    void printMode (int m) {
        final int INTEGER_MODE = 1;
        final int REAL_MODE = 2;
        final int STRING_MODE = 3;
        final int BOOLEAN_MODE = 4;
        switch (m) {
            case INTEGER_MODE:
                System.out.print ("[mode=INTEGER] ");
                break;
            case REAL_MODE:
                System.out.print ("[mode=REAL] ");
                break;
            case BOOLEAN_MODE:
                System.out.print ("[mode=BOOLEAN] ");
                break;
            case STRING_MODE:
                System.out.print ("[mode=STRING] ");
                break;
            case 0:
                System.out.print ("[mode=0] ");
                break;
            default:
                System.out.print ("[mode=***** Garbage *****] ");
                break;
        }
    }

}