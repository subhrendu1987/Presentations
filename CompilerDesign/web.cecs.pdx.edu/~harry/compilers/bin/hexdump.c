/* hexdump  -  This program reads a file from the standard input containing
               arbitrary hex data and outputs it to the standard output
               in a format suitable for human examination.  Unlike various
               versions of 'od', it prints the bytes in exactly the order
               read.  */

#define EOF -1

int row [16];
int size;

main () {
   int addr;

   /* Each execution of this loop prints a single output line. */
   addr = 0;
   readline ();
   while (size > 0) {
     putlong (addr);
     printf (": ");
     printline ();
     addr = addr + 16;
     readline ();
   }

}



/* readline - This routine reads in the next 16 bytes from the file and
              places them in the array named 'row', setting 'size' to
              be the number of bytes read in.  Size will be less than
              16 if EOF was encountered, and may possibly be 0.  */
readline () {
int c;
  size = 0;
  c = getchar ();
  while (c != EOF) {
    row [size] = c;
    size = size + 1;
    if (size >= 16) break;
      c = getchar ();
  }
}



/* putlong - This routine is passed an integer, which it displays as 8
             hex digits.  */
putlong (i) {
  putbyt ((i>>24) & 0x000000ff);
  putbyt ((i>>16) & 0x000000ff);
  putbyt ((i>>8) & 0x000000ff);
  putbyt ((i>>0) & 0x000000ff);
}



/* printline - This routine prints the current 'row'.  */
printline () {
  int i, c;
  if (size > 0) {
    i = 0;
    while (i<16) {
      if (i < size) {
        putbyt (row[i]);
      } else {
        printf ("  ");
      }
      i++;
      if ((i%2) == 0) {
        putchar (' ');
      }
    }
    printf ("  ");
    for (i=0; i<size; i++) {
      c = row[i];
      if ((c>=' ') && (c <= '~')) {
        putchar (c);
      } else {
        putchar ('.');
      }
    }
    printf ("\n");
  }
}



/* putbyt - This routine is passed a byte (i.e., an integer < 256) which
          it displays as 2 hex characters.  If passed a number out of that
          range, it outputs nothing.  */
putbyt (c)
   int c;
{
  int i;
  if ((c >= 0) && (c <= 255)) {
    i = (c & 0x000000f0) >> 4;
    if (i < 10) {
      putchar ('0' + i);
    } else {
      putchar ('A' + i - 10);
    }
    i = (c & 0x0000000f) >> 0;
    if (i < 10) {
      putchar ('0' + i);
    } else {
      putchar ('A' + i - 10);
    }
  }
}

