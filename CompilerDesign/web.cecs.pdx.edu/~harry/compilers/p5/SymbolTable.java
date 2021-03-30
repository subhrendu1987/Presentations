// -------------------------------- SymbolTable --------------------------------
//
// This class implements the symbol table, which maintains a mapping from Strings
// to Ast.Nodes.  There will be no instances of this class; all methods are
// static.  The primary methods are:
//
//     enter (string, node)
//     find (string)  -->  node
//     alreadyDefined (string)  -->  boolean
//
// The symbol table also supports a notion of "scope".  The following routines
// control the scope:
//
//     openScope ()
//     closeScope ()
//
// Calling "closeScope" will delete all mappigns added since the last call to
// "openScope".  There is a globally accessible variable called
//
//     SymbolTable.level
//
// which may be accessed to find the current scope level.  It begins at zero.
// "Level" is incremented whenever "openScope" is called, and decremented whenever
// "closeScope" is called.  You do not need to call "openScope" before adding
// entries.
//
// Finally, the following method may be used in debugging, if desired:
//
//     printTable ()
//
// The symbol table is stored using a hash-table.  There is an array of
// pointers to linked lists.  Each element on the linked list is an
// instance of class "Bucket", so the linked list is called a "bucket list".
// For each key-value pair, there is a Bucket which contains both the key
// (a String) and the value (a ptr to an Ast.Node).  Each string "s" is hashed
// to an integer, which is used as an index into the array.  The entry for "s"
// will be stored on the corresponding bucket list.  The bucket list is linked
// on a field called "next".  If there are several entries for "s", then they
// will all be stored on the bucket list, with the most recent entries nearer
// the beginning of the list.  Each bucket also contains the scope level (an
// integer) at the time it was created.
//
// When "closeScope" is called, we may have to delete a number of Buckets.
// The buckets to be deleted will all be at the front of their bucket lists.
// To avoid going through the entire array, we maintain a linked list for each
// scope.  When a new Bucket is created, it is placed on this list, which is
// called the "scope list".  Each Bucket has a field called "slink" which is
// used as a next pointer for this list.
//
// Harry Porter -- 02/12/03
//

import java.util.*;

class SymbolTable {

    //
    // There is one "Bucket" object for each key-value mapping.  This object
    // is on 2 linked lists: the "bucket list" (linked on "next") and the
    // "scope list" (linked on "slink").  In addition, we keep the scope at
    // which the mapping was created (field "scope") and the hash-value of
    // the key (field "hashVal").  The hashVal makes deletions faster.
    //
    static class Bucket {
        String   id;          // Key: The symbol
        Ast.Node def;         // Value: The definition of this symbol
        int      hashVal;     // What this id hashed to
        int      scope;       // Scope level at which this symbol entered
        Bucket   next;        // Ptr to next Bucket in this bucket list
        Bucket   slink;       // Ptr to next Bucket in this scope list
    }

    //
    // There is one "Scope" object for each active scope level; they are
    // linked together in decreasing scope-level order by field "next".
    // All symbols at a given level are linked into a list (the "scope
    // list"), which is pointed to by the "slink" field.
    //
    static class Scope {
        Bucket slink;         // Ptr to latest Bucket for this scope
        Scope next;           // Ptr to next Scope record
    }

    //
    // Class Variables
    //
    static final int HASH_TABLE_SIZE = 211;
    static Bucket [] symbolTable = new Bucket [HASH_TABLE_SIZE];
    static int level = 0;
    static Scope scopeList = new Scope ();



    //
    //  Constructor -- There are no instances.
    //
    private SymbolTable () { }



    //
    // enter (str, def)
    //
    // This method adds an entry to the symbol table mapping this
    // string to the given definition.  If there is already an entry
    // at this scope level, the new entry will override it.
    //
    static void enter (String str, Ast.Node def) {
        Bucket buck;
        int i = hash (str);

        System.out.println ("enter          called with '" + str + "'");
     
        // Allocate a new Bucket...
        buck = new Bucket ();
        buck.id = str;
        buck.scope = level;
        buck.def = def;
        buck.hashVal = i;

        // Add the new Bucket into the hash table bucket list...
        buck.next = symbolTable [i];
        symbolTable [i] = buck;

        // Add this Bucket into the slink list for the current scope level...
        buck.slink = scopeList.slink;
        scopeList.slink = buck;

    }



    //
    // find (string)
    //
    // This routine searches the symbol table for the entry for "string".
    // If found, it returns a ptr to its definition; otherwise it returns NULL.
    //
    static Ast.Node find (String str) {
        int i = hash (str);
        Bucket buck = symbolTable [i];

        System.out.println ("find           called with '" + str + "'");

        while (buck != null) {
            if (buck.id == str) {
                return buck.def;
            }
            buck = buck.next;
        }
        return null;
    }



    //
    // alreadyDefined (str)  -->  boolean
    //
    // This routine looks 'str' up in the current scope and returns TRUE
    // if found and FALSE otherwise.  It searches only the current scope.
    //
    static boolean alreadyDefined (String str) {
        int i = hash (str);
        Bucket buck = symbolTable [i];

        System.out.println ("alreadyDefined called with '" + str + "'");

        while (buck != null) {
            if (buck.id == str) {
                return (buck.scope == level);
            }
            buck = buck.next;
        }
        return false;
    }



    //
    // openScope()
    //
    // This routine opens a new scope and increments the static variable "level."
    //
    static void openScope () {
            Scope sc = new Scope ();
            sc.slink = null;
            sc.next = scopeList;
            scopeList = sc;
            level++;
     
            System.out.println ("openScope      called: new level=" + level);
    }



    //
    // closeScope ()
    //
    // This method closes the highest scope and removes all entries in that
    // scope.  It decrements "level".
    //
    static void closeScope ()
        throws FatalError
    {
        Bucket buck, temp;
        Scope sc;
        if (level <= 0) {
            throw new LogicError ("Compiler logic error in closeScope");
        }
        buck = scopeList.slink;
        while (buck != null) {
            symbolTable [buck.hashVal] = buck.next;
            temp = buck.slink;
            buck.def = null;
            buck.next = null;
            buck.slink = null;
            buck = temp;
        }
        sc = scopeList;
        scopeList = scopeList.next;
        sc.slink = null;
        sc.next = null;
        level--;

        System.out.println ("closeScope     called: new level=" + level);
    }



    //
    // hash (p)  -->  int
    //
    // This routine computes a hash value for the string pointed to by p.
    // It then transforms it into a range making it suitable as an index
    // into the hash table.
    //
    static int hash (String p) {
        int i = p.hashCode ();         // May return a negative number.
        if (i == 0x80000000) {
            i = 123;
        } else if (i < 0) {
            i = -i;
        }
        return i % HASH_TABLE_SIZE;
    }



    //
    // printTable ()
    //
    // Print the entire symbol table, starting with the current level
    // and working down.
    //
    static void printTable () {
        Bucket buck;
        System.out.println ("==========  The Symbol Table  ==========");
        for (int lev = level; 0 <= lev; lev--) {
            System.out.println ("  ==========  Scope Level=" + lev +"  ==========");
            for (int i = 0; i < HASH_TABLE_SIZE; i++) {
                buck = symbolTable [i];
                if (buck != null && buck.scope == lev) {
                  System.out.println ("      ==========  Bucket List # " + i +
                                          "  ==========");
                  while (buck != null) {
                      if (buck.scope == lev) {
                          System.out.println ("        " + buck.id +
                                              "  [level=" + buck.scope +
                                              ", hashVal=" + buck.hashVal + "]");
                          printDef (buck.def);
                      }
                      buck = buck.next;
                  }
               }
            }
//             System.out.println ("  ===============================");
        }
        System.out.println ("===============================");
    }



    //
    // printDef (p)
    //
    // This method is passed a pointer to a VarDecl, TypeDecl, ProcDecl, or NULL.
    // It prints it.
    //
    static void printDef (Ast.Node p) {
        if (p == null) {
            System.out.println ("            NULL");
        } else if (p instanceof Ast.VarDecl) {
            System.out.println ("            VarDecl...");
        } else if (p instanceof Ast.TypeDecl) {
            System.out.println ("            TypeDecl...");
        } else if (p instanceof Ast.ProcDecl) {
            System.out.println ("            ProcDecl... ");
        } else if (p instanceof Ast.Formal) {
            System.out.println ("            Formal... ");
        } else {
            System.out.println ("            *****  Not a VAR_DECL, PROC_DECL, TYPE_DECL, or FORMAL  *****");
        }
    }


}
