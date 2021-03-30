/* main.c
**
** CS-322 - project 7 - Driver main routine
*/

#include <stdio.h>

int * iscan (int *, int);
double * dscan (double *, int);

int    a[] = {3, 4, 5, 6, 7, -8};
double b[] = {3.0, 4.0, 5.0, 6.0, 7.0, -8.0};

main()
{
    int i;
    int *c;
    double *d;

    c = iscan (a, 6);
    if (c == NULL) {
      printf ("*****  iscan returned NULL!  *****\n");
    } else {
      printf ("c = [");
      for (i=0; i<7; i++)
        printf (" %4d", c[i]);
      printf ("]\n");
    }

    c = iscan (a, 0);
    if (c == NULL) {
      printf ("iscan returned NULL as required.\n");
    } else {
      printf ("*****  iscan failed to return NULL as required.  *****\n");
    }

    d = dscan (b, 6);
    if (d == NULL) {
      printf ("*****  dscan returned NULL!  *****\n");
    } else {
      printf ("d = [");
      for (i=0; i<7; i++)
        printf (" %4.1f", d[i]);
      printf ("]\n");
    }

    d = dscan (b, 0);
    if (d == NULL) {
      printf ("dscan returned NULL as required.\n");
    } else {
      printf ("*****  dscan failed to return NULL as required.  *****\n");
    }

}   
