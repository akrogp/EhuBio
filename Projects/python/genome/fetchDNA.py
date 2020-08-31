#!/usr/bin/python

import argparse
import requests
#import json

parser = argparse.ArgumentParser(description='Retrieves DNA sequence based on genome coordinates.')
#parser.add_argument('--genome', '-g', default='hg38', help='genome assembly')
parser.add_argument('--chromosome', '-c', required=True, help='chromosome name')
parser.add_argument('--begin', '-b', required=True, help='begin coordinate')
parser.add_argument('--end', '-e', required=True, help='end coordinate')
parser.add_argument('--strand', '-s', default=1, help='strand [1|-1]')
args = parser.parse_args()

# UCSC
#url = f'https://api.genome.ucsc.edu/getData/sequence?genome={args.genome};chrom={args.chromosome};start={args.begin};end={args.end}'
#response = json.loads(requests.get(url).content)
#print(response['dna'])

# Ensembl
url = f"http://rest.ensembl.org/sequence/region/human/{args.chromosome}:{args.begin}..{args.end}:{args.strand}"
response = requests.get(url, headers={ "Content-Type" : "text/plain"}).text
print(response)