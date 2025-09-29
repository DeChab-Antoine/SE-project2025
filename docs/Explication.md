1 12 3 4 15 6 7 18 9 10

00001 01100 00011 00100 01111 00110 00111 10010 01001 01010





sans Overlap : n = 10, k = 5, c = 32/k = 6 (r=2)



createTabWords:

tabWorldsLength = (n+c-1) / c = (10+6-1) / 6 = 15/6 = 2 (r=3)

tabWorlds aura 2 mots.

              6      15       4      3      12      1

Mot 1 : 00/00 110/0 1111 /0010 0/000 11/01 100/0 0001

                          10      9      18     7

Mot 2 : 0000 0000 0000 /0101 0/010 01/10 010/0 0111



writeValue : i = 0 (val = 1)

indWord = i/c = 0/6 = 0

pos = i%c = 0%6 = 0

offset = pos\*k = 0\*5 = 0



readValue : i = 7 (val = 18)

indWord = i/c = 7/6 = 1

pos = i%c = 7%6 = 2

offset = pos\*k = 1\*5 = 5





avec Overlap : n = 10, k = 5, nb = 50 (k\*n),



createTabWords:

tabWorldsLength = nb/32 = 50/32 = 1 (r=18) if 50%32 > 0 then return len + 1 else len

tabWorlds aura 2 mots.



              6      15       4      3      12      1

Mot 1 : 11/00 110/0 1111 /0010 0/000 11/01 100/0 0001

                            10      9      18    7

Mot 2 : 0000 0000 0000 00/01 010/0 1001 /1001 0/001



writeValue : i = 2 (val = 3)

borne\_inf = (i\*k)%32 = (2\*5)% 32 = 10

borne\_sup = borne\_inf + k = 10 + 5 = 15

if borne\_sup <= 32

then (cas sans débordement) :

 	BitOps.writeBits(tabWords, i\*k/32, borne\_inf, k, val);

else (cas avec) :

&nbsp;	val\_inf = val >>> k-(32-borne\_inf);

&nbsp;	val\_sup = val <<< borne\_sup- 32;

 	BitOps.writeBits(tabWords, i\*k/32, borne\_inf, k-(32-borne\_inf), val\_inf);

 	BitOps.writeBits(tabWords, (i\*k/32)+1, 0, borne\_sup- 32, val\_sup);

