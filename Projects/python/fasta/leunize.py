#!/usr/bin/python3

import sys
import re

if len(sys.argv) != 2:
	print("Usage: {} <database.fasta>".format(sys.argv[0]))
	sys.exit()

with open(sys.argv[1]) as fasta:
	for line in fasta:
		if line[0] != '>':
			line = re.sub('[iI]','L',line)
		print(line, end='')
