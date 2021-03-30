/* post.c
**
** Harry Porter - 10/30/98
**
** This program removes all newline characters.  It also translates every
** dollar character ($) to a newline.  It is used to post-process output
** produced by PCAT programs, which have limited flexibility in outputting
** newlines.
*/

#include <stdio.h>

main () {
  int c;
  while (1) {
    c = getchar();
    if (c == '$') {
      putchar ('\n');
    } else if (c == '\n') {
    } else if (c == EOF) {
      return;
    } else {
      putchar(c);
    } 
  };
}
