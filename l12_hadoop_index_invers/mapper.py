#!/usr/bin/env python3
"""mapper"""

from random import randrange
import sys
import os

#<word, {document_id:1}>... cum fac document_id? cel mai usor e cu uuid4 si speri sa fie diferite id-urile, dar incerc mai bine

filename = os.environ['map_input_file'] # asta cica imi da numele fisierului de input si arata naspa, gen http://localhost:9000/user/hduser/doc1.txt si vreau fara path, doar doc1.txt
filename = filename[filename.rfind('/')+1:]
doc_id = f'{filename}{randrange(0,1000)}'
for line in sys.stdin:
    line = line.strip()
    words = line.split()
    for word in words:
        print(f'{word}\t{doc_id}:1')
