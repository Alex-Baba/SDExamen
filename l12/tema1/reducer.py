#!/usr/bin/env python3
"""reducer.py"""

import sys

current_url = None
current_list = []
url = None

for line in sys.stdin:
    line = line.strip()
    url, ref = line.split('~~~')
    if current_url != url:
        if current_url:
            print(f'{current_url}\t{current_list}')
        current_list = []
        current_url = url
    current_list.append(ref)

if current_url != None and len(current_list) > 0:
    print(f'{current_url}\t{current_list}')
