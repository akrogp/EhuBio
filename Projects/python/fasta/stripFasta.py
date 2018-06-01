#!/usr/bin/python
#
#StripFasta - Fasta header parsing utility
#
#MIT License
#
#Copyright (c) 2016 James Christopher Wright - Wellcome Trust Sanger Institute
#
#Permission is hereby granted, free of charge, to any person obtaining a copy
#of this software and associated documentation files (the "Software"), to deal
#in the Software without restriction, including without limitation the rights
#to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#copies of the Software, and to permit persons to whom the Software is
#furnished to do so, subject to the following conditions:
#
#The above copyright notice and this permission notice shall be included in all
#copies or substantial portions of the Software.
#
#THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
#SOFTWARE.
#

#used to get cmd line arguments 
import argparse

#used to parse fasta header
import re

#read command line arguments and create help documentation using argparse
parser = argparse.ArgumentParser(
    description='''Strips fasta headers into a single word.
This process may loose helpful information in the header, but it is sometimes mandatory for communicating different software tools referring the entries in the falta file.
Gorka.Prieto@ehu.eus 2018''')
parser.add_argument('fasta', metavar='*.fasta|*.fa', help='FASTA file to be stripped')
parser.add_argument('--output_fasta', '-o', dest='dout', default='stripped.fasta', help='Set file to write stripped entries to. Default=stripped.fasta')
args = parser.parse_args()

#open input FASTA file using first cmd line argument
fasta = open(args.fasta, 'r')

#open output FASTA file
outfa = open(args.dout, 'w')

#header formats supported
sp = re.compile(r'>sp\|([\w.-]*)\|', re.IGNORECASE)
tr = re.compile(r'>tr\|([\w.-]*)\|', re.IGNORECASE)
nx = re.compile(r'>nxp:([\w.-]*)\s', re.IGNORECASE)
de = re.compile(r'>(\S*)', re.IGNORECASE)
formats = [sp, tr, nx, de]

for line in fasta:
	if line[0] == '>':
		for fmt in formats:
			name = fmt.match(line)
			if name:
				break
		if name:
			outfa.write('>' + str(name.group(1)) + '\n')
		else:
			outfa.write(line)
	else:
		outfa.write(line)

#close files
fasta.close()
outfa.close()
