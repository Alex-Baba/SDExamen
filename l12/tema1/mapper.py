#!/usr/bin/env python3
"""mapper.py"""

import sys
#sys.path.append('./')
import requests
import bs4 as bs
# input comes from STDIN (standard input)
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()
    # split the line into words
    words = line.split()
    # increase counters
    soup = bs.BeautifulSoup(requests.get(line).text, 'html.parser')
    for link in soup.find_all('a'):
        print('%s~~~%s' % (line, link.get('href')))
