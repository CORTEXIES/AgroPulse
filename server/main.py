import pandas as pd
import numpy as np
from pathlib import Path
  
def preproc_texts(data, preproc_dir):
    # Предобработка текста
    abbreviations = pd.read_excel(preproc_dir / 'abbriviations.xlsx').to_numpy() # TODO: Добавить точки к словам для надежности
    abbreviations = dict(abbreviations[:, 1:3].tolist())
    for key, val in list(abbreviations.items()):
        new_key = ''.join(key.split())
        abbreviations[new_key] = val
        new_key = ''.join(map(lambda s: s + '.', key.split()))
        abbreviations[new_key] = val
        new_key = ' '.join(map(lambda s: s + '.', key.split()))
        abbreviations[new_key] = val
    abbreviations = np.array(list(abbreviations.items()))

    abbreviations = abbreviations[abbreviations[:, 0].argsort()[::-1]]
    save_sorted_abbr_table(abbreviations, preproc_dir)

    for i in range(len(data)):
        cur = data[i].replace('\xa0', ' ').lower()
        for pair in abbreviations:
            if cur.startswith(pair[0] + ' '):
                cur.replace(pair[0] + ' ', pair[1] + ' ', 1)
            cur = cur.replace(' ' + pair[0] + ' ', ' ' + pair[1] + ' ')
            cur = cur.replace(' ' + pair[0] + '\n', ' ' + pair[1] + '\n')
            cur = cur.replace('\n' + pair[0] + ' ', '\n' + pair[1] + ' ')
            cur = cur.replace('\n' + pair[0] + '\n', '\n' + pair[1] + '\n')
        data[i] = cur
    
    return data

def save_sorted_abbr_table(data, dir):
    output = pd.DataFrame({'abbreviation': data[:,0], 'full_text': data[:,1]})
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
