#!/usr/bin/python3

acc = None
seq = ''
with open('UniqSeq2Prot.tsv') as rels:
    for rel in rels:
        fields= rel.split('\t')
        if fields[0] != acc:
            if acc:
                print('>' + acc)
                print(seq)
            acc = fields[0]
            seq = ''
        seq += fields[1].strip()
    print('>' + acc)
    print(seq)
