/* lexer.c
**
** The PCAT lexer with modifications for use as a YAPP preprocessor.
**
** Harry Porter, 10/8/98.
**               11/4/98 - YAPP modifications.
**
** This file provides the routine getToken() which returns a single token
** for every call.  It returns zero after the source code file
** has been exhausted.  It reads from stdin.
**
** This file also contains calls to lexError().
*/

#include <stdio.h>
#include <stdarg.h>
#include <float.h>
#include <string.h>
#include "lexer.h"

int getToken (void);
void lexError (char *msg);
void initKeywords ();
char *strsave (char *str);

union tokenValue tokenValue;
int currentLine;
int errorsDetected;

#define MAX_STR_LEN 255          /* Strings are limited to 255 characters. */

#define NUM_KEYWORDS 90
char * keywords [NUM_KEYWORDS+1];
int keywordTokens [NUM_KEYWORDS+1];
int tableInitialized = 0;



/* getToken ()
**
** Scan the next token and return it.  Side-effects tokenVal and
** currentLine.
*/
int getToken (void) {
  int ch, ch2;
  int intVal, t, intOverflow, realOverflow;
  double exp, realVal;
  char lexError2 [] = "Illegal character xxxxxxx in string ignoredxxxxxx";
  char buffer [MAX_STR_LEN+1];          /* buffer for saving a string */
  int next, i;                             /* index into buffer */

  while (1) {
    ch = getchar ();

    /* Process enf-of-file... */
    if (ch == EOF) {
      return 0;

    /* Process newline... */
    } else if (ch == '\n') {
      currentLine ++;
      if (currentLine > 0) {
        printf ("\n-%d ", currentLine);
      }

    /* Process other white space... */
    } else if (ch == ' ' || ch == '\t') {
      /* do nothing */

    /* Process left-parenthesis and comments... */
    } else if (ch == '(') {
      ch2 = getchar ();
      if (ch2 != '*') {     /* Just a left-parenthesis... */
        ungetc (ch2, stdin);
        return '(';
      } else {              /* A comment... */
        ch2 = ' ';
        do {
          ch = ch2;
          ch2 = getchar ();
          if (ch2 == EOF) {
            lexError ("End-of-file encountered within a comment");
            ungetc (ch2, stdin);
            return 0;
          } else if (ch2 == '\n') {
            currentLine++;
          }
        } while (ch != '*' || ch2 != ')');
      }

    /* Process strings... */
    } else if (ch == '"') {
      next = 0;
      while (1) {
        ch2 = getchar ();
        if (ch2 == '"') {
          break;
        } else if (ch2 == '\n') {
          lexError ("End-of-line encountered within a string");
          ungetc (ch2, stdin);
          break;
        } else if (ch2 == 0) {
          lexError ("EOF encountered within a string--VALID MESSAGE?");
          ungetc (ch2, stdin);
          break;
        } else if ((ch2 < 32) || (ch2 > 126)) {
          sprintf (lexError2,
              "Illegal character \\%03o in string ignored", ch2);
          lexError (lexError2);
        } else {
          if (next >= MAX_STR_LEN) {
            lexError ("Maximum string length (255) exceeded");
          } else {
            buffer [next++] = ch2;
          }
        }
      }
      buffer [next++] = '\0';
      tokenValue.svalue = strsave (buffer);
      return STRING;

    /* Process identifiers... */
    } else if (isalpha (ch)) {
      next = 0;
      while (isalpha (ch) || isdigit (ch)) {
        if (next >= MAX_STR_LEN) {
          lexError ("Maximum identifier length (255) exceeded");
        } else {
          buffer [next++] = ch;
        }
        ch = getchar ();
      }
      ungetc (ch, stdin);
      buffer [next++] = '\0';
      tokenValue.svalue = strsave (buffer);
      if (!tableInitialized) {
        initKeywords ();
        tableInitialized = 1;
      }
      for (i = 0; i <= NUM_KEYWORDS; i++) {
        if (tokenValue.svalue == keywords [i]) {
          return keywordTokens [i];
        }
      }
      return ID;

    /* Process numbers... */
    } else if (isdigit (ch)) {
      next = intVal = intOverflow = realOverflow= 0;
      exp = 1.0;
      realVal = 0.0;
      while (isdigit (ch)) {
        t = intVal * 10 + (ch - '0');
        if (t < intVal) {
          intOverflow = 1;
        }
        intVal = t;
        realVal = (realVal * 10.0) + (double) (ch - '0');
        if (realVal > DBL_MAX) {
          realOverflow = 1;
        }
        ch = getchar ();
      }
      /* If no decimal point, this is an integer so return. */
      if (ch != '.') {
        ungetc (ch, stdin);
        if (intOverflow) {
          lexError ("Integer out of range (0..2147483647)");
          intVal = 0;
        }
        tokenValue.ivalue = intVal;
        return INTEGER;
      }
      /* We have a decimal number; scan the fractional part. */
      ch = getchar ();
      while (isdigit (ch)) {
        exp *= 10.0;
        realVal = realVal + ((float) (ch - '0') / exp);
        ch = getchar ();
      }
      ungetc (ch, stdin);
      if (realOverflow) {
        lexError ("Real number is too large");
        tokenValue.rvalue = 0.0;
      } else {
        tokenValue.rvalue = realVal;
      }
      return REAL;

    /* Check for : and := ... */
    } else if (ch == ':') {
      ch2 = getchar ();
      if (ch2 == '=') {
        return ASSIGN;
      } else {
        ungetc (ch2, stdin);
        return ':';
      }

    /* Check for > >= >] ... */
    } else if (ch == '>') {
      ch2 = getchar ();
      if (ch2 == '=') {
        return GEQ;
      } else if (ch2 == ']') {
        return RBAG;
      } else {
        ungetc (ch2, stdin);
        return '>';
      }

    /* Check for < <= <> ... */
    } else if (ch == '<') {
      ch2 = getchar ();
      if (ch2 == '=') {
        return LEQ;
      } else if (ch2 == '>') {
        return NEQ;
      } else {
        ungetc (ch2, stdin);
        return '<';
      }

    /* Check for [ and [< ... */
    } else if (ch == '[') {
      ch2 = getchar ();
      if (ch2 == '<') {
        return LBAG;
      } else {
        ungetc (ch2, stdin);
        return '[';
      }

    /* Check for remaining single character operator symbols */
    } else if (strchr("+-*/=;,.()]{}", ch)) {
      return ch;

    /* Otherwise, we have an invalid character; ignore it. */
    } else {
      sprintf (lexError2,
          "Illegal character \\%03o ignored", ch);
      lexError (lexError2);
    }
  }
}



/* initKeywords ()
**
** This routine initializes the "keywords" and the "keywordTokens" arrays.
*/
void initKeywords () {
  keywords [0] = strsave ("and");		keywordTokens [0] = AND;
  keywords [1] = strsave ("array");		keywordTokens [1] = ARRAY;
  keywords [2] = strsave ("begin");		keywordTokens [2] = BEGIN;
  keywords [3] = strsave ("by");		keywordTokens [3] = BY;
  keywords [4] = strsave ("div");		keywordTokens [4] = DIV;
  keywords [5] = strsave ("do");		keywordTokens [5] = DO;
  keywords [6] = strsave ("else");		keywordTokens [6] = ELSE;
  keywords [7] = strsave ("elseif");		keywordTokens [7] = ELSEIF;
  keywords [8] = strsave ("end");		keywordTokens [8] = END;
  keywords [9] = strsave ("exit");		keywordTokens [9] = EXIT;
  keywords [10] = strsave ("for");		keywordTokens [10] = FOR;
  keywords [11] = strsave ("if");		keywordTokens [11] = IF;
  keywords [12] = strsave ("is");		keywordTokens [12] = IS;
  keywords [13] = strsave ("loop");		keywordTokens [13] = LOOP;
  keywords [14] = strsave ("mod");		keywordTokens [14] = MOD;
  keywords [15] = strsave ("not");		keywordTokens [15] = NOT;
  keywords [16] = strsave ("of");		keywordTokens [16] = OF;
  keywords [17] = strsave ("or");		keywordTokens [17] = OR;
  keywords [18] = strsave ("procedure");	keywordTokens [18] = PROCEDURE;
  keywords [19] = strsave ("program");		keywordTokens [19] = PROGRAM;
  keywords [20] = strsave ("read");		keywordTokens [20] = READ;
  keywords [21] = strsave ("record");		keywordTokens [21] = RECORD;
  keywords [22] = strsave ("return");		keywordTokens [22] = RETURN;
  keywords [23] = strsave ("then");		keywordTokens [23] = THEN;
  keywords [24] = strsave ("to");		keywordTokens [24] = TO;
  keywords [25] = strsave ("type");		keywordTokens [25] = TYPE;
  keywords [26] = strsave ("var");		keywordTokens [26] = VAR;
  keywords [27] = strsave ("while");		keywordTokens [27] = WHILE;
  keywords [28] = strsave ("write");		keywordTokens [28] = WRITE;

  keywords [29] = strsave ("E");		keywordTokens [29] = SY_E;
  keywords [30] = strsave ("T");		keywordTokens [30] = SY_T;
  keywords [31] = strsave ("F");		keywordTokens [31] = SY_F;
  keywords [32] = strsave ("prog");		keywordTokens [32] = NT_PROGRAM;
  keywords [33] = strsave ("body");		keywordTokens [33] = NT_BODY;
  keywords [34] = strsave ("decls");		keywordTokens [34] = NT_DECLS;
  keywords [35] = strsave ("stmts");		keywordTokens [35] = NT_STMTS;
  keywords [36] = strsave ("varDecls");		keywordTokens [36] = NT_VARDECLS;
  keywords [37] = strsave ("typeDecls");	keywordTokens [37] = NT_TYPEDECLS;
  keywords [38] = strsave ("procDecls");	keywordTokens [38] = NT_PROCDECLS;
  keywords [39] = strsave ("decl");		keywordTokens [39] = NT_DECL;
  keywords [40] = strsave ("stmt");		keywordTokens [40] = NT_STMT;
  keywords [41] = strsave ("varDecl");		keywordTokens [41] = NT_VARDECL;
  keywords [42] = strsave ("typeDecl");		keywordTokens [42] = NT_TYPEDECL;
  keywords [43] = strsave ("procDecl");		keywordTokens [43] = NT_PROCDECL;
  keywords [44] = strsave ("idList");		keywordTokens [44] = NT_IDLIST;
  keywords [45] = strsave ("optionalType");	keywordTokens [45] = NT_OPTIONALTYPE;
  keywords [46] = strsave ("expr");		keywordTokens [46] = NT_EXPR;
  keywords [47] = strsave ("type2");		keywordTokens [47] = NT_TYPE;
  keywords [48] = strsave ("components");	keywordTokens [48] = NT_COMPONENTS;
  keywords [49] = strsave ("component");	keywordTokens [49] = NT_COMPONENT;
  keywords [50] = strsave ("formalParams");	keywordTokens [50] = NT_FORMALPARAMS;
  keywords [51] = strsave ("fpSections");	keywordTokens [51] = NT_FPSECTIONS;
  keywords [52] = strsave ("fpSection");	keywordTokens [52] = NT_FPSECTION;
  keywords [53] = strsave ("lValues");		keywordTokens [53] = NT_LVALUES;
  keywords [54] = strsave ("lValue");		keywordTokens [54] = NT_LVALUE;
  keywords [55] = strsave ("actualParams");	keywordTokens [55] = NT_ACTUALPARAMS;
  keywords [56] = strsave ("actuals");		keywordTokens [56] = NT_ACTUALS;
  keywords [57] = strsave ("writeParams");	keywordTokens [57] = NT_WRITEPARAMS;
  keywords [58] = strsave ("writeExprs");	keywordTokens [58] = NT_WRITEEXPRS;
  keywords [59] = strsave ("writeExpr");	keywordTokens [59] = NT_WRITEEXPR;
  keywords [60] = strsave ("elseIfs");		keywordTokens [60] = NT_ELSEIFS;
  keywords [61] = strsave ("optionalElse");	keywordTokens [61] = NT_OPTIONALELSE;
  keywords [62] = strsave ("optionalBy");	keywordTokens [62] = NT_OPTIONALBY;
  keywords [63] = strsave ("optionalExpr");	keywordTokens [63] = NT_OPTIONALEXPR;
  keywords [64] = strsave ("unaryOp");		keywordTokens [64] = NT_UNARYOP;
  keywords [65] = strsave ("binaryOp2");	keywordTokens [65] = NT_BINARYOP2;
  keywords [66] = strsave ("compValues");	keywordTokens [66] = NT_COMPVALUES;
  keywords [67] = strsave ("moreCompValues");	keywordTokens [67] = NT_MORECOMPVALUES;
  keywords [68] = strsave ("arrayValues");	keywordTokens [68] = NT_ARRAYVALUES;
  keywords [69] = strsave ("moreArrayValues");	keywordTokens [69] = NT_MOREARRAYVALUES;
  keywords [70] = strsave ("arrayValue");	keywordTokens [70] = NT_ARRAYVALUE;
  keywords [71] = strsave ("moreExpr2");	keywordTokens [71] = NT_MOREEXPR2;
  keywords [72] = strsave ("moreExpr3");	keywordTokens [72] = NT_MOREEXPR3;
  keywords [73] = strsave ("moreExpr4");	keywordTokens [73] = NT_MOREEXPR4;
  keywords [74] = strsave ("binaryOp3");	keywordTokens [74] = NT_BINARYOP3;
  keywords [75] = strsave ("binaryOp4");	keywordTokens [75] = NT_BINARYOP4;
  keywords [76] = strsave ("expr2");	keywordTokens [76] = NT_EXPR2;
  keywords [77] = strsave ("expr3");	keywordTokens [77] = NT_EXPR3;
  keywords [78] = strsave ("expr4");	keywordTokens [78] = NT_EXPR4;
  keywords [79] = strsave ("expr5");	keywordTokens [79] = NT_EXPR5;
  keywords [80] = strsave ("E2");	keywordTokens [80] = SY_E2;
  keywords [81] = strsave ("T2");	keywordTokens [81] = SY_T2;
  keywords [82] = strsave ("bexpr");	keywordTokens [82] = SY_BEXPR;
  keywords [83] = strsave ("bterm");	keywordTokens [83] = SY_BTERM;
  keywords [84] = strsave ("bfactor");	keywordTokens [84] = SY_BFACTOR;
  keywords [85] = strsave ("true");	keywordTokens [85] = SY_TRUE;
  keywords [86] = strsave ("false");	keywordTokens [86] = SY_FALSE;
  keywords [87] = strsave ("A");	keywordTokens [87] = SY_A;
  keywords [88] = strsave ("a");	keywordTokens [88] = SY_a;
  keywords [89] = strsave ("b");	keywordTokens [89] = SY_b;
  keywords [90] = strsave ("S");	keywordTokens [90] = SY_S;
  
  /* Make sure NUM_KEYWORDS is the same as the number on the line above. */
}



/* strsave.c
**
** A string saving routine to be used by the lexer.
**
** For CS301
**
** See Aho,Sethi,Ullman pp.435-437 for details.
**
** Jingke Li, 1/20/97.
** Modified: Harry Porter, 10/9/97.
**
*/

#define STRING_TABLE_SIZE 211    /* Size of hash table for storing strings */

typedef struct string {
    struct string *next;         /* pointer to next bucket entry */
    char s[1];                   /* the string itself */
} String;

static String *string_table [STRING_TABLE_SIZE];

/*
** strsave()
**   This routine is passed a pointer to a string of characters, terminated
**   by '\0'.  It looks it up in the table.  If an equal string is already
**   there, it returns a pointer to the copy previously stored in the table.
**   If not found, it copies the new string into the table and returns a
**   pointer to this new copy.
**
**   After calling this routine, you can use pointer comparisons to check
**   for equality, rather than the more expensive test for character equality.
**   Furthermore, each different string will only be stored once, saving space.
**
*/
char *strsave (char *str)
{
   unsigned h = 0, g;
   char *p;
   String *b;

   for ( p = str; *p != '\0'; p++ ) {
     h = (h << 4) + (*p);
     if (g = h & 0xf0000000) {
       h = h ^ (g >> 24);
       h = h ^ g;
     }
   }
   h %= STRING_TABLE_SIZE;

   for (b = string_table[h]; b; b = b->next) 
     if (strcmp(b->s,str) == 0)
       return(b->s);

   b = (String *) malloc(sizeof(String) + strlen(str) + 1);
   if (b==0) {
     fprintf (stderr, "*****  Compiler Error: Malloc failed in strsave  *****\n");
     exit (1);
   }
   strcpy(b->s, str);
   b->next = string_table[h];
   string_table[h] = b;
   return(b->s);
}
