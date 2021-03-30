// --------------------------- IR ------------------------------
//
// This file contains the classes used to represent an
// Intermediate Representation instruction.
//
// Harry Porter -- 04/13/03
//

class IR {

    //
    // Constants - These are the op-codes for the IR instructions
    //
    static final int OPassign = 1;
    static final int OPloadAddr = 2;
    static final int OPstore = 3;
    static final int OPloadIndirect = 4;
    static final int OPiadd = 5;
    static final int OPisub = 6;
    static final int OPimul = 7;
    static final int OPidiv = 8;
    static final int OPimod = 9;
    static final int OPineg = 10;
    static final int OPfadd = 11;
    static final int OPfsub = 12;
    static final int OPfmul = 13;
    static final int OPfdiv = 14;
    static final int OPfneg = 15;
    static final int OPitof = 16;
    static final int OPcall = 17; 
    static final int OPparam = 18;
    static final int OPresultTo = 19;
    static final int OPmainEntry = 20;
    static final int OPmainExit = 21;
    static final int OPprocEntry = 22;
    static final int OPformal = 23;
    static final int OPreturnExpr = 24;
    static final int OPreturnVoid = 25;
    static final int OPlabel = 26;
    static final int OPgoto = 27;
    static final int OPgotoiEQ = 28;
    static final int OPgotoiNE = 29;
    static final int OPgotoiLT = 30;
    static final int OPgotoiLE = 31;
    static final int OPgotoiGT = 32;
    static final int OPgotoiGE = 33;
    static final int OPgotofEQ = 34;
    static final int OPgotofNE = 35;
    static final int OPgotofLT = 36;
    static final int OPgotofLE = 37;
    static final int OPgotofGT = 38;
    static final int OPgotofGE = 39;
    static final int OPcomment = 40;
    static final int OPalloc = 41;
    static final int OPreadInt = 42;
    static final int OPreadFloat = 43;
    static final int OPwriteInt = 44;
    static final int OPwriteFloat = 45;
    static final int OPwriteString = 46;
    static final int OPwriteBoolean = 47;
    static final int OPwriteNewline = 48;

    //
    // Static Fields -- This is the linked list of IR instructions.
    //
    static IR firstInstruction;               // Pointer to head
    static IR lastInstruction;                // Pointer to tail



    //
    // Fields  -- These fields describe one IR instruction.  Instructions
    //            are kept in a linked list, using the field "next".  The
    //            "op" field tells what kind of instruction it is, e.g.,
    //            OPassign, OPiadd, OPgoto,...  Each instruction will
    //            have zero or more operands, which will be stored in the
    //            fields "result", "arg1", "arg2", "str", or "iValue".
    //
    int         op;
    Ast.Node    result;
    Ast.Node    arg1;
    Ast.Node    arg2;
    String      str;
    int         iValue;
    IR          next;



    //
    // Constructor -- Create a new IR instruction and link it into the
    //                linked list of instructions.  The op-code is filled
    //                in, but none of the operands are filled in; this is
    //                the responsibility of the caller.
    //
    IR (int o) {
        super();
        op = o;
        next = null;
        if (firstInstruction == null) {
            firstInstruction = this;
        } else {
            lastInstruction.next = this;
        }
        lastInstruction = this;
    }



    //
    // Each of these methods is used to generate a new IR instruction.
    // The new instruction is added to the end of the list of instructions.
    //
    // assign:  result := arg
    //
            static void assign (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPassign);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // loadAddr:  result := &arg
    //
            static void loadAddr (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPloadAddr);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // store:  *result := arg
    //
            static void store (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPstore);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // loadIndirect:  result := *arg
    //
            static void loadIndirect (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPloadIndirect);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // iadd:  result := arg1 + arg2 (int)
    //
            static void iadd (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPiadd);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // isub:  result := arg1 - arg2 (int)
    //
            static void isub (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPisub);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // imul:  result := arg1 * arg2 (int)
    //
            static void imul (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPimul);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // idiv:  result := arg1 DIV arg2 (int)
    //
            static void idiv (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPidiv);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // imod:  result := arg1 MOD arg2 (int)
    //
            static void imod (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPimod);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // ineg:  result := - arg (int)
    //
            static void ineg (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPineg);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // fadd:  result := arg1 + arg2 (float)
    //
            static void fadd (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPfadd);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // fsub:  result := arg1 - arg2 (float)
    //
            static void fsub (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPfsub);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // fmul:  result := arg1 * arg2 (float)
    //
            static void fmul (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPfmul);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // fdiv:  result := arg1 / arg2 (float)
    //
            static void fdiv (Ast.Node result, Ast.Node arg1, Ast.Node arg2) {
                IR inst = new IR (OPfdiv);
                inst.result = result;
                inst.arg1 = arg1;
                inst.arg2 = arg2;
            }
    //
    // fneg:  result := - arg (float)
    //
            static void fneg (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPfneg);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // itof:  result := intToFloat(arg)
    //
            static void itof (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPitof);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // call:  call proc
    //
            static void call (Ast.ProcDecl arg) {
                IR inst = new IR (OPcall);
                inst.arg1 = arg;
            }
    //
    // param:  param int,arg
    //
            static void param (int i, Ast.Node arg) {
                IR inst = new IR (OPparam);
                inst.iValue = i;
                inst.arg1 = arg;
            }
    //
    // resultTo:  resultTo result
    //
            static void resultTo (Ast.Node result) {
                IR inst = new IR (OPresultTo);
                inst.result = result;
            }
    //
    // mainEntry:  mainEntry body
    //
            static void mainEntry (Ast.Body arg) {
                IR inst = new IR (OPmainEntry);
                inst.arg1 = arg;
            }
    //
    // mainExit:  mainExit
    //
            static void mainExit () {
                IR inst = new IR (OPmainExit);
            }
    //
    // procEntry:  procEntry proc
    //
            static void procEntry (Ast.ProcDecl proc) {
                IR inst = new IR (OPprocEntry);
                inst.arg1 = proc;
            }
    //
    // formal:  formal int,form
    //
            static void formal (int i, Ast.Formal form) {
                IR inst = new IR (OPformal);
                inst.iValue = i;
                inst.arg1 = form;
            }
    //
    // returnExpr:  return arg
    //
            static void returnExpr (Ast.Node arg) {
                IR inst = new IR (OPreturnExpr);
                inst.arg1 = arg;
            }
    //
    // returnVoid:  return
    //
            static void returnVoid () {
                IR inst = new IR (OPreturnVoid);
            }
    //
    // label:  label:
    //
            static void label (String lab) {
                IR inst = new IR (OPlabel);
                inst.str = lab;
            }
    //
    // goto:  goto label
    //
            static void go_to (String lab) {
                IR inst = new IR (OPgoto);
                inst.str = lab;
            }
    //
    // gotoiEQ:  if arg1==arg2 then goto result (integer)
    //
            static void gotoiEQ (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotoiEQ);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotoiNE:  if arg1!=arg2 then goto result (integer)
    //
            static void gotoiNE (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotoiNE);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotoiLT:  if arg1<arg2 then goto result (integer)
    //
            static void gotoiLT (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotoiLT);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotoiLE:  if arg1<=arg2 then goto result (integer)
    //
            static void gotoiLE (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotoiLE);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotoiGT:  if arg1>arg2 then goto result (integer)
    //
            static void gotoiGT (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotoiGT);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotoiGE:  if arg1>=arg2 then goto result (integer)
    //
            static void gotoiGE (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotoiGE);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotofEQ:  if arg1==arg2 then goto result (float)
    //
            static void gotofEQ (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotofEQ);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotofNE:  if arg1!=arg2 then goto result (float)
    //
            static void gotofNE (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotofNE);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotofLT:  if arg1<arg2 then goto result (float)
    //
            static void gotofLT (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotofLT);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotofLE:  if arg1<=arg2 then goto result (float)
    //
            static void gotofLE (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotofLE);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotofGT:  if arg1>arg2 then goto result (float)
    //
            static void gotofGT (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotofGT);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // gotofGE:  if arg1>=arg2 then goto result (float)
    //
            static void gotofGE (Ast.Node arg1, Ast.Node arg2, String lab) {
                IR inst = new IR (OPgotofGE);
                inst.arg1 = arg1;
                inst.arg2 = arg2;
                inst.str = lab;
            }
    //
    // comment:  ! ...string...
    //
            static void comment (String s) {
                IR inst = new IR (OPcomment);
                inst.str = s;
            }
    //
    // alloc:  result := alloc(arg)
    //
            static void alloc (Ast.Node result, Ast.Node arg) {
                IR inst = new IR (OPalloc);
                inst.result = result;
                inst.arg1 = arg;
            }
    //
    // readInt:  result := readInt()
    //
            static void readInt (Ast.Node result) {
                IR inst = new IR (OPreadInt);
                inst.result = result;
            }
    //
    // readFloat:  result := readFloat()
    //
            static void readFloat (Ast.Node result) {
                IR inst = new IR (OPreadFloat);
                inst.result = result;
            }
    //
    // writeInt:  writeInt arg
    //
            static void writeInt (Ast.Node arg) {
                IR inst = new IR (OPwriteInt);
                inst.arg1 = arg;
            }
    //
    // writeFloat:  writeFloat arg
    //
            static void writeFloat (Ast.Node arg) {
                IR inst = new IR (OPwriteFloat);
                inst.arg1 = arg;
            }
    //
    // writeString:  writeString nameOfConstant
    //
            static void writeString (String name) {
                IR inst = new IR (OPwriteString);
                inst.str = name;
            }
    //
    // writeBoolean:  writeBoolean arg
    //
            static void writeBoolean (Ast.Node arg) {
                IR inst = new IR (OPwriteBoolean);
                inst.arg1 = arg;
            }
    //
    // writeNewline:  writeNewline
    //
            static void writeNewline () {
                IR inst = new IR (OPwriteNewline);
            }



    // printIR ()
    //
    // This routine prints out the list of IR instructions.
    //
    static void printIR ()
        throws LogicError
    {

        // This print is for Project 9 data...
        System.out.println ("maxLexicalLevel = " + Main.generator.maxLexicalLevel);

        // These prints are for Project 10 data...
           System.out.println ("=====  String List Follows  =====");
           for (Ast.StringConst s = Main.generator.stringList; s != null; s = s.next) {
               System.out.println ("   " + s.nameOfConstant + ":  \"" + s.sValue + "\"");
           }
           System.out.println ("=====  Float List Follows  =====");
           for (Ast.RealConst r = Main.generator.floatList; r != null; r = r.next) {
               System.out.println ("   " + r.nameOfConstant + ":  " + r.rValue);
           }

        System.out.println ("=====  Intermediate Code Follows  =====");
        for (IR inst = firstInstruction; inst != null; inst = inst.next) {
            switch (inst.op) {
                case OPassign:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPloadAddr:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := &");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPstore:
                    System.out.print ("                *");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPloadIndirect:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := *");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPiadd:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" + ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(integer)");
                    break;
                case OPisub:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" - ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(integer)");
                    break;
                case OPimul:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" * ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(integer)");
                    break;
                case OPidiv:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" DIV ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(integer)");
                    break;
                case OPimod:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" MOD ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(integer)");
                    break;
                case OPineg:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := - ");
                    printANode (inst.arg1);
                    System.out.println ("\t\t(integer)");
                    break;
                case OPfadd:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" + ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(float)");
                    break;
                case OPfsub:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" - ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(float)");
                    break;
                case OPfmul:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" * ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(float)");
                    break;
                case OPfdiv:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := ");
                    printANode (inst.arg1);
                    System.out.print (" / ");
                    printANode (inst.arg2);
                    System.out.println ("\t\t(float)");
                    break;
                case OPfneg:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := - ");
                    printANode (inst.arg1);
                    System.out.println ("\t\t(float)");
                    break;
                case OPitof:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := intToFloat (");
                    printANode (inst.arg1);
                    System.out.println (")");
                    break;
                case OPcall:
                    System.out.print ("                call ");
                    System.out.println (((Ast.ProcDecl) inst.arg1).id);
                    break;
                case OPparam:
                    System.out.print ("                param ");
                    System.out.print (inst.iValue + ",");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPresultTo:
                    System.out.print ("                resultTo ");
                    printANode (inst.result);
                    System.out.println ();
                    break;
                case OPmainEntry:
                    // Framesize is now printed in "printOffsets"...
                    // System.out.println ("                mainEntry frameSize=" +
                    //                     ((Ast.Body) inst.arg1).frameSize);
                    System.out.println ("                mainEntry");
                    break;
                case OPmainExit:
                    System.out.println ("                mainExit");
                    break;
                case OPprocEntry:
                    System.out.print ("                procEntry ");
                    Ast.ProcDecl pd = (Ast.ProcDecl) inst.arg1;
                    System.out.print (pd.id);
                    // Also print lexLevel and frameSize to make sure they're correct...
                    System.out.println (",lexLevel=" + pd.lexLevel +
                                        ",frameSize=" + pd.body.frameSize);
                    break;
                case OPformal:
                    System.out.print ("                formal ");
                    System.out.print (inst.iValue + ",");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPreturnExpr:
                    System.out.print ("                returnExpr ");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPreturnVoid:
                    System.out.print ("                returnVoid ");
                    System.out.println ();
                    break;
                case OPlabel:
                    System.out.println ("        " + inst.str + ":");
                    break;
                case OPgoto:
                    System.out.print ("                goto ");
                    System.out.println (inst.str);
                    break;
                case OPgotoiEQ:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" = ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(integer)");
                    break;
                case OPgotoiNE:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" != ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(integer)");
                    break;
                case OPgotoiLT:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" < ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(integer)");
                    break;
                case OPgotoiLE:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" <= ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(integer)");
                    break;
                case OPgotoiGT:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" > ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(integer)");
                    break;
                case OPgotoiGE:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" >= ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(integer)");
                    break;
                case OPgotofEQ:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" = ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(float)");
                    break;
                case OPgotofNE:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" != ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(float)");
                    break;
                case OPgotofLT:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" < ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(float)");
                    break;
                case OPgotofLE:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" <= ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(float)");
                    break;
                case OPgotofGT:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" > ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(float)");
                    break;
                case OPgotofGE:
                    System.out.print ("                if ");
                    printANode (inst.arg1);
                    System.out.print (" >= ");
                    printANode (inst.arg2);
                    System.out.println (" then goto " + inst.str + "\t\t(float)");
                    break;
                case OPcomment:
                    System.out.println ("! " + inst.str);
                    break;
                case OPalloc:
                    System.out.print ("                ");
                    printANode (inst.result);
                    System.out.print (" := alloc (");
                    printANode (inst.arg1);
                    System.out.println (")");
                    break;
                case OPreadInt:
                    System.out.print ("                readInt ");
                    printANode (inst.result);
                    System.out.println ();
                    break;
                case OPreadFloat:
                    System.out.print ("                readFloat ");
                    printANode (inst.result);
                    System.out.println ();
                    break;
                case OPwriteInt:
                    System.out.print ("                writeInt ");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPwriteFloat:
                    System.out.print ("                writeFloat ");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPwriteString:
                    System.out.print ("                writeString ");
                    System.out.println (inst.str);
                    break;
                case OPwriteBoolean:
                    System.out.print ("                writeBoolean ");
                    printANode (inst.arg1);
                    System.out.println ();
                    break;
                case OPwriteNewline:
                    System.out.println ("                writeNewline");
                    break;
                default:
                    throw new LogicError ("Unexpected Opcode in PrintIR");
            }
        }
        System.out.println ("=======================================");
    }



    // printANode (p)
    //
    // For some IR instructions, "result", "arg1", and/or "arg2" will be a pointer
    // to an AST node.  This routine is called by printIR to print such arguments.
    //
    static void printANode (Ast.Node p)
        throws LogicError
    {
        if (p == null) {
            System.out.print ("*****NULL*****");
            throw new LogicError ("Some IR instruction has a null operand");
        } else if (p instanceof Ast.VarDecl) {
            System.out.print (((Ast.VarDecl) p).id);
        } else if (p instanceof Ast.Formal) {
            System.out.print (((Ast.Formal) p).id);
        } else if (p instanceof Ast.IntegerConst) {
            System.out.print (((Ast.IntegerConst) p).iValue);
        } else if (p instanceof Ast.RealConst) {
            System.out.print (((Ast.RealConst) p).rValue);
        } else {
            System.out.print ("*****????*****");
            throw new LogicError ("Some IR instruction has a bad operand");
        }
    }



    // printOffsets (body)
    //
    // This method is passed a pointer to the AST. It runs through it, printing
    // the offsets in all Formal and VarDecl nodes in the following format.
    //
    //    Printing Offset Information...
    //       MAIN BODY:
    //         FrameSize = 112
    //         Offsets of local variables:
    //           -4      i
    //           -8      j
    //       <...Offsets for procedures...>
    //
    static void printOffsets (Ast.Body body) {
        System.err.println ("Printing Offset Information...");
        System.err.println ("  MAIN BODY:");
        System.err.println ("    FrameSize = " + body.frameSize);
        System.err.println ("    Offsets of local variables:");
        for (Ast.VarDecl vd = body.varDecls; vd != null; vd =vd.next) {
            System.err.println ("      " + vd.offset + "\t" + vd.id);
        }
        for (Ast.ProcDecl pr = body.procDecls; pr != null; pr = pr.next) {
            printOffsetsInProcDecl (pr);
        }
    }



    // printOffsetsInProcDecl (procDecl)
    //
    // This method is passed a pointer to a ProcDecl. It runs through it, printing
    // the offsets in all Formal and VarDecl nodes.  It then runs through all
    // nested procedures, doing the same.
    //
    //     PROCEDURE foo:
    //       Offsets of formals:
    //         68        z1
    //         72        z2
    //         76        z3
    //       Offsets of local variables:
    //         -4        v1
    //         -8        v2
    //         -12       w1
    //
    static void printOffsetsInProcDecl (Ast.ProcDecl procDecl) {
        System.err.println ("  PROCEDURE " + procDecl.id + ":");
        System.err.println ("    FrameSize = " + procDecl.body.frameSize);
        System.err.println ("    Lex Level = " + procDecl.lexLevel);
        System.err.println ("    Offsets of formals:");
        for (Ast.Formal form = procDecl.formals; form != null; form = form.next) {
            System.err.println ("      " + form.offset + "\t" + form.id);
        }
        System.err.println ("    Offsets of local variables:");
        for (Ast.VarDecl vd = procDecl.body.varDecls; vd != null; vd = vd.next) {
            System.err.println ("      " + vd.offset + "\t" + vd.id);
        }
        for (Ast.ProcDecl pr = procDecl.body.procDecls; pr != null; pr = pr.next) {
            printOffsetsInProcDecl (pr);
        }
    }


}
