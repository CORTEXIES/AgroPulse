import pandas as pd
import numpy as np
from pathlib import Path
  
def preproc_texts(data, preproc_dir):
    # Предобработка текста
    abbreviations = pd.read_excel(preproc_dir / 'abbriviations.xlsx').to_numpy()
    print(abbreviations)
    abbreviations = abbreviations[abbreviations[:, 1].argsort()[::-1]]
    print(abbreviations)
    save_sorted_abbr_table(abbreviations, preproc_dir)

    for i in range(len(data)):
        cur = data[i].replace('\xa0', ' ').lower()
        for pair in abbreviations:
            cur = cur.replace(' ' + pair[1] + ' ', ' ' + pair[2] + ' ')
            cur = cur.replace(' ' + pair[1] + '\n', ' ' + pair[2] + '\n')
        data[i] = cur
    
    return data

def save_sorted_abbr_table(data, dir):
    output = pd.DataFrame({'abbreviation': data[:,1], 'full_text': data[:,2]})
    output.to_excel(dir / 'sortedabbriviations.xlsx')

def save_data_table(data, initial_data, dir):
    output = pd.DataFrame({'initial': initial_data, 'postproc_data': data})
    output.to_excel(dir / "postprocdata.xlsx")


def main():

    # Получение информации
    root = Path('.')
    data_dir = root / "data"
    preproc_dir = root / "text_preproc_info"
    data = pd.read_excel(data_dir / "data.xlsx").transpose().to_numpy()[0]
    initial_data = data.copy()

    data = preproc_texts(data, preproc_dir)
    save_data_table(data, initial_data, data_dir)


main()
