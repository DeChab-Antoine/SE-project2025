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



writeValue : i = 6 (val = 7)



start = i\*k = 6 \* 5 = 30

indWord = start / 32 = 30 / 32 = 0

borneInf = start%32 = 30%32 = 30 (offset)

borne\_sup = borneInf + k = 30 + 5 = 35



if borne\_sup <= 32

then (cas sans débordement) :

 	BitOps.writeBits(tabWords, indWord, borneInf, k, val);

else (cas avec) :

 	space = 32 - borneInf = 2 (k-space = 3)

 	val\_inf = val \& ((1 << space) - 1) = 00111 \& (100 - 001) = 00111 \& 00011 = 00011

 	val\_sup = val >>> space = 00111 >>> 2 = 00001 (décallage des bits à droite)

 	BitOps.writeBits(tabWords,   indWord, borneInf,   space, val\_inf); ('11' sur 30 et 31)

 	BitOps.writeBits(tabWords, indWord+1,        0, k-space, val\_sup); ('001' sur 0 à 2)





Overflow Areas:



pour : 13 3 9 560 6 670 4 12



13 3 9 6 4 12 -> se code sur 4 bits

1101 0011 1001 0110 0100 1100



560 670 -> se code sur 10 bits

10 0011 0000  10 1001 1110



On utilise un flag 1 si le nombre est codé dans la zone de débordement, 0 sinon



donc on aura comme base :

01101 00011 01001 10000 00110 10001 00100 01100



et comme overflow:

10 0011 0000 10 1001 1110



si overlap:

           1-1    0-6     1-0    0-9    0-3   0-13

mot0: 00/10 001/0 0110 /1000 0/010 01/00 011/0 1101

                                      0-12    0-4

mot1: 0000 0000 0000 0000 0000 0000 /0110 0/001

                            670         560

mot2: 0000 0000 0000 /1010 0111 10/10 0011 0000



sans overlap:

           1-1    0-6     1-0    0-9    0-3   0-13

mot0: 00/10 001/0 0110 /1000 0/010 01/00 011/0 1101

                                   0-12    0-4

mot1: 0000 0000 0000 0000 0000 00/01 100/0 0100

                            670         560

mot2: 0000 0000 0000 /1010 0111 10/10 0011 0000





calculer k (base) = à définir et ko (overflow) = 10 (nbBitsMax)



parcourir le tab et noté dans un dictionnaire trié

val k: nb de fois



pour notre cas:

4: 3 (13, 9, 12)

2: 1 (3)

3: 2 (6, 4)

10: 2 (560, 670)



classe dans un tab : \[8, 8, 7, 5, 2, 2, 2, 2, 2, 2, 0] (overflowCount)



donc on teste k = 4, 2, 3 avec ko = 10 à chaque fois puis on regarde si sans débordement c'est mieux



on calcule pour chaque cas:

si k = 4 (6), ko = 10 (2)

nbBitsFlag = f = log2(2) = 1

nbBitsBase = w = 1 + max(k, f) = 1 + 4 = 5

nbBitsTotal = 8\*5(base) + 2\*10 (overflow) = 40 + 20 = 60 bits



si k = 2 (1) , ko = 10 (7)

nbBitsFlag = f = log2(7) = 3

nbBitsBase = w = 1 + max(k, f) = 1 + 3 = 4

nbBitsTotal = 8\*4(base) + 7\*10 (overflow) = 32 + 70 = 102 bits



si k = 3 (3), ko = 10 (5)

nbBitsFlag = f = log2(5) = 3

nbBitsBase = w = 1 + max(k, f) = 1 + 3 = 4

nbBitsTotal = 8\*4(base) + 5\*10 (overflow) = 32 + 50 = 82 bits



Donc le cas retenu est k = 4 si zone de débordement



sinon k= 10 -> 10\*8 = 80 bits > 60 donc on doit bien choisir la zone de débordement


Librairies requises :

pip install pandas
pip install numpy
pip install matplotlib




























