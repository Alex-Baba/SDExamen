#!/usr/bin/env python3
"""reducer.py"""

import sys

current_word = None
current_count = 0
word = None

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    word, touple = line.split('\t', 1)  # line.split('\t', 1)
    filename, count = touple.split(':', 1)  # line.split('\t', 1)

    try:
        count = int(count)
    except ValueError:
        continue

    if current_word != word:
        if current_word:
            # write result to STDOUT
            print('%s\t%s:%s'% (current_word,filename,current_count - 1))
        current_count = count
        current_word = word
    current_count += count

# do not forget to output the last word if needed!
if current_count > 0:
	print('%s\t%s:%s'% (current_word,filename,current_count - 1))
