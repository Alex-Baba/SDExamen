#!/usr/bin/env python3
"""mapper.py"""
import os
import sys

filename = os.environ['map_input_file']
filename = filename[filename.rfind('/')+1:]

for line in sys.stdin:
    line = line.strip()
    
    words = line.split()
    for word in words:
        print('%s\t%s:%s' % (word, filename, 1))
