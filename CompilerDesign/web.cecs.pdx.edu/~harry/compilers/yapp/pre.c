/* main.c
**
** Driver routine for the PCAT lexer.
** ... modified to use as a preprocessor for YAPP.
**
** Harrry Porter, 10/8/98.
**                11/4/98 - YAPP modifications.
*/

#include <stdio.h>
#include <stdarg.h>
#include "lexer.h"

int getToken (void);
void lexError (char *msg);

union tokenValue tokenValue;
int currentLine;
int errorsDetected;



/* main()
**
** Calls getToken in a loop until EOF, printing out each token as it goes.
*/
main (void) {
    int tok, oldLine;

    errorsDetected = 0;
    currentLine = -99999;
    /* getToken() returns 0 at end-of-file. */
    printf("%d\n", EOFSYMBOL);
    tok = getToken ();
    while (1) {
      printf("%d ", tok);
      tok = getToken ();
      if (tok != '=') {
        lexError ("Expecting = in rule");
      }
      while (1) {
        oldLine = currentLine;
        tok = getToken ();
        if (oldLine < currentLine) {
          printf ("0\n");
          break;
        }
        printf("%d ", tok);
      }
      oldLine ++;
      if (oldLine < currentLine) {
        printf ("0\n");
        break;
      }
    }
    currentLine = 1;
    printf ("-1 ");
    printf("%d ", tok);
    while (1) {
      tok = getToken ();
      if (tok <= 0) {
        break;
      }
      printf("%d ", tok);
    }
    printf("%d ", EOFSYMBOL);
    if (errorsDetected) {
	fprintf (stderr, "%d errors were detected!\n", errorsDetected);
    }
}

 

/* lexError (msg)
**
** This routine is called to print an error message and the current line
** number.  It returns.
*/
void lexError (char *msg) {
    fprintf (stderr, "Error on line %d: %s\n", currentLine, msg);
    errorsDetected++;
}
