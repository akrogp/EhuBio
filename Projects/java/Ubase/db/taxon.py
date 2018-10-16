import csv
import gzip

CSV_PATH='/home/gorka/Descargas/Sequences/UniProt/2018_09/taxonomy-filtered-annotated.tab.gz'

def fmt(str):
	if str:
		return "'" + str.replace("'","\\'") + "'"
	return 'NULL'

with gzip.open(CSV_PATH) as csvFile:
    rd = csv.DictReader(csvFile, delimiter='\t')
    print 'USE Ubase;'
    print 'START TRANSACTION;'
    for row in rd:
        print('INSERT INTO `Taxon` (`id`, `sciName`, `commonName`) VALUES ('
              + row['Taxon'] + ", " + fmt(row['Scientific name']) + ", " + fmt(row['Common name']) + ");")
    print 'COMMIT;'
