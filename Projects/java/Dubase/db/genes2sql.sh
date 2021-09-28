#!/bin/bash

echo "USE Dubase;"
echo "START TRANSACTION;"
sort uniprot_genes.txt | uniq | while read GENES; do
    GENE=`echo $GENES | cut -f1 -d' '`
    if [ ! -z "$GENE" ]; then
        echo "INSERT INTO Gene VALUES(DEFAULT,\"$GENE\",\"$GENES\",NULL);" 
    fi
done
echo "COMMIT;"
