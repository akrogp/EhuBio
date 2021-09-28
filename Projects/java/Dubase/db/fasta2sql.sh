#!/bin/bash

zgrep '^>' uniprot.fasta.gz | while read HEADER; do
    ACC=`echo $HEADER | cut -f2 -d\|`
    DESC=`echo $HEADER | cut -f3 -d\|`
    NAME=`echo $DESC | cut -f1 -d\ `
    GENE=`echo $DESC | sed -e 's/.*GN=//g' -e 's/ .*//g'`
    DESC=`echo $DESC | cut -f2- -d\  | sed 's/ OS=.*//g'`
    if [ -z "$GENE" ]; then
        continue
    fi
    echo "INSERT INTO Protein VALUES (DEFAULT,\"$ACC\",\"$NAME\",\"$DESC\",(SELECT id FROM Gene WHERE name = \"$GENE\"));"
done
