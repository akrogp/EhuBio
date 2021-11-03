import pandas as pd

def split_xlsx(path):
    df = pd.read_excel(path)
    for index, dub in df.DUB.drop_duplicates().items():
        df.loc[df.DUB == dub, :].to_excel(path[:-5]+"."+dub+".xlsx", index=False)


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    split_xlsx('/home/gorka/Descargas/DataBases/Sequences/Dubase/Nago/20211102-Nago.fixed.xlsx')
