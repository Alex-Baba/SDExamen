#!/usr/bin/env python3
"""reducer"""

import sys

#<word,{doc_id:1}>

current_word = ""
current_doc = ""
current_count = 0

for line in sys.stdin:
    line = line.strip()
    word, doc_id = line.split()
    doc_id, count = doc_id.split(":", maxsplit=2)

    try:
        count = int(count)
    except ValueError:
        # count was not a number, so silently
        # ignore/discard this line
        continue

    if word != current_word:
        if current_word != "":
            print(f'<{current_word},' + '{' + f'{doc_id}:{current_count}' + '}>')
        current_word = word
        current_count = 1
        current_doc = doc_id

    elif current_doc != doc_id:
        if current_doc != "":
            print(f'<{current_word},' + '{' + f'{doc_id}:{current_count}' + '}>')
        current_word = word
        current_count = 1
        current_doc = doc_id

    else:
        current_count += 1

if current_doc != "" and current_word != "":
    print(f'<{current_word},' + '{' + f'{doc_id}:{current_count}' + '}>')
