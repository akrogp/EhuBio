#!/usr/bin/python
# TO-DO: still developing ...
import pandas

wregex_csv = '/home/gorka/Bio/Workspace/NES/Estudios/2020-SmProt/Wregex/highConfidenceHumanNLS.csv'
nesmap_csv = '/home/gorka/Bio/Workspace/NES/Estudios/2020-SmProt/NESmapper/highConfidenceHumanMin0.csv'
output_csv = '/home/gorka/Bio/Workspace/NES/Estudios/2020-SmProt/merged.csv'

def match(wregex, nesmap):
    if wregex['Entry'] != nesmap['<query>']:
        return False
    w1 = wregex['Begin']
    w2 = wregex['End']
    n1 = nesmap['<pos>']
    n2 = n1 + len(nesmap['<nes>']) - 1
    nesmap['<end>'] = n2
    return (n1 >= w1 and n1 <= w2) or (w1 >= n1 and w1 <= n2)

def merge(wregex_csv, nesmap_csv, output_csv):
    wregex = pandas.read_csv(wregex_csv)
    nesmap = pandas.read_csv(nesmap_csv, sep='\t').dropna()
    output = pandas.DataFrame(columns=['Entry',
       'Begin (Wregex)', 'End (Wregex)', 'Combinations (Wregex)', 'Sequence (Wregex)', 'Alignment (Wregex)', 'NES Score (Wregex)', 'NLS Score (Wregex)',
       'Begin (NESmapper)', 'End (NESmapper)', 'Sequence (NESmapper)', 'NES Score (NESmapper)', 'NES Class (NESmapper)'])
    for i, wentry in wregex.iterrows():
        for j, nentry in nesmap.iterrows():
            if match(wentry, nentry):
                output.at[i, 'Entry'] = wentry['Entry']
                output.at[i, 'Begin (Wregex)'] = wentry['Begin']
                output.at[i, 'End (Wregex)'] = wentry['End']
                output.at[i, 'Combinations (Wregex)'] = wentry['Combinations']
                output.at[i, 'Sequence (Wregex)'] = wentry['Sequence']
                output.at[i, 'Alignment (Wregex)'] = wentry['Alignment']
                output.at[i, 'NES Score (Wregex)'] = wentry['Score']
                output.at[i, 'NLS Score (Wregex)'] = wentry['Aux']
                output.at[i, 'Begin (NESmapper)'] = nentry['<pos>']
                output.at[i, 'End (NESmapper)'] = nentry['<end>']
                output.at[i, 'Sequence (NESmapper)'] = nentry['<nes>']
                output.at[i, 'NES Score (NESmapper)'] = nentry['<score>']
                output.at[i, 'NES Class (NESmapper)'] = nentry['<nes-class>']
        print(i)
    output.to_csv(output_csv)

merge(wregex_csv, nesmap_csv, output_csv)